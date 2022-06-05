cd ..
# kill anything on 8080
fuser -k 8080/tcp

echo "service mysql status"
service mysql status

echo "./mvnw spring-boot:run -Dspring-boot.run.profiles=prod"
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod