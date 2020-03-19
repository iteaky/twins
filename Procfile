heroku ps:scale web=2 worker=1
web: java $JAVA_OPTS -Dspring.profiles.active=default -jar target/dependency/webapp-runner.jar --port 8080 --expand-war target/*.war
