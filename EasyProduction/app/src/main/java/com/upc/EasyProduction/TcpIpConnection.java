package com.upc.EasyProduction;

import android.util.Log;

import com.upc.EasyProduction.SubPackages.JointData;
import com.upc.EasyProduction.SubPackages.MasterBoardData;
import com.upc.EasyProduction.SubPackages.RobotModeData;
import com.upc.EasyProduction.SubPackages.ToolData;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
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
            socket.close();
            in.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isSocketConnected(){
        return socket_connected;
    }

    public void receivePackage(){
        try {

            byte package_size[] = new byte[4]; // int

            if (socket_connected && in.available() >= 4) {
                int num_read_bytes = in.read(package_size, 0, 4);

                if (num_read_bytes == 4) {

                    int len = ByteBuffer.wrap(package_size).getInt();

                    if (in.available() >= len-4) { // make sure that all package is sent

                        int type = in.read();

                        int current_len = len - 4 - 1;

                        byte body[] = new byte[current_len]; // body

                        int num_bytes_read = in.read(body, 0, current_len);

                        if (num_bytes_read == current_len) {
                            decodeSubpackages(type, body);
                        } else {
                            Log.d("ups", "num_bytes_read != current_len");
                            current_len -= num_bytes_read;
                            while (current_len != 0) {
                                current_len -= in.read(body, 0, current_len);
                            }
                        }
                    }

                }
            }
            /*else {
                Log.d("info_package", "not available packages");
            }*/
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

    private void decodeSubpackages(int type, byte[] body){
        int index = 0;

        if (type == 16){

            while (index < body.length){

                // len of subpackage
                int subp_size = ByteBuffer.wrap(Arrays.copyOfRange(body, index, index + 4)).getInt();
                index += 4;

                // type of subpackage
                int subp_type = body[index];
                index += 1;

                if (subp_size == 0) Log.d("ups", "subp_size = 0");

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
}
