package io.confluent.developer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class ProducerExample {

    public static void main(final String[] args) {
        final Properties props = new Properties() {{
            put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
            put("schema.registry.url", "http://localhost:8085");

            put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
            put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getCanonicalName());
            put(ACKS_CONFIG, "all");
        }};

        final String topic = "purchases";

        String[] users = {"eabara", "jsmith", "sgarcia", "jbernard", "htanaka", "awalther"};
        String[] items = {"book", "alarm clock", "t-shirts", "gift card", "batteries"};

        try (final Producer<String, Purchase> producer = new KafkaProducer<>(props)) {
            final Random rnd = new Random();
            final int numMessages = 10;

            for (int i = 0; i < numMessages; i++) {
                String user = users[rnd.nextInt(users.length)];
                String item = items[rnd.nextInt(items.length)];
                long timestamp = System.currentTimeMillis();

                Purchase purchase = new Purchase(user, item, timestamp);

                producer.send(
                        new ProducerRecord<>(topic, user, purchase),
                        (event, ex) -> {
                            if (ex != null)
                                ex.printStackTrace();
                            else
                                System.out.printf("Produced Avro event to topic %s: key = %-10s value = %s%n", topic, user, purchase);
                        });
            }

            System.out.printf("%s Avro events were produced to topic %s%n", numMessages, topic);
        }
    }
}
