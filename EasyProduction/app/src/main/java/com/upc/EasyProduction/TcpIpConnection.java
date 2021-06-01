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

    private boolean socket_connected = false;


    TcpIpConnection(String robotIP){
        hostname = robotIP;
    }

    public void connect(){
        try {
            socket = new Socket(hostname, portNumber);

            in = socket.getInputStream();

            socket_connected = true;
        }
        catch (Exception e) {
            e.printStackTrace();
            socket_connected = false;
        }
    }

    public boolean isSocketConnected(){
        return socket_connected;
    }

    private void receivePackage(){
        try {

            byte package_size[] = new byte[4]; // int

            if (socket_connected && in.available() > 0) {
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
            e.printStackTrace();
            socket_connected = false;
        }
        catch (IOException e){
            e.printStackTrace();
            socket_connected = false;
        }
        catch (Exception e){
            e.printStackTrace();
            socket_connected = false;
        }
    }
}
