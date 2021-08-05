FROM openjdk:11
COPY build/libs/Sandbox_Vnpay-0.0.1-SNAPSHOT.jar /src/app/Sandbox_Vnpay-0.0.1-SNAPSHOT.jar
WORKDIR /src/app/
EXPOSE 8080
RUN ["java", "-jar", "Sandbox_Vnpay-0.0.1-SNAPSHOT.jar"]
