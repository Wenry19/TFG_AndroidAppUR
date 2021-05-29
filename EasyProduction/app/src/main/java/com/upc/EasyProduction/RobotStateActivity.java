package com.upc.EasyProduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class RobotStateActivity extends AppCompatActivity {

    NetworkService networkService;
    boolean bound = false; // bounded to service?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_state);
    }

    @Override
    protected void onStart(){
        super.onStart();
        // bind to ConnectionService
        Intent intent = new Intent(this, NetworkService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
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


    // NOTES:
    // If your client is still bound to a service when your app destroys the client, destruction causes the client to unbind.
    // It is better practice to unbind the client as soon as it is done interacting with the service.
    // Doing so allows the idle service to shut down.


}