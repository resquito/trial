FROM openjdk:8-jre

EXPOSE 8080

WORKDIR trial

COPY trial-api/target/trial-api-1.0-SNAPSHOT.jar ./trial-api.jar


CMD ["java", \
  "-Xms1g", "-Xmx2g", \
  "-XX:+UseConcMarkSweepGC", \
 # "-XX:NewSize=512M", "-XX:SurvivorRatio=8", "-XX:MaxGCPauseMillis=50", \
 # "-XX:MaxHeapFreeRatio=10", "-XX:MinHeapFreeRatio=10", \
  "-Dfile.encoding=UTF8",
  "-jar", "trial-api.jar"]