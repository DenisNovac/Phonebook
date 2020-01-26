FROM openjdk:11-jre-slim

RUN mkdir -p /opt/app

WORKDIR /opt/app

COPY ./application.conf ./run_jar.sh ./target/scala-2.12/Phonebook-assembly-0.1.jar ./swagger.yaml ./

RUN chmod +x ./run_jar.sh

ENTRYPOINT ["./run_jar.sh"]