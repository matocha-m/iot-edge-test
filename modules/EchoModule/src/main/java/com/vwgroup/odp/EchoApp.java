package com.vwgroup.odp;

import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodData;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeCallback;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.ModuleClient;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoApp {

  private static final Logger log = LoggerFactory.getLogger(EchoApp.class);

  private static MessageCallbackMqtt msgCallback = new MessageCallbackMqtt();
  private static EventCallback eventCallback = new EventCallback();

  protected static class EventCallback implements IotHubEventCallback {

    @Override
    public void execute(IotHubStatusCode status, Object context) {
      log.info("Send message with status: {}", status.name());
    }
  }

  protected static class MessageCallbackMqtt implements DeviceMethodCallback {

    @Override
    public DeviceMethodData call(String methodName, Object methodData, Object context) {
      String message = new String((byte[]) methodData);
      log.info("Received message {} for method {}.", message, methodName);
      return new DeviceMethodData(200, message);
    }
  }

  protected static class ConnectionStatusChangeCallback implements IotHubConnectionStatusChangeCallback {

    @Override
    public void execute(IotHubConnectionStatus status,
        IotHubConnectionStatusChangeReason statusChangeReason,
        Throwable throwable, Object callbackContext) {
      String statusStr = "Connection Status: {}";
      switch (status) {
        case CONNECTED:
          log.info(statusStr, "Connected");
          break;
        case DISCONNECTED:
          log.info(statusStr, "Disconnected");
          if (throwable != null) {
            log.warn("Error caught", throwable);
          }
          System.exit(1);
          break;
        case DISCONNECTED_RETRYING:
          log.info(statusStr, "Retrying");
          break;
      }
    }
  }

  public static void main(String[] args) {
    try {
      log.info("Start to create client with MQTT protocol");
      ModuleClient client = ModuleClient.createFromEnvironment(IotHubClientProtocol.MQTT);
      log.info("Client created");
      client.registerConnectionStatusChangeCallback(new ConnectionStatusChangeCallback(), null);
      client.open();
      client.subscribeToMethod(msgCallback, null, eventCallback, null);
    } catch (Exception e) {
      log.error("Exception occurred", e);
      System.exit(1);
    }
  }
}
