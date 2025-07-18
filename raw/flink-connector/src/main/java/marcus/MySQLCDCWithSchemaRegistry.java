package marcus;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import marcus.avro.AssignmentCdcEvent;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroSerializationSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLCDCWithSchemaRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLCDCWithSchemaRegistry.class);
    private static final AtomicLong counter = new AtomicLong(0);
    private static final long startTime = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        LOG.info("Starting CDC application MySQLCDCWithSchemaRegistry...");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Optional: set parallelism higher if Kafka topic has enough partitions
        env.setParallelism(2);

        // CDC source
        MyAssignmentDebeziumDeserializer deserializer = new MyAssignmentDebeziumDeserializer();

        MySqlSource<AssignmentCdcEvent> source = MySqlSource.<AssignmentCdcEvent>builder()
                .hostname("ai-db")
                .port(3306)
                .username("assignments")
                .password("assignments")
                .databaseList("assignmentassignintent")
                .tableList("assignmentassignintent.assignments")
                .startupOptions(StartupOptions.initial()) // production should update to initialOrLatest
                // snapshot phase -> binlog phase, 
                // snapshot phase only op is read , which has performance issue on the database, 
                // in the binlog phase, even you set parallelism > 1, it will only single thread to read the binlog
                // MySQL binlog is append-only and ordered
                // Flink CDC (via Debezium) reads the binlog in strict sequence from one offset
                // Parallelizing it would break event ordering and consistency guarantees
                // even if you set setParallelism, the subtask will become idle
                .deserializer(deserializer)
                .build();

        // Kafka sink
        KafkaSink<AssignmentCdcEvent> kafkaSink = KafkaSink.<AssignmentCdcEvent>builder()
                .setBootstrapServers("kafka:9092")
                .setRecordSerializer(
                        KafkaRecordSerializationSchema.<AssignmentCdcEvent>builder()
                                .setTopic("cdc-avro-assignment")
                                .setValueSerializationSchema(
                                        ConfluentRegistryAvroSerializationSchema.forSpecific(
                                                AssignmentCdcEvent.class,
                                                "assignments-value",
                                                "http://cp-schema-registry:8085"
                                        )
                                )
                                .setKeySerializationSchema(event -> {
                                    String poolKey = null;
                                    if (event.getAfter() != null && event.getAfter().getPoolKey() != null) {
                                        poolKey = event.getAfter().getPoolKey();
                                    } else if (event.getBefore() != null && event.getBefore().getPoolKey() != null) {
                                        poolKey = event.getBefore().getPoolKey();
                                    }

                                    if (poolKey != null) {
                                        return poolKey.getBytes();
                                    } else {
                                        LOG.info("Processed CDC event without poolKey: {}", event);
                                        return null;
                                    }
                                })
                                .build()
                )
                // Add producer properties for topic auto-creation
                .setProperty("acks", "all")
                .setProperty("retries", "3")
                .setProperty("batch.size", "16384")
                .setProperty("linger.ms", "5")
                .setProperty("buffer.memory", "33554432")
                // Topic auto-creation settings
                .setProperty("allow.auto.create.topics", "true")  // Enable auto topic creation
                // Optional: Configure default topic settings for auto-created topics
                .setProperty("default.replication.factor", "3")  // For MSK, set appropriate replication
                .setProperty("num.partitions", "3")  // Default number of partitions
                // For MSK with IAM authentication (if using IAM)
                // .setProperty("security.protocol", "SASL_SSL")
                // .setProperty("sasl.mechanism", "AWS_MSK_IAM")
                // .setProperty("sasl.jaas.config", "software.amazon.msk.auth.iam.IAMLoginModule required;")
                // .setProperty("sasl.client.callback.handler.class", "software.amazon.msk.auth.iam.IAMClientCallbackHandler")
                .build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "MySQL CDC Source")
                .keyBy(event -> {
                    if (event.getAfter() != null && event.getAfter().getPoolKey() != null) {
                        return event.getAfter().getPoolKey();
                    } else if (event.getBefore() != null && event.getBefore().getPoolKey() != null) {
                        return event.getBefore().getPoolKey();
                    } else {
                        return "unknown-poolKey"; // fallback key for grouping
                    }
                })
                .map((MapFunction<AssignmentCdcEvent, AssignmentCdcEvent>) event -> {
                    long count = counter.incrementAndGet();
                    if (count % 10000 == 0) {
                        long elapsedMillis = System.currentTimeMillis() - startTime;
                        double elapsedSeconds = elapsedMillis / 1000.0;
                        LOG.info("Processed {} CDC events in {} seconds", count, elapsedSeconds);
                    }
                    return event;
                })
                .sinkTo(kafkaSink);

        env.execute("MySQL CDC to Kafka (Avro Schema)");
    }
}
