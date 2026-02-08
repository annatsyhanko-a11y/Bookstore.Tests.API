FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

RUN apk add --no-cache bash

COPY . .

RUN chmod +x gradlew

ENTRYPOINT ["./gradlew"]
CMD ["test"]