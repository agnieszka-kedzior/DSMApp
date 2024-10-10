package com.example.dsmapp.Tasks.AssignedSingleTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.SingleImageViewer;

import org.json.JSONObject;


public class AssignedSingleTask extends Fragment {

    private String mToken;
    private String mTaskId;

    private Toolbar toolbar;
    private ImageView taskImage;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private TextView text5;
    private TextView text7;
    private TextView text8;
    private TextView text9;
    private TextView text10;

    private RelativeLayout layoutReopen;
    private TextView reopenText;

    private Bitmap image;
    private String imageId;
    private String userCreatedBy;
    private String userAssigned;

    private String taskExpDate;
    private String taskCreationDate;
    private String taskClosedDate;
    private String taskStatus;
    private String taskStep;
    private String taskType;
    private String taskActionStatus;
    private String taskTitle;
    private String taskDesc;
    private String taskActionComment;
    private String reopenReason;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mToken = getArguments().getString("access_token");
            mTaskId = getArguments().getString("task_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_task, container,false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        taskImage = (ImageView) view.findViewById(R.id.taskImage);
        text1 = (TextView) view.findViewById(R.id.text1);
        text2 = (TextView) view.findViewById(R.id.text2);
        text3 = (TextView) view.findViewById(R.id.text3);
        text4 = (TextView) view.findViewById(R.id.text4);
        text5 = (TextView) view.findViewById(R.id.text5);
        text7 = (TextView) view.findViewById(R.id.text7);
        text8 = (TextView) view.findViewById(R.id.text8);
        text9 = (TextView) view.findViewById(R.id.text9);
        text10 = (TextView) view.findViewById(R.id.text10);

        layoutReopen = (RelativeLayout) view.findViewById(R.id.layoutReopen);
        reopenText = (TextView) view.findViewById(R.id.reason);


        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext());
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        taskImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = SingleImageViewer.newInstance(mToken, imageId);
                FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public static AssignedSingleTask newInstance(String mToken, String mTaskId){
        AssignedSingleTask fragmentTasks = new AssignedSingleTask();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("task_id", mTaskId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private Context ctx;
        private String res;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            res = clientDataSource.getOneTask(mTaskId);

            try {
                JSONObject jsonObj = new JSONObject(res);

                taskStatus = jsonObj.getString("taskStatus");
                taskStep = jsonObj.getString("taskStep");
                taskType = jsonObj.getString("taskType");
                taskActionStatus = jsonObj.getString("taskActionStatus");
                taskExpDate = jsonObj.getString("tasksExpirationDate");
                taskCreationDate = jsonObj.getString("tasksCreationDate");
                taskClosedDate = jsonObj.getString("taskClosedDate");
                taskTitle = jsonObj.getString("taskTitle");
                taskDesc = jsonObj.getString("taskDesc");
                taskActionComment = jsonObj.getString("taskActionComment");
                reopenReason = jsonObj.getString("taskReopenReason");

                String id = clientDataSource.getTaskImageId(mTaskId);
                String username = clientDataSource.getTaskUserName(mTaskId);
                String createdBy = clientDataSource.getCreatedBy(mTaskId);
                Bitmap myBitmap = clientDataSource.getImageFirstFrame(id);

                image = myBitmap;
                imageId = id;
                userAssigned = username;
                userCreatedBy = createdBy;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            toolbar.setSubtitle(taskTitle);
            taskImage.setImageBitmap(image);
            text1.setText(taskStatus);
            text2.setText(taskStep);
            text3.setText(taskType);
            text4.setText(taskActionStatus);
            if(taskDesc == "null"){
                String text = "None";
                text5.setText(text);
            }else{
                text5.setText(taskDesc);
            }
            text7.setText(userCreatedBy);
            text8.setText(userAssigned);
            text9.setText(taskCreationDate);
            text10.setText(taskExpDate);
            reopenText.setText(reopenReason);

            if( reopenReason != "null"){
                layoutReopen.setVisibility(View.VISIBLE);
            }

            setFrag(taskStep);
        }
    }

    public void setFrag(String step){

        Fragment frag = null;

        switch (step){
            case "ACCEPT":
                frag = FragActionAccept.newInstance(mToken,mTaskId);
                break;
            case "ASSIGN":
                frag = FragActionAssign.newInstance(mToken,mTaskId);
                break;
            case "ANALYSIS":
                frag = FragActionAnalysis.newInstance(mToken,mTaskId);
                break;
            case "SUBMIT":
                frag = FragActionSubmit.newInstance(mToken, mTaskId);
                break;
            case "REVIEW":
                frag = FragActionReview.newInstance(mToken, mTaskId);
                break;
            case "REOPEN":
                frag = FragActionSubmit.newInstance(mToken, mTaskId);
                break;
        }

        getChildFragmentManager().beginTransaction().replace(R.id.layoutCustom, frag).commit();
    }
}
