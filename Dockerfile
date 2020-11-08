FROM openjdk:8-jdk-slim

RUN mkdir /alignment-service
COPY . /alignment-service
WORKDIR /alignment-service
RUN chmod +x entrypoint.sh
RUN ls -ltarh

CMD ["./entrypoint.sh"]