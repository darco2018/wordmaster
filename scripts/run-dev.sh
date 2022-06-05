cd ..

# kill anything on 8080
fuser -k 8080/tcp

echo "./mvnw spring-boot:run -Dspring-boot.run.profiles=dev"
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev -DskipTests