FROM adoptopenjdk/openjdk8:alpine-jre
EXPOSE 8080
ADD target/payrollservice-1.0.jar payrollservice.jar
CMD  ["java","-jar","/payrollservice.jar"]
#ENTRYPOINT ["java","-jar","/payrollservice.jar"]
