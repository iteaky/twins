heroku ps:scale web=2 worker=1
web: java $JAVA_OPTS -Dspring.profiles.active=default -jar target/twins-0.0.1-SNAPSHOT.jar --port 8080 --expand-war target/*.war
