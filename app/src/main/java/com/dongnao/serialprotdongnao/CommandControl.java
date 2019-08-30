package com.dongnao.serialprotdongnao;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by 林飞堞 on 2019/8/28
 * <p>
 * 版本号：SerialProtDongnao
 */
public class CommandControl {
    //启动
    public static byte[] start(){
        byte[] buff = new byte[8];
        buff[0] =  (byte)0X55;
        buff[1] = (byte)0X08;
        buff[2] = (byte)0X01;
        buff[3] = (byte)0X01;
        buff[4] = (byte)0X01;
        buff[5] = (byte)0X00;
        buff[6] = CRC8Util.calcCrc8(buff,0,6);
        buff[7] = (byte)0XAA;

        return buff;

    }


    //停止
    public static byte[] stop(){
        byte[] buff = new byte[8];
        buff[0] =  (byte)0X55;
        buff[1] = (byte)0X08;
        buff[2] = (byte)0X01;
        buff[3] = (byte)0X01;
        buff[4] = (byte)0X00;
        buff[5] = (byte)0X00;
        buff[6] = CRC8Util.calcCrc8(buff,0,6);
        buff[7] = (byte)0XAA;

        return buff;

    }

    //移动一个线路(按钮例如:加工区3)
    public static byte[] moveLine(byte[] bytes){
        int size = bytes.length + 7 ;
        byte[] buff = new byte[size];
        buff[0] =  (byte)0X55;
        buff[1] = (byte)(size & 0xff);
        buff[2] = (byte)0X01;
        buff[3] = (byte)0X02;
        buff[4] = (byte)0X01;
        for (int i = 0; i < bytes.length; i++) {
            buff[5+i] = (byte) bytes[i];//左
        }
        buff[size-2] = CRC8Util.calcCrc8(buff,0,size-2);
        buff[size-1] = (byte)0XAA;

        return buff;
    }

    //回程
    public static byte[] goback(){
        byte[] buff = new byte[8];
        buff[0] =  (byte)0X55;
        buff[1] = (byte)0X08;
        buff[2] = (byte)0X01;
        buff[3] = (byte)0X02;
        buff[4] = (byte)0X02;
        buff[5] = (byte)0X00;

        buff[6] = CRC8Util.calcCrc8(buff,0,5);
        buff[7] = (byte)0XAA;

        return buff;
    }

}
