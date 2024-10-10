package com.example.dsmapp.Gallery;

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
import android.widget.ProgressBar;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;

public class BkupShareImage extends Fragment {

    private String mToken;
    private String mImageId;
    private ProgressBar progressBar;
    private Button createButton;
    private Button cancelButton;

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
        View view = inflater.inflate(R.layout.frag_share_img, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingShare);
        createButton = (Button) view.findViewById(R.id.submitAuth);
        cancelButton = (Button) view.findViewById(R.id.cancelAuth);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = FragmentGallery.newInstance(mToken,0);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClientDataSource clientDataSource = new ClientDataSource(mToken);
                    AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource);
                    execute.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        return view;
    }

    public static BkupShareImage newInstance(String mToken, String imageId){
        BkupShareImage fragmentTasks = new BkupShareImage();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("image", imageId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String list;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource) {
            this.clientDataSource = clientDataSource;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
