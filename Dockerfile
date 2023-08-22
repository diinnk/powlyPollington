FROM adoptopenjdk/openjdk11:debian-slim

COPY stage/ /usr/src/app/
WORKDIR /usr/src/app

HEALTHCHECK --start-period=15s --interval=30s --timeout=10s --retries=2 \
  CMD curl -f http://localhost:9000/health || exit 1

CMD ["bin/powlypollington"]
