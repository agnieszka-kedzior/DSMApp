package com.example.dsmapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dsmapp.R;

import java.util.ArrayList;
import java.util.List;

public class TaskRecycleViewAdapter extends RecyclerView.Adapter<TaskRecycleViewAdapter.ViewHolder>{

    private Context ctx;
    private OnTaskClickListener onTaskClickListener;

    private ArrayList<Bitmap> mImageList;
    private List<String> imageIdList;
    private List<String> taskIdList;
    private List<String> taskTitleList;
    private List<String> taskStatusList;
    private List<String> taskDetailsList;
    private List<String> tasksExpDateList;
    private List<String> userList;
    private String text3;
    private String text4;


    public TaskRecycleViewAdapter(Context ctx, OnTaskClickListener onTaskClickListener, ArrayList<Bitmap> mImageList, List<String> imageIdList, List<String> taskIdListId, List<String> title, List<String> status, List<String> det, List<String> date, List<String> user, String text3, String text4) {
        this.ctx = ctx;
        this.onTaskClickListener = onTaskClickListener;
        this.mImageList = mImageList;
        this.imageIdList = imageIdList;
        this.taskIdList = taskIdListId;
        this.taskTitleList = title;
        this.taskStatusList = status;
        this.taskDetailsList = det;
        this.tasksExpDateList = date;
        this.userList = user;
        this.text3 = text3;
        this.text4 = text4;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tasks_custom_recycle_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view, onTaskClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Glide.with(ctx)
                .asBitmap()
                .load(mImageList.get(position))
                .into(viewHolder.imageView);

        String name;
        if (userList.get(position).length() >= 7){
            name = userList.get(position).substring(0,7)+"..";
        } else {
            name = userList.get(position);
        }

        viewHolder.title.setText(taskTitleList.get(position));
        viewHolder.textView1.setText(taskStatusList.get(position));
        viewHolder.textView2.setText(taskDetailsList.get(position));
        viewHolder.textView3.setText(text3);
        viewHolder.textView4.setText(text4);
        viewHolder.textView5.setText(name);
        viewHolder.textView6.setText(tasksExpDateList.get(position));
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        RelativeLayout relativeLayout;
        OnTaskClickListener onTaskClickListener;

        TextView title;
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;
        TextView textView6;

        public ViewHolder(View itemView, OnTaskClickListener onTaskClickListener){
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.taskImage);
            relativeLayout = itemView.findViewById(R.id.taskLayout);
            this.onTaskClickListener = onTaskClickListener;

            title = (TextView) itemView.findViewById(R.id.taskTitle);
            textView1 = (TextView) itemView.findViewById(R.id.text1);
            textView2 = (TextView) itemView.findViewById(R.id.text2);
            textView3 = (TextView) itemView.findViewById(R.id.text3);
            textView4 = (TextView) itemView.findViewById(R.id.text4);
            textView5 = (TextView) itemView.findViewById(R.id.text5);
            textView6 = (TextView) itemView.findViewById(R.id.text6);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTaskClickListener.onTaskClick(getAdapterPosition());
        }
    }

    public interface OnTaskClickListener{
        void onTaskClick(int position);
    }
}
