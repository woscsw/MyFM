package com.example.admin.myfm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.example.admin.myfm.R;
import com.example.admin.myfm.adapter.ListViewAdapter;
import com.example.admin.myfm.adapter.TestAdapter;
import com.example.admin.myfm.db.RadioDao;
import com.example.admin.myfm.model.ActivityBroad;
import com.example.admin.myfm.model.RadioDaoModel;
import com.example.admin.myfm.model.TestModel;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2017/7/17.
 */

public class ListViewFragment extends BaseFragment {
    private RecyclerView listLv;
    private ListViewAdapter adapter;
    private TestAdapter fremdnessAdapter;
    private View errorView;
    private View loadingView;
    private int id;
    private static int pageType = -1;
    private int pageNum = 1;
    public static final int HOT = 0;//热门
    public static final int WENYI = 1;//文艺
    public static final int XINWEN = 2;//新闻
    public static final int JINGJI = 3;//经济
    public static final int QITA = 4;//其他
    public static final int MUSIC = 5;//音乐
    public static final int SHENGSHI = 6;//省市
    public static final int BENDI = 7;//本地
    public static final int SHOUCANG = 8;//收藏
    public static final int WAIGUO = 9;//外国
    private ActivityBroad activityBroad;
    private TestModel fremdnessModel;
    private static String url;
    private static final int pageSize = 2000;//要跟adapter同样
    
    private RotateAnimation animation;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (pageType == SHOUCANG) {
            setDBData();
        } else {
            setData();
        } 
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = View.inflate(getActivity(), R.layout.fragment_list, null);
        listLv = (RecyclerView) v.findViewById(R.id.list_lv);
        loadingView = v.findViewById(R.id.loading_view);
        errorView = v.findViewById(R.id.error_view);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorView.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                if (pageType == SHOUCANG) {
                    //访问数据库获取数据
                    setDBData();
                } else {
                    setData();
                }
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listLv.setLayoutManager(manager);
        listLv.setHasFixedSize(true);
        adapter = new ListViewAdapter(getContext());
        fremdnessAdapter = new TestAdapter(getContext());
        id = getArguments().getInt("id");
        pageType = getArguments().getInt("pageType");
        if (pageType == WAIGUO) {
            listLv.setAdapter(fremdnessAdapter);
        } else {
            listLv.setAdapter(adapter);
        }
        animation = new RotateAnimation(
                0,
                360,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(3000);
        // 设置动画重复的次数
        animation.setRepeatCount(Animation.INFINITE);
        // 设置动画重复的模式
        animation.setRepeatMode(Animation.INFINITE);
        // 设置动画结束以后的状态
        animation.setFillAfter(false);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        loadingView.findViewById(R.id.img_loading).setAnimation(animation);

        animation.start();
        return v;
    }

    /**
     * 收藏页面的
     */
    private void setDBData() {
        RadioDao dao = new RadioDao(getContext());
        List<RadioDaoModel> list = dao.getCollectData();
        if (list != null && list.size() > 0) {
            loadingView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
            List<ActivityBroad.Datas> datas = new ArrayList<>();
            for (RadioDaoModel model : list) {
                ActivityBroad.Datas data = new ActivityBroad.Datas();
                data.coverSmall=model.getCoverSmall();
                data.name=model.getName();
                data.programName=model.getProgramName();
                data.id=model.getRadioId();
                ActivityBroad.PlayUrl playUrl = new ActivityBroad.PlayUrl();
                playUrl.ts24=model.getTs24Url();
                playUrl.ts64=model.getTs64Url();
                playUrl.aac24=model.getAac24Url();
                playUrl.aac64=model.getAac64Url();
                data.playUrl = playUrl;
                datas.add(data);
            }
            adapter.setData(datas);
            
        } else {
            errorView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            Toast.makeText(getContext(), "当前收藏列表为空", Toast.LENGTH_SHORT).show();
        }
        
    }
    private void setData() {
        getUrl();
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                loadingView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                String result = responseInfo.result;
                Gson gson = new Gson();
                if (pageType == WAIGUO) {
                    fremdnessModel = gson.fromJson(result, TestModel.class);
                    if (pageNum == 1) {
                        fremdnessAdapter.setData(fremdnessModel.getJson());
                    } else {
                        fremdnessAdapter.addData(fremdnessModel.getJson());
                    }
                } else {
                    activityBroad = gson .fromJson(result, ActivityBroad.class);
                    if (pageNum == 1) {
                        adapter.setData(activityBroad.data.data);
                    } else {
                        adapter.addData(activityBroad.data.data);
                    }
                }


//                RadioConstant.playList.clear();
//                RadioConstant.nameList.clear();
//                RadioConstant.programNameList.clear();
//                RadioConstant.imgList.clear();
//                RadioConstant.idList.clear();
//                for (int i = 0; i < adapter.getDatas().size(); i++) {
//                    RadioConstant.playList.add(adapter.getDatas().get(i).playUrl.aac64);// TODO: 2017/7/20 音频
//                    RadioConstant.nameList.add(adapter.getDatas().get(i).name);
//                    RadioConstant.programNameList.add(adapter.getDatas().get(i).programName);
//                    RadioConstant.imgList.add(adapter.getDatas().get(i).coverSmall);
//                    RadioConstant.idList.add(adapter.getDatas().get(i).id);
//                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (errorView != null) {
                    errorView.setVisibility(View.VISIBLE);
                    loadingView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getUrl() {
        if (pageType == HOT) {
            url = "http://live.ximalaya.com/live-web/v2/radio/hot?pageNum=" + pageNum + "&pageSize="+pageSize;
        } else if (pageType == WENYI) {
            url = "http://live.ximalaya.com/live-web/v2/radio/category?categoryId="+4+"&pageNum="+pageNum+"&pageSize="+pageSize;
        } else if (pageType == XINWEN) {
            url = "http://live.ximalaya.com/live-web/v2/radio/category?categoryId="+5+"&pageNum="+pageNum+"&pageSize="+pageSize;
        }else if (pageType == JINGJI) {
            url = "http://live.ximalaya.com/live-web/v2/radio/category?categoryId="+11+"&pageNum="+pageNum+"&pageSize="+pageSize;
        }else if (pageType == QITA) {
            url = "http://live.ximalaya.com/live-web/v2/radio/category?categoryId="+15+"&pageNum="+pageNum+"&pageSize="+pageSize;
        }else if (pageType == MUSIC) {
            url = "http://live.ximalaya.com/live-web/v2/radio/category?categoryId="+14+"&pageNum="+pageNum+"&pageSize="+pageSize;
        } else if (pageType == SHENGSHI) {
            url = "http://live.ximalaya.com/live-web/v2/radio/province?pageNum=" + pageNum + "&pageSize=" + pageSize + "&provinceCode=" + getArguments().getLong("provinceCode");

        } else if (pageType == BENDI){
            url = "http://live.ximalaya.com/live-web/v2/radio/province?pageNum=" + pageNum + "&pageSize=" + pageSize + "&provinceCode=" + getArguments().getLong("provinceCode");
        }else if (pageType == WAIGUO){
            url = "http://www.radiomoob.com/radiomoob/api.php?cat_id=95";//test
        }
    }
    public static int getPageType() {
        return pageType;
    }
    public static String getPageUrl() {
        return url;
    }
    
}
