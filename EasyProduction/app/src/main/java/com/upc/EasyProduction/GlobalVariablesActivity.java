package com.upc.EasyProduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.upc.EasyProduction.DataPackages.GVarsData;

public class GlobalVariablesActivity extends AppCompatActivity {

    private NetworkService networkService;
    private boolean bound = false;

    private Thread updatingValuesThread;

    private Button addButton;
    private Button delButton;
    private EditText varName;
    private TextView vars;
    private TextView varsByUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_variables);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // bind to NetworkService
        if (!bound) { // pre: service is started!! it is not possible to reach this activity without starting service
            doBindService(); // asynchronous!!
        }

        vars = findViewById(R.id.vars);

        addButton = findViewById(R.id.add);
        delButton = findViewById(R.id.delete);
        varName = findViewById(R.id.var_name);
        varsByUser = findViewById(R.id.vars_by_user);

        (new Thread(){
            @Override
            public void run() {
                while (networkService == null);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(networkService.getVarNamesSize() == 0){
                            delButton.setEnabled(false);
                        }
                        else{
                            delButton.setEnabled(true);
                        }

                        if(networkService.getVarNamesSize() >= 10){
                            // it can not be larger than 10 but to be sure >=
                            addButton.setEnabled(false);
                        }
                        else{
                            addButton.setEnabled(true);
                        }
                    }
                });
            }
        }).start();

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

    public void onClickAddButton(View v){
        // to be sure not blocking anything... new thread
        (new Thread(){
            @Override
            public void run() {
                while (networkService == null);

                networkService.addVarName(varName.getText().toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (networkService.getVarNamesSize() == 10){
                            addButton.setEnabled(false);
                        }
                    }
                });
            }
        }).start();

        varName.setText("");
        delButton.setEnabled(true);

    }
    public void onClickDelButton(View v){

        // to be sure not blocking anything... new thread
        (new Thread(){
            @Override
            public void run() {
                while (networkService == null);
                networkService.delVarName(varName.getText().toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (networkService.getVarNamesSize() == 0){
                            delButton.setEnabled(false);
                        }
                    }
                });

            }
        }).start();

        varName.setText("");
        addButton.setEnabled(true);

    }

    private void startUpdatingValues() {
        // make sure that we are bound

        updatingValuesThread = (new MyThread() {
            @Override
            public void run() {
                super.run();

                while (networkService == null) ;

                GVarsData gvData = networkService.getGlobalVariablesData();

                while (bound) { // if stop activity then we do unbind

                    String[] names = gvData.getVarsNames();
                    String[] names_by_user = networkService.getVarNamesByUser();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String auxNamesByUser = "";

                            for (int i = 0; i< names_by_user.length; i++){
                                auxNamesByUser += names_by_user[i];
                                if (i != names_by_user.length - 1){
                                    auxNamesByUser += ", ";
                                }
                            }

                            varsByUser.setText("Variables Entered by User: " + auxNamesByUser);


                            String aux = "";
                            for (int i = 0; i < names.length; i++){

                                aux += names[i] + "\n";

                            }
                            vars.setText(aux);
                        }
                    });

                    try {
                        this.sleep(500);
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
}