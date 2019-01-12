#!/bin/bash

IMAGE_NAME="akeboshiwind/obelisk-exporter"
TAG="${1}"

lein clean

docker build -t ${IMAGE_NAME}:${TAG} -t ${IMAGE_NAME}:latest .
docker push ${IMAGE_NAME}
