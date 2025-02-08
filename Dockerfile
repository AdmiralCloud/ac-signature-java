FROM maven:3.9-eclipse-temurin-17

WORKDIR /app
COPY . .

# Run tests
CMD ["mvn", "clean", "test"]