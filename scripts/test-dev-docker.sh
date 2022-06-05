# CMD from Dockerfile is not read when a different command line is given in docker run
# -X for full debugging
# -e for stacktrace
# -Dsurefire.skipAfterFailureCount=1 not woring... why?
docker run -it --rm --name wordmaster-H2-test word_h2_img ./mvnw test -Dspring-boot.run.profiles=dev  -Dspring.profiles.active=dev