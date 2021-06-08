package com.upc.EasyProduction;


import android.util.Log;

import com.upc.EasyProduction.DataPackages.GVarsData;
import com.upc.EasyProduction.DataPackages.JointData;
import com.upc.EasyProduction.DataPackages.MasterBoardData;
import com.upc.EasyProduction.DataPackages.PopUpData;
import com.upc.EasyProduction.DataPackages.RobotModeData;
import com.upc.EasyProduction.DataPackages.ToolData;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TcpIpConnection {

    private final String hostname; // robot IP

    private final int portNumber = 30001; // primary client connection via TCP/IP

    private Socket socket;
    private InputStream in;

    private boolean socket_connected = false;

    // subpackages

    private RobotModeData rmData = new RobotModeData();
    private JointData jData = new JointData();
    private ToolData tData = new ToolData();
    private MasterBoardData mbData = new MasterBoardData();
    private GVarsData gvData = new GVarsData();
    private PopUpData puData = new PopUpData();


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

    public void close(){
        try{
            if (socket != null) socket.close();
            if (in != null) in.close();
            socket_connected = false;
        }
        catch (Exception e){
            e.printStackTrace();
            socket_connected = false;
        }
    }

    public boolean isSocketConnected(){
        return socket_connected;
    }

    public void receivePackage(){
        try {

            if (socket_connected && in.available() >= 4096) {
                // all packages are < 4096 bytes, so we are sure that there are at least 1 package available

                byte package_size[] = new byte[4]; // int

                in.read(package_size, 0, 4);

                int len = ByteBuffer.wrap(package_size).getInt();

                int type = in.read() & 0xff;

                int current_len = len - 4 - 1;

                byte body[] = new byte[current_len]; // body

                in.read(body, 0, current_len);

                if (type == 16) {
                    decodeSubpackages(body);
                } else if (type == 25) {
                    decodeVarsPackages(body);
                }
                else if (type == 20){
                    decodePopUpPackage(body);
                }
            }
            else{
                // check if socket is alive!!
                if(socket.getInetAddress() == null || !socket.getInetAddress().isReachable(2000)){
                    socket_connected = false;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            socket_connected = false;
        }
    }

    private void decodeSubpackages(byte[] body){
        int index = 0;

        while (index < body.length){

            // len of subpackage
            int subp_size = ByteBuffer.wrap(Arrays.copyOfRange(body, index, index + 4)).getInt();
            index += 4;

            // type of subpackage
            int subp_type = body[index] & 0xff;
            index += 1;

            if (subp_type == 0){ // Robot Mode Data
                rmData.updateData(Arrays.copyOfRange(body, index, index + subp_size - 5));
            }
            else if (subp_type == 1){ // Joint Data
                jData.updateData(Arrays.copyOfRange(body, index, index + subp_size - 5));
            }
            else if (subp_type == 2){ // Tool Data
                tData.updateData(Arrays.copyOfRange(body, index, index + subp_size - 5));
            }
            else if (subp_type == 3){ // Master Board Data
                mbData.updateData(Arrays.copyOfRange(body, index, index + subp_size - 5));
            }

            index += subp_size - 5;

        }
    }

    private void decodeVarsPackages(byte[] body){
        // jump 8 bytes of timestamp
        // read byte of type var package
        int type = body[8] & 0xff;
        int startIndex = ((body[9] << 8) & 0x0000ff00) | (body[10] & 0x000000ff);
        // The startIndex value is usually 0 and I am fairly certain this will only be used when you have a very large number of variables in your program
        // and so they can’t all be sent in one message, so allows you to again match them with the correct names.
        // https://forum.universal-robots.com/t/primary-interface-messages/1850/4
        if (type == 0){
            gvData.updateDataNames(Arrays.copyOfRange(body, 11, body.length), startIndex);
        }
        else if (type == 1){
            gvData.updateDataValues(Arrays.copyOfRange(body, 11, body.length), startIndex);
        }

    }

    private void decodePopUpPackage(byte[] body){
        // jump 8 bytes of timestamp
        // jump 1 byte of source
        int type = body[9] & 0xff;

        //Log.d("POPUP", "POPUP MESSAGE RECEIVED " + String.valueOf(type));

        if (type == 9){ // pop up message
            puData.updateData(Arrays.copyOfRange(body, 10, body.length));
        }
    }

    // getters

    public RobotModeData getRobotModeData(){
        return rmData;
    }
    public JointData getJointData(){
        return jData;
    }
    public ToolData getToolData(){
        return tData;
    }
    public MasterBoardData getMasterBoardData(){
        return mbData;
    }
    public GVarsData getGlobalVariablesData(){
        return gvData;
    }
    public PopUpData getPopUpData(){
        return puData;
    }
}
