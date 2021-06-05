package com.upc.EasyProduction.DataPackages;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MasterBoardData extends SubPackage {

    private float master_board_temperature;
    private float robot_voltage_48V;
    private float robot_current;

    public MasterBoardData(){
        this.type = 3;
    }

    @Override
    public void updateData(byte[] body) {
        super.updateData(body);

        // 4 + 4 + 1 + 1 + 8 + 8 + 1 + 1 + 8 + 8

        master_board_temperature = ByteBuffer.wrap(Arrays.copyOfRange(body, 44, 44 + 4)).getFloat();
        robot_voltage_48V = ByteBuffer.wrap(Arrays.copyOfRange(body, 48, 48 + 4)).getFloat();
        robot_current = ByteBuffer.wrap(Arrays.copyOfRange(body, 52, 52 + 4)).getFloat();

    }

    public String getMasterBoardTemperatureStr(){
        return String.format("%.2f", master_board_temperature);
    }
    public String getRobotVoltageStr(){
        return String.format("%.2f", robot_voltage_48V);
    }
    public String getRobotCurrentStr(){
        return String.format("%.2f", robot_current);
    }
}
