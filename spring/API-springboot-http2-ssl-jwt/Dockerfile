FROM adoptopenjdk/openjdk11

# change this if your port changes too or if protocol becomes https
HEALTHCHECK --interval=1m --timeout=10s \
  CMD curl -kf http://localhost:8443/healthcheck || exit 1

RUN mkdir /app
ADD target/*.jar /app/
WORKDIR /app

CMD java -jar app.jar
