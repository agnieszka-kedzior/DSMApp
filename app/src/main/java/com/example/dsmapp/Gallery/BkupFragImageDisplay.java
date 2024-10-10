package com.example.dsmapp.Gallery;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;

public class BkupFragImageDisplay extends Fragment {

    private String mToken;
    private String mImageId;
    private ProgressBar progressBar;
    private ImageView imageView;


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
        View view = inflater.inflate(R.layout.frag_display_img, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingDisplay);
        imageView = (ImageView) view.findViewById(R.id.imageDisplay);

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource);
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    public static BkupFragImageDisplay newInstance(String mToken, String imageId){
        BkupFragImageDisplay fragmentTasks = new BkupFragImageDisplay();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("image", imageId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String response;
        private Bitmap image;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource) {
            this.clientDataSource = clientDataSource;
        }

        @Override
        protected String doInBackground(Void... voids) {
            response = clientDataSource.getListOfUserImages();

            try {
                Bitmap myBitmap = clientDataSource.getImageFirstFrame(mImageId+1);
                image = myBitmap;

            } catch (Exception e){
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
            imageView.setImageBitmap(image);
        }
    }
}
