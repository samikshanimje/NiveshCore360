# Stage 1: Build the Fat JAR
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Prefetch dependencies for caching
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Provision light desktop environment with VNC/noVNC
FROM ubuntu:22.04
ENV DEBIAN_FRONTEND=noninteractive

# Install Java 21, Xvfb, Fluxbox (window manager), x11vnc, noVNC, and websockify
RUN apt-get update && apt-get install -y \
    openjdk-21-jre \
    xvfb \
    fluxbox \
    x11vnc \
    novnc \
    websockify \
    curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Workaround for package naming discrepancy in websockify/novnc
RUN ln -s /usr/share/novnc/vnc.html /usr/share/novnc/index.html

WORKDIR /app
COPY --from=build /app/target/niveshcore360-1.0.0.jar app.jar

# Setup entrypoint script
RUN echo '#!/bin/bash\n\
# 1. Start X virtual framebuffer\n\
Xvfb :99 -screen 0 1280x800x24 &\n\
export DISPLAY=:99\n\
sleep 1\n\
\n\
# 2. Start Fluxbox window manager\n\
fluxbox &\n\
sleep 1\n\
\n\
# 3. Start VNC server mapping display :99\n\
x11vnc -display :99 -nopw -listen localhost -xkb -forever &\n\
sleep 1\n\
\n\
# 4. Start Websockify to bridge VNC to Web (noVNC)\n\
websockify --web=/usr/share/novnc 8080 localhost:5900 &\n\
sleep 1\n\
\n\
# 5. Start the Swing Application\n\
java -Djava.awt.headless=false -jar app.jar\n\
' > /app/entrypoint.sh && chmod +x /app/entrypoint.sh

# Expose VNC web port
EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]
