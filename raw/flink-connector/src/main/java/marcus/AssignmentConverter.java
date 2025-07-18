package marcus;

import marcus.avro.Assignment;
import org.apache.kafka.connect.data.Struct;

import java.time.Instant;

public class AssignmentConverter {

    public static Assignment fromStruct(Struct struct) {
        if (struct == null) {
            return null;
        }

        Assignment assignment = new Assignment();
        assignment.setId(struct.getString("id"));
        assignment.setPoolKey(struct.getString("poolKey"));
        assignment.setPoolType(struct.getString("poolType"));
        assignment.setSubjectId(struct.getString("subjectId"));
        assignment.setSubjectType(struct.getString("subjectType"));
        assignment.setSubscriberContextId(struct.getString("subscriberContextId"));
        assignment.setFeatures(struct.getString("features"));
        assignment.setSubscriptionIdentifier(struct.getString("subscriptionIdentifier"));
        assignment.setAssignmentType(struct.getString("assignmentType"));
        assignment.setEnabled(parseBoolean(struct.get("enabled")));
        assignment.setCreatedAt(getInstant(struct, "createdAt"));
        assignment.setUpdatedAt(getInstant(struct, "updatedAt"));
        assignment.setCreatedBy(struct.getString("createdBy"));
        assignment.setUpdatedBy(struct.getString("updatedBy"));
        assignment.setUtsMigrationId(struct.getString("utsMigrationId"));

        return assignment;
    }

    private static Instant getInstant(Struct struct, String fieldName) {
        Object value = struct.get(fieldName);
        if (value instanceof Number) {
            return Instant.ofEpochMilli(((Number) value).longValue());
        } else if (value instanceof String) {
            return Instant.parse((String) value);
        } else {
            return null;
        }
    }    

    // private static Instant getInstant(Struct struct, String fieldName) {
    //     Object value = struct.get(fieldName);
    //     return value != null ? Instant.ofEpochMilli(((Number) value).longValue()) : null;
    // }

    private static boolean parseBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        return true; // fallback default
    }
}
