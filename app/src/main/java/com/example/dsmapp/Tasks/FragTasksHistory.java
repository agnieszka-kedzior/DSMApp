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
import android.widget.ProgressBar;

import com.example.dsmapp.Adapters.TaskRecycleViewAdapter;
import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.Tasks.HistorySingleTask.HistorySingleTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragTasksHistory extends Fragment implements TaskRecycleViewAdapter.OnTaskClickListener{

    private String mToken;
    private ProgressBar progressBar;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "FragTasksHistory";

    private ArrayList<Bitmap> imageList = new ArrayList<>();

    private List<String> imageIdList = new ArrayList<>();
    private List<String> userAssignedList = new ArrayList<>();

    private List<String> tasksIdList = new ArrayList<>();
    private List<String> taskStatusList = new ArrayList<>();
    private List<String> taskActionList = new ArrayList<>();
    private List<String> taskTypeList = new ArrayList<>();
    private List<String> tasksClosedDateList = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.tasks_history, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingTasksHistory);
        recyclerView = (RecyclerView) view.findViewById(R.id.rec_hist_tasks);

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext(), this);
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    public static FragTasksHistory newInstance(String mToken, int sectionNumber){
        FragTasksHistory fragmentTasks = new FragTasksHistory();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    @Override
    public void onTaskClick(int position) {
        Fragment fragment = HistorySingleTask.newInstance(mToken, tasksIdList.get(position));
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
            res = clientDataSource.getClosedTasks();

            try {
                JSONArray jsonArr = new JSONArray(res);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String taskId = jsonObj.getString("tasksId");
                    String taskStatus = jsonObj.getString("taskStatus");
                    String taskActionStatus = jsonObj.getString("taskActionStatus");
                    String taskType = jsonObj.getString("taskType");
                    String taskDate = jsonObj.getString("taskClosedDate");
                    String taskTitle = jsonObj.getString("taskTitle");

                    String id = clientDataSource.getTaskImageId(taskId);
                    String username = clientDataSource.getCompletedBy(taskId);
                    Bitmap myBitmap = clientDataSource.getImageFirstFrame(id);

                    imageList.add(myBitmap);
                    imageIdList.add(id);
                    userAssignedList.add(username);

                    tasksIdList.add(taskId);
                    taskStatusList.add(taskStatus);
                    taskActionList.add(taskActionStatus);
                    taskTypeList.add(taskType);
                    tasksClosedDateList.add(taskDate);
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
            taskRecycleViewAdapter = new TaskRecycleViewAdapter(ctx, onTaskClickListener, imageList, imageIdList, tasksIdList, taskTitleList, taskStatusList, taskTypeList, tasksClosedDateList, taskActionList, "",  "");
            recyclerView.setAdapter(taskRecycleViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        }
    }
}
