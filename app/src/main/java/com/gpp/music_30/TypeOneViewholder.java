package com.gpp.music_30;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/8/20.
 */

public class TypeOneViewholder extends BaseViewHolder {
    public ImageView icon, small;
    public TextView name, content;
    private Context context;

    public TypeOneViewholder(View itemView, Context context) {
        super(itemView);
        icon = itemView.findViewById(R.id.imageIcon);
        small = itemView.findViewById(R.id.imageSmall);
        name = itemView.findViewById(R.id.name);
        content = itemView.findViewById(R.id.content);
        this.context = context;
    }

    @Override
    public void bindHolder(DataModel dataModel) {
        icon.setImageBitmap(dataModel.icon);
        small.setImageBitmap(dataModel.smallIcon);
        name.setText(dataModel.name);
        content.setText(dataModel.content);
    }

}
