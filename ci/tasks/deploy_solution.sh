#!/bin/bash -ex

MODULE_PATH=source-code/modules/$MODULE_NAME
DEPLOYMENT_JSON=source-code/config/deployment.amd64.json

az login --service-principal -u $USERNAME -p $PASSWORD --tenant $AZ_AD
wget https://github.com/Azure/azure-iot-cli-extension/releases/download/v0.7.0/azure_cli_iot_ext-0.7.0-py2.py3-none-any.whl
az extension add --source ./azure_cli_iot_ext-0.7.0-py2.py3-none-any.whl --yes
VERSION=$(grep -m 1 "<version>" $MODULE_PATH/pom.xml | cut -d '>' -f 2 | cut -d '<' -f 1)
sed -i 's/#AZURE_CR_USERNAME/'$ACR_USERNAME'/g' $DEPLOYMENT_JSON
sed -i 's/#AZURE_CR_PASSWORD/'$ACR_PASSWORD'/g' $DEPLOYMENT_JSON
sed -i 's/#VERSION/'$VERSION'/g' $DEPLOYMENT_JSON


az iot edge deployment create -d test_${VERSION//./_} -n $IOT_HUB_NAME --content $DEPLOYMENT_JSON --target-condition "deviceId='"$DEVICE_ID"'"
