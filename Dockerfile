FROM openjdk:11
WORKDIR /zkcourse
ADD course_service-0.0.1-SNAPSHOT.jar /zkcourse
EXPOSE 9001
ENTRYPOINT ["java","-jar"]
CMD ["course_service-0.0.1-SNAPSHOT.jar"]