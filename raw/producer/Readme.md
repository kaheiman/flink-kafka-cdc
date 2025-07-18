### Check gradle exist
```bash
gradle -v
```

------------------------------------------------------------
Gradle 8.12.1
------------------------------------------------------------

Build time:    2025-01-24 12:55:12 UTC
Revision:      0b1ee1ff81d1f4a26574ff4a362ac9180852b140

Kotlin:        2.0.21
Groovy:        3.0.22
Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
Launcher JVM:  23.0.2 (Homebrew 23.0.2)
Daemon JVM:    /opt/homebrew/Cellar/openjdk/23.0.2/libexec/openjdk.jdk/Contents/Home (no JDK specified, using current Java home)
OS:            Mac OS X 15.5 aarch64

### Compile all the classes under main
```bash
gradle classes
```

### Generate Avro schema in build
```bash
gradle generateAvroJava
```

### Build fat jar
```bash
gradle clean build
gradle shadowJar
gradle clean shadowJar
```

### Run fat jar
```bash
java -jar build/libs/kafka-java-getting-started-0.0.1.jar

# Run producer
java -cp build/libs/kafka-java-getting-started-0.0.1.jar io.confluent.developer.ProducerExample

# Run consumer
java -cp build/libs/kafka-java-getting-started-0.0.1.jar io.confluent.developer.ConsumerExample

```
### Upload flink job
curl -X POST -F "jarfile=@/Users/manmar/Personal/cp-all-in-one/cp-all-in-one/raw/flink-connector/build/libs/flink-connector-1.0-SNAPSHOT-all.jar" http://localhost:8081/jars/upload
