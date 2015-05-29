package com.alkaid.winerapp;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte randNo = (byte) (Math.random()*0xff );
//		randNo=0x3a;
		p(randNo);
		byte encodeNo=encode(randNo);
		p(encodeNo);
		byte decodeNo=decode(encodeNo);
		p(decodeNo);
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
	
	private static void p(byte num){
		System.out.println(String.format("%02X", new Byte[]{Byte.valueOf(num)}));
	}

}
