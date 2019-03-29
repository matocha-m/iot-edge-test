#!/bin/bash -ex

cd source-code/modules/$MODULE_NAME
mvn clean install

NAME=`ls target/*.jar | cut -d '/' -f 2 | grep 'with-deps.jar$'`

cd -
mv source-code/modules/$MODULE_NAME/target/$NAME artifacts/app.jar
grep -m 1 "<version>" source-code/modules/$MODULE_NAME/pom.xml | cut -d '>' -f 2 | cut -d '<' -f 1 > version/version.txt

