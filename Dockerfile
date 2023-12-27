FROM maven:3.9.1 AS builder
WORKDIR /app

COPY ./  ./
RUN mvn clean package -DskipTests

FROM openjdk:11.0.2-jre-slim-stretch

COPY --from=builder /app/target/ak-sk-demo.jar  /bin/

EXPOSE 8080

ENTRYPOINT java -XX:+PrintFlagsFinal \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+UseContainerSupport \
 $JAVA_OPTS -jar /bin/ak-sk-demo.jar