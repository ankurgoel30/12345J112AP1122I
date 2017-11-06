FROM java:8
EXPOSE 8085
ADD target/api-0.1.jar api-0.1.jar
ENTRYPOINT ["java", "-jar", "api-0.1.jar"]

