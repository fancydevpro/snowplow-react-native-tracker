#!/bin/bash

root_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )/..

mkdir -p $root_path/tmp

set -e
cleanup() {
  echo 'removing tmp dir'
  cd $root_path
  rm -rf tmp
}
trap cleanup EXIT

# pack tracker to local asset
cd  $root_path/tmp
npm pack ..

mv ./snowplow-react-native-tracker*.tgz ./snowplow-react-native-tracker-test-asset.tgz

# clean build
cd $root_path/DemoApp

# js
rm -rf node_modules
rm -rf package-lock.json
npm install
npm install $root_path/tmp/snowplow-react-native-tracker-test-asset.tgz


if [ "$1" == "android" ]; then
  pwd
  rm -rf android/app/build
  cd android
  rm -rf .gradle
  ./gradlew clean
  cd ..

  react-native run-android
else
  echo 'skipping android'
fi

if [ "$1" == "ios" ]; then
  pwd
  rm -rf ios/build

  # cd ios
  # rm -rf Pods
  # pod install
  # cd ..

  react-native run-ios
else
  echo 'skipping ios'
fi
