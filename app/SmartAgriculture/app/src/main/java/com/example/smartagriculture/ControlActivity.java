package com.example.smartagriculture;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControlActivity extends AppCompatActivity {

    // cac thanh phan giao dien
    TextView txt_device;
    TextView txt_time;
    TextView txt_lamp;
    TextView txt_pump;
    TextView txt_temperature;
    TextView txt_humidity;
    TextView txt_soil_moisture;
    SwitchCompat switch_lamp;
    SwitchCompat switch_pump;

    // cac thong so ket noi iot
    final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com";
    String deviceType;  //thong tin device se dung de nhan va gui du lieu
    String deviceId;
    final String organization = "i4tud6";
    final String appID = "app01";
    final String IOT_API_KEY = "wctfyfppf2";
    final String authorizationToken = "HtGR15MOEu7taLi02Q";
    MqttAndroidClient client;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        // lay cac bien duoc truyen tu activity qua lop Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            deviceType = bundle.getString("deviceType");
            deviceId = bundle.getString("deviceId");
        }

        // an thanh ActionBar
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Control");
        ab.setDisplayHomeAsUpEnabled(true);

        // lay ra cac thanh phan trong view cua control fragment
        txt_device = findViewById(R.id.txt_device);
        txt_time = findViewById(R.id.txt_time);
        txt_lamp = findViewById(R.id.txt_lamp);
        txt_pump = findViewById(R.id.txt_pump);
        txt_temperature = findViewById(R.id.txt_temperature);
        txt_humidity = findViewById(R.id.txt_humidity);
        txt_soil_moisture = findViewById(R.id.txt_soil_moisture);
        switch_lamp = findViewById(R.id.switch_lamp);
        switch_pump = findViewById(R.id.switch_pump);

        // ket noi IoT kieu api key
        String clientID = "a:" + organization + ":" + appID;
        String connectionURI = "tcp://" + organization + IOT_ORGANIZATION_TCP;
        String username = "a-" + organization + "-" + IOT_API_KEY;
        char[] password = authorizationToken.toCharArray();
        String pubTopic = "iot-2/type/" + deviceType + "/id/" + deviceId + "/cmd/control/fmt/json"; //topic dieu khien
        String subTopic = "iot-2/type/" + deviceType + "/id/" + deviceId + "/evt/status/fmt/json"; //topic nhan du lieu data sensor

        // connect
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password);
        client = new MqttAndroidClient(getApplicationContext(), connectionURI, clientID);
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    // Subscribe topic
                    subMQTT(subTopic);
                    txt_device.setText("Thiết bị " + deviceType + ": " + deviceId);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        // Set callback cho Subscribe
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                try {
                    JSONObject payload = new JSONObject(message.toString());
                    JSONObject data = payload.getJSONObject("d");
                    long timestamp = data.getLong("time_send");
                    int temp = (int) data.getDouble("temperature_air");
                    int hum = (int) data.getDouble("humidity_air");
                    int soil = (int) data.getDouble("humidity_soil");
                    Date time_send = new Date(timestamp * 1000);
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                    txt_time.setText("Time: " + formatter.format(time_send));
                    txt_temperature.setText(String.valueOf(temp));
                    txt_humidity.setText(String.valueOf(hum));
                    txt_soil_moisture.setText(String.valueOf(soil));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        // lamp
        switch_lamp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switch_lamp.isChecked()) {
                    String payload = "{\"lamp\":1}";
                    if (pubMQTT(pubTopic, payload)) {
                        txt_lamp.setText("Lamp is On");
                        Toast.makeText(getApplicationContext(), "Bật đèn thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        switch_lamp.setChecked(false);
                    }
                } else {
                    String payload = "{\"lamp\":0}";
                    if (pubMQTT(pubTopic, payload)) {
                        txt_lamp.setText("Lamp is Off");
                        Toast.makeText(getApplicationContext(), "Tắt đèn thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        switch_lamp.setChecked(true);
                    }
                }
            }
        });

        // pump
        switch_pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switch_pump.isChecked()) {
                    String payload = "{\"pump\":1}";
                    if (pubMQTT(pubTopic, payload)) {
                        txt_pump.setText("Pump is On");
                        Toast.makeText(getApplicationContext(), "Bật máy bơm thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        switch_pump.setChecked(false);
                    }
                } else {
                    String payload = "{\"pump\":0}";
                    if (pubMQTT(pubTopic, payload)) {
                        txt_pump.setText("Pump is Off");
                        Toast.makeText(getApplicationContext(), "Tắt máy bơm thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        switch_pump.setChecked(true);
                    }
                }
            }
        });
        
    }

    // an vao back tren actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); //xu ly tuong ung voi nut back cung
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Subscribe
    public void subMQTT(String topic) {
        int qos = 0;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Publish
    public boolean pubMQTT(String topic, String payload) {
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
            return true;
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Có lỗi xảy ra, publish thất bại", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    
}