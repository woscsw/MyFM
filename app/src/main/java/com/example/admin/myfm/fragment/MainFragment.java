package com.example.admin.myfm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.admin.myfm.MainActivity;
import com.example.admin.myfm.R;

/**
 * Created by Admin on 2017/7/17.
 */

public class MainFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = MainFragment.class.getSimpleName();

    private void initViews(View view) {
        view.findViewById(R.id.btn_hot).setOnClickListener(this);
        view.findViewById(R.id.btn_shengshi).setOnClickListener(this);
        view.findViewById(R.id.btn_wenyi).setOnClickListener(this);
        view.findViewById(R.id.btn_zixun).setOnClickListener(this);
        view.findViewById(R.id.btn_jingji).setOnClickListener(this);
        view.findViewById(R.id.btn_bendi).setOnClickListener(this);
        view.findViewById(R.id.btn_qita).setOnClickListener(this);
        view.findViewById(R.id.btn_music).setOnClickListener(this);
        view.findViewById(R.id.btn_shoucang).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_main, null);
        initViews(view);
        return view;
    }

    Fragment fragment = null;
    @Override
    public void onClick(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.btn_hot:
                if (fragment != null && fragment instanceof ListViewFragment) {

                } else {
                    fragment = new ListViewFragment();
                }
                Bundle bundle = new Bundle();
//                bundle.putInt("pageType",ListViewFragment.HOT);
                bundle.putInt("pageType",ListViewFragment.WAIGUO);// TODO: 2017/8/7 waiguo 
                fragment.setArguments(bundle);
                
                break;
            case R.id.btn_shengshi:
                if (fragment != null && fragment instanceof ShengShiFragment) {

                } else {
                    fragment = new ShengShiFragment();
                }
                break;
            case R.id.btn_wenyi:
                if (fragment != null && fragment instanceof ListViewFragment) {

                } else {
                    fragment = new ListViewFragment();
                }
                Bundle wyBundle = new Bundle();
                wyBundle.putInt("pageType",ListViewFragment.WENYI);
                fragment.setArguments(wyBundle);
                break;
            case R.id.btn_zixun:
                if (fragment != null && fragment instanceof ListViewFragment) {

                } else {
                    fragment = new ListViewFragment();
                }
                Bundle xwBundle = new Bundle();
                xwBundle.putInt("pageType",ListViewFragment.XINWEN);
                fragment.setArguments(xwBundle);
                break;
            case R.id.btn_jingji:
                if (fragment != null && fragment instanceof ListViewFragment) {

                } else {
                    fragment = new ListViewFragment();
                }
                Bundle jingjiBundel = new Bundle();
                jingjiBundel.putInt("pageType",ListViewFragment.JINGJI);
                fragment.setArguments(jingjiBundel);
                break;
            case R.id.btn_bendi:
                if (((MainActivity) getActivity()).getLocationModel() == null) {
                    Toast.makeText(getContext(), "获取地址失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fragment != null && fragment instanceof ListViewFragment) {

                } else {
                    fragment = new ListViewFragment();
                }
                Bundle bBundle = new Bundle();
                bBundle.putInt("pageType",ListViewFragment.BENDI);
                bBundle.putLong("provinceCode",((MainActivity) getActivity()).getLocationModel().getProvinceCode());
                fragment.setArguments(bBundle);
                break;
            case R.id.btn_qita:
                if (fragment != null && fragment instanceof ListViewFragment) {

                } else {
                    fragment = new ListViewFragment();
                }
                Bundle qitaBundle = new Bundle();
                qitaBundle.putInt("pageType",ListViewFragment.QITA);
                fragment.setArguments(qitaBundle);
                break;
            case R.id.btn_music:
                if (fragment != null && fragment instanceof ListViewFragment) {

                } else {
                    fragment = new ListViewFragment();
                }
                Bundle musicBundle = new Bundle();
                musicBundle.putInt("pageType",ListViewFragment.MUSIC);
                fragment.setArguments(musicBundle);
                break;
            case R.id.btn_shoucang:
                if (fragment != null && fragment instanceof ListViewFragment) {

                } else {
                    fragment = new ListViewFragment();
                }
                Bundle scBundle = new Bundle();
                scBundle.putInt("pageType",ListViewFragment.SHOUCANG);
                fragment.setArguments(scBundle);
                break;

        }
        ft.replace(R.id.main_fl_show, fragment).commit();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
