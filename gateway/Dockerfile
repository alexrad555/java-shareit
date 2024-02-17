FROM amazoncorretto:11
COPY target/shareit-gateway-1.0.jar app/shareit-gateway.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app/shareit-gateway.jar"]
