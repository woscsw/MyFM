package com.example.admin.myfm.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Admin on 2017/7/19.
 */

public class RadioOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "myradio.db";
    private static final String TAG = RadioOpenHelper.class.getSimpleName();
    public RadioOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public RadioOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
    public RadioOpenHelper(Context context) {
        super(context,DB_NAME, null, 1);
    }
    public RadioOpenHelper(Context context, int version) {
        super(context, DB_NAME, null, version);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//        if (tabbleIsExist("dingyueting_subscription_table","rs.db")){
//            //把表数据导入新表 删掉旧表
//            //  String sql="ALTER TABLE Teachers ADD COLUMN Sex text";//追加一列
//            //先按照删除表创建一个新表，新表不包含要删除的列，删掉旧表，在重新命名新表，删除一列
//        }else {
            String collectTable = "create table collect_table " +
                    "(id integer primary key autoincrement," +"radioId integer,"+
                    "name varchar(20),"+"programName  varchar(20)," //+"play_time_last varchar(40),"
                    +"ts24Url varchar(80),"+"ts64Url varchar(80),"+"aac24Url varchar(80),"
                    +"aac64Url varchar(80),"+"coverSmall varchar(80),"+"collect integer)";
            db.execSQL(collectTable);
//        }

        Log.i(TAG, "=====onCreate====");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
