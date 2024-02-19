FROM amazoncorretto:11
COPY target/shareit-server-1.0.jar app/shareit-server.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app/shareit-server.jar"]
