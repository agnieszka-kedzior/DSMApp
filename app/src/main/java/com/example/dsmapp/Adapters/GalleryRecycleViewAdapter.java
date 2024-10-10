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

public class GalleryRecycleViewAdapter extends RecyclerView.Adapter<GalleryRecycleViewAdapter.ViewHolder>{

    private Context ctx;
    private OnGalleryClickListener onGalleryClickListener;

    private ArrayList<Bitmap> mImageList;
    private List<String> mGrantedAuthDates;
    private List<String> mUserNameList;

    private String mText;

    public GalleryRecycleViewAdapter(Context ctx, OnGalleryClickListener onGalleryClickClickListener ,ArrayList<Bitmap> mImageList, List<String> mGrantedAuthDates, List<String> mUserNameList, String text) {
        this.ctx = ctx;
        this.onGalleryClickListener = onGalleryClickClickListener;
        this.mImageList = mImageList;
        this.mGrantedAuthDates =  mGrantedAuthDates;
        this.mUserNameList = mUserNameList;
        this.mText = text;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_custom_recycle_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view, onGalleryClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Glide.with(ctx)
                .asBitmap()
                .load(mImageList.get(position))
                .into(viewHolder.imageView);

        viewHolder.textViewDate.setText(mGrantedAuthDates.get(position));
        viewHolder.textView.setText(mUserNameList.get(position));
        viewHolder.text.setText(mText);

    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnGalleryClickListener onGalleryClickListener;

        ImageView imageView;
        TextView textView;
        TextView textViewDate;
        TextView text;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView, OnGalleryClickListener onGalleryClickListener){
            super(itemView);


            imageView = (ImageView) itemView.findViewById(R.id.cardImage);
            textView = (TextView) itemView.findViewById(R.id.cardText);
            text = (TextView) itemView.findViewById(R.id.textWhat);
            textViewDate = (TextView) itemView.findViewById(R.id.cardDateText);
            relativeLayout = itemView.findViewById(R.id.cardLayout);

            this.onGalleryClickListener = onGalleryClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onGalleryClickListener.onGalleryItemClick(getAdapterPosition());
        }
    }

    public interface OnGalleryClickListener{
        void onGalleryItemClick(int position);
    }
}
