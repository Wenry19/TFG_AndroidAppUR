package com.upc.EasyProduction.DataPackages;

import java.util.Arrays;

public class PopUpData {

    private boolean pendingNotification = false;

    private String title = "";
    private String message = "";

    private boolean warning = false;
    private boolean error = false;
    private boolean blocking = false;

    public void updateData(byte[] body){

        pendingNotification = true;

        // jump 4 bytes of requestId
        // jump 4 byte of requestedType

        warning = (body[8] != 0);
        error = (body[9] != 0);
        blocking = (body[10] != 0);

        int popupMessageTitleSize = body[11] & 0xff;

        title = new String(Arrays.copyOfRange(body, 12, 12 + popupMessageTitleSize));

        message = new String(Arrays.copyOfRange(body,12 + popupMessageTitleSize, body.length));

    }

    public String getTitle(){
        String aux = "";

        if (blocking){
            aux += "Blocking ";
        }

        if (error){
            aux += "Error: ";
        }
        else if (warning){
            aux+= "Warning: ";
        }
        else{
            aux += "Message: ";
        }

        aux += title;
        return aux;
    }

    public String getMessage(){
        return message;
    }
    public boolean getWarning(){
        return warning;
    }
    public boolean getError(){
        return error;
    }
    public boolean getBlocking(){
        return blocking;
    }
    public boolean getPendingNotification(){
        return pendingNotification;
    }

    public void setPendingNotification(boolean pendingNotification){
        this.pendingNotification = pendingNotification;
    }

}
