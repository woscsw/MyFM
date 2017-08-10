package com.example.admin.myfm.model;

import java.util.List;

/**
 * Created by ZXW23 on 2017/8/5.
 */

public class TestModel {

    private List<JsonBean> Json;

    public List<JsonBean> getJson() {
        return Json;
    }

    public void setJson(List<JsonBean> Json) {
        this.Json = Json;
    }

    public static class JsonBean {
        /**
         * cid : 95
         * category_name : Albania
         * category_image : 7272-2017-02-27.png
         * category_image2 : albania
         * category_continent : europe
         * id : 3247
         * category_id : 95
         * radio_name : Top Albania Radio
         * radio_image : 5264-2017-03-01.jpg
         * radio_url : http://tar.stream.dev.al:9078/
         */

        private String cid;
        private String category_name;
        private String category_image;
        private String category_image2;
        private String category_continent;
        private String id;
        private String category_id;
        private String radio_name;
        private String radio_image;
        private String radio_url;

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public String getCategory_image() {
            return category_image;
        }

        public void setCategory_image(String category_image) {
            this.category_image = category_image;
        }

        public String getCategory_image2() {
            return category_image2;
        }

        public void setCategory_image2(String category_image2) {
            this.category_image2 = category_image2;
        }

        public String getCategory_continent() {
            return category_continent;
        }

        public void setCategory_continent(String category_continent) {
            this.category_continent = category_continent;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategory_id() {
            return category_id;
        }

        public void setCategory_id(String category_id) {
            this.category_id = category_id;
        }

        public String getRadio_name() {
            return radio_name;
        }

        public void setRadio_name(String radio_name) {
            this.radio_name = radio_name;
        }

        public String getRadio_image() {
            return radio_image;
        }

        public void setRadio_image(String radio_image) {
            this.radio_image = radio_image;
        }

        public String getRadio_url() {
            return radio_url;
        }

        public void setRadio_url(String radio_url) {
            this.radio_url = radio_url;
        }
    }
}
