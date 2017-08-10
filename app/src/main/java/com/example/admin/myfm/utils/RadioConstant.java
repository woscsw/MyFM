package com.example.admin.myfm.utils;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/7/27.
 */
public class RadioConstant {
    public static final String RADIO_SERVICE_BROADCAST="com.example.admin.myfm.service.ServiceBroadCast.RadioServiceBroadcastReceiver";
    public static final String MAIN_BROADCAST = "com.example.admin.myfm.MainActivity.MainBroadcastR";
    
    //当前播放状态
    public static boolean isPlaying = false;
    
    /**
     * 上个广播的id,不用上了
     */
    public static int preRadioId = -1;
    
    public static int radioId = -1;

    /**
     * 当前播放音乐的总长度
     */
    public static int duration = 0;
    /**
     * 当前播放音乐的进度值
     */
    public static int progress = 0;
    /**
     * 确定当前播放列表中的第几个
     */
    public static int PLAYING_POSITION = -1;

    /**
     * 继续播放
     */
    public static final int PLAY = 1;
    /**
     * 暂停播放
     */
    public static final int PAUSE = 2;
    /**
     * 没退出页面，继续播放
     */
    public static final int REPLAY = 10;
    /**
     * 下一曲
     */
    public static final int NEXT_RADIO = 3;
    /**
     * 上一曲
     */
    public static final int PRE_RADIO = 4;

    
    public static List<Integer> idList = new ArrayList<>();
    //播放列表
    public static List<String> playList = new ArrayList<>();
    //播放列表名字
    public static List<String> nameList = new ArrayList<>();
    //播放列表节目名字
    public static List<String> programNameList = new ArrayList<>();
    //播放列表图片
    public static List<String> imgList = new ArrayList<>();
//    public static boolean count;
//    public static int pre_albumID=0 ;
//    public static int pre_history_albumID=0 ;
//    public static int history_albumID=0;
//    public static int radio_id=0;
//    /**
//     * 上个广播的id
//     */
//    public static int getRadio_id_pre=0;
////    public static PlayData playData;
//    public static boolean IS_PLAYACRIVITY_RUNNING=false;
//   
//    /**
//     * 改变音乐播放的位置（快进或者快退）
//     */
//    public static final int SWITCH_PROGRESS = 5;
//
//    /**
//     * 音乐播放模式 1:循环
//     */
//    public static final int LOOPING = 0;
//    /**
//     * 音乐播放模式 2：随机
//     */
//    public static final int RANDOM = 1;
//    /**
//     * 音乐播放模式 3：单曲
//     */
//    public static final int SINGLE = 2;
//    /**
//     * 当前的播放模式
//     */
//    public static int CURRENT_MODE = LOOPING;
}
