package com.alkaid.winerapp;


public class Utils {


	/**
	 * 
	 * @param a
	 * @return
	 */
	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}
	
	public static String byte2HexStr(byte num){
		return String.format("%02X", num);
	}

	public static byte encode(byte random) {
		// 高低位取反
		byte temp = (byte) ((random << 4) & 0xf0);
		random &= 0xf0;
		random = (byte) ((random >> 4) & 0x0f);
		random |= temp;
		// 和0x5a Xor
		random ^= 0x5a;
		return random;
	}

	public static byte decode(byte num) {
		// 和0x5a Xor
		num ^= 0x5a;
		// 高低位取反
		byte temp = (byte) ((num << 4) & 0xf0);
		num &= 0xf0;
		num = (byte) ((num >> 4) & 0x0f);
		num |= temp;
		return num;
	}
	
    public static byte[] toByteArray(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }
	
	 // 将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
    public static int byteArrayToInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;

        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }
}
