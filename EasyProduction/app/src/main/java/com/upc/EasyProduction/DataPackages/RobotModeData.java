package com.upc.EasyProduction.DataPackages;

public class RobotModeData extends SubPackage {

    private boolean isEmergencyStopped = false;
    private boolean isProtectiveStopped = false;

    private boolean isProgramRunning = false;
    private boolean isProgramPaused = false;

    private int robotMode = 0;
    private int controlMode = 0;

    private String robotModeStr = "Unknown";
    private String controlModeStr = "Unknown";


    // redundant with robot Mode?!
    //private boolean isRealRobotConnected;
    //private boolean isRealRobotEnabled;
    //private boolean isRobotPowerOn;

    public RobotModeData(){
        this.type = 0;
    }

    @Override
    public void updateData(byte[] body) {
        super.updateData(body);
        // jump 8 bytes of timestamp
        isEmergencyStopped = body[11] != 0;
        isProtectiveStopped = body[12] != 0;

        isProgramRunning = body[13] != 0;
        isProgramPaused = body[14] != 0;

        robotMode = body[15] & 0xff;
        controlMode = body[16] & 0xff;

        updateStrings();
    }

    public void updateStrings(){

        if (robotMode == -1) robotModeStr = "NO_CONTROLLER";
        else if (robotMode == 0) robotModeStr = "DISCONNECTED";
        else if (robotMode == 1) robotModeStr = "CONFIRM_SAFETY";
        else if (robotMode == 2) robotModeStr = "BOOTING";
        else if (robotMode == 3) robotModeStr = "POWER_OFF";
        else if (robotMode == 4) robotModeStr = "POWER_ON";
        else if (robotMode == 5) robotModeStr = "IDLE";
        else if (robotMode == 6) robotModeStr = "BACKDRIVE";
        else if (robotMode == 7) robotModeStr = "RUNNING";
        else if (robotMode == 8) robotModeStr = "UPDATING_FIRMWARE";
        else robotModeStr = "Unknown";

        if (controlMode == 0) controlModeStr = "POSITION";
        else if (controlMode == 1) controlModeStr = "TEACH";
        else if (controlMode == 2) controlModeStr = "FORCE";
        else if (controlMode == 3) controlModeStr = "TORQUE";
        else controlModeStr = "Unknown";

    }

    public boolean getIsEmergencyStopped(){
        return isEmergencyStopped;
    }

    public boolean getIsProtectiveStopped(){
        return isProtectiveStopped;
    }

    public boolean getIsProgramRunning(){
        return isProgramRunning;
    }

    public boolean getIsProgramPaused(){
        return isProgramPaused;
    }

    public String getRobotModeStr(){
        return robotModeStr;
    }

    public String getControlModeStr(){
        return controlModeStr;
    }
}
