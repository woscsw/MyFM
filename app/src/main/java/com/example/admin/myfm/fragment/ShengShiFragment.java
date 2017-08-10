package com.example.admin.myfm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.admin.myfm.R;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

/**
 * Created by Admin on 2017/7/17.
 */

public class ShengShiFragment extends BaseFragment {
    
    private ViewPager viewPager;
    private MyAdapter adapter;
    
    private ShengshiModel model;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = View.inflate(getActivity(), R.layout.fragment_shengshi, null);
        viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, "http://live.ximalaya.com/live-web/v1/getProvinceList", new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Gson gson = new Gson();
                model = gson .fromJson(result, ShengshiModel.class);
                adapter = new MyAdapter();


                viewPager.setAdapter(adapter);
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridView = new GridView(getActivity());
            gridView.setNumColumns(6);//[30,84][994,480]
            ViewGroup.LayoutParams layoutParams = new ViewPager.LayoutParams();
            layoutParams.height=396;
            layoutParams.width=960;
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(layoutParams);
//            gridView.setLayoutParams(layoutParams);
            marginLayoutParams.leftMargin=30;
            marginLayoutParams.rightMargin=30;
            marginLayoutParams.topMargin=0;
            marginLayoutParams.bottomMargin=0;
            gridView.setLayoutParams(marginLayoutParams);
            gridView.setHorizontalSpacing(6);
            gridView.setVerticalSpacing(6);
            GAdapter gAdapter = new GAdapter();
            if (model == null || model.getResult() == null || model.getResult().size() < 1) {
                return gridView;
            }
            if (position == 0) {
                gAdapter.setData(model.getResult().subList(0, 18));
            } else if (position == 1) {
                gAdapter.setData(model.getResult().subList(18, model.getResult().size()));
            }
                gridView.setAdapter(gAdapter);
            
            container.addView(gridView);
            return gridView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            if (model == null || model.getResult() == null || model.getResult().size() < 1) {
                return 0;
            }
            if (model.getResult().size() % 18 > 0) {
                return model.getResult().size() / 18 + 1;
            } else {
                return model.getResult().size() / 18;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public class GAdapter extends BaseAdapter {
        List<ShengshiModel.ResultBean> data;
        public void setData(List<ShengshiModel.ResultBean> data) {
            this.data = data;
        }
        @Override
        public int getCount() {
            return data==null||data.size()==0?0:data.size();
        }

        @Override
        public ShengshiModel.ResultBean getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_item_shengshi, parent, false);
            
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(data.get(position).getProvinceName()+"");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment fragment = new ListViewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong("provinceCode",data.get(position).provinceCode);
                    bundle.putInt("pageType",ListViewFragment.SHENGSHI);
                    fragment.setArguments(bundle);
                    ft.add(R.id.main_fl_show,fragment).commit();
                }
            });
            return convertView;
        }
    }

    public static class ShengshiModel {

        private String ret;
        private String msg;
        private List<ResultBean> result;

        public String getRet() {
            return ret;
        }

        public void setRet(String ret) {
            this.ret = ret;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        public static class ResultBean {

            private int provinceCode;
            private String provinceName;

            public int getProvinceCode() {
                return provinceCode;
            }

            public void setProvinceCode(int provinceCode) {
                this.provinceCode = provinceCode;
            }

            public String getProvinceName() {
                return provinceName;
            }

            public void setProvinceName(String provinceName) {
                this.provinceName = provinceName;
            }
        }
    }
}
