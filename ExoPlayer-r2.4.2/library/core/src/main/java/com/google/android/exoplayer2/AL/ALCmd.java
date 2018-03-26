package com.google.android.exoplayer2.AL;

/**
 * Created by lihongsheng on 12/19/17.
 */

public class ALCmd {
    //为了做实验用的公共变量
    public static int CURRENT_CHUNK = 0;
    public static int CURRENT_MOVE_STATE = -1;//前进
    public static final int MOVE_STATE_STOP = 0;//前进
    public static final int MOVE_STATE_FORWARD = 1;//前进
    public static final int MOVE_STATE_BACK = 2;//后退
    public static final int MOVE_STATE_LOOK_UP = 6;//向上看
    public static final int MOVE_STATE_LOOK_DOWN = 7;//向下看
    public static long RTTtime = 0;
    public static int currentMs = 10000;
    public static long decodeTime = 0;
    public static int currentDecodeMs = 10000;

//    long nanoTime = System.nanoTime() - ALCmd.RTTtime;
//    //      long tempMs = 0.000001;
//    long MsTime = nanoTime/1000000;
//    //0.000 001
//    String RTTstr1 = String.valueOf(nanoTime);
//    String RTTstr2 = String.valueOf(MsTime);
//      Log.d("===== RTTstr nanoTime:",RTTstr1);
//      Log.d("===== RTTstr MsTime:",RTTstr2);
//      Log.d("ALCmd.RTTtime: ",String.valueOf(ALCmd.RTTtime));
////      Log.d("presentationTimeUs: ",String.valueOf(outputBufferInfo.presentationTimeUs));
    //testNumPre
}
