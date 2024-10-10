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

public class ImageViewerEditReview extends Fragment {

    private String mToken;
    private String mTaskId;
    private String mImageId;
    private String mFramesNumber;
    private ProgressBar progressBar;
    private ImageView imageView;

    private ImageButton cancelEdit;
    private ImageButton taskInfo;
    private ImageButton sentComment;
    private ImageButton editComment;
    private FloatingActionButton next;
    private FloatingActionButton back;
    private TextView frameInfo;
    private TextView frameText;
    private TextView frameTextNr;
    private EditText commentText;
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

    private LinearLayout editorLayout;
    private LinearLayout reviewLayout;

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
        View view = inflater.inflate(R.layout.single_task_image_edit, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingPicture);
        imageView = (ImageView) view.findViewById(R.id.myPicture);
        cancelEdit = (ImageButton) view.findViewById(R.id.cancelEdit);
        sentComment = (ImageButton) view.findViewById(R.id.saveFrameComment);
        editComment = (ImageButton) view.findViewById(R.id.editComment);
        next = (FloatingActionButton) view.findViewById(R.id.imageNext);
        back = (FloatingActionButton) view.findViewById(R.id.imageBack);
        frameInfo = (TextView) view.findViewById(R.id.frameNumber);
        frameText = (TextView) view.findViewById(R.id.frame);
        frameTextNr = (TextView) view.findViewById(R.id.frameNr);
        commentText = (EditText) view.findViewById(R.id.textFrameDesc);
        taskInfo = (ImageButton) view.findViewById(R.id.taskInfo);
        editorLayout = (LinearLayout) view.findViewById(R.id.layEdit);
        reviewLayout = (LinearLayout) view.findViewById(R.id.layReview);
        commentDescText = (TextView) view.findViewById(R.id.textFrameDescText);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pAttached = new PhotoViewAttacher(imageView);
                pAttached.update();
                return false;
            }
        });

        sentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Save")
                        .setMessage("Frame: "+frameNumberList.get(currentFramePosition)+ "\n\nComment: "+commentText.getText())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    ClientDataSource clientDataSource = new ClientDataSource(mToken);
                                    AsyncTask<Void, Void, String> execute = new EditedCommentExecuteNetworkOperation(clientDataSource, getContext());
                                    execute.execute();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(),"Comment save canceled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });

        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorLayout.setVisibility(View.INVISIBLE);
                reviewLayout.setVisibility(View.VISIBLE);
            }
        });

        editComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorLayout.setVisibility(View.VISIBLE);
                reviewLayout.setVisibility(View.INVISIBLE);
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
                    commentText.setText(frameCommentList.get(currentFramePosition));
                } else {
                    Toast.makeText(getContext(),"Image last frame is displayed", Toast.LENGTH_SHORT).show();
                    currentFramePosition--;
                }
                frame = 1+ currentFramePosition;
                textFrame = "Frame: "+frame;
                frameInfo.setText(textFrame);
                textFrame = "F#"+frame;
                frameText.setText(textFrame);
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
                    commentText.setText(frameCommentList.get(currentFramePosition));
                }
                frame = 1+ currentFramePosition;
                textFrame = "Frame: "+frame;
                frameInfo.setText(textFrame);
                textFrame = "F#"+frame;
                frameText.setText(textFrame);
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

    public static ImageViewerEditReview newInstance(String mToken, String mTaskId){
        ImageViewerEditReview fragmentTasks = new ImageViewerEditReview();
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
            commentText.setText(frameCommentList.get(currentFramePosition));

            frame = 1+ currentFramePosition;
            textFrame = "Frame: "+frame;
            frameInfo.setText(textFrame);
            textFrame = "F#"+frame;
            frameText.setText(textFrame);
            frameTextNr.setText(textFrame);
        }
    }

    public class EditedCommentExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private Context ctx;

        public EditedCommentExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            clientDataSource.addFrameComment(mTaskId,frameIdList.get(currentFramePosition),commentText.getText().toString());
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast toast = Toast.makeText(getActivity(),"Comment saved",Toast.LENGTH_SHORT);
            toast.show();
            if(toast == null || toast.getView().getWindowVisibility() != View.VISIBLE){
                Fragment fragment = ImageViewerEditReview.newInstance(mToken,mTaskId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }
}
