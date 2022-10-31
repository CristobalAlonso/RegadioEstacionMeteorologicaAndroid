package com.example.testandroidmqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String clientId, topic, swtText;
    private MqttAndroidClient client;

    private TextView textView;
    private Switch swt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //objestos del view
        textView = findViewById(R.id.textTest);
        swt = findViewById(R.id.swtTest);

        swtText="";
        clientId ="";
        init();

        swt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swt.isChecked())
                {
                    try
                    {
                        MqttMessage message = new MqttMessage(("ON").getBytes());
                        client.publish("led/01", message);
                    }
                    catch (MqttException e)
                    {
                        e.printStackTrace();
                    }
                }else
                {
                    try
                    {
                        MqttMessage message = new MqttMessage(("OFF").getBytes());
                        client.publish("led/01", message);
                    }
                    catch (MqttException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void init(){
        clientId = "crisss"; //los usuarios id deben ser todos distintos
        topic = "led/01";

        client=new MqttAndroidClient(this.getApplicationContext(), "tcp://68.183.119.177:1883",clientId);
        conect();
    }

    public void conect()
    {
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    sub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sub()
    {
        try
        {
            client.subscribe(topic, 0);
            client.setCallback(new MqttCallback()
            {
                @Override
                public void connectionLost(Throwable cause)
                {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception
                {
                    if(topic.matches("led/01"))
                    {
                        swtText = new String(message.getPayload());
                        if(swtText.matches("ON"))
                        {
                            Log.d(TAG, "true switch");
                            textView.setText("true switch");
                            swt.setChecked(true);
                        }
                        if(swtText.matches("OFF"))
                        {
                            Log.d(TAG, "false switch");
                            textView.setText("false switch");
                            swt.setChecked(false);
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token)
                {

                }
            });
        }
        catch (MqttException e)
        {

        }
    }
}