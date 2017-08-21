package com.gpp.music_30;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/8/20.
 */

class TypeTwoViewholder extends BaseViewHolder {
    public ImageView icon;
    public Context context;
    public TypeTwoViewholder(View item,Context context) {
        super(item);
        icon = itemView.findViewById(R.id.entryImage);
        this.context=context;
    }
    @Override
    public void bindHolder(DataModel dataModel) {
        icon.setImageBitmap(dataModel.icon);
    }
}
