package com.example.admin.myfm.model;

import java.util.List;

/**
 * Created by user on 2016/8/3.
 */
public class ActivityBroad {//用这个
    public Data data;

    public static class Data{

        public List<Datas> data;
    }
    public static class Datas{
        public String coverSmall;
        public String name;
        public String programName;
        public int playCount;
        public PlayUrl playUrl;
        //哪个？
        public int fmUid;
        public int id;
        public int programId;
    }
    public static class PlayUrl{
        public String aac24;
        public String aac64;
        public String ts24;
        public String ts64;
    }


}
