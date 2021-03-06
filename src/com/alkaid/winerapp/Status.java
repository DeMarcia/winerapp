package com.alkaid.winerapp;

/**
 * Created by df on 2015/4/7.
 */
public class Status {
    public static final int CMD_LIGHT_ON = 0x10;
    public static final int CMD_LIGHT_OFF = 0x11;
    public static final int CMD_SWITCH_ON = 0x20;
    public static final int CMD_SWITCH_OFF = 0x21;
//    public static final int CMD_MOTO= 0x30;	//TODO 指令方式改变 看下面的getCurMotoCmd()  
    public static final int CMD_MOTO_ZERO=0x7f;	//初始化特殊马达
    public static final int CMD_MOTO_BEGIN=0x80;
    public static final int CMD_MOTO_END=0x97;
    public static final int CMD_TURN_FOWARD = 0x40;
    public static final int CMD_TURN_BACK = 0x41;
    public static final int CMD_TURN_ALL = 0x42;
    public static final int CMD_INIT_MOTO=0xbb;	//初始化MOTO的指令
//    public static final int CMD_TPD = 0x50;	//TODO指令方式改变 看下面的数组
    
    //向下位机发送验证指令 TODO 这里只是为了标识状态来判断什么时候接受验证数据，并不是真的指令
    public static final int CMD_AUTH_FLAG=1000;	
    //向下位机发送变更moto的指令 TODO 这里只是为了标识状态来判断当前的指令是用来改变MOTO的是为了方便后面的状态改变而用的标识，并非真的指令，真的智力见getCurMotoCmd()
    public static final int CMD_MOTO_FLAG = 1001;

    public static final int TURN_STATUS_FORWARD = 0;
    public static final int TURN_STATUS_BACK = 1;
    public static final int TURN_STATUS_ALL = 2;

    private int motoNums = 0;
    private int curMoto = 0;
    //MOTO类型分为两种 两种TPD不同
    public static final int MOTO_TYPE_ZERO=0;
    public static final int MOTO_TYPE_NORMAL=1;
    /**当前MOTO类型 有两种: {@link #MOTO_TYPE_ZERO},{@link #MOTO_TYPE_NORMAL}*/
    private int motoType=-1;
    /** TPD数组 对应MOTO类型 {@link #MOTO_TYPE_NORMAL}*/
    public static final int[] TPDS_NORMAL = {650, 750 , 850 , 1000 , 1950};
    public static final int[] CMD_TPD_NORMAL = {0x50, 0x51 , 0x52 , 0x53 , 0x54};
    /** TPD数组 对应MOTO类型 {@link #MOTO_TYPE_ZERO}*/
    public static final int[] TPDS_ZERO = {650 , 785 , 950 , 1150 , 1440 , 1570 , 1728 , 1838 , 1920 , 2107 , 2335 , 2618 , 2787 , 2880 , 3600};
    public static final int[] CMD_TPD_ZERO = {0x50, 0x51 , 0x52 , 0x53 , 0x54, 0x55 , 0x56 , 0x57 , 0x58, 0x59 , 0x5a , 0x5b , 0x5c, 0x5d , 0x5e };
    private int curTpdIndex=0;

    private byte authCode = 0x10;
    private boolean isLightOn = false;
    private boolean isSwitchOn = false;
    private int turnStatus = TURN_STATUS_FORWARD;
    private int curCmd=-1;
    
    //是否登录成功  验证成功+初始化(马达)成功
    private boolean logined=false;
    //验证头是否正确 即验证头是否是0xcc
    @Deprecated
    private boolean authHeadRight=false;
    //是否验证成功
    private boolean authed=false;

    public void changeMoto(){
        curMoto++;
        if(curMoto>=motoNums){
            curMoto=0;
        }
        curTpdIndex=0;
    }
    public int getCurMotoCmd(){
    	int index=curMoto+1;
    	int cmd=-1;
    	if(index==motoNums)
    		index=0;
    	cmd=index+CMD_MOTO_BEGIN;
    	return cmd;
    }

    public void changeTpd() {
        curTpdIndex++;
        if (motoType == MOTO_TYPE_ZERO && curTpdIndex >= TPDS_ZERO.length) {
            curTpdIndex = 0;
        } else if (motoType == MOTO_TYPE_NORMAL && curTpdIndex >= TPDS_NORMAL.length) {
            curTpdIndex = 0;
        }
    }
    /**
     * 获得当前的tpd指令
     * @return
     */
    public int getCurTpdCmd() {
        int index=curTpdIndex+1;
        int cmd=-1;
        switch (motoType) {
		case MOTO_TYPE_ZERO:
			if(index==CMD_TPD_ZERO.length)
				index=0;
			cmd=CMD_TPD_ZERO[index];
			break;
		case MOTO_TYPE_NORMAL:
			if(index==CMD_TPD_NORMAL.length)
				index=0;
			cmd=CMD_TPD_NORMAL[index];
			break;
		default:
			break;
		}
        
        return cmd;
    }
    /**
     * 判断指令是否是改变转速的指令
     * @return
     */
    public boolean isTpdCmd(){
    	switch (motoType) {
		case MOTO_TYPE_NORMAL:
			for (int c : CMD_TPD_NORMAL) {
				if(c==curCmd){
					return true;
				}
			}
			break;
		case MOTO_TYPE_ZERO:
			for (int c : CMD_TPD_ZERO) {
				if(c==curCmd){
					return true;
				}
			}
			break;
		default:
			break;
		}
    	return false;
    }

    public byte getAuthCode() {
        return authCode;
    }

    public void setAuthCode(byte authCode) {
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
        if(motoType==MOTO_TYPE_ZERO){
            return TPDS_ZERO[curTpdIndex];
        }
        return TPDS_NORMAL[curTpdIndex];
    }

	public boolean isLogined() {
		return logined;
//		return true;
	}

	public void setLogined(boolean logined) {
		this.logined = logined;
	}

	public int getMotoType() {
		return motoType;
	}

	public void setMotoType(int motoType) {
		this.motoType = motoType;
	}

	public boolean isAuthed() {
		if(!Constants.isNeedAuth){
			return true;
		}
		return authed;
//		return true;
	}

	public void setAuthed(boolean authed) {
		this.authed = authed;
	}

	@Deprecated
	public boolean isAuthHeadRight() {
		return authHeadRight;
//		return true;
	}

	@Deprecated
	public void setAuthHeadRight(boolean authHeadRight) {
		this.authHeadRight = authHeadRight;
	}
    
}
