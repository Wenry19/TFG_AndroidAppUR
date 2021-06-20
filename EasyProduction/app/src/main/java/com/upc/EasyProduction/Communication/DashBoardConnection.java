package com.upc.EasyProduction.Communication;

import java.io.PrintWriter;
import java.net.Socket;

public class DashBoardConnection {

    private final String hostname; // robot IP

    private final int portNumber = 29999; // DashBoard Connection

    // https://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoClient.java
    private Socket socket;
    private PrintWriter out; // to be able to sent encoded strings

    private boolean socket_connected = false;

    DashBoardConnection(String robotIP){
        hostname = robotIP;
    }

    public void connect(){
        try {
            socket = new Socket(hostname, portNumber);

            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

            socket_connected = true;
        }
        catch (Exception e){
            e.printStackTrace();
            socket_connected = false;
        }
    }

    public void close(){
        try {
            if (socket != null) socket.close();
            if(out != null) out.close();
        }
        catch (Exception e){
            e.printStackTrace();
            socket_connected = false;
        }
    }

    public boolean isSocketConnected(){
        return socket_connected;
    }

    // in theory println adds \n in the end of the string

    public void play(){
        out.println("play"); // does not rise an exception
    }

    public void pause(){
        out.println("pause");
    }

    public void stop(){
        out.println("stop");
    }

    public void popup(String text){
        out.println("popup " + text);
    }
}
