package com.upc.EasyProduction.SubPackages;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ToolData extends SubPackage{

    private float tool_voltage_48V = -1;
    private float tool_current = -1;
    private float tool_temperature = -1;
    private int tool_mode = -1;

    public ToolData(){
        this.type = 2;
    }

    @Override
    public void updateData(byte[] body) {
        super.updateData(body);

        // 1 + 1 + 8 + 8

        tool_voltage_48V = ByteBuffer.wrap(Arrays.copyOfRange(body, 18, 18 + 4)).getFloat();
        tool_current = ByteBuffer.wrap(Arrays.copyOfRange(body, 23, 23 + 4)).getFloat();
        tool_temperature = ByteBuffer.wrap(Arrays.copyOfRange(body, 27, 27 + 4)).getFloat();
        tool_mode = 0xff & body[31];

    }

    public String getToolVoltageStr(){
        return String.format("%.2f", tool_voltage_48V);
    }

    public String getToolCurrentStr(){
        return String.format("%.2f", tool_current);
    }

    public String getToolTemperatureStr(){
        return String.format("%.2f", tool_temperature);
    }

    public String getToolModeStr(){
        return String.valueOf(tool_mode);
    }
}
