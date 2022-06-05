cd ..

# kill anything on 8080
fuser -k 8080/tcp

echo "./mvnw test  -Dspring.profiles.active=dev  -Dsurefire.skipAfterFailureCount=1 -f pom.xml"
./mvnw test  -Dspring.profiles.active=dev  -Dsurefire.skipAfterFailureCount=1 -f pom.xml