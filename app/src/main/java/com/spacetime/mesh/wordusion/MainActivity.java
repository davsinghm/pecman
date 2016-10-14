package com.spacetime.mesh.wordusion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.mehul.wordusion.R;
import com.spacetime.mesh.wordusion.activity.MainMenu;
import com.spacetime.mesh.wordusion.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {

    private WebView mWebView;
    Socket mSocket;
    private String username;

    private SensorManager mSensorManager;
    private Sensor mAccel;
    private Sensor mMagnet;
    private Sensor mRotate;
    private JSONObject joAccel;
    private JSONObject joMagnet;
    private JSONObject joRotate;

    private void addValues(JSONObject jo, float[] values) {
        try {
            jo.put("e0", values[0]);
            jo.put("e1", values[1]);
            jo.put("e2", values[2]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initValues(JSONObject jo) {
        try {
            jo.put("e0", 0);
            jo.put("e1", 0);
            jo.put("e2", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isMulti = getIntent().getBooleanExtra("isMultiplayer", false);
        username = getIntent().getStringExtra("username");

        if (isMulti) {
            Toast.makeText(this, username + " just joined.", Toast.LENGTH_LONG).show();
            //Toast.makeText(this, )
        }

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(Constants.GAME_SERVER_URL);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotate = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        joAccel = new JSONObject();
        joMagnet = new JSONObject();
        joRotate = new JSONObject();
        initValues(joAccel);
        initValues(joMagnet);
        initValues(joRotate);

        try {
            mSocket = IO.socket(Constants.SOCKET_SERVER_URL);
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    float velocity;
    int direction;
    long lastTime;


    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        long time = System.currentTimeMillis();
        float accelY = Math.round(event.values[1] * 10) / 10.0f;
        float t = Math.abs((int) (time - lastTime)) / 1000.0f;

        velocity += accelY * t;

        if (event.sensor == mAccel) {
            addValues(joAccel, event.values);
        } else if (event.sensor == mMagnet) {
            addValues(joMagnet, event.values);
        } else if (event.sensor == mRotate) {
            addValues(joRotate, event.values);
        }

        JSONObject jo = new JSONObject();
        try {
            jo.put("accel", joAccel);
            jo.put("magnet", joMagnet);
            jo.put("rotate", joRotate);
            jo.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (time - lastTime > 200) {
            mSocket.emit("sensor", jo);
            Log.d("SENSOR", jo.toString());
            lastTime = time;

        }


        // Do something with this sensor value.
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnet, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotate, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you really want to quit the game?").setCancelable(
                false).setPositiveButton("Quit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(MainActivity.this, MainMenu.class);
                        startActivity(i);
                        MainActivity.this.finish();

                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}