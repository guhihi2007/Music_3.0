package com.gpp.music_30;

import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/20.
 */

public class MyApplication extends Application {

    public static MyApplication application;
    public List<DataModel> list;
    public static String url = "http://mobilecdngz.kugou.com/api/v3/tag/specialList?tagid=12&plat=0&sort=2&ugc=1&id=68&page=1&pagesize=30";
    public DataCallBack callBack;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        list = new ArrayList<>();
//        new MyAsycTask().execute(url);
        Gpp.e("application:" + list.size());
    }

    public static MyApplication castFrom(Context context) {
        return (MyApplication) (context.getApplicationContext());
    }

    class MyAsycTask extends AsyncTask<String, Void, List<DataModel>> {

        @Override
        protected List<DataModel> doInBackground(String... strings) {
            return requestData(strings[0]);
        }

        @Override
        protected void onPostExecute(List<DataModel> models) {
            list.addAll(models);
//            callBack.result(list);
        }
    }

    private List<DataModel> requestData(String url) {
        String jasonString = "";
        try {
            jasonString = getJasonString(new URL(url).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseJason(jasonString);
    }

    private List<DataModel> parseJason(String jason) {
        List<DataModel> dataList = new ArrayList<>();
        try {
            JSONObject job = new JSONObject(jason);
            JSONObject data = job.getJSONObject("data");
            JSONArray array = data.getJSONArray("info");
            for (int i = 0; i < array.length(); i++) {
                JSONObject info = array.getJSONObject(i);
                DataModel model = new DataModel();
                model.icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                model.smallIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                model.type = 1;
                model.name = info.getString("specialname");
                model.content = info.getString("username");
                Gpp.e("name:" + model.name);
                Gpp.e("content:" + model.content);
                dataList.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private String getJasonString(InputStream is) {
        String jason = "";
        String len;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);
            while ((len = br.readLine()) != null) {
                jason += len;
            }
            return jason;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null) isr.close();
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void setCallBack(DataCallBack callBack) {
        this.callBack = callBack;
    }

    interface DataCallBack {
        void result(List<DataModel> list);
    }
}
