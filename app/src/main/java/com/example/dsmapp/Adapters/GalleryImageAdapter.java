package com.example.dsmapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.dsmapp.Gallery.BkupFragImageDisplay;
import com.example.dsmapp.Gallery.BkupShareImage;
import com.example.dsmapp.R;
import com.example.dsmapp.SingleImageViewer;

import java.util.ArrayList;
import java.util.List;

public class GalleryImageAdapter extends BaseAdapter {

    private ArrayList<Bitmap> imageList;
    private List<String> imageIdList;
    private String mToken;
    Context ctx;

    public GalleryImageAdapter(ArrayList<Bitmap> imageList, Context ctx, String token, List<String> imageIdList) {
        this.imageList = imageList;
        this.ctx = ctx;
        this.mToken = token;
        this.imageIdList = imageIdList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View gridView = convertView;

        if(gridView == null){
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.custom_image_layout,null);
        }

        ImageView img = (ImageView)gridView.findViewById(R.id.myImage);
        img.setImageBitmap(imageList.get(position));

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = SingleImageViewer.newInstance(mToken, imageIdList.get(position));
                FragmentManager fragmentManager = ((AppCompatActivity)ctx).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return gridView;
    }
}
