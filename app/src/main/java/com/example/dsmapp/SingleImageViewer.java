package com.example.dsmapp;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class SingleImageViewer extends Fragment {

    private String mToken;
    private String mImageId;
    private String mFramesNumber;
    private ProgressBar progressBar;
    private ImageView imageView;

    private ImageButton info;
    private FloatingActionButton next;
    private FloatingActionButton back;
    private TextView frameInfo;

    private List<String> frameIdList = new ArrayList<>();
    private List<String> frameNumberList = new ArrayList<>();
    private ArrayList<Bitmap> frameImageList = new ArrayList<>();

    private Integer currentFramePosition;
    private Integer frame;
    private String textFrame;
    PhotoViewAttacher pAttached;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mToken = getArguments().getString("access_token");
            mImageId = getArguments().getString("image");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_single_image_layout, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingPicture);
        imageView = (ImageView) view.findViewById(R.id.myPicture);
        info = (ImageButton) view.findViewById(R.id.imageInfo);
        next = (FloatingActionButton) view.findViewById(R.id.imageNext);
        back = (FloatingActionButton) view.findViewById(R.id.imageBack);
        frameInfo = (TextView) view.findViewById(R.id.frameNumber);


        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pAttached = new PhotoViewAttacher(imageView);
                pAttached.update();
                return false;
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Information")
                        .setMessage("Image has "+mFramesNumber+" frames")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(),"Image information closed", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFramePosition++;
                if(currentFramePosition < frameImageList.size()){
                    imageView.setImageBitmap(frameImageList.get(currentFramePosition));
                } else {
                    Toast.makeText(getContext(),"Image last frame is displayed", Toast.LENGTH_SHORT).show();
                    currentFramePosition--;
                }
                frame = 1+ currentFramePosition;
                textFrame = "Frame: "+frame;
                frameInfo.setText(textFrame);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFramePosition--;
                if(currentFramePosition < 0){
                    Toast.makeText(getContext(),"Image first frame is displayed", Toast.LENGTH_SHORT).show();
                    currentFramePosition++;
                } else {
                    imageView.setImageBitmap(frameImageList.get(currentFramePosition));
                }
                frame = 1+ currentFramePosition;
                textFrame = "Frame: "+frame;
                frameInfo.setText(textFrame);
            }
        });

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource);
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    public static SingleImageViewer newInstance(String mToken, String imageId){
        SingleImageViewer fragmentTasks = new SingleImageViewer();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("image", imageId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String res;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource) {
            this.clientDataSource = clientDataSource;
        }

        @Override
        protected String doInBackground(Void... voids) {

            res = clientDataSource.getImageFrames(mImageId);

            try {
                JSONArray jsonArr = new JSONArray(res);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String frameId = jsonObj.getString("imageFramesId");
                    String frameNumber = jsonObj.getString("imageFrameNumber");

                    Bitmap myBitmap = clientDataSource.getImageByFrame(mImageId,frameNumber);

                    frameIdList.add(frameId);
                    frameNumberList.add(frameNumber);
                    frameImageList.add(myBitmap);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            currentFramePosition = 0;
            mFramesNumber = frameNumberList.get(frameNumberList.size()-1);

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(frameImageList.get(currentFramePosition));

            frame = 1+ currentFramePosition;
            textFrame = "Frame: "+frame;
            frameInfo.setText(textFrame);
        }
    }
}
