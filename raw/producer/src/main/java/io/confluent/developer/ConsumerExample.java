package io.confluent.developer;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class ConsumerExample {

    public static void main(final String[] args) {
        final Properties props = new Properties() {{
            // Required configuration
            put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
            put("schema.registry.url", "http://localhost:8085");

            // Deserializers
            put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            put(VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getCanonicalName());

            // Tell the deserializer to return specific Avro class instead of GenericRecord
            put("specific.avro.reader", true);

            // Consumer group settings
            put(GROUP_ID_CONFIG, "kafka-java-getting-started");
            put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        }};

        final String topic = "purchases";

        try (final Consumer<String, Purchase> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Arrays.asList(topic));
            while (true) {
                ConsumerRecords<String, Purchase> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, Purchase> record : records) {
                    String key = record.key();
                    Purchase value = record.value();

                    System.out.printf("Consumed Avro event from topic %s: key = %-10s value = %s%n", topic, key, value);
                }
            }
        }
    }
}
