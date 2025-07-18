## Quick Start
Full KRaft-mode Kafka stack with:

✅ Bitnami Kafka (KRaft mode, no Zookeeper)
✅ Karapace Schema Registry (fully KRaft-compatible)
✅ Kafka UI
✅ kcat CLI

### Default Setup
```bash
https://developer.confluent.io/confluent-tutorials/kafka-on-docker/#the-docker-compose-file
docker-compose down -v
docker volume prune -f

docker-compose up -d
docker logs kafka

curl http://localhost:8081
```


| Component       | Address                                        | What it does                         |
| --------------- | ---------------------------------------------- | ------------------------------------ |
| Kafka UI        | [http://localhost:8080](http://localhost:8080) | View/create topics, inspect messages |
| Schema Registry | [http://localhost:8085/](http://localhost:8085/) | REST endpoint for Avro schemas       |
| Kafka           | PLAINTEXT://localhost:9092                     | Bootstrap server                     |
| kcat            | `docker exec -it kcat sh`                      | Use CLI tools                        |


### Create a Topic
✅ 1. Create a topic (e.g., users)
```bash
docker exec kafka kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic users \
  --partitions 1 \
  --replication-factor 1
```


### Create a message through ui
- cannot validate by schema

### Create a message through rest-proxy (create schema automatically)
```bash
curl -X POST http://localhost:8082/topics/user \
  -H "Content-Type: application/vnd.kafka.avro.v2+json" \
  -d '{
    "value_schema": "{\"type\":\"record\",\"name\":\"User\",\"namespace\":\"com.example\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"email\",\"type\":\"string\"}]}",
    "records": [
      { "value": { "id": "123", "email": "john@example.com" } }
    ]
  }'
```

### PLAINTEXT_HOST
```bash
🔍 What Does PLAINTEXT_HOST Mean in Your Docker Compose?
This is a custom listener name that you created in your Kafka container:

KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093,PLAINTEXT_HOST://:9094
You then mapped its advertised listener for outside access:

KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:9094
🔑 Meaning:
Listener	Bind To (Inside Container)	Advertised As (Externally Seen)
PLAINTEXT	:9092	kafka:9092 (internal for containers)
PLAINTEXT_HOST	:9094	localhost:9094 (external from your host machine)
```

### Register Schema
```bash
curl -X POST http://localhost:8085/subjects/assignments-value/versions \
  -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  -d @<(echo '{"schema": '"$(jq -Rs . src/main/avro/assignments.avsc)"'}')
``` or

```bash
SCHEMA_CONTENT=$(< src/main/avro/assignments.avsc)
curl -X POST http://localhost:8085/subjects/assignments-value/versions \
  -H "Content-Type: application/vnd.schemaregistry.v1+json" \
  -d "{\"schema\": $(jq -Rs . <<< "$SCHEMA_CONTENT")}"
```

### Connect ai-db through dBeaver
Before connect, run
```bash
sh insert-data.sh
```