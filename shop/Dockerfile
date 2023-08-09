FROM openjdk:17-jdk-slim
LABEL authors="Nina Madra"
ENV JAVA_HOME /usr/local/openjdk-17
ENV PATH $JAVA_HOME/bin:$PATH
EXPOSE 8080
WORKDIR /app
COPY target/shop-0.0.1-SNAPSHOT.jar /app/shop.jar
ENTRYPOINT ["java", "-jar", "shop.jar"]