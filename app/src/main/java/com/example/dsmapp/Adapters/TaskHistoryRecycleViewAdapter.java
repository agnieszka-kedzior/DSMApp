package com.example.dsmapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsmapp.R;

import java.util.ArrayList;
import java.util.List;

public class TaskHistoryRecycleViewAdapter extends RecyclerView.Adapter<TaskHistoryRecycleViewAdapter.ViewHolder>{

    private Context ctx;

    private List<String> mHistTypeList;
    private List<String> mHistDateList;
    private List<String> mHistCommentList;

    public TaskHistoryRecycleViewAdapter(Context ctx, List<String> typeList,  List<String> dateList, List<String> comList) {
        this.ctx = ctx;
        this.mHistTypeList = typeList;
        this.mHistDateList = dateList;
        this.mHistCommentList = comList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_custom_recycle_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.type.setText(mHistTypeList.get(position));
        viewHolder.date.setText(mHistDateList.get(position));
        viewHolder.com.setText(mHistCommentList.get(position));
    }

    @Override
    public int getItemCount() {
        return mHistCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView type;
        TextView date;
        TextView com;

        public ViewHolder(View itemView){
            super(itemView);

            type = (TextView) itemView.findViewById(R.id.histType);
            date = (TextView) itemView.findViewById(R.id.histDate);
            com = (TextView) itemView.findViewById(R.id.histComment);

        }

    }
}
