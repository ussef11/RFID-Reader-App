package com.idata.serialport;

import java.io.FileDescriptor;

public class SerialPortControl {

    static {
        try {
            System.loadLibrary("iDataSerialPortJni");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SerialPortControl getInstance() {
        return MySingleton.singleton;
    }

    private static class MySingleton {
        final static SerialPortControl singleton = new SerialPortControl();
    }

    private SerialPortControl() {
    }

    //指令接口，用于控制模块上下单
    public static native boolean ioControl(String powerName, int io);

    public native FileDescriptor openUart(String uartName, int nSpeed, int nBits, char nEvent, int nStop);

    //关闭串口
    public native boolean closeUart();

    public FileDescriptor mFd;


    /**
     * 打开串口，获取FileDescriptor用于读写流操作
     *
     * @param uartName 串口节点名
     * @param nSpeed   波特率（nSpeed）支持  1200，2400，9600，19200，115200
     * @param nBits    数据位固定为8
     * @param nEvent   校验位固定为字符n
     * @param nStop    停止位固定为1
     * @return 返回用于IO操作的文件描述符
     */
    public FileDescriptor getMFD(String uartName, int nSpeed, int nBits, char nEvent, int nStop) {
        return mFd = openUart(uartName, nSpeed, nBits, nEvent, nStop);
    }
}
