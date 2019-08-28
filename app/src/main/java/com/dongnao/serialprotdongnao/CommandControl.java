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
    public static byte[] moveLine(){
        byte[] buff = new byte[10];
        buff[0] =  (byte)0X55;
        buff[1] = (byte)0X0A;
        buff[2] = (byte)0X01;
        buff[3] = (byte)0X02;
        buff[4] = (byte)0X01;
        buff[5] = (byte)0X02;//左
        buff[6] = (byte)0X03;//右
        buff[7] = (byte)0X03;//右
        buff[8] = CRC8Util.calcCrc8(buff,0,8);
        buff[9] = (byte)0XAA;

        return buff;
    }


}
