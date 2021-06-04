package com.upc.EasyProduction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.upc.EasyProduction.SubPackages.JointData;
import com.upc.EasyProduction.SubPackages.MasterBoardData;
import com.upc.EasyProduction.SubPackages.RobotModeData;
import com.upc.EasyProduction.SubPackages.ToolData;

public class NetworkService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private final IBinder binder = new MyBinder();

    private String ip;

    private TcpIpConnection tcpIp;

    private MyThread tcpIpThread;

    private boolean triedToConnect = false;

    //private MediaPlayer player;

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

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ip = intent.getStringExtra("ip");

        // https://androidwave.com/foreground-service-android-example/
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Easy Production Foreground Service")
                .setContentText("Network Service Running with IP = " + ip)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        /*player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(true);
        player.start();*/
        
        startTcpIpThread();

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
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

        tcpIpThread = new MyThread() {
            @Override
            public void run() {
                super.run();

                tcpIp = new TcpIpConnection(ip);

                tcpIp.connect();
                triedToConnect = true; // now we can check in MainActivity if the socket connection was successful

                while(!isStopped() && tcpIp.isSocketConnected()){

                    // receive and decode info packages of robot state
                    // when necessary notifies user

                    tcpIp.receivePackage();
                }
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

}