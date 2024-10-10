package com.example.dsmapp.Tasks.AssignedSingleTask;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewerReview extends Fragment {

    private String mToken;
    private String mTaskId;
    private String mImageId;
    private String mFramesNumber;
    private ProgressBar progressBar;
    private ImageView imageView;

    private ImageButton taskInfo;
    private FloatingActionButton next;
    private FloatingActionButton back;
    private TextView frameTextNr;
    private TextView commentDescText;

    private List<String> frameCommentList = new ArrayList<>();
    private List<String> frameIdList = new ArrayList<>();
    private List<String> frameNumberList = new ArrayList<>();
    private ArrayList<Bitmap> frameImageList = new ArrayList<>();

    private Integer currentFramePosition;
    private Integer frame;
    private String textFrame;
    PhotoViewAttacher pAttached;

    private String taskExpDate;
    private String taskCreationDate;
    private String taskStatus;
    private String taskStep;
    private String taskType;
    private String taskActionStatus;
    private String taskTitle;
    private String taskDesc;
    private String taskActionComment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mToken = getArguments().getString("access_token");
            mTaskId = getArguments().getString("task");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_task_image_review, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingPicture);
        imageView = (ImageView) view.findViewById(R.id.myPicture);
        next = (FloatingActionButton) view.findViewById(R.id.imageNext);
        back = (FloatingActionButton) view.findViewById(R.id.imageBack);
        frameTextNr = (TextView) view.findViewById(R.id.frameNr);
        taskInfo = (ImageButton) view.findViewById(R.id.taskInfo);
        commentDescText = (TextView) view.findViewById(R.id.textFrameDescText);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pAttached = new PhotoViewAttacher(imageView);
                pAttached.update();
                return false;
            }
        });

        taskInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Information")
                        .setMessage("Task type: "+taskType
                                +"\nStep: "+taskStep
                                +"\n\n"+ taskDesc
                                +"\n\nImage nr frames: "+mFramesNumber
                                +"\nDue by: "+taskExpDate)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(),"Information closed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Back to Task", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Fragment fragment = AssignedSingleTask.newInstance(mToken, mTaskId);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.frag_container, fragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
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
                    commentDescText.setText(frameCommentList.get(currentFramePosition));
                } else {
                    Toast.makeText(getContext(),"Image last frame is displayed", Toast.LENGTH_SHORT).show();
                    currentFramePosition--;
                }
                frame = 1+ currentFramePosition;
                textFrame = "F#"+frame;
                frameTextNr.setText(textFrame);
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
                    commentDescText.setText(frameCommentList.get(currentFramePosition));
                }
                frame = 1+ currentFramePosition;
                textFrame = "F#"+frame;
                frameTextNr.setText(textFrame);
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

    public static ImageViewerReview newInstance(String mToken, String mTaskId){
        ImageViewerReview fragmentTasks = new ImageViewerReview();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("task", mTaskId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String res;
        private String com;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource) {
            this.clientDataSource = clientDataSource;
        }

        @Override
        protected String doInBackground(Void... voids) {
            mImageId = clientDataSource.getTaskImageId(mTaskId);
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


            res = clientDataSource.getOneTask(mTaskId);

            try {
                JSONObject jsonObj = new JSONObject(res);

                taskStatus = jsonObj.getString("taskStatus");
                taskStep = jsonObj.getString("taskStep");
                taskType = jsonObj.getString("taskType");
                taskActionStatus = jsonObj.getString("taskActionStatus");
                taskExpDate = jsonObj.getString("tasksExpirationDate");
                taskCreationDate = jsonObj.getString("tasksCreationDate");
                taskTitle = jsonObj.getString("taskTitle");
                taskDesc = jsonObj.getString("taskDesc");
                taskActionComment = jsonObj.getString("taskActionComment");

            } catch (Exception e) {
                e.printStackTrace();
            }

            for(int i = 0; i < frameImageList.size(); i++){
                com = clientDataSource.getTaskFrameComment(mTaskId, frameIdList.get(i));

                if(com.isEmpty()){
                    frameCommentList.add("No comment");
                }else {
                    try {
                        JSONObject jsonObj = new JSONObject(com);
                        String commentTexts = jsonObj.getString("commentText");
                        frameCommentList.add(commentTexts);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
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
            commentDescText.setText(frameCommentList.get(currentFramePosition));

            frame = 1+ currentFramePosition;
            textFrame = "F#"+frame;
            frameTextNr.setText(textFrame);
        }
    }
}
