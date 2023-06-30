FROM adoptopenjdk:11-jdk-hotspot

WORKDIR /app

COPY target/your-application.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]