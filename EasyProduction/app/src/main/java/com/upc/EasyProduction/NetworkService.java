package com.upc.EasyProduction;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.upc.EasyProduction.Communication.TcpIpConnection;
import com.upc.EasyProduction.DataPackages.GVarsData;
import com.upc.EasyProduction.DataPackages.JointData;
import com.upc.EasyProduction.DataPackages.MasterBoardData;
import com.upc.EasyProduction.DataPackages.RobotModeData;
import com.upc.EasyProduction.DataPackages.ToolData;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class NetworkService extends Service {

    private final IBinder binder = new MyBinder();

    private String ip;

    private TcpIpConnection tcpIp;

    private MyThread tcpIpThread;

    private boolean triedToConnect = false;

    private NotificationManagerCompat notificationManager;

    private SharedPreferences sharedPref;

    //private int last_program_state = -1; // 0 running, 1 paused, 2 stopped

    public class MyBinder extends Binder {
        NetworkService getService() {
            // returns this instance of service, so clients can call public methods
            return NetworkService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        toastMessage("Network Service Started");

        notificationManager = NotificationManagerCompat.from(this);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ip = intent.getStringExtra("ip");
        tcpIp = new TcpIpConnection(ip);

        // https://androidwave.com/foreground-service-android-example/

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("Easy Production Foreground Service")
                .setContentText("Network Service Running with IP = " + ip)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        // load sharedPreferences

        loadSharedPreferences();
        
        startTcpIpThread();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        toastMessage("Network Service Destroyed");
        //player.stop();

        // kill threads

        tcpIpThread.myStop();

        // save names by user in sharedPreferences

        updateSharedPreferences();
    }

    private void updateSharedPreferences(){

        Set<String> aux = new HashSet<String>(tcpIp.getGlobalVariablesData().getVarNamesByUser());
        Log.d("updateSP", String.valueOf(aux.size()));

        (new Thread(){
            @Override
            public void run() {
                super.run();
                sharedPref.edit().putStringSet("UserVars", aux).commit();
            }
        }).start();

        /*
        apply() changes the in-memory SharedPreferences object immediately but writes the updates to disk asynchronously.
        Alternatively, you can use commit() to write the data to disk synchronously.
        But because commit() is synchronous, you should avoid calling it from your main thread because it could pause your UI rendering.
         */
    }

    private void loadSharedPreferences(){

        Set<String> aux = sharedPref.getStringSet("UserVars", new HashSet<String>());

        LinkedList<String> var = new LinkedList<String>(aux);

        Log.d("loadSP", String.valueOf(var.size()));

        tcpIp.getGlobalVariablesData().setVarNamesByUser(var);

    }

    // https://stackoverflow.com/questions/38239291/showing-a-toast-notification-from-a-service
    private void toastMessage(String message) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NetworkService.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTcpIpThread() {

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // EMERGENCY STOP NOTIFICATION
        NotificationCompat.Builder builderEmergStop = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Warning")
                .setContentText("Emergency Stop")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);

        // PROTECTIVE STOP NOTIFICATION
        NotificationCompat.Builder builderProtectStop = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Warning")
                .setContentText("Protective Stop")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true);

        // PROGRAM STATE NOTIFICATION
        /*NotificationCompat.Builder builderProgramState = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Program State")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(false);*/

        // POPUP NOTIFICATION
        NotificationCompat.Builder builderPopUp = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(false);

        tcpIpThread = new MyThread() {
            @Override
            public void run() {
                super.run();

                tcpIp.connect();
                triedToConnect = true; // now we can check in MainActivity if the socket connection was successful

                while(!isStopped() && tcpIp.isSocketConnected()){

                    // receive and decode info packages of robot state
                    // when necessary notifies user

                    // receive package
                    tcpIp.receivePackage();

                    // notifications
                    // it is possible to fire a notification on another thread than the UI

                    // https://stackoverflow.com/questions/31099984/android-service-thread-and-notification
                    // https://stackoverflow.com/questions/15530293/can-noticationmanager-notify-be-called-from-a-worker-thread

                    // EMERGENCY STOP
                    if (tcpIp.getRobotModeData().getIsEmergencyStopped()){
                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(2, builderEmergStop.build());
                    }

                    // PROTECTIVE STOP
                    if (tcpIp.getRobotModeData().getIsProtectiveStopped()){
                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(3, builderProtectStop.build());
                    }

                    // PROGRAM STATE
                    /*if (tcpIp.getRobotModeData().getIsProgramRunning() && last_program_state != 0){
                        builderProgramState.setContentText("Program: RUNNING");
                        notificationManager.notify(4, builderProgramState.build());
                        last_program_state = 0;
                    }
                    else if (tcpIp.getRobotModeData().getIsProgramPaused() && last_program_state != 1){
                        builderProgramState.setContentText("Program: PAUSED");
                        notificationManager.notify(4, builderProgramState.build());
                        last_program_state = 1;
                    }
                    else{ // stopped
                        if (last_program_state != 2) {
                            builderProgramState.setContentText("Program: STOPPED");
                            notificationManager.notify(4, builderProgramState.build());
                            last_program_state = 2;
                        }
                    }*/

                    // POPUP
                    if (tcpIp.getPopUpData().getPendingNotification()){

                        //Log.d("notifyPOPUP", "hola");

                        tcpIp.getPopUpData().setPendingNotification(false);

                        builderPopUp.setContentTitle(tcpIp.getPopUpData().getTitle());
                        builderPopUp.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(tcpIp.getPopUpData().getMessage()));

                        //builderPopUp.setContentText(tcpIp.getPopUpData().getMessage());

                        notificationManager.notify(5, builderPopUp.build());
                    }

                }
                if (!tcpIp.isSocketConnected()){
                    // notification instead of a toast message...
                    toastMessage("Not connected to robot");
                }
                notificationManager.cancel(2);
                notificationManager.cancel(3);
                notificationManager.cancel(4);
                notificationManager.cancel(5);
                tcpIp.close();
            }
        };

        tcpIpThread.start();
    }


    // public methods for clients

    public boolean isSocketConnected() {
        return tcpIp.isSocketConnected();
    }

    public boolean doWeTryToConnect(){
        return triedToConnect;
    }

    public String getIP(){
        return ip;
    }

    public RobotModeData getRobotModeData(){
        return tcpIp.getRobotModeData();
    }
    public JointData getJointData(){
        return tcpIp.getJointData();
    }
    public ToolData getToolData(){
        return tcpIp.getToolData();
    }
    public MasterBoardData getMasterBoardData(){
        return tcpIp.getMasterBoardData();
    }
    public GVarsData getGlobalVariablesData(){
        return tcpIp.getGlobalVariablesData();
    }

    // __________

    public void addVarName(String name){
        if (!tcpIp.getGlobalVariablesData().getVarNamesByUser().contains(name)) { // if it already contains name, do not add again...
            tcpIp.getGlobalVariablesData().getVarNamesByUser().add(name);
        }
    }
    public void delVarName(String name){
        tcpIp.getGlobalVariablesData().getVarNamesByUser().remove(name);
    }

    public int getVarNamesSize(){
        return tcpIp.getGlobalVariablesData().getVarNamesByUser().size();
    }

    public String[] getVarNamesByUser(){
        return tcpIp.getGlobalVariablesData().getVarNamesByUser().toArray(new String[tcpIp.getGlobalVariablesData().getVarNamesByUser().size()]);
    }

    // __________

    public void setShowAllVars(boolean showAllVars){
        tcpIp.getGlobalVariablesData().setShowAllVars(showAllVars);
    }

    public boolean getShowAllVars(){
        return tcpIp.getGlobalVariablesData().getShowAllVars();
    }
}