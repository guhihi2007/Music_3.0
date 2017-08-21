package com.gpp.music_30;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class Fragment_Home extends Fragment implements MyApplication.DataCallBack {
    private RecyclerView recyclerView;
    public List<DataModel> list;
    public RecyclerAdapter adapter;
    public MyApplication application;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.home_recycler);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);//2列
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = recyclerView.getAdapter().getItemViewType(position);
                if (type == DataModel.TYPE_1) {
                    return 1;//返回1，表示占1列
                } else {
                    return gridLayoutManager.getSpanCount();//count=2,占2列
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new RecyclerAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanSize = params.getSpanSize();
                int spanIndex = params.getSpanIndex();
                outRect.top = 20;
//                if (spanSize != gridLayoutManager.getSpanCount()) {
                    outRect.left = 10;
                    outRect.right = 10;
//                }
            }
        });
        adapter.addData(list);
        adapter.notifyDataSetChanged();
//        application = MyApplication.castFrom(getContext());
//        application.setCallBack(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            DataModel data = new DataModel();
            int type;
            if ((i > 5 && i < 8) || (i > 12 && i < 18)) {
                type = 2;
            } else {
                type = 1;
            }
            data.type = type;
            data.name = "name:" + i;
            data.content = "content:" + i;
            data.icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            data.smallIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            list.add(data);
        }
    }

    @Override
    public void result(List<DataModel> list) {
//        adapter.addData(list);
//        adapter.notifyDataSetChanged();
    }
}
