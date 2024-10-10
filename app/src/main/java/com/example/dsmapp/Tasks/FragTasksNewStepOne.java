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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dsmapp.Adapters.TaskImageAdapter;
import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragTasksNewStepOne extends Fragment {

    private String mToken;
    private String mChosenImageId;
    private ProgressBar progressBar;
    private Button nextTaskButton;
    private Button cancelTaskButton;
    private GridView gridView;

    TaskImageAdapter taskImageAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mToken = getArguments().getString("access_token");
            mChosenImageId = getArguments().getString("img_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasks_new_one, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingTasks);
        nextTaskButton = (Button) view.findViewById(R.id.nextStep);
        cancelTaskButton = (Button) view.findViewById(R.id.cancelTask);
        gridView = (GridView) view.findViewById(R.id.choseImageGrid);

        cancelTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = FragmentTasks.newInstance(mToken, 1);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        nextTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChosenImageId = taskImageAdapter.getCurrentImageId();
                if(taskImageAdapter.getCurrentImageId() == null){
                    Toast.makeText(getActivity(),"Please select image",Toast.LENGTH_LONG).show();
                }else if (taskImageAdapter.getCurrentImageId() != null){
                    Fragment fragment = FragTasksNewStepTwo.newInstance(mToken, mChosenImageId);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frag_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext());
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    public static FragTasksNewStepOne newInstance(String mToken, String chosenImageId){
        FragTasksNewStepOne fragmentTasks = new FragTasksNewStepOne();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("img_id", chosenImageId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String response;
        private ArrayList<Bitmap> imageList = new ArrayList<>();
        private Context ctx;
        private List<String> listImageIds = new ArrayList<>();
        private List<String> listFramesNr = new ArrayList<>();
        private List<String> listUploadDate = new ArrayList<>();
        private List<String> listPatientName = new ArrayList<>();
        private List<String> listImgName = new ArrayList<>();

        ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            response = clientDataSource.getListOfUserImages();

            try {
                JSONArray jsonArr = new JSONArray(response);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String id = jsonObj.getString("imageId");
                    String frames = jsonObj.getString("imageNumberOfFrames");
                    String date = jsonObj.getString("imageUploadDate");
                    String name = jsonObj.getString("imageName");

                    String patient = clientDataSource.getImagePatientName(id);
                    Bitmap myBitmap = clientDataSource.getImageFirstFrame(id);

                    imageList.add(myBitmap);
                    listImageIds.add(id);
                    listFramesNr.add(frames);
                    listUploadDate.add(date);
                    listPatientName.add(patient);
                    listImgName.add(name);
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
            taskImageAdapter = new TaskImageAdapter(this.imageList, this.ctx, mToken, this.listImageIds, mChosenImageId, this.listFramesNr,  this.listUploadDate, this.listPatientName, this.listImgName);
            gridView.setAdapter(taskImageAdapter);

        }
    }

}
