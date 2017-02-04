package com.lc.puppet.storage;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lody.virtual.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by legency on 2017/1/29.
 */

public class IObComment {
    static HashMap<IObIndex, Comment> hashMap;

    public static void get(Context context) {
        if(hashMap!=null)return;
        InputStream raw = context.getResources().openRawResource(R.raw.iob);
        Reader rd = new BufferedReader(new InputStreamReader(raw));
        Gson gson = new Gson();
        List<Comment> comments = gson.fromJson(rd, new TypeToken<ArrayList<Comment>>() {
        }.getType());
        // Now iterate through the list
        hashMap = new HashMap<>();
        for (Comment comment : comments) {
            hashMap.put(comment.getIndex(), comment);
        }
    }
    public static Comment get(String key){
        return hashMap.get(IObIndex.valueOf(key));
    }


    public class Comment {

        /**
         * index : NETWORK_INFO
         * is_sync : true
         * min_api : -1
         * max_api : 999
         * sys_service : connectivity
         * sys_manager : ConnectivityManager
         * method : getActiveNetworkInfo
         * params : []
         * comment : 获取当前活动网络信息
         */

        private IObIndex index;
        private boolean is_sync;
        private int min_api;
        private int max_api;
        private String sys_service;
        private String sys_manager;
        private String method;
        private String comment;
        private List<?> params;

        public IObIndex getIndex() {
            return index;
        }

        public void setIndex(IObIndex index) {
            this.index = index;
        }

        public boolean isIs_sync() {
            return is_sync;
        }

        public void setIs_sync(boolean is_sync) {
            this.is_sync = is_sync;
        }

        public int getMin_api() {
            return min_api;
        }

        public void setMin_api(int min_api) {
            this.min_api = min_api;
        }

        public int getMax_api() {
            return max_api;
        }

        public void setMax_api(int max_api) {
            this.max_api = max_api;
        }

        public String getSys_service() {
            return sys_service;
        }

        public void setSys_service(String sys_service) {
            this.sys_service = sys_service;
        }

        public String getSys_manager() {
            return sys_manager;
        }

        public void setSys_manager(String sys_manager) {
            this.sys_manager = sys_manager;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public List<?> getParams() {
            return params;
        }

        public void setParams(List<?> params) {
            this.params = params;
        }
        public String toMarkDown(String result){
            String  template= "# %s\n" +
                    "\n" +
                    "%s\n" +
                    "## service\n" +
                    "`%s` %s \n" +
                    "\n" +
                    "### method\n" +
                    "\n" +
                    "%s.\n" +
                    "\n" +
                    "### isSynchronization\n" +
                    "%b\n" +
                    "\n" +
                    "### 结果\n" +
                    "```json\n" +
                    "  %s\n" +
                    "```";;
            return String.format(template,index.toString(),comment,sys_manager,sys_service,method,is_sync,result);
        }
    }


}
