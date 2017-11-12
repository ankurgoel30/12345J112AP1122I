FROM java:8
EXPOSE 8085
ADD target/japi-0.1.jar japi-0.1.jar
ENTRYPOINT ["java", "-jar", "japi-0.1.jar"]

