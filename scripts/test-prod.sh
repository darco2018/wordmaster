cd ..

# kill anything on 8080
fuser -k 8080/tcp

echo "service mysql status"
service mysql status

echo "./mvnw test  -Dspring.profiles.active=prod  -Dsurefire.skipAfterFailureCount=1 -f pom.xml"
./mvnw test  -Dspring.profiles.active=prod  -Dsurefire.skipAfterFailureCount=1 -f pom.xml