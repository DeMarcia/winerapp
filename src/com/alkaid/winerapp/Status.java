package com.alkaid.winerapp;

/**
 * Created by df on 2015/4/7.
 */
public class Status {
    public static final int CMD_LIGHT_ON = 0x10;
    public static final int CMD_LIGHT_OFF = 0x11;
    public static final int CMD_SWITCH_ON = 0x20;
    public static final int CMD_SWITCH_OFF = 0x21;
    public static final int CMD_MOTO = 0x30;
    public static final int CMD_TURN_FOWARD = 0x40;
    public static final int CMD_TURN_BACK = 0x41;
    public static final int CMD_TURN_ALL = 0x42;
    public static final int CMD_TPD = 0x50;

    public static final int TURN_STATUS_FORWARD = 0;
    public static final int TURN_STATUS_BACK = 1;
    public static final int TURN_STATUS_ALL = 2;

    public static final int[] TPDS_NORMAL = {650, 750 , 850 , 1000 , 1950};
    public static final int[] TPDS_ZERO = {650 , 785 , 950 , 1150 , 1440 , 1570 , 1728 , 1838 , 1920 , 2107 , 2335 , 2618 , 2787 , 2880 , 3600};

    private int authCode = 100;
    private int motoNums = 0;
    private int curMoto = 0;
    private boolean isLightOn = false;
    private boolean isSwitchOn = false;
    private int turnStatus = TURN_STATUS_FORWARD;
    private int curTpdIndex=0;
    private int curCmd=-1;

    public void changeMoto(){
        curMoto++;
        //TODO motoNums=4时是否包括ID为00的Moto
        if(curMoto>motoNums){
            curMoto=0;
        }
        curTpdIndex=0;
    }

    public void changeTpd() {
        curTpdIndex++;
        if (curMoto == 0 && curTpdIndex >= TPDS_ZERO.length) {
            curTpdIndex = 0;
        } else if (curMoto != 0 && curTpdIndex >= TPDS_NORMAL.length) {
            curTpdIndex = 0;
        }
    }

    public int getAuthCode() {
        return authCode;
    }

    public void setAuthCode(int authCode) {
        this.authCode = authCode;
    }

    public int getMotoNums() {
        return motoNums;
    }

    public void setMotoNums(int motoNums) {
        this.motoNums = motoNums;
    }

    public int getCurMoto() {
        return curMoto;
    }

    public void setCurMoto(int curMoto) {
        this.curMoto = curMoto;
    }

    public boolean isLightOn() {
        return isLightOn;
    }

    public void setLightOn(boolean isLightOn) {
        this.isLightOn = isLightOn;
    }

    public boolean isSwitchOn() {
        return isSwitchOn;
    }

    public void setSwitchOn(boolean isSwitchOn) {
        this.isSwitchOn = isSwitchOn;
    }

    public int getTurnStatus() {
        return turnStatus;
    }

    public void setTurnStatus(int turnStatus) {
        this.turnStatus = turnStatus;
    }

    public int getCurTpdIndex() {
        return curTpdIndex;
    }

    public void setCurTpdIndex(int curTpdIndex) {
        this.curTpdIndex = curTpdIndex;
    }

    public int getCurCmd() {
        return curCmd;
    }

    public void setCurCmd(int curCmd) {
        this.curCmd = curCmd;
    }

    public int getTpd(){
        if(curMoto==0){
            return TPDS_ZERO[curTpdIndex];
        }
        return TPDS_NORMAL[curTpdIndex];
    }
}
