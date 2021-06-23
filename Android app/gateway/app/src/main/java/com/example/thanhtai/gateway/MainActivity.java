package com.example.thanhtai.gateway;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

public class MainActivity extends Activity implements SerialInputOutputManager.Listener {
    TextView textView_topic,textView_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView_topic=findViewById(R.id.textViewTopic);
        textView_data=findViewById(R.id.textViewData);
        startMQTT();
        openUART();
        //sendDataToMQTT("aa","bb");
    }




    MQTTHelper mqttHelper;
    private void startMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.d("MQTT", mqttMessage.toString());
                //textView_data.setText(mqttMessage.toString());
                //textView_topic.setText(topic);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    private void sendDataToMQTT(String value){

        MqttMessage msg = new MqttMessage(); msg.setId(1234);
        msg.setQos(0); msg.setRetained(true);

        String data = value;

        byte[] b = data.getBytes(Charset.forName("UTF-8")); msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish("anhlaga06/feeds/thanhtai", msg);

        }catch (MqttException e){
        }
    }


    final String TAG = "MAIN_TAG";
    private static final String ACTION_USB_PERMISSION = "com.android.recipes.USB_PERMISSION";
    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";

    UsbSerialPort port;

    private void openUART(){
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

        if (availableDrivers.isEmpty()) {
            Log.d(TAG, "UART is not available");

        }else {
            Log.d(TAG, "UART is available");

            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {

                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                manager.requestPermission(driver.getDevice(), usbPermissionIntent);

                manager.requestPermission(driver.getDevice(), PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0));


                return;
            } else {

                port = driver.getPorts().get(0);
                try {
                    port.open(connection);
                    port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

                    SerialInputOutputManager usbIoManager = new SerialInputOutputManager(port, this);
                    Executors.newSingleThreadExecutor().submit(usbIoManager);
                    Log.d(TAG, "UART is openned");

                } catch (Exception e) {
                    Log.d(TAG, "There is error");
                }
            }
        }

    }


    String buffer  = "";
    int ii=0;

    @Override
    public void onNewData(byte[] data) {
        try {
            buffer = new String(data);
            textView_topic.setText(buffer);
            String[] data_js=buffer.split("//");
            int id=Integer.parseInt(data_js[0]);
            double temp=round(Double.parseDouble(data_js[1]),1);
            double hum=round(Double.parseDouble(data_js[2]),1);
            if(temp==0.0||hum==0.0) return;
            JSONObject ob = new JSONObject();
            ob.put("id",String.valueOf(id));
            ob.put("temp",String.valueOf(temp));
            ob.put("hum",String.valueOf(hum));
            textView_data.setText(ob.toString());
            sendDataToMQTT(ob.toString());
        } catch (Exception e) {
            e.printStackTrace();
            String a="failed";
            textView_data.setText(a);
        }


    }

    @Override
    public void onRunError(Exception e) {

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


}