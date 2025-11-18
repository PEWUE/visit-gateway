FROM alpine/java:21-jdk
COPY target/visit-gateway-0.0.1-SNAPSHOT.jar visit-gateway-app.jar
ENTRYPOINT ["java", "-jar", "/visit-gateway-app.jar"]