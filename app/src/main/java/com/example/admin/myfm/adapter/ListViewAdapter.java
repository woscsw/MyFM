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
import com.example.admin.myfm.model.ActivityBroad;
import com.example.admin.myfm.model.RadioDaoModel;
import com.example.admin.myfm.utils.ImageLoaderUtils;
import com.example.admin.myfm.utils.RadioConstant;
import com.example.admin.myfm.utils.SharedPreferencesUtil;


/**
 * Created by Admin on 2017/7/17.
 */

public class ListViewAdapter extends LoadMoreAdapter<ActivityBroad.Datas> {
    private static final String TAG = ListViewAdapter.class.getSimpleName();

    public ListViewAdapter(Context context) {
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
    public void bindViewHolder(final ViewHolder holder, final ActivityBroad.Datas data, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioConstant.PLAYING_POSITION = position;
                if (data.id == RadioConstant.radioId) {
                    //同一首
                    if (RadioConstant.isPlaying==false) {
                        Intent intent = new Intent();
                        intent.setAction(RadioConstant.RADIO_SERVICE_BROADCAST);
                        intent.putExtra("type", RadioConstant.PLAY);
                        context.sendBroadcast(intent);
                    }
                } else {
                    RadioConstant.radioId = data.id;
                    Intent intent = new Intent();
                    intent.setAction(RadioConstant.RADIO_SERVICE_BROADCAST);
                    intent.putExtra("type", RadioConstant.PLAY);
                    context.sendBroadcast(intent);
                }
                //用于重开应用时播放广播
                SharedPreferencesUtil util = new SharedPreferencesUtil(context);
                util.edit().setId(data.id)
                        .setImageUrl(data.coverSmall)
                        .setName(data.name)
                        .setprogramName(data.programName)
                        .setPlayUrl(data.playUrl.aac64)// TODO: 2017/7/20 音频 
                        .setListType(ListViewFragment.getPageType())
                        .setPageUrl(ListViewFragment.getPageUrl())
                        .setPlayingPosition(position)
                        .commit();
                
                RadioConstant.playList.clear();
                RadioConstant.nameList.clear();
                RadioConstant.programNameList.clear();
                RadioConstant.imgList.clear();
                RadioConstant.idList.clear();
                for (int i = 0; i < ListViewAdapter.this.getDatas().size(); i++) {
                    RadioConstant.playList.add(ListViewAdapter.this.getDatas().get(i).playUrl.aac64);// TODO: 2017/7/20 音频
                    RadioConstant.nameList.add(ListViewAdapter.this.getDatas().get(i).name);
                    RadioConstant.programNameList.add(ListViewAdapter.this.getDatas().get(i).programName);
                    RadioConstant.imgList.add(ListViewAdapter.this.getDatas().get(i).coverSmall);
                    RadioConstant.idList.add(ListViewAdapter.this.getDatas().get(i).id);
                }
//                setInsertData(broadcastModel.data,"top",0);
            }
        });
        
        
        final int type = dao.getCollect(data.id);
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
        ImageLoaderUtils.getImageByloader(data.coverSmall, iv);
        name.setText(data.name);
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
    private void collect(ActivityBroad.Datas datas) {
        RadioDaoModel model = new RadioDaoModel();
        model.setRadioId(datas.id);
        model.setName(datas.name);
        model.setProgramName(datas.programName);
        model.setCoverSmall(datas.coverSmall);
        model.setTs24Url(datas.playUrl.ts24);
        model.setTs64Url(datas.playUrl.ts64);
        model.setAac24Url(datas.playUrl.aac24);
        model.setAac64Url(datas.playUrl.aac64);
        model.setCollect(1);
        dao.insert(model);
        notifyDataSetChanged();
    }
    private void cancelCollect(ActivityBroad.Datas datas) {
        RadioDaoModel model = new RadioDaoModel();
        model.setRadioId(datas.id);
        model.setName(datas.name);
        model.setProgramName(datas.programName);
        model.setCoverSmall(datas.coverSmall);
        model.setTs24Url(datas.playUrl.ts24);
        model.setTs64Url(datas.playUrl.ts64);
        model.setAac24Url(datas.playUrl.aac24);
        model.setAac64Url(datas.playUrl.aac64);
        model.setCollect(0);
        dao.insert(model);
        notifyDataSetChanged();
    }
}
