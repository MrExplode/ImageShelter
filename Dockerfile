FROM gradle:jdk21-alpine AS builder

WORKDIR /app

COPY . .
RUN gradle build --no-daemon

FROM eclipse-temurin:21-jre-alpine as runner

WORKDIR /app
COPY --from=builder /app/build/libs/ImageShelter.jar .

CMD [ "java", "-jar", "ImageShelter.jar" ]