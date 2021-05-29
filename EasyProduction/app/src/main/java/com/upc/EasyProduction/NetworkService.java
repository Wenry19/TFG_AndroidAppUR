package com.upc.EasyProduction;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

public class NetworkService extends Service {

    private final IBinder binder = new MyBinder();

    private String ip;

    private TcpIpConnection c;

    //private MediaPlayer player;

    public class MyBinder extends Binder {
        NetworkService getService(){
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
    public int onStartCommand(Intent intent, int flags, int startId){

        ip = intent.getStringExtra("ip");

        /*player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(true);
        player.start();*/

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
    }

    // https://stackoverflow.com/questions/38239291/showing-a-toast-notification-from-a-service
    public void toastMessage(String message){

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable(){
            @Override
            public void run(){
                Toast.makeText(NetworkService.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    // public methods for clients

    public boolean isConnected(){
        return true;
    }
}