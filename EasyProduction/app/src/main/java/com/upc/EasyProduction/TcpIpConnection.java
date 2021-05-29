package com.upc.EasyProduction;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class TcpIpConnection {

    private final String hostname; // robot IP

    private final int portNumber = 30001; // primary client connection via TCP/IP

    private Socket socket;
    private InputStream in;


    TcpIpConnection(String robotIP){
        hostname = robotIP;
    }

    public void connect(){
        try {
            socket = new Socket(hostname, portNumber);

            in = socket.getInputStream();
        }
        catch (Exception e) {
            Log.d("exceptEnric", e.toString());
        }
    }

    private void receivePackage(){
        try {

            byte package_size[] = new byte[4]; // int

            if (in.available() > 0) {
                int num_read_bytes = in.read(package_size, 0, 4);
                if (num_read_bytes == 4) {

                    int len = ByteBuffer.wrap(package_size).getInt();
                    //Log.d("info_package", "len: " + String.valueOf(len));

                    int type = in.read();
                    //Log.d("info_package", "type: " + String.valueOf(type));

                    int current_len = len - 4 - 1;

                    byte body[] = new byte[current_len]; // body

                    int num_bytes_read = in.read(body, 0, current_len);

                    //Log.d("info_package", "body read bytes: " + String.valueOf(aux));
                }
            } else {
                Log.d("info_package", "not available packages");
            }
        }
        catch (UnknownHostException e){
            System.out.println(e.toString());
            Log.d("exceptEnric", e.toString());
        }
        catch (IOException e){
            System.out.println(e.toString());
            Log.d("exceptEnric", e.toString());
        }
        catch (Exception e){
            System.out.println(e.toString());
            Log.d("exceptEnric", e.toString());
        }
    }


}
