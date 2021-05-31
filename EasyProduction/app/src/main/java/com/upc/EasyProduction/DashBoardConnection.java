package com.upc.EasyProduction;

import java.io.PrintWriter;
import java.net.Socket;

public class DashBoardConnection {

    private final String hostname; // robot IP

    private final int portNumber = 29999; // DashBoard Connection

    private Socket socket;
    private PrintWriter out; // to be able to sent encoded strings


    DashBoardConnection(String robotIP){
        hostname = robotIP;
    }

    public void connect(){
        try {
            socket = new Socket(hostname, portNumber);

            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // in theory println adds \n in the end of the string

    public void play(){
        out.println("play");
    }

    public void pause(){
        out.println("pause");
    }

    public void stop(){
        out.println("stop");
    }

    public void shutdown(){
        out.println("shutdown");
    }

    public void popup(String text){
        out.println("popup " + text);
    }

    public void closePopup(){
        out.println("close popup");
    }

    public void powerOn(){
        out.println("power on");
    }

    public void powerOff(){
        out.println("power off");
    }

}
