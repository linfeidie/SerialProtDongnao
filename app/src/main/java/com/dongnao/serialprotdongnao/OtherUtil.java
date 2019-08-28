package com.dongnao.serialprotdongnao;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by 林飞堞 on 2019/8/27
 * <p>
 * 版本号：SerialProtDongnao
 */
public class OtherUtil {

    //16进制字符串转字节数组（亲测有效）
    public static byte [] hexStingTobytes(String hexstring){
        byte[] destByte = new byte[hexstring.length()/2];
        int j=0;
        for(int i=0;i<destByte.length;i++) {
            byte high = (byte) (Character.digit(hexstring.charAt(j), 16) & 0xff);
            byte low = (byte) (Character.digit(hexstring.charAt(j + 1), 16) & 0xff);
            destByte[i] = (byte) (high << 4 | low);
            j+=2;
        }
        return destByte;
    }
}
