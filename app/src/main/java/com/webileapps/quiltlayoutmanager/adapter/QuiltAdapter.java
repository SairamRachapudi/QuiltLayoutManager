package com.webileapps.quiltlayoutmanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import com.webileapps.quiltlayoutmanager.util.CellView;
import java.util.List;

/**
 * Created by sairam on 24/3/17.
 */

public abstract class QuiltAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    private final Context mContext;

    public QuiltAdapter(Context context){
        this.mContext = context;
    }

    protected abstract List<Integer> getProritiesList();
    protected abstract void onBindData(T holder, int position);

    @Override
    public abstract T onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(T holder, int position) {
        generateNewParams(holder,position);
        onBindData(holder,position);
    }

    private void generateNewParams(RecyclerView.ViewHolder holder, int position) {
        if(getProritiesList() ==null){
            return;
        }

        int priority = getProritiesList().get(position);
        RecyclerView.LayoutParams params;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int partSize = width/3;

        if(priority>=1 && priority <=3){
            params = new RecyclerView.LayoutParams(partSize,partSize);
        }else if(priority>=4 && priority <=6){
            params = new RecyclerView.LayoutParams(partSize,2 * partSize);
        }else if(priority>=7 && priority <=8){
            params = new RecyclerView.LayoutParams(2 * partSize,partSize);
        }
        else{
            params = new RecyclerView.LayoutParams(2 * partSize,2 * partSize);
        }

        CellView cellView = new CellView(params.width,params.height,partSize);
        holder.itemView.setTag(cellView);

        holder.itemView.setLayoutParams(params);
    }

    @Override
    public abstract int getItemCount();
}
