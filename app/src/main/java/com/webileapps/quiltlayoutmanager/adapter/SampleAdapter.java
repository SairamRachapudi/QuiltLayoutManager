package com.webileapps.quiltlayoutmanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.webileapps.quiltlayoutmanager.R;

import java.util.List;

/**
 * Created by sairam on 8/3/17.
 */

public class SampleAdapter extends QuiltAdapter<SampleAdapter.MyViewHolder> {
    private List<String> data;
    private List<Integer> priotityList;

    public SampleAdapter(Context context) {
        super(context);
    }

    @Override
    protected List<Integer> getProritiesList() {
        return priotityList;
    }

    @Override
    protected void onBindData(SampleAdapter.MyViewHolder holder, int position) {
        holder.articleTitle.setText(data.get(position));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data == null?0:data.size();
    }

    public void setData(List<String> data, List<Integer> priotities) {
        this.data = data;
        this.priotityList = priotities;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView articleTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            articleTitle = (TextView) itemView.findViewById(R.id.article_title);
        }
    }
}
