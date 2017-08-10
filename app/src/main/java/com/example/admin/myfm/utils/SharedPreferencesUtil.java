package com.example.admin.myfm.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Admin on 2017/7/19.
 */

public class SharedPreferencesUtil {
    //name	programName 	id	coverSmall
    private static final String spName = "radio_sharePre";
    private SharedPreferences sp;
    //    private SharedPreferences.Editor editor;
    private Editor sEditor;

    public SharedPreferencesUtil(Context context) {
        sp = context.getSharedPreferences(spName, Activity.MODE_PRIVATE);
//        editor = sp.edit();
        sEditor = new Editor(sp);
    }

    
    public int getPlayingPosition() {
        return sp.getInt("playingPosition", -1);
    }
    //存储当前广播是哪个列表的,应该就刚开启的时候用的上
    public int getListType() {
        return sp.getInt("listType", -1);
    }

    public String getPageUrl() {
        return sp.getString("pageUrl", null);
    }
    public String getImageUrl() {
        return sp.getString("coverSmall", null);
    }

    public String getprogramName() {
        return sp.getString("programName", null);
    }

    public String getName() {
        return sp.getString("name", null);
    }

    public String getPlayUrl() {
        return sp.getString("playUrl", null);
    }

    public int getId() {
        return sp.getInt("id", -1);
    }

    public Editor edit() {
        return sEditor;
    }


    public class Editor {
        SharedPreferences.Editor editor;

        public Editor(SharedPreferences sp) {
            editor = sp.edit();
        }

        public Editor setPageUrl(String pageUrl) {
            editor.putString("pageUrl", pageUrl);
            return this;
        }
        public Editor setPlayingPosition(int playingPosition) {
            editor.putInt("playingPosition",playingPosition);
            return this;
        }
        public Editor setListType(int listType) {
            editor.putInt("listType", listType);
            return this;
        }

        public Editor setImageUrl(String url) {
            editor.putString("coverSmall", url);
            return this;
        }

        public Editor setprogramName(String programName) {
            editor.putString("programName", programName);
            return this;
        }

        public Editor setName(String name) {
            editor.putString("name", name);
            return this;
        }

        public Editor setId(int id) {
            editor.putInt("id", id);
            return this;
        }

        public Editor setPlayUrl(String url) {
            editor.putString("playUrl", url);
            return this;
        }

        public void commit() {
            editor.commit();
        }
    }
}
