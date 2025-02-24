FROM openjdk:17-jdk-slim

WORKDIR /app

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/delivery
ENV SPRING_DATASOURCE_USERNAME=twenty1
ENV SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}
ENV AI_API_KEY=${AI_API_KEY}

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]