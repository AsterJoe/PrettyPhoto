package com.aster.xyzhou.prettyphoto.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aster.xyzhou.prettyphoto.MainActivity;
import com.aster.xyzhou.prettyphoto.R;

import java.util.List;

/**
 * Created by Administrator on 2017/5/7 0007.
 */

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MyViewHolder>
    implements View.OnClickListener{

    public Context mContext;
    public List<String> mData;
    public Bitmap mBitmap;
    public dealBitmap mDealBitmap;
    public MenuItemAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.feature_item, null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mFeatureName.setText(mData.get(position));

        holder.mFeatureName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feature_name:
                if (((TextView)v).getText().toString().equals("底片效果")) {
                    mDealBitmap.addEffect(MainActivity.REVERSE_BITMAP);
                } else if (((TextView)v).getText().toString().equals("灰度效果")) {
                    mDealBitmap.addEffect(MainActivity.GRAY_EFFECT);
                } else if (((TextView)v).getText().toString().equals("怀旧效果")) {
                    mDealBitmap.addEffect(MainActivity.OLD_EFFECT);
                } else if (((TextView)v).getText().toString().equals("去色效果")) {
                    mDealBitmap.addEffect(MainActivity.QUSHE);
                } else if (((TextView)v).getText().toString().equals("高饱和度")) {
                    mDealBitmap.addEffect(MainActivity.GAOBAOHE);
                } else if (((TextView)v).getText().toString().equals("浮雕效果")) {
                    mDealBitmap.addEffect(MainActivity.FUDIAO);
                } else if (((TextView)v).getText().toString().equals("旗帜效果")) {
                    mDealBitmap.addEffect(MainActivity.FLAG);
                }
        }
    }

    public void setDealBitmapListener(dealBitmap deal) {
        mDealBitmap = deal;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mFeatureName;
        public MyViewHolder(View itemView) {
            super(itemView);
            mFeatureName = (TextView) itemView.findViewById(R.id.feature_name);
        }
    }

    public interface dealBitmap {
        void addEffect(int effectName);
    }
}
