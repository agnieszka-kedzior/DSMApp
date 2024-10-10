package com.example.dsmapp.Tasks.HistorySingleTask;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.dsmapp.Adapters.TaskHistoryRecycleViewAdapter;
import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.SingleImageViewer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HistorySingleTask extends Fragment {

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

    private Bitmap image;
    private String imageId;
    private String userCreatedBy;
    private String userCompletedBy;

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

    private List<String> histIdList = new ArrayList<>();
    private List<String> histTypeList = new ArrayList<>();
    private List<String> histDateList = new ArrayList<>();
    private List<String> histCommentList = new ArrayList<>();

    private TaskHistoryRecycleViewAdapter taskHistoryRecycleViewAdapter;
    private RecyclerView recyclerView;

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
        View view = inflater.inflate(R.layout.history_single_task, container,false);

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

        recyclerView = (RecyclerView) view.findViewById(R.id.rec_task_hist);

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

    public static HistorySingleTask newInstance(String mToken, String mTaskId){
        HistorySingleTask fragmentTasks = new HistorySingleTask();
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
        private String hist;

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


                String id = clientDataSource.getTaskImageId(mTaskId);
                String username = clientDataSource.getCompletedBy(mTaskId);
                String createdBy = clientDataSource.getCreatedBy(mTaskId);
                Bitmap myBitmap = clientDataSource.getImageFirstFrame(id);

                image = myBitmap;
                imageId = id;
                userCompletedBy = username;
                userCreatedBy = createdBy;

            } catch (Exception e) {
                e.printStackTrace();
            }

            hist = clientDataSource.getTaskHistory(mTaskId);

            try {
                JSONArray jsonArr = new JSONArray(hist);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String histId = jsonObj.getString("tasksHistId");
                    String histType = jsonObj.getString("taskHistType");
                    String histDate = jsonObj.getString("taskHistDate");
                    String histComment = jsonObj.getString("taskHistComment");

                    histIdList.add(histId);
                    histTypeList.add(histType);
                    histDateList.add(histDate);
                    histCommentList.add(histComment);
                }
            }catch (Exception e){
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
            text8.setText(userCompletedBy);
            text9.setText(taskCreationDate);
            text10.setText(taskClosedDate);

            taskHistoryRecycleViewAdapter = new TaskHistoryRecycleViewAdapter(ctx, histTypeList, histDateList, histCommentList);
            recyclerView.setAdapter(taskHistoryRecycleViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));

        }
    }

}
