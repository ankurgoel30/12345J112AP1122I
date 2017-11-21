FROM openjdk:8-jre
MAINTAINER ThinkHR <engineering@thinkhr.com>

ENV RUNNERDIR /opt/runner
ENV LOGGERDIR /var/opt/logs

RUN mkdir -p ${RUNNERDIR}
RUN mkdir -p ${LOGGERDIR}

COPY target/japi-0.1.jar ${RUNNERDIR}/japi.jar

# Install the AWS CLI, enable this after setting up password, etc in the keystore.
#RUN apt-get update && \
#    apt-get -y install python curl unzip && cd /tmp && \
#    curl "https://s3.amazonaws.com/aws-cli/awscli-bundle.zip" \
#    -o "awscli-bundle.zip" && \
#    unzip awscli-bundle.zip && \
#    ./awscli-bundle/install -i /usr/local/aws -b /usr/local/bin/aws && \
#    rm awscli-bundle.zip && rm -rf awscli-bundle


EXPOSE 8081
WORKDIR ${RUNNERDIR}

ENTRYPOINT java -XX:+UseConcMarkSweepGC -jar -Dserver.port=8081 japi.jar  >> /var/opt/logs/japi.log 2>&1


