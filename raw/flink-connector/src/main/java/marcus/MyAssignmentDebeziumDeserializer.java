package marcus;

import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.kafka.connect.source.SourceRecord;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import marcus.avro.Assignment;
import marcus.avro.AssignmentCdcEvent;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

import java.util.Map;

public class MyAssignmentDebeziumDeserializer implements DebeziumDeserializationSchema<AssignmentCdcEvent> {

    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<AssignmentCdcEvent> out) throws Exception {
        Struct value = (Struct) sourceRecord.value();

        Struct beforeStruct = value.getStruct("before");
        Struct afterStruct = value.getStruct("after");
        String op = value.getString("op");
        Long ts = value.getInt64("ts_ms");

        Assignment before = beforeStruct != null ? AssignmentConverter.fromStruct(beforeStruct) : null;
        Assignment after = afterStruct != null ? AssignmentConverter.fromStruct(afterStruct) : null;

        AssignmentCdcEvent event = AssignmentCdcEvent.newBuilder()
                .setBefore(before)
                .setAfter(after)
                .setOp(op)
                .setTsMs(ts)
                .build();

        out.collect(event);
    }

    @Override
    public TypeInformation<AssignmentCdcEvent> getProducedType() {
        return TypeInformation.of(AssignmentCdcEvent.class);
    }
}
