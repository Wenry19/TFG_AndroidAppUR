package com.upc.EasyProduction.SubPackages;

public class RobotModeData extends SubPackage{

    private boolean isEmergencyStopped = false;
    private boolean isProtectiveStopped = false;

    private boolean isProgramRunning = false;
    private boolean isProgramPaused = false;

    private int robotMode = 0;
    private int controlMode = 0;

    // redundant with robot Mode?!
    //private boolean isRealRobotConnected;
    //private boolean isRealRobotEnabled;
    //private boolean isRoboPowerOn;

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

        robotMode = body[15];
        controlMode = body[16];
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

    public int getRobotMode(){
        return robotMode;
    }

    public int getControlMode(){
        return controlMode;
    }
}
