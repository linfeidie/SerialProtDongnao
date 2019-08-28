package com.dongnao.serialprotdongnao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements ProtDataInterface {

    public static final String TAG = MainActivity.class.getSimpleName();

    EditText edit_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_content = findViewById(R.id.edit_content);

    }
//dev/ttsy
    public void open(View view) {
        //SerialPortManager.getInstance().openSerialPort("/dev/ttySAC2", 115200);
        SerialPortManager.getInstance().openSerialPort("/dev/ttySAC3", 115200);
        SerialPortManager.getInstance().regist(this);

    }

    public void send(View view) {

        String command = edit_content.getText().toString().trim();

//        if (TextUtils.isEmpty(command)) {
//            return;
//        }
       // byte[] sendContentBytes = command.getBytes();
        //byte[] sendContentBytes = {(byte)0xAA};


        byte[] sendContentBytes = new byte[8];
        sendContentBytes[0] =  (byte)0X55;
        sendContentBytes[1] = (byte)0X08;
        sendContentBytes[2] = (byte)0X01;
        sendContentBytes[3] = (byte)0X01;
        sendContentBytes[4] = (byte)0X01;
        sendContentBytes[5] = (byte)0X00;
        sendContentBytes[6] = CRC8Util.calcCrc8(sendContentBytes,0,6);
        sendContentBytes[7] = (byte)0XAA;

        byte crc8 = CRC8Util.calcCrc8(F.HexToByteArr("550801010100"));
//        String str = F.Byte2Hex(crc8);
//        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();

        SerialPortManager.getInstance().putCommand(CommandControl.start());
    }

    @Override
    public void onDataReceived(byte[] bytes) {
        Log.i("david", "   收到信息   " + new String(bytes));
    }

    //启动
    public void start(View view) {
        SerialPortManager.getInstance().putCommand(F.HexToByteArr("55080101010014aa"));//5c

    }

    //停止
    public void stop(View view) {
        byte data = CRCHelper.calCRC8(F.HexToByteArr("550801010100"));
        byte data2 = CRC8Util.calcCrc8(F.HexToByteArr("550801010100"));


         byte data3 = CRC8Util.calcCrc8(OtherUtil.hexStingTobytes("550801010100"));
        SerialPortManager.getInstance().putCommand(F.HexToByteArr("55080101000001aa"));//d9




    }
}
