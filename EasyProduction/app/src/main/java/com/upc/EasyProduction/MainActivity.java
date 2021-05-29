package com.upc.EasyProduction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private NetworkService networkService;
    private boolean bound = false; // bounded to service?

    private Button connectButton;
    private Button disconnectButton;
    private Button startButton;
    private EditText ipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*MediaPlayer player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(true);
        player.start();*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean connectionServiceStarted = isMyServiceRunning(NetworkService.class);

        connectButton = findViewById(R.id.connect_button);
        disconnectButton = findViewById(R.id.disconnect_button);
        startButton = findViewById(R.id.start_button);
        ipText = findViewById(R.id.ip_robot);

        if (!connectionServiceStarted){
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            startButton.setEnabled(false);
        }
        else{
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            startButton.setEnabled(true);
        }
    }

    public void onClickConnectButton(View v){

        String robotIP = ipText.getText().toString();

        // start network service

        Intent i = new Intent(this, NetworkService.class);
        i.putExtra("ip", robotIP);

        if (!isMyServiceRunning(NetworkService.class)) {
            startService(i);
        }
        // until stop service, this service will be executed (if SO do not kill it)
        // do not mind if there are not any activity bound or if app is inactive

        if (!bound){
            bindService(i, connection, Context.BIND_AUTO_CREATE); // asynchronous!!!!
        }

        // wait until bound in another thread, avoiding blocking this one which has to execute the binding

        (new Thread(){
            @Override
            public void run() {
                while (!bound);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // make sure that connection succeed
                        if (!networkService.isConnected()){
                            Toast.makeText(MainActivity.this, "Unable to connect", Toast.LENGTH_LONG).show();
                            // The Classname. this syntax is used to refer to an outer class instance when you are using nested classes
                        }
                        else{
                            connectButton.setEnabled(false);
                            disconnectButton.setEnabled(true);
                            startButton.setEnabled(true);
                        }
                    }
                });
            }
        }).start();
    }

    public void onClickStartButton(View v){
        Intent i = new Intent(this, RobotStateActivity.class);
        // our activity inherits from context
        startActivity(i);
    }

    public void onClickDisconnectButton(View v){
        unbindService(connection);
        bound = false;
        stopService(new Intent(this, NetworkService.class));

        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
        startButton.setEnabled(false);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            NetworkService.MyBinder binder = (NetworkService.MyBinder) service;
            networkService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // The Android system calls this when the connection to the service is unexpectedly lost,
            // such as when the service has crashed or has been killed. This is not called when the client unbinds.
            bound = false;
        }
    };

    // https://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    private boolean isMyServiceRunning(Class<?> serviceClass) { // eye!!
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}