package com.upc.EasyProduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.upc.EasyProduction.SubPackages.RobotModeData;

public class RobotStateActivity extends AppCompatActivity {

    private NetworkService networkService;
    private boolean bound = false; // bounded to service?

    private DashBoardConnection db;
    private String robotIP;

    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Button msgButton;

    private TextView programStatus;

    private Thread updatingValuesThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_state);

        Intent i = getIntent();
        robotIP = i.getStringExtra("ip");
        // check ip again? checked in each connection

        db = new DashBoardConnection(robotIP);
    }

    @Override
    protected void onStart(){
        super.onStart();

        // bind to NetworkService
        if (!bound) { // pre: service is started!! it is not possible to reach this activity without starting service
            doBindService(); // asynchronous!!
        }
        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        stopButton = findViewById(R.id.stop_button);
        msgButton = findViewById(R.id.msg_button);

        programStatus = findViewById(R.id.program_status);

        startUpdatingValues();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
    }

    //https://stackoverflow.com/questions/22079909/android-java-lang-illegalargumentexception-service-not-registered
    public void doBindService() {
        bound = bindService(new Intent(this, NetworkService.class), connection, Context.BIND_AUTO_CREATE);
    }

    public void doUnbindService() {
        if (bound) {
            unbindService(connection);
            bound = false;
            networkService = null;
        }
    }

    public void onClickPlayButton(View v){

        (new Thread(){
            @Override
            public void run() {
                super.run();
                db.connect();
                if (db.isSocketConnected()) {
                    db.play();
                }
                else{
                    //...
                }
                db.close();
            }
        }).start();

    }

    public void onClickPauseButton(View v){

        (new Thread(){
            @Override
            public void run() {
                super.run();
                db.connect();
                if (db.isSocketConnected()) {
                    db.pause();
                }
                else{

                }
                db.close();
            }
        }).start();

    }

    public void onClickStopButton(View v){

        (new Thread(){
            @Override
            public void run() {
                super.run();
                db.connect();
                if (db.isSocketConnected()) {
                    db.stop();
                }
                else{

                }
                db.close();
            }
        }).start();

    }

    public void onClickMsgButton(View v){

    }

    private void startUpdatingValues(){
        // make sure that we are bound

        updatingValuesThread = (new MyThread(){
            @Override
            public void run() {
                super.run();

                while (networkService == null);

                while (bound){ // if stop activity then we do unbind

                    RobotModeData rmData = networkService.getRobotModeData();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (rmData.getIsProgramRunning()) {
                                programStatus.setText("programStatus: RUNNING");
                            }
                            else if (rmData.getIsProgramPaused()){
                                programStatus.setText("programStatus: PAUSED");
                            }
                            else {
                                programStatus.setText("programStatus: STOPPED");
                            }
                        }
                    });
                    try {
                        this.sleep(1000);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });

        updatingValuesThread.start();
    }




    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            NetworkService.MyBinder binder = (NetworkService.MyBinder) service;
            networkService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // The Android system calls this when the connection to the service is unexpectedly lost,
            // such as when the service has crashed or has been killed. This is not called when the client unbinds.
            networkService = null;
        }
    };

    // NOTES:
    // If your client is still bound to a service when your app destroys the client, destruction causes the client to unbind.
    // It is better practice to unbind the client as soon as it is done interacting with the service.
    // Doing so allows the idle service to shut down.

}