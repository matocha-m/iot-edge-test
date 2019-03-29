#!/bin/bash -ex

MODULE_PATH=source-code/modules/$MODULE_NAME
DEPLOYMENT_JSON=source-code/config/deployment.amd64.json

az login --service-principal -u $USERNAME -p $PASSWORD --tenant $AZ_AD
az extension add --name azure-cli-iot-ext
VERSION=$(grep -m 1 "<version>" $MODULE_PATH/pom.xml | cut -d '>' -f 2 | cut -d '<' -f 1)
sed -i 's/#AZURE_CR_USERNAME/$USERNAME/g' $DEPLOYMENT_JSON
sed -i 's/#AZURE_CR_PASSWORD/$PASSWORD/g' $DEPLOYMENT_JSON
sed -i 's/#VERSION/$VERSION/g' $DEPLOYMENT_JSON


az iot edge deployment create -d test_$VERSION -n $IOT_HUB_NAME --content $ --target-condition "deviceId='"$DEVICE_ID"'"
