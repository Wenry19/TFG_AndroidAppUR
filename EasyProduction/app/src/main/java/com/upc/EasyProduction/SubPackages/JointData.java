package com.upc.EasyProduction.SubPackages;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class JointData extends SubPackage {

    // base

    private double base_q_actual = -1; // 64 bits -> 8 bytes
    private float base_I_actual = -1; // 4 bytes
    private float base_V_actual = -1; // 4 bytes
    private float base_T_motor = -1; // 4 bytes
    private int base_jointMode = -1; // 1 byte

    // shoulder

    private double shoulder_q_actual = -1; // 64 bits -> 8 bytes
    private float shoulder_I_actual = -1; // 4 bytes
    private float shoulder_V_actual = -1; // 4 bytes
    private float shoulder_T_motor = -1; // 4 bytes
    private int shoulder_jointMode = -1; // 1 byte

    // elbow

    private double elbow_q_actual = -1; // 64 bits -> 8 bytes
    private float elbow_I_actual = -1; // 4 bytes
    private float elbow_V_actual = -1; // 4 bytes
    private float elbow_T_motor = -1; // 4 bytes
    private int elbow_jointMode = -1; // 1 byte

    // wirst1

    private double wirst1_q_actual = -1; // 64 bits -> 8 bytes
    private float wirst1_I_actual = -1; // 4 bytes
    private float wirst1_V_actual = -1; // 4 bytes
    private float wirst1_T_motor = -1; // 4 bytes
    private int wirst1_jointMode = -1; // 1 byte

    // wirst2

    private double wirst2_q_actual = -1; // 64 bits -> 8 bytes
    private float wirst2_I_actual = -1; // 4 bytes
    private float wirst2_V_actual = -1; // 4 bytes
    private float wirst2_T_motor = -1; // 4 bytes
    private int wirst2_jointMode = -1; // 1 byte

    // wirst3

    private double wirst3_q_actual = -1; // 64 bits -> 8 bytes
    private float wirst3_I_actual = -1; // 4 bytes
    private float wirst3_V_actual = -1; // 4 bytes
    private float wirst3_T_motor = -1; // 4 bytes
    private int wirst3_jointMode = -1; // 1 byte

    public JointData(){
        this.type = 1;
    }


    @Override
    public void updateData(byte[] body) {
        super.updateData(body);

        for (int i = 0; i < 6; i++){
            // for each joint
            // each joint has 8+8+8+4+4+4+4+1 = 41 bytes

            Double aux_q_actual = (ByteBuffer.wrap(Arrays.copyOfRange(body, (i * 41), (i * 41) + 8)).getDouble()) * 360/(2*Math.PI); // rad to º
            Float aux_I_actual = ByteBuffer.wrap(Arrays.copyOfRange(body, (i * 41) + 24, (i * 41) + 24 + 4)).getFloat();
            Float aux_V_actual = ByteBuffer.wrap(Arrays.copyOfRange(body, (i * 41) + 28, (i * 41) + 28 + 4)).getFloat();
            Float aux_T_motor = ByteBuffer.wrap(Arrays.copyOfRange(body, (i * 41) + 32, (i * 41) + 32 + 4)).getFloat();
            int aux_jointMode = body[i*41 + 40] & 0xff;

            switch (i) {
                case 0:
                    base_q_actual = aux_q_actual;
                    base_I_actual = aux_I_actual;
                    base_V_actual = aux_V_actual;
                    base_T_motor = aux_T_motor;
                    base_jointMode = aux_jointMode;
                    break;
                case 1:
                    shoulder_q_actual = aux_q_actual;
                    shoulder_I_actual = aux_I_actual;
                    shoulder_V_actual = aux_V_actual;
                    shoulder_T_motor = aux_T_motor;
                    shoulder_jointMode = aux_jointMode;
                    break;
                case 2:
                    elbow_q_actual = aux_q_actual;
                    elbow_I_actual = aux_I_actual;
                    elbow_V_actual = aux_V_actual;
                    elbow_T_motor = aux_T_motor;
                    elbow_jointMode = aux_jointMode;
                    break;
                case 3:
                    wirst1_q_actual = aux_q_actual;
                    wirst1_I_actual = aux_I_actual;
                    wirst1_V_actual = aux_V_actual;
                    wirst1_T_motor = aux_T_motor;
                    wirst1_jointMode = aux_jointMode;
                    break;
                case 4:
                    wirst2_q_actual = aux_q_actual;
                    wirst2_I_actual = aux_I_actual;
                    wirst2_V_actual = aux_V_actual;
                    wirst2_T_motor = aux_T_motor;
                    wirst2_jointMode = aux_jointMode;
                    break;
                case 5:
                    wirst3_q_actual = aux_q_actual;
                    wirst3_I_actual = aux_I_actual;
                    wirst3_V_actual = aux_V_actual;
                    wirst3_T_motor = aux_T_motor;
                    wirst3_jointMode = aux_jointMode;
            }
        }
    }

    // getters

    // BASE
    public String getBaseQactualStr(){
        return String.format("%.2f", base_q_actual);
    }
    public String getBaseIactualStr(){
        return String.format("%.2f", base_I_actual);
    }
    public String getBaseVactualStr(){
        return String.format("%.2f", base_V_actual);
    }
    public String getBaseTmotorStr(){
        return String.format("%.2f", base_T_motor);
    }
    public String getBaseJointModeStr(){
        return String.valueOf(base_jointMode);
    }
    // SHOULDER
    public String getShoulderQactualStr(){
        return String.format("%.2f", shoulder_q_actual);
    }
    public String getShoulderIactualStr(){
        return String.format("%.2f", shoulder_I_actual);
    }
    public String getShoulderVactualStr(){
        return String.format("%.2f", shoulder_V_actual);
    }
    public String getShoulderTmotorStr(){
        return String.format("%.2f", shoulder_T_motor);
    }
    public String getShoulderJointModeStr(){
        return String.valueOf(shoulder_jointMode);
    }
    // ELBOW
    public String getElbowQactualStr(){
        return String.format("%.2f", elbow_q_actual);
    }
    public String getElbowIactualStr(){
        return String.format("%.2f", elbow_I_actual);
    }
    public String getElbowVactualStr(){
        return String.format("%.2f", elbow_V_actual);
    }
    public String getElbowTmotorStr(){
        return String.format("%.2f", elbow_T_motor);
    }
    public String getElbowJointModeStr(){
        return String.valueOf(elbow_jointMode);
    }
    // WIRST1
    public String getWirst1QactualStr(){
        return String.format("%.2f", wirst1_q_actual);
    }
    public String getWirst1IactualStr(){
        return String.format("%.2f", wirst1_I_actual);
    }
    public String getWirst1VactualStr(){
        return String.format("%.2f", wirst1_V_actual);
    }
    public String getWirst1TmotorStr(){
        return String.format("%.2f", wirst1_T_motor);
    }
    public String getWirst1JointModeStr(){
        return String.valueOf(wirst1_jointMode);
    }
    // WIRST2
    public String getWirst2QactualStr(){
        return String.format("%.2f", wirst2_q_actual);
    }
    public String getWirst2IactualStr(){
        return String.format("%.2f", wirst2_I_actual);
    }
    public String getWirst2VactualStr(){
        return String.format("%.2f", wirst2_V_actual);
    }
    public String getWirst2TmotorStr(){
        return String.format("%.2f", wirst2_T_motor);
    }
    public String getWirst2JointModeStr(){
        return String.valueOf(wirst2_jointMode);
    }
    // WIRST3
    public String getWirst3QactualStr(){
        return String.format("%.2f", wirst3_q_actual);
    }
    public String getWirst3IactualStr(){
        return String.format("%.2f", wirst3_I_actual);
    }
    public String getWirst3VactualStr(){
        return String.format("%.2f", wirst3_V_actual);
    }
    public String getWirst3TmotorStr(){
        return String.format("%.2f", wirst3_T_motor);
    }
    public String getWirst3JointModeStr(){
        return String.valueOf(wirst3_jointMode);
    }

}
