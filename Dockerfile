FROM openjdk:11
COPY target/Sandbox_Vnpay_Maven-0.0.1-SNAPSHOT.jar /src/app/applicaiton.jar
WORKDIR /src/app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "applicaiton.jar", "--spring.datasource.url=jdbc:postgresql://db:5432/vnpay_sandbox"]