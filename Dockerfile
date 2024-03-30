################################################################################
### Build Image
################################################################################
FROM eclipse-temurin:21 as build-image

WORKDIR /build

# Install SBT
ENV SBT_VERSION 1.9.8
RUN curl -sSL "https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.tgz" | gunzip | tar -x -C /usr/local \
    && ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt

# Copy Source
COPY . /build/

# Build Jar
RUN sbt "server/assembly"

################################################################################
### Final Image
################################################################################
FROM eclipse-temurin:21

# Ensure DNS lookups aren't indefinitely cached
RUN sed -i 's/#networkaddress.cache.ttl.*/networkaddress.cache.ttl=10/' /opt/java/openjdk/conf/security/java.security

# Create a non-root user - gitops expects uid 1000
RUN addgroup --gid 1000 --system appuser && adduser --uid 1000 --system appuser --ingroup appuser

WORKDIR /home/appuser

COPY --from=build-image /build/server.jar /home/appuser/server.jar
COPY --from=build-image /build/run.sh /home/appuser/start

RUN chmod +x /home/appuser/start

USER appuser


EXPOSE 9000 5858
