package com.example.admin.myfm;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.admin.myfm.adapter.TestAdapter;
import com.example.admin.myfm.db.RadioDao;
import com.example.admin.myfm.fragment.ListViewFragment;
import com.example.admin.myfm.fragment.MainFragment;
import com.example.admin.myfm.fragment.ShengShiFragment;
import com.example.admin.myfm.model.ActivityBroad;
import com.example.admin.myfm.model.LocationModel;
import com.example.admin.myfm.model.RadioDaoModel;
import com.example.admin.myfm.model.TestModel;
import com.example.admin.myfm.service.ServiceBroadCast;
import com.example.admin.myfm.utils.ImageLoaderUtils;
import com.example.admin.myfm.utils.RadioConstant;
import com.example.admin.myfm.utils.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import static com.example.admin.myfm.fragment.ListViewFragment.SHENGSHI;
import static com.example.admin.myfm.fragment.ListViewFragment.WAIGUO;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    LocationModel  locationModel;
    private LocationClient mLocationClient;
    public BDLocationListener myListener = new MyLocationListener();
    /**
     * All Radio World
     * http://www.radiomoob.com/radiomoob/api.php
     * http://www.radiomoob.com/radiomoob/api.php?cat_id=95
     */
    private ImageView btnPlay;
    private Fragment fragment;
    private FragmentManager supportFragmentManager;
    private FragmentTransaction fragmentTransaction;
    //    private PlayData playData;
    private MainBroadcastR mainBroadcastR;
    private SharedPreferencesUtil sharedPreferencesUtil;
    Intent serviceBroadCast;
    private ObjectAnimator objectAnimator;
    private long mCurrentPlayTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenHeight = display.getHeight();
        if (screenHeight == 890) {

        } else if (screenHeight==600){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window window = getWindow();
                // Translucent status bar
                window.setFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }

        setContentView(R.layout.activity_main);
        initLocation();
        supportFragmentManager = getSupportFragmentManager();

        serviceBroadCast = new Intent(this, ServiceBroadCast.class);
        startService(serviceBroadCast);

        initView();
        sharedPreferencesUtil = new SharedPreferencesUtil(this);
        playRadio();


    }

    private void initView() {
        btnPlay = (ImageView) findViewById(R.id.btnPlay);
        fragment = new MainFragment();
        fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_fl_show, fragment);
        fragmentTransaction.commit();
        Drawable drawable = getResources().getDrawable(R.drawable.common_player_play);
        drawable.setBounds(0, 0, 90, 90);
        btnPlay.setImageDrawable(drawable);
        findViewById(R.id.btnPre).setOnClickListener(this);

        findViewById(R.id.btnNext).setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        objectAnimator = ObjectAnimator.ofFloat(btnPlay, "rotation", 0f, 360f);
        objectAnimator.setDuration(3000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);

        mainBroadcastR = new MainBroadcastR();
        IntentFilter intentFilter = new IntentFilter(RadioConstant.MAIN_BROADCAST);
        registerReceiver(mainBroadcastR, intentFilter);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void playRadio() {
        if (sharedPreferencesUtil == null || sharedPreferencesUtil.getName() == null) {
            Log.e(TAG, "sharedPreferencesUtil== null||sharedPreferencesUtil.getName()==null");
            return;
        }
        if (sharedPreferencesUtil.getListType() == ListViewFragment.SHOUCANG) {
            RadioDao dao = new RadioDao(this);
            List<RadioDaoModel> list = dao.getCollectData();
            if (list != null && list.size() > 0) {
                List<ActivityBroad.Datas> datas = new ArrayList<>();
                for (RadioDaoModel model : list) {
                    ActivityBroad.Datas data = new ActivityBroad.Datas();
                    data.coverSmall = model.getCoverSmall();
                    data.name = model.getName();
                    data.programName = model.getProgramName();
                    data.id = model.getRadioId();
                    ActivityBroad.PlayUrl playUrl = new ActivityBroad.PlayUrl();
                    playUrl.ts24 = model.getTs24Url();
                    playUrl.ts64 = model.getTs64Url();
                    playUrl.aac24 = model.getAac24Url();
                    playUrl.aac64 = model.getAac64Url();
                    data.playUrl = playUrl;
                    datas.add(data);
                }
                RadioConstant.playList.clear();
                RadioConstant.nameList.clear();
                RadioConstant.programNameList.clear();
                RadioConstant.imgList.clear();
                RadioConstant.idList.clear();
                for (int i = 0; i < datas.size(); i++) {
                    RadioConstant.playList.add(datas.get(i).playUrl.aac64);// TODO: 2017/7/20 音频
                    RadioConstant.nameList.add(datas.get(i).name);
                    RadioConstant.programNameList.add(datas.get(i).programName);
                    RadioConstant.imgList.add(datas.get(i).coverSmall);
                    RadioConstant.idList.add(datas.get(i).id);
                }
                RadioConstant.PLAYING_POSITION = sharedPreferencesUtil.getPlayingPosition();
                RadioConstant.radioId = sharedPreferencesUtil.getId();
                Intent intent = new Intent();
                intent.setAction(RadioConstant.RADIO_SERVICE_BROADCAST);
                intent.putExtra("type", RadioConstant.PLAY);
                sendBroadcast(intent);
            } else {
                Log.e(TAG, "数据库数据空");
            }
            return;
        }
        String url = sharedPreferencesUtil.getPageUrl();
        if (url == null) {
            Log.e(TAG, "sharedPreferencesUtil.getPageUrl()==null");
            return;
        }
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Gson gson = new Gson();
                RadioConstant.playList.clear();
                RadioConstant.nameList.clear();
                RadioConstant.programNameList.clear();
                RadioConstant.imgList.clear();
                RadioConstant.idList.clear();
                if (sharedPreferencesUtil.getListType() == WAIGUO) {
                    TestModel model = gson.fromJson(result, TestModel.class);
                    for (int i = 0; i < model.getJson().size(); i++) {
                        RadioConstant.playList.add(model.getJson().get(i).getRadio_url());// TODO: 2017/7/20 音频
                        RadioConstant.nameList.add(model.getJson().get(i).getRadio_name());
                        RadioConstant.programNameList.add("");
                        RadioConstant.imgList.add(TestAdapter.IMG_URL+model.getJson().get(i).getRadio_image());
                        RadioConstant.idList.add(Integer.valueOf(model.getJson().get(i).getId())+TestAdapter.IDADD);
                    }
                } else {
                    ActivityBroad activityBroad = gson.fromJson(result, ActivityBroad.class);
                    for (int i = 0; i < activityBroad.data.data.size(); i++) {
                        RadioConstant.playList.add(activityBroad.data.data.get(i).playUrl.aac64);// TODO: 2017/7/20 音频
                        RadioConstant.nameList.add(activityBroad.data.data.get(i).name);
                        RadioConstant.programNameList.add(activityBroad.data.data.get(i).programName);
                        RadioConstant.imgList.add(activityBroad.data.data.get(i).coverSmall);
                        RadioConstant.idList.add(activityBroad.data.data.get(i).id);
                    }
                }

                RadioConstant.PLAYING_POSITION = sharedPreferencesUtil.getPlayingPosition();
                RadioConstant.radioId = sharedPreferencesUtil.getId();
                Intent intent = new Intent();
                intent.setAction(RadioConstant.RADIO_SERVICE_BROADCAST);
                intent.putExtra("type", RadioConstant.PLAY);
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.e(TAG, "获取播放列表失败");
//                Toast.makeText(MainActivity.this, "获取播放列表失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            fragment = supportFragmentManager.findFragmentById(R.id.main_fl_show);
            if (fragment == null || fragment instanceof MainFragment) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
            } else if (fragment instanceof ListViewFragment && ListViewFragment.getPageType() == SHENGSHI) {
                supportFragmentManager.beginTransaction().detach(fragment).commit();
            } else if (fragment != null && (fragment instanceof ListViewFragment || fragment instanceof ShengShiFragment)) {
                supportFragmentManager.beginTransaction().replace(R.id.main_fl_show, new MainFragment()).commit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainBroadcastR != null) {
            unregisterReceiver(mainBroadcastR);
        }
        if (serviceBroadCast != null) {
            stopService(serviceBroadCast);
        }
        if (mLocationClient != null) {
            mLocationClient.stop();
            if (myListener != null)
                mLocationClient.unRegisterLocationListener(myListener);
        }
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    public void setStatus(String imgUrl, String title, String name) {
        if (imgUrl != null)
            ImageLoaderUtils.getImageByloader(imgUrl, (ImageView) findViewById(R.id.img));
        if (title != null)
            ((TextView) findViewById(R.id.title)).setText(title);
        if (name != null)
            ((TextView) findViewById(R.id.name)).setText(name);
    }

    @Override
    public void onClick(View v) {
        if (RadioConstant.radioId == -1) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(RadioConstant.RADIO_SERVICE_BROADCAST);
        switch (v.getId()) {
            case R.id.btnPre:
                intent.putExtra("type", RadioConstant.PRE_RADIO);
                break;
            case R.id.btnNext:
                intent.putExtra("type", RadioConstant.NEXT_RADIO);
                break;
            case R.id.btnPlay:
                if (RadioConstant.isPlaying) {
                    intent.putExtra("type", RadioConstant.PAUSE);
                } else {
                    if (RadioConstant.preRadioId == RadioConstant.radioId) {
                        intent.putExtra("type", RadioConstant.REPLAY);
                    } else {
                        intent.putExtra("type", RadioConstant.PLAY);
                    }
                }

                break;
        }
        sendBroadcast(intent);
    }


    public class MainBroadcastR extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("type", 0)) {
                case RadioConstant.REPLAY://继续播放
                    if (objectAnimator.isPaused()) {
                        objectAnimator.resume();
                    }
                    setStatus(sharedPreferencesUtil.getImageUrl(), sharedPreferencesUtil.getName(), sharedPreferencesUtil.getprogramName());
                    break;
                case RadioConstant.PLAY://播放
                    if (RadioConstant.isPlaying) {
                        objectAnimator.start();
                    } else {
                        if (objectAnimator.isStarted()) {
                            objectAnimator.pause();
                        }
                    }
                    setStatus(sharedPreferencesUtil.getImageUrl(), sharedPreferencesUtil.getName(), sharedPreferencesUtil.getprogramName());
                    break;
                case RadioConstant.PAUSE://暂停
                    if (objectAnimator.isStarted()) {
                        objectAnimator.pause();
                    }
                    setStatus(sharedPreferencesUtil.getImageUrl(), sharedPreferencesUtil.getName(), sharedPreferencesUtil.getprogramName());
                    break;
            }

        }
    }
    public LocationModel getLocationModel() {
        return locationModel;
    }
    public class MyLocationListener implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                Log.i(TAG, "onReceiveLocation  location == null");
                return;
            } else {
                Log.i(TAG, "getLocType=" + location.getLocType());
                Log.i(TAG, "Longitude=" + location.getLongitude() + "    Latitude=" + location.getLatitude());
                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    Log.i(TAG, "GPS定位结果" + location.getLocType());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    Log.i(TAG, "网络定位结果" + location.getLocType());
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    Log.i(TAG, "离线定位结果" + location.getLocType());
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    Log.e(TAG, "服务端网络定位失败" + location.getLocType());
                    return;
                } else if (location.getLocType() == BDLocation.TypeOffLineLocationFail) {
                    Log.e(TAG, "离线定位失败" + location.getLocType());
                    return;
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    Log.e(TAG, "网络不同导致定位失败，请检查网络是否通畅" + location.getLocType());
                    return;
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    Log.e(TAG, "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机" + location.getLocType());
                    return;
                } else if (location.getLocType() == BDLocation.TypeServerDecryptError) {
                    Log.e(TAG, "so文件有问题" + location.getLocType());
                    return;
                } else if (location.getLocType() == BDLocation.TypeServerCheckKeyError) {
                    Log.e(TAG, "key验证失败" + location.getLocType());
                    return;
                }
                try {
                    Log.i("检查是否启动百度定位", "" + location.getCity());
                    HttpUtils utils = new HttpUtils();
                    utils.send(HttpRequest.HttpMethod.GET, "http://location.ximalaya.com/location-web/location?latitude=" +
                            location.getLatitude() +
                            "&longitude=" +
                            location.getLongitude(), new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            String result = responseInfo.result;
                            Gson gson = new Gson();
                            locationModel = gson.fromJson(result, LocationModel.class);
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
//                            Toast.makeText(MainActivity.this, s + "", Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, e.getMessage() + "eeeeeeeeee");
                    mLocationClient.stop();
                    mLocationClient.start();
                }

            }
        }
        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

    }
    private void initLocation() {
        Log.i(TAG, "initLocation");
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("WGS84");//可选，默认gcj02，设置返回的定位结果坐标系...在海外地区，只能获得WGS84坐标
        int span = 60000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        Log.i(TAG, "mLocationClient start");
    }

}
