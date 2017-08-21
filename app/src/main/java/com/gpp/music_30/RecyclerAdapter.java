package com.gpp.music_30;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/20.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DataModel> list = new ArrayList();
    private LayoutInflater layoutInflater;
    private Context context;

    public RecyclerAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case DataModel.TYPE_1:
                return new TypeOneViewholder(layoutInflater.inflate(R.layout.type_1_layout, parent, false),context );
            case DataModel.TYPE_2:
                return new TypeTwoViewholder(layoutInflater.inflate(R.layout.type_2_layout, parent, false),context);
        }
        return null;
    }

    public void addData(List<DataModel> models) {
        list.addAll(models);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindHolder(list.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).type;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
