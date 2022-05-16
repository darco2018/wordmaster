cd ..
echo "./mvnw test  -Dspring.profiles.active=prod  -Dsurefire.skipAfterFailureCount=1 -f pom.xml"
./mvnw test  -Dspring.profiles.active=prod  -Dsurefire.skipAfterFailureCount=1 -f pom.xml