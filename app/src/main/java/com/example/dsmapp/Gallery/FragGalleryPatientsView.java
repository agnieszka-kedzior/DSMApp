package com.example.dsmapp.Gallery;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.dsmapp.Adapters.GalleryImageAdapter;
import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragGalleryPatientsView extends Fragment {

    private String mToken;
    private String mPatient;
    private GridView gridView;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private ImageButton imageButton;
    private String birthDate;
    private String sex;
    private String age;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mToken = getArguments().getString("access_token");
            mPatient = getArguments().getString("pat_name");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_pat_view, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingImages);
        gridView = view.findViewById(R.id.patImageGrid);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        imageButton = (ImageButton) view.findViewById(R.id.imageButton);

        toolbar.setSubtitle(mPatient);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(mPatient)
                        .setMessage("Age: "+age +"\nSex: "+sex +"\nDate of Birth: "+birthDate)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(),"Patient information closed", Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
            }
        });

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext(), mPatient);
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    public static FragGalleryPatientsView newInstance(String mToken, String pat) {
        FragGalleryPatientsView fragmentGallery = new FragGalleryPatientsView();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("pat_name", pat);
        fragmentGallery.setArguments(bundle);
        return fragmentGallery;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String response;
        private String resPatient;
        private List<String> listImageIds = new ArrayList<>();
        private ArrayList<Bitmap> imageList = new ArrayList<>();
        private Context ctx;
        private String pat;


        ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx, String pat){
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
            this.pat = pat;
        }

        @Override
        protected String doInBackground(Void... voids) {
            response = clientDataSource.getListOfUserImagesForPatient(pat);

            try {
                JSONArray jsonArr = new JSONArray(response);

                for (int i = 0; i < jsonArr.length(); i++)
                {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String id = jsonObj.getString("imageId");

                    Bitmap myBitmap = clientDataSource.getImageFirstFrame(id);
                    imageList.add(myBitmap);
                    listImageIds.add(id);
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            resPatient = clientDataSource.getPatientDetails(pat);

            try {
                JSONObject jsonObj = new JSONObject(resPatient);
                birthDate = jsonObj.getString("patientBirthDate");
                sex = jsonObj.getString("patientSex");
                age = jsonObj.getString("patientAge");
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
            GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(this.imageList, this.ctx, mToken, this.listImageIds);
            gridView.setAdapter(galleryImageAdapter);
        }

    }

}
