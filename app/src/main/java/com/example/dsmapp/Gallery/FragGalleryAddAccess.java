package com.example.dsmapp.Gallery;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dsmapp.Adapters.TaskImageAdapter;
import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragGalleryAddAccess extends Fragment {

    private String mToken;
    private String mChosenImageId;
    private String mChosenUserFullName;
    private ProgressBar progressBar;
    private Button addButton;
    private Button cancelButton;
    private GridView gridView;
    private Spinner userSpinner;

    private List<String> listImgName = new ArrayList<>();
    private ArrayList<String> userList = new ArrayList<>();

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
        final View view = inflater.inflate(R.layout.gallery_access_add, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        addButton = (Button) view.findViewById(R.id.addAccess);
        cancelButton = (Button) view.findViewById(R.id.cancel);
        gridView = (GridView) view.findViewById(R.id.choseImageGrid);
        userSpinner = (Spinner) view.findViewById(R.id.spinner_user);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = FragmentGallery.newInstance(mToken, 2);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChosenImageId = taskImageAdapter.getCurrentImageId();
                mChosenUserFullName = userList.get(userSpinner.getSelectedItemPosition());

                if(userList.size() == 0){
                    Toast.makeText(getActivity(),"Please add friends",Toast.LENGTH_LONG).show();
                } else if(mChosenImageId == null | mChosenUserFullName == null){
                    Toast.makeText(getActivity(),"Please select user and image",Toast.LENGTH_LONG).show();
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle("Confirm")
                            .setMessage("User: "+ mChosenUserFullName +
                                    "\nImage: "+ taskImageAdapter.getCurrentImageName() +
                                    "\n\nDo you want to add access?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        ClientDataSource clientDataSource = new ClientDataSource(mToken);
                                        AsyncTask<Void, Void, String> execute = new AddExecuteNetworkOperation(clientDataSource, getContext());
                                        execute.execute();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(),"Canceled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
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

    public static FragGalleryAddAccess newInstance(String mToken, String chosenImageId){
        FragGalleryAddAccess fragmentTasks = new FragGalleryAddAccess();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("img_id", chosenImageId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String response;
        private String res;
        private ArrayList<Bitmap> imageList = new ArrayList<>();
        private Context ctx;
        private List<String> listImageIds = new ArrayList<>();
        private List<String> listFramesNr = new ArrayList<>();
        private List<String> listUploadDate = new ArrayList<>();
        private List<String> listPatientName = new ArrayList<>();

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

            res = clientDataSource.getFriends();

            try {
                JSONArray jsonArr2 = new JSONArray(res);
                for (int i = 0; i < jsonArr2.length(); i++) {
                    JSONObject jsonObj2 = jsonArr2.getJSONObject(i);
                    String name = jsonObj2.getString("userFullName");
                    userList.add(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Users!!!!!!!!! " +userList);

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            userSpinner.setAdapter(spinnerAdapter);

            taskImageAdapter = new TaskImageAdapter(this.imageList, this.ctx, mToken, this.listImageIds, mChosenImageId, this.listFramesNr,  this.listUploadDate, this.listPatientName, listImgName);
            gridView.setAdapter(taskImageAdapter);

        }
    }

    public class AddExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String response;
        private Context ctx;

        AddExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            response = clientDataSource.getUserId(mChosenUserFullName);
            clientDataSource.addAuth(response, mChosenImageId);
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(),"Access added", Toast.LENGTH_SHORT).show();
            Fragment fragment = FragmentGallery.newInstance(mToken, 2);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frag_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
