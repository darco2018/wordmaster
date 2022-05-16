cd ..
echo "./mvnw test  -Dspring.profiles.active=dev  -Dsurefire.skipAfterFailureCount=1 -f pom.xml"
./mvnw test  -Dspring.profiles.active=dev  -Dsurefire.skipAfterFailureCount=1 -f pom.xml