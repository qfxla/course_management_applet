FROM java:8
WORKDIR /zk_course
ADD course_service-0.0.1-SNAPSHOT.jar /zk_course
EXPOSE 9001
ENTRYPOINT ["java","-jar"]
CMD ["course_service-0.0.1-SNAPSHOT.jar"]