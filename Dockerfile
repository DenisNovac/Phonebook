FROM openjdk:8-jre-alpine

RUN mkdir -p /opt/app

WORKDIR /opt/app

COPY ./run_jar.sh ./target/scala-2.12/Phonebook-assembly-0.1.jar ./

RUN chmod +x ./run_jar.sh

ENTRYPOINT ["./run_jar.sh"]