package com.example.admin.myfm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.myfm.R;
import com.example.admin.myfm.db.RadioDao;
import com.example.admin.myfm.fragment.ListViewFragment;
import com.example.admin.myfm.model.RadioDaoModel;
import com.example.admin.myfm.model.TestModel;
import com.example.admin.myfm.utils.ImageLoaderUtils;
import com.example.admin.myfm.utils.RadioConstant;
import com.example.admin.myfm.utils.SharedPreferencesUtil;


/**
 * Created by Admin on 2017/7/17.
 * id应该要加个后缀，防止跟喜马拉雅的冲突
 */
public class TestAdapter extends LoadMoreAdapter<TestModel.JsonBean> {
    private static final String TAG = TestAdapter.class.getSimpleName();
    public static final String IMG_URL = "http://www.radiomoob.com/radiomoob/upload/";
    private static final String CATEGORY_IMG_URL = "http://www.radiomoob.com/radiomoob/upload/category/";
    public static final int IDADD = 6000000;//避免国外的id与喜马拉雅重复
    public TestAdapter(Context context) {
        super(context);
        dao = new RadioDao(context);
    }
    private RadioDao dao;
    @Override
    public int getPageSize() {
        return 2000;
    }

    @Override
    protected boolean getHasLoadMore() {
        return false;
    }

    @Override
    public int resId() {
        return R.layout.activity_broad_item;
    }

    @Override
    public void bindViewHolder(final ViewHolder holder, final TestModel.JsonBean data, final int position) {
//        data.setId(String.valueOf(Integer.valueOf(data.getId())+IDADD));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioConstant.PLAYING_POSITION = position;
                if (data.getId().equals(String.valueOf(RadioConstant.radioId-IDADD))) {
                    //同一首
                    if (RadioConstant.isPlaying==false) {
                        Intent intent = new Intent();
                        intent.setAction(RadioConstant.RADIO_SERVICE_BROADCAST);
                        intent.putExtra("type", RadioConstant.PLAY);
                        context.sendBroadcast(intent);
                    }
                } else {
                    RadioConstant.radioId = Integer.valueOf(data.getId())+IDADD;
                    Intent intent = new Intent();
                    intent.setAction(RadioConstant.RADIO_SERVICE_BROADCAST);
                    intent.putExtra("type", RadioConstant.PLAY);
                    context.sendBroadcast(intent);
                }
                //用于重开应用时播放广播
                SharedPreferencesUtil util = new SharedPreferencesUtil(context);
                util.edit().setId(Integer.valueOf(data.getId())+IDADD)
                        .setImageUrl(data.getRadio_image())
                        .setName(data.getRadio_name())
                        .setprogramName("")
                        .setPlayUrl(data.getRadio_url())
                        .setListType(ListViewFragment.getPageType())
                        .setPageUrl(ListViewFragment.getPageUrl())
                        .setPlayingPosition(position)
                        .commit();
                
                RadioConstant.playList.clear();
                RadioConstant.nameList.clear();
                RadioConstant.programNameList.clear();
                RadioConstant.imgList.clear();
                RadioConstant.idList.clear();
                for (int i = 0; i < TestAdapter.this.getDatas().size(); i++) {
                    RadioConstant.playList.add(TestAdapter.this.getDatas().get(i).getRadio_url());// TODO: 2017/7/20 音频
                    RadioConstant.nameList.add(TestAdapter.this.getDatas().get(i).getRadio_name());
                    RadioConstant.programNameList.add("");
                    RadioConstant.imgList.add(IMG_URL+TestAdapter.this.getDatas().get(i).getRadio_image());
                    RadioConstant.idList.add(Integer.valueOf(TestAdapter.this.getDatas().get(i).getId())+IDADD);
                }
//                setInsertData(broadcastModel.data,"top",0);
            }
        });
        
        
        final int type = dao.getCollect(Integer.valueOf(data.getId())+IDADD);
        ImageView iv = holder.getView(R.id.activity_broad_item_iv);
        TextView name = holder.getView(R.id.activity_broad_item_name);
        final TextView shoucang = holder.getView(R.id.btn_shoucang);
         if (type == 1) {
            shoucang.setText("取消");
            Drawable drawable = context.getResources().getDrawable(R.drawable.gb_new_tv_favo);
            drawable.setBounds(0, 0, 41, 35);
            shoucang.setCompoundDrawables(drawable, null, null, null);
        } else {
            shoucang.setText("收藏");
            Drawable drawable = context.getResources().getDrawable(R.drawable.gb_new_tv_unfavo);
            drawable.setBounds(0, 0, 41, 35);
            shoucang.setCompoundDrawables(drawable, null, null, null);
        }
        ImageLoaderUtils.getImageByloader(IMG_URL+data.getRadio_image(), iv);
        name.setText(data.getRadio_name());
        shoucang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                //收藏
                if (type == 0) {
                    Log.i(TAG, "收藏----");
                    shoucang.setText("取消");
                    Drawable drawable = context.getResources().getDrawable(R.drawable.gb_new_tv_favo);
                    drawable.setBounds(0, 0, 41, 35);
                    shoucang.setCompoundDrawables(drawable, null, null, null);
                    collect(data);
                } else if (type == 1) {
                    Log.i(TAG, "取消收藏---");
                    shoucang.setText("收藏");
                    Drawable drawable = context.getResources().getDrawable(R.drawable.gb_new_tv_unfavo);
                    drawable.setBounds(0, 0, 41, 35);
                    shoucang.setCompoundDrawables(drawable, null, null, null);
                    cancelCollect(data);
                } else {
                    Log.i(TAG, "还没存入数据库----");
                    shoucang.setText("取消");
                    Drawable drawable = context.getResources().getDrawable(R.drawable.gb_new_tv_favo);
                    drawable.setBounds(0, 0, 41, 35);
                    shoucang.setCompoundDrawables(drawable, null, null, null);
                    collect(data);
                    
                }
            }
        });
    }
    private void collect(TestModel.JsonBean datas) {
        RadioDaoModel model = new RadioDaoModel();
        model.setRadioId(Integer.valueOf(datas.getId())+IDADD);
        model.setName(datas.getRadio_name());
        model.setProgramName("");
        model.setCoverSmall(IMG_URL+datas.getRadio_image());
        model.setTs24Url(datas.getRadio_url());
        model.setTs64Url(datas.getRadio_url());
        model.setAac24Url(datas.getRadio_url());
        model.setAac64Url(datas.getRadio_url());
        model.setCollect(1);
        dao.insert(model);
        notifyDataSetChanged();
    }
    private void cancelCollect(TestModel.JsonBean datas) {
        RadioDaoModel model = new RadioDaoModel();
        model.setRadioId(Integer.valueOf(datas.getId())+IDADD);
        model.setName(datas.getRadio_name());
        model.setProgramName("");
        model.setCoverSmall(IMG_URL+datas.getRadio_image());
        model.setTs24Url(datas.getRadio_url());
        model.setTs64Url(datas.getRadio_url());
        model.setAac24Url(datas.getRadio_url());
        model.setAac64Url(datas.getRadio_url());
        model.setCollect(0);
        dao.insert(model);
        notifyDataSetChanged();
    }
}
