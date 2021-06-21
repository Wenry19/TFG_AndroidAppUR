package com.upc.EasyProduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.upc.EasyProduction.Communication.DashBoardConnection;

public class MessageActivity extends AppCompatActivity {

    private DashBoardConnection db;
    private String robotIP;

    private Button send;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent i = getIntent();
        robotIP = i.getStringExtra("ip");
        db = new DashBoardConnection(robotIP);
    }

    @Override
    protected void onStart(){
        super.onStart();
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
    }

    public void onClickSend(View v){

        (new Thread(){
            @Override
            public void run() {
                super.run();
                db.connect();
                if (db.isSocketConnected()) {
                    db.popup(message.getText().toString());
                }
                db.close();
            }
        }).start();

    }

    public void onClickClear(View v){
        message.setText("");
    }


}