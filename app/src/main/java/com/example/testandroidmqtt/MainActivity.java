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

public class MainActivity extends AppCompatActivity implements MqttCallback, IMqttActionListener
{
    //implementacion de dos callback para recibir respuestas

    private String TAG;
    private String server, clientId, topic, swtText;
    private MqttAndroidClient client;

    private TextView textView;
    private Switch swt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //objestos del view
        textView = findViewById(R.id.textTest); //etiqueta de texto para ejemplo viual
        swt = findViewById(R.id.swtTest); //switch para ejemplo viual

        //Config
        TAG = "MainActivity"; //titulo de la app para mostrar en consola
        topic = "led/01"; //topico para interactuar con la app
        clientId ="cris"; //nombre del usuario no puede ser igual a otro en el server o crashea
        server = "tcp://68.183.119.177:1883"; //servidor
        init(); //funcion para iniciar

        /*
            Action Listener para el boton de switch
         */
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

    /*
        init()
        Funcion para iniciar la conexion, llama a la funcion conect
        param   -> void
        return  -> void
    */
    public void init(){

        client=new MqttAndroidClient(this.getApplicationContext(), server,clientId);
        conect();
    }

    /*
        conect()
        funcion para conectar, llama a un evento callback para recibir una respuesta del servidor
        y revisar si esta conectado, este evento tambien llama a sub si es que la llamada de respuesta
        es una conexion exitosa
        param   -> void
        return  -> void
    */
    public void conect()
    {
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(this);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /*
        sub()
        Funcion de subcripcion para enviar un string, esta funcion llama un callback
        para comunicarse con el servidor a travez de un topico
        param   -> void
        return  -> void
    */
    private void sub()
    {
        try
        {
            client.subscribe(topic, 0);
            client.setCallback(this);
        }
        catch (MqttException e)
        {

        }
    }

    // CALLBACK CONECTION

    /*
        onSuccess(IMqttToken asyncActionToken)
        En caso de que la conexion sea exitosa mostrara por consola detalles de la conexion y
        una respuesta de retroalimentacion
        param   -> IMqttToken
        return  -> void
    */
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        printDetail();
        Log.d(TAG, "onSucces");
        sub();
    }

    /*
        onFailure(IMqttToken asyncActionToken, Throwable exception)
        En caso de que la conexion falle mostrara por consola detalles de la conexion y
        una respuesta de retroalimentacion
        param   -> IMqttToken, Throwable
        return  -> void
    */
    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        printDetail();
        Log.d(TAG, "onFailure");
    }

    //EVENT TOKEN STRING

    /*
        connectionLost(Throwable cause)
        En caso de que la conexion se pierda
        param   -> Throwable
        return  -> void
    */
    @Override
    public void connectionLost(Throwable cause)
    {
        Log.d(TAG, "Conexion perdida con el servidor");
    }

    /*
        messageArrived(String topic, MqttMessage message)
        El codigo de esta seccion es el que conecta he interactua con el servidor
        param   -> String, MqttMessage
        return  -> void
    */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception
    {
        if(topic.matches("led/01"))
        {
            swtText = new String(message.getPayload());
            if(swtText.matches("ON"))
            {
                Log.d(TAG, "true switch");
                textView.setText("true switch"); // se cambia el texto visible
                swt.setChecked(true);
            }
            if(swtText.matches("OFF"))
            {
                Log.d(TAG, "false switch");
                textView.setText("false switch"); // se cambia el texto visible
                swt.setChecked(false);
            }
        }
    }

    /*
        deliveryComplete(IMqttDeliveryToken token)

        param   -> IMqttDeliveryToken
        return  -> void
    */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    private void printDetail()
    {
        Log.d(TAG, "Client: "+clientId);
        Log.d(TAG, "Server: "+server);
        Log.d(TAG, "Topic: "+topic);
    }
}