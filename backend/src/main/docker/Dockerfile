FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD commitstrip-api.jar app.jar
ADD notification.properties notification.properties
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
EXPOSE 8181
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --spring.config.location=notification.properties" ]