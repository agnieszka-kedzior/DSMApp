package com.example.dsmapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dsmapp.R;

import java.util.List;

public class ProfileSearchRecycleViewAdapter extends RecyclerView.Adapter<ProfileSearchRecycleViewAdapter.ViewHolder>{

    private Context ctx;
    private OnProfileClickListener onProfileClickListener;

    private List<String> mUserIdList;
    private List<String> mUserNameList;

    public ProfileSearchRecycleViewAdapter(Context ctx, OnProfileClickListener onProfileClickListener , List<String> mUserIdList, List<String> mUserNameList) {
        this.ctx = ctx;
        this.onProfileClickListener = onProfileClickListener;
        this.mUserIdList = mUserIdList;
        this.mUserNameList = mUserNameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.profile_search_recycle_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view, onProfileClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.text.setText(mUserNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserIdList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnProfileClickListener onProfileClickListener;
        TextView text;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView, OnProfileClickListener onProfileClickListener){
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.textName);
            relativeLayout = itemView.findViewById(R.id.cardLayout);

            this.onProfileClickListener = onProfileClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProfileClickListener.onProfileItemClick(getAdapterPosition());
        }
    }

    public interface OnProfileClickListener{
        void onProfileItemClick(int position);
    }
}
