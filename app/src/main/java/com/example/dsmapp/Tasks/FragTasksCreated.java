package com.example.dsmapp.Tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.dsmapp.Adapters.TaskRecycleViewAdapter;
import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.Tasks.CreatedSingleTask.CreatedSingleTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragTasksCreated extends Fragment  implements TaskRecycleViewAdapter.OnTaskClickListener{

    private String mToken;
    private ProgressBar progressBar;
    private Button addTaskButton;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "FragTasksCreated";

    private ArrayList<Bitmap> imageList = new ArrayList<>();

    private List<String> imageIdList = new ArrayList<>();
    private List<String> userAssignedList = new ArrayList<>();

    private List<String> tasksIdList = new ArrayList<>();
    private List<String> taskStatusList = new ArrayList<>();
    private List<String> taskActionStatusList = new ArrayList<>();
    private List<String> tasksExpDateList = new ArrayList<>();
    private List<String> taskTitleList = new ArrayList<>();

    private TaskRecycleViewAdapter taskRecycleViewAdapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mToken = getArguments().getString("access_token");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasks_created, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingTasksCreated);
        addTaskButton =  (Button) view.findViewById(R.id.addTaskButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.rec_created_tasks);

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext(), this);
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }


        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = FragTasksNewStepOne.newInstance(mToken,null);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        return view;
    }

    public static FragTasksCreated newInstance(String mToken, int sectionNumber){
        FragTasksCreated fragmentTasks = new FragTasksCreated();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    @Override
    public void onTaskClick(int position) {
        Fragment fragment = CreatedSingleTask.newInstance(mToken, tasksIdList.get(position));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private TaskRecycleViewAdapter.OnTaskClickListener onTaskClickListener;
        private Context ctx;
        private String res;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx, TaskRecycleViewAdapter.OnTaskClickListener onTaskClickListener) {
            this.clientDataSource = clientDataSource;
            this.onTaskClickListener = onTaskClickListener;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            res = clientDataSource.getOpenTasks();

            try {
                JSONArray jsonArr = new JSONArray(res);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String taskId = jsonObj.getString("tasksId");
                    String taskStatus = jsonObj.getString("taskStatus");
                    String taskActionStatus = jsonObj.getString("taskActionStatus");
                    String taskExpDate = jsonObj.getString("tasksExpirationDate");
                    String taskTitle = jsonObj.getString("taskTitle");

                    String id = clientDataSource.getTaskImageId(taskId);
                    String username = clientDataSource.getTaskUserName(taskId);
                    Bitmap myBitmap = clientDataSource.getImageFirstFrame(id);

                    imageList.add(myBitmap);
                    imageIdList.add(id);
                    userAssignedList.add(username);

                    tasksIdList.add(taskId);
                    taskStatusList.add(taskStatus);
                    taskActionStatusList.add(taskActionStatus);
                    tasksExpDateList.add(taskExpDate);
                    taskTitleList.add(taskTitle);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            taskRecycleViewAdapter = new TaskRecycleViewAdapter(ctx, onTaskClickListener, imageList, imageIdList, tasksIdList, taskTitleList, taskStatusList, taskActionStatusList, tasksExpDateList, userAssignedList, "Assignee:",  "Due by:");
            recyclerView.setAdapter(taskRecycleViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        }
    }
}
