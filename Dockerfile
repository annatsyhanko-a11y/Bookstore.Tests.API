FROM gradle:8.5-jdk21-alpine

WORKDIR /app

COPY . .

ENTRYPOINT ["./gradlew"]
CMD ["test"]