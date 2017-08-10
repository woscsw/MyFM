package com.example.admin.myfm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.admin.myfm.model.RadioDaoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2017/7/19.
 */

public class RadioDao {
    private RadioOpenHelper helper;
    private SQLiteDatabase db;
    private static final String tableName = "collect_table";

    public RadioDao(Context context) {
        helper = new RadioOpenHelper(context);
    }

    /**
     * 更新数据库时使用
     *
     * @param context
     * @param newVersion
     */
    public RadioDao(Context context, int newVersion) {
        super();
        this.helper = new RadioOpenHelper(context, newVersion);
    }

    public void insert(RadioDaoModel model) {
        db = helper.getWritableDatabase();
        Cursor cursor = db.query(tableName,
                new String[]{"name", "programName", "coverSmall", "ts24Url", "ts64Url", "aac24Url", "aac64Url","collect"},
                "radioId=?",
                new String[]{model.getRadioId()+""},
                null, null, null);
        if (cursor.getCount() > 0) {
            update(model);
        } else {
            ContentValues values = new ContentValues();
            values.put("radioId", model.getRadioId());
            values.put("name", model.getName());
            values.put("programName", model.getProgramName());
            values.put("coverSmall", model.getCoverSmall());
            values.put("ts24Url", model.getTs24Url());
            values.put("ts64Url", model.getTs64Url());
            values.put("aac24Url", model.getAac24Url());
            values.put("aac64Url", model.getTs64Url());
            values.put("collect", model.getCollect());
            db.insert(tableName, null, values);
        }
        cursor.close();
        db.close();
    }

    public void update(RadioDaoModel model) {
//        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("radioId", model.getRadioId());
        values.put("name", model.getName());
        values.put("programName", model.getProgramName());
        values.put("coverSmall", model.getCoverSmall());
        values.put("ts24Url", model.getTs24Url());
        values.put("ts64Url", model.getTs64Url());
        values.put("aac24Url", model.getAac24Url());
        values.put("aac64Url", model.getTs64Url());
        values.put("collect", model.getCollect());
        db.update(tableName, values, "radioId=?", new String[]{model.getRadioId() + ""});
//        db.close();
    }

    public List<RadioDaoModel> getCollectData() {
        List<RadioDaoModel> list = new ArrayList<>();
        db = helper.getWritableDatabase();
        Cursor cursor = db.query(tableName,
                new String[]{"radioId", "name", "programName", "coverSmall", "ts24Url", "ts64Url", "aac24Url", "aac64Url"},
                "collect=?",
                new String[]{"1"},
                null, null, null);
        while (cursor.moveToNext()) {
            RadioDaoModel model = new RadioDaoModel();
            model.setRadioId(cursor.getInt(cursor.getColumnIndex("radioId")));
            model.setName(cursor.getString(cursor.getColumnIndex("name")));
            model.setProgramName(cursor.getString(cursor.getColumnIndex("programName")));
            model.setCoverSmall(cursor.getString(cursor.getColumnIndex("coverSmall")));
            model.setTs24Url(cursor.getString(cursor.getColumnIndex("ts24Url")));
            model.setTs64Url(cursor.getString(cursor.getColumnIndex("ts64Url")));
            model.setAac24Url(cursor.getString(cursor.getColumnIndex("aac24Url")));
            model.setAac64Url(cursor.getString(cursor.getColumnIndex("aac64Url")));
            list.add(model);
        }
        cursor.close();
        db.close();
        return list;
    }
    public int getCollect(int radioId) {
        db = helper.getWritableDatabase();
        Cursor cursor = db.query(tableName,
                new String[]{ "collect"},
                "radioId=?",
                new String[]{radioId+""},
                null, null, null);
        if (cursor.moveToNext()) {
            int i = cursor.getInt(cursor.getColumnIndex("collect"));
            db.close();
            return i;
        } else {
            db.close();
            return -1;
        }
    }
}
