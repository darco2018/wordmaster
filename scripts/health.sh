# chech if app is running
curl --request GET \
--url http://localhost:8080/actuator/health \
--header 'content-type: application/json'
