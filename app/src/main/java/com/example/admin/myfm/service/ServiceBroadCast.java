package com.example.admin.myfm.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.admin.myfm.utils.RadioConstant;
import com.example.admin.myfm.utils.SharedPreferencesUtil;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

import okhttp3.OkHttpClient;

import static com.google.android.exoplayer2.ExoPlayer.STATE_BUFFERING;
import static com.google.android.exoplayer2.ExoPlayer.STATE_ENDED;
import static com.google.android.exoplayer2.ExoPlayer.STATE_IDLE;
import static com.google.android.exoplayer2.ExoPlayer.STATE_READY;

/**
 * Created by user on 2016/7/27.
 */


public class ServiceBroadCast extends Service implements ExoPlayer.EventListener{
    private static final String TAG = ServiceBroadCast.class.getSimpleName();
    RadioServiceBroadcastReceiver receiver;
    MediaPlayer mediaPlayer;
    SimpleExoPlayer player;
    DataSource.Factory dataSourceFactory;
    MediaSource hmSource;
//    Handler mainHandler;
    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new RadioServiceBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RadioConstant.RADIO_SERVICE_BROADCAST);
        registerReceiver(receiver, filter);
        sharedPreferencesUtil = new SharedPreferencesUtil(this);
//        mainHandler = new Handler();
        //old
//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        TrackSelection.Factory videoTrackSelectionFactory =
//                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
//        TrackSelector trackSelector =
//                new DefaultTrackSelector(mainHandler, videoTrackSelectionFactory);

//        LoadControl loadControl = new DefaultLoadControl();
//        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
//        DefaultBandwidthMeter bandwidthMeter1 = new DefaultBandwidthMeter();
//        dataSourceFactory = new DefaultDataSourceFactory(this,
//                Util.getUserAgent(this, "yourApplicationName"), bandwidthMeter1);
//        player.addListener(this);

        //new
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

//        dataSourceFactory = new DefaultDataSourceFactory(this,
//                Util.getUserAgent(this, "yourApplicationName"), bandwidthMeter);

        Stetho.initializeWithDefaults(this);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
         dataSourceFactory = new OkHttpDataSourceFactory(okHttpClient, Util.getUserAgent(this, "yourApplicationName"), bandwidthMeter);
        player.addListener(this);
        //MediaPlayer-------------------------------------------------------------------
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                return false;
//            }
//        });
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//                Log.i(TAG, "MediaPlayer start");
//                RadioConstant.isPlaying = true;
//                Intent playIntent = new Intent();
//                playIntent.setAction(RadioConstant.MAIN_BROADCAST);
//                playIntent.putExtra("type", RadioConstant.PLAY);
//                sendBroadcast(playIntent);
//                //保存当前广播信息
//                sharedPreferencesUtil.edit().setId(RadioConstant.idList.get(RadioConstant.PLAYING_POSITION))
//                        .setPlayUrl(RadioConstant.playList.get(RadioConstant.PLAYING_POSITION))
//                        .setprogramName(RadioConstant.programNameList.get(RadioConstant.PLAYING_POSITION))
//                        .setName(RadioConstant.nameList.get(RadioConstant.PLAYING_POSITION))
//                        .setImageUrl(RadioConstant.imgList.get(RadioConstant.PLAYING_POSITION))
//                        .setPlayingPosition(RadioConstant.PLAYING_POSITION)
//                        .commit();
//            }
//        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null && player.getPlayWhenReady()) {
            player.stop();
            player.release();
            player = null;
        }
//        if (mediaPlayer != null ) {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//            }
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
    }
    //  //ExoPlayer.EventListener---------------------------------------start---------------  
    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.i(TAG, "onLoadingChanged---"+isLoading);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playWhenReady) {
            switch (playbackState) {
                case STATE_IDLE://播放失败后的空闲

                    break;
                case STATE_BUFFERING:
                    //显示缓冲...
                    break;
                case STATE_READY://播放暂停都是这个状态

                    break;
                case STATE_ENDED:
                    break;
            }
        } else {
            //这是暂停--STATE_READY

        }
        Log.i(TAG, "onPlayerStateChanged--"+playWhenReady+"----playbackState="+playbackState);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.i(TAG, "onTimelineChanged--" + timeline.toString());
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.i(TAG, "onTracksChanged--" );
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        //要做点处理,发个广播给MainActivity吧，关闭动画，提示出错
        Log.i(TAG,"onPlayerError----");
        Intent intent = new Intent();
        intent.setAction(RadioConstant.RADIO_SERVICE_BROADCAST);
        intent.putExtra("type", RadioConstant.PAUSE);
        sendBroadcast(intent);
        Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
        error.printStackTrace();
        if (error!=null)
            Log.e(TAG, "onPlayerError="+error.getMessage()+"---getLocalizedMessage="+error.getLocalizedMessage());
    }

    @Override
    public void onPositionDiscontinuity() {
        Log.i(TAG, "onPositionDiscontinuity");
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.i(TAG, "onPlaybackParametersChanged");
    }

//  //ExoPlayer.EventListener------------------------------------------end---------------

    private class RadioServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            
            // 对应传过来的type值，进行不同的消息处理
            switch (intent.getIntExtra("type", 0)) {
                case RadioConstant.PLAY:
//                    if (RadioConstant.preRadioId==-1) {
//                        //第一次---------------------------------------------------------------------
//                        Log.i(TAG, "我是第一次");
////                        cursor = radioDao.select(tableName);
////                        if (cursor.getCount() > 0) {
////                            updataDB(RadioConstant.radioId);
////                        }
////                        updataDB(RadioConstant.radioId, System.currentTimeMillis());
//                    } else {
//                        //不是第一次
//                        if (RadioConstant.preRadioId == RadioConstant.radioId) {
//                            //同一个广播------------------貌似不用动-------------------------------------------
////                            cursor = radioDao.select(tableName);
////                            if (cursor.getCount() > 0) {
////                                cursor = radioDao.getMaxId(tableName);
////                                String tag = null;
////                                int maxId = 0;
////                                while (cursor.moveToNext()) {
////                                    maxId = cursor.getInt(cursor.getColumnIndex("maxId"));
////                                }
////                                cursor = radioDao.select(tableName, maxId - 1);
////                                while (cursor.moveToNext()) {
////                                    tag = cursor.getString(cursor.getColumnIndex("tag"));
////                                    if (tag.equals("mp3")) {
////                                        updataDB(MusicSong.albumID);
////                                    }
////                                    break;
////                                }
////                            }
////                            updataDB(MusicConstant.radio_id, System.currentTimeMillis());
////                            handler.sendEmptyMessage(0);
//                            Log.i(TAG, "我不是第一次，我是同");
//                            return;
//                        } else {
//                            //不同广播----------------------------------------------------------------
//                            //不一样的广播频道
//                            Log.i(TAG, "我不是第一次，我不是同");
////                            cursor = radioDao.select(tableName);
////                            if (cursor.getCount() > 0) {
////                                cursor = radioDao.getMaxId(tableName);
////                                String tag = null;
////                                int maxId = 0;
////                                while (cursor.moveToNext()) {
////                                    maxId = cursor.getInt(cursor.getColumnIndex("maxId"));
////                                }
////                                cursor = radioDao.select(tableName, maxId - 1);
////                                while (cursor.moveToNext()) {
////                                    updataDB(RadioConstant.radioId, System.currentTimeMillis());
////                                    break;
////                                }
////                            }
//                        }
//                    }

                    play(RadioConstant.playList.get(RadioConstant.PLAYING_POSITION));
                    break;
                case RadioConstant.PAUSE://暂停

                    pause();
                    break;
                case RadioConstant.REPLAY://没退出页面，继续

                    playGoOn(RadioConstant.playList.get(RadioConstant.PLAYING_POSITION));
                    break;
                case RadioConstant.NEXT_RADIO:

                    nextRadio();
                    break;
                case RadioConstant.PRE_RADIO:

                    preRadio();
                    break;
                default:

                    break;
            }
        }
    }
    /**
     * 根据传入的path来播放
     */
    private void play(String path) {
        Log.i(TAG, "play---");
            if (player != null&&path!=null) {
                Log.i(TAG, path);
                player.setPlayWhenReady(false);
                if (!player.getPlayWhenReady()) {
                    if (path != null) {
                        if (path.contains(".m3u8") || path.contains(".m3u8?transcode=ts")) {//喜马拉雅url
                            hmSource = new HlsMediaSource(Uri.parse(path),
                                    dataSourceFactory, 6, null, null);
                        } else {
                            //国外的,url会有ICY 200的状态
                            ExtractorsFactory factory = new DefaultExtractorsFactory();
                            hmSource = new ExtractorMediaSource(Uri.parse(path),
                                    dataSourceFactory, factory, null, null);
                        }
                    }

                    player.prepare(hmSource);
                    player.setPlayWhenReady(true);
                    RadioConstant.isPlaying = true;
                    Intent playIntent = new Intent();
                    playIntent.setAction(RadioConstant.MAIN_BROADCAST);
                    playIntent.putExtra("type", RadioConstant.PLAY);
                    sendBroadcast(playIntent);
                }
                //保存当前广播信息
                sharedPreferencesUtil.edit().setId(RadioConstant.idList.get(RadioConstant.PLAYING_POSITION))
                        .setPlayUrl(RadioConstant.playList.get(RadioConstant.PLAYING_POSITION))
                        .setprogramName(RadioConstant.programNameList.get(RadioConstant.PLAYING_POSITION))
                        .setName(RadioConstant.nameList.get(RadioConstant.PLAYING_POSITION))
                        .setImageUrl(RadioConstant.imgList.get(RadioConstant.PLAYING_POSITION))
                        .setPlayingPosition(RadioConstant.PLAYING_POSITION)
                        .commit();
            }
        }
    //MediaPlayer--------------
//    private void playGG(String path) {
////        path = "http://s2.stationplaylist.com:8240/";
//        path = "http://s3.myradiostream.com:9812/";
////        path = "http://167.114.131.90:5066/index.html?sid=1";
////        path = "http://tar.stream.dev.al:9080";
//        path = "http://tar.stream.dev.al:9078/";
//        if (path == null||mediaPlayer==null) {
//            return;
//        }
//        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(path),
//                dataSourceFactory, extractorsFactory, null, null);
//        player.prepare(mediaSource);
//        Log.i(TAG, path);
//        if (player != null) {
//            player.setPlayWhenReady(false);
//            RadioConstant.isPlaying = false;
//        }
//        try {
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(path);
//            mediaPlayer.prepareAsync();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    /**
     * 回复播放状态
     */
    private void playGoOn(String path) {
        Log.i(TAG, "replay---");
        if (player != null && !player.getPlayWhenReady()) {
            if (path != null) {
                if (path.contains(".m3u8") || path.contains(".m3u8?transcode=ts")) {//喜马拉雅url
                    hmSource = new HlsMediaSource(Uri.parse(path),
                            dataSourceFactory, 8, null, null);
                } else {
                    //国外的,url会有ICY 200的状态
                    ExtractorsFactory factory = new DefaultExtractorsFactory();
                    hmSource = new ExtractorMediaSource(Uri.parse(path),
                            dataSourceFactory, factory, null, null);
                }
            }
            player.setPlayWhenReady(false);
            player.prepare(hmSource);
            player.setPlayWhenReady(true);
        }
        sharedPreferencesUtil.edit().setId(RadioConstant.idList.get(RadioConstant.PLAYING_POSITION))
                .setPlayUrl(RadioConstant.playList.get(RadioConstant.PLAYING_POSITION))
                .setprogramName(RadioConstant.programNameList.get(RadioConstant.PLAYING_POSITION))
                .setName(RadioConstant.nameList.get(RadioConstant.PLAYING_POSITION))
                .setImageUrl(RadioConstant.imgList.get(RadioConstant.PLAYING_POSITION))
                .setPlayingPosition(RadioConstant.PLAYING_POSITION)
                .commit();
        RadioConstant.isPlaying = true;
        Intent pauseIntent = new Intent();
        pauseIntent.setAction(RadioConstant.MAIN_BROADCAST);
        pauseIntent.putExtra("type", RadioConstant.REPLAY);
        sendBroadcast(pauseIntent);
    }

    /**
     * 暂停播放状态
     */
    private void pause() {
        Log.i(TAG, "pause---");
//        updataDB(RadioConstant.radioId, System.currentTimeMillis());
        RadioConstant.isPlaying = false;
        RadioConstant.preRadioId = RadioConstant.idList.get(RadioConstant.PLAYING_POSITION);
        player.setPlayWhenReady(false);
        //需要发个广播停止动画
        Intent pauseIntent = new Intent();
        pauseIntent.setAction(RadioConstant.MAIN_BROADCAST);
        pauseIntent.putExtra("type", RadioConstant.PAUSE);
        sendBroadcast(pauseIntent);
    }

    SharedPreferencesUtil sharedPreferencesUtil;
    /**
     * 播放下一曲
     * 在该方法中控制，当前音乐的播放模式
     */
    private void nextRadio() {
        Log.i(TAG, "next---");
        RadioConstant.PLAYING_POSITION++;
        /*
		 * 1、如果播放音频的位置变量 小于 播放列表的长度 继续播放当前位置的音频 2、如果 大于 把位置变量赋值为0，从从开始播放
		 */
        if (RadioConstant.PLAYING_POSITION < RadioConstant.playList.size()) {
        } else {
            RadioConstant.PLAYING_POSITION = 0;
        }
        play(RadioConstant.playList.get(RadioConstant.PLAYING_POSITION));
    }
    //上一曲
    private void preRadio() {
        Log.i(TAG, "pre---");
        RadioConstant.PLAYING_POSITION--;
        if (RadioConstant.PLAYING_POSITION < 0) {
            RadioConstant.PLAYING_POSITION = RadioConstant.playList.size()-1;
        }
        
        play(RadioConstant.playList.get(RadioConstant.PLAYING_POSITION));
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
