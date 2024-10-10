package com.example.dsmapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dsmapp.R;

import java.util.ArrayList;
import java.util.List;

public class TaskImageAdapter extends BaseAdapter {

    private ArrayList<Bitmap> imageList;
    private String mToken;
    private List<String> listImageIds;
    private List<String> listFramesNr;
    private List<String> listUploadDate;
    private List<String> listPatientName;
    private List<String> listImgName;

    private List<Integer> checkBoxVisibility= new ArrayList<>();
    private List<Boolean> checkBoxChecked= new ArrayList<>();
    private String currentImageId;
    private String currentImageName;
    Context ctx;

    public TaskImageAdapter(
            ArrayList<Bitmap> imageList
            , Context ctx
            , String token
            , List<String> idList
            , String mImgId
            , List<String> listFramesNr
            , List<String> listUploadDate
            , List<String> listPatientName
            , List<String> listImgName) {
        this.imageList=imageList;
        this.ctx = ctx;
        this.mToken = token;
        this.listImageIds = idList;
        this.currentImageId = mImgId;
        this.listFramesNr = listFramesNr;
        this.listUploadDate = listUploadDate;
        this.listPatientName = listPatientName;
        this.listImgName = listImgName;
        if(mImgId == null){
            for (int i = 0; i < listImageIds.size(); i++){
                checkBoxVisibility.add(i,View.VISIBLE);
                checkBoxChecked.add(i,false);
            }
        } else {
            for (int i = 0; i < listImageIds.size(); i++){
                if(listImageIds.get(i) == mImgId){
                    checkBoxVisibility.add(i, View.VISIBLE);
                    checkBoxChecked.add(i, true);
                } else {
                    checkBoxVisibility.add(i, View.INVISIBLE);
                    checkBoxChecked.add(i, false);
                }
            }
        }

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
            gridView = inflater.inflate(R.layout.tasks_new_one_image_layout,null);
        }

        ImageView img = (ImageView)gridView.findViewById(R.id.myImage);
        TextView text = (TextView) gridView.findViewById(R.id.imageText);
        final CheckBox checkBox = (CheckBox) gridView.findViewById(R.id.choseImageCheckBox);

        img.setImageBitmap(imageList.get(position));
        text.setText(listImageIds.get(position));
        checkBox.setVisibility(checkBoxVisibility.get(position));
        checkBox.setChecked(checkBoxChecked.get(position));

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBoxChecked.get(position)) {
                    for (int i = 0; i < listImageIds.size(); i++){
                        if( i != position ){
                            checkBoxVisibility.set(i,View.INVISIBLE);
                            checkBoxChecked.set(i,false);
                        } else {
                            checkBoxVisibility.set(i,View.VISIBLE);
                            checkBoxChecked.set(i,true);
                            currentImageId = listImageIds.get(i);
                            currentImageName = listImgName.get(i);
                        }
                    }
                    notifyDataSetChanged();
                } else {
                    for (int i = 0; i < listImageIds.size(); i++){
                        checkBoxVisibility.set(i,View.VISIBLE);
                        checkBoxChecked.set(i,false);
                    }
                    currentImageId = null;
                    currentImageName = null;
                    notifyDataSetChanged();
                }
            }
        });

        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                        .setTitle("Information")
                        .setMessage("Patient name: " +  listPatientName.get(position)
                                +"\nImage name: " + listImgName.get(position)
                                +"\nNumber of frames: " + listFramesNr.get(position)
                                +"\nUpload date: " + listUploadDate.get(position))
                        .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return false;
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    for (int i = 0; i < listImageIds.size(); i++){
                        if( i != position ){
                            checkBoxVisibility.set(i,View.INVISIBLE);
                            checkBoxChecked.set(i,false);
                        } else {
                            checkBoxVisibility.set(i,View.VISIBLE);
                            checkBoxChecked.set(i,true);
                            currentImageId = listImageIds.get(i);
                            currentImageName = listImgName.get(i);
                        }
                    }
                    notifyDataSetChanged();
                } else {
                    for (int i = 0; i < listImageIds.size(); i++){
                        checkBoxVisibility.set(i,View.VISIBLE);
                        checkBoxChecked.set(i,false);
                    }
                    currentImageId = null;
                    currentImageName = null;
                    notifyDataSetChanged();
                }
            }
        });

        return gridView;
    }

    public String getCurrentImageId(){
        return currentImageId;
    }

    public String getCurrentImageName(){ return currentImageName;}
}
