#!/bin/bash

PROJECT_ROOT=$(cd -- "$(dirname -- "$0")" && pwd)

BUILD_PATH="$PROJECT_ROOT/build/libs"
JAR_NAME=$(ls -t "$BUILD_PATH" | grep '\.jar$' | grep -v 'plain' | head -n 1)
JAR_PATH="$BUILD_PATH/$JAR_NAME"

KAKAO_CLIENT_ID="카카오 클라이언트 ID 입력"
KAKAO_REDIRECT_URI="카카오 API 에서 설정한 redirect_uri 입력"

echo "> 현재 실행중인 애플리케이션 PID 확인"
CURRENT_PID=$(pgrep -f "$JAR_NAME")

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> 현재 구동중인 애플리케이션을 종료하겠습니다. (PID: $CURRENT_PID)"
  kill -15 "$CURRENT_PID"
  sleep 5
fi

DEPLOY_PATH="$PROJECT_ROOT/.."

echo "> JAR 파일 복사"
cp "$JAR_PATH" "$DEPLOY_PATH"

echo "> 배포 폴더로 이동"
cd "$DEPLOY_PATH"

echo "> 새 애플리케이션 배포: $JAR_NAME"
nohup java -jar \
        -DKAKAO_CLIENT_ID=$KAKAO_CLIENT_ID \
        -DKAKAO_REDIRECT_URI=$KAKAO_REDIRECT_URI \
        "$JAR_NAME" > /dev/null 2> /dev/null < /dev/null &