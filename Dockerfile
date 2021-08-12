FROM adoptopenjdk/openjdk16-openj9:jre-16.0.1_9_openj9-0.26.0
COPY ./target/api.jar /api.jar
EXPOSE 8080
CMD ["java", "-jar", "/api.jar"]
