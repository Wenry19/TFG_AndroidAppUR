package com.upc.EasyProduction;

// My Thread
// thread killed when run method has completed

public class MyThread extends Thread {
    private boolean stopped = false;

    public void myStop(){
        stopped = true;
    }

    public boolean isStopped(){
        return stopped;
    }
}
