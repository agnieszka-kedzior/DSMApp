package com.example.dsmapp.Gallery;

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
import android.widget.ProgressBar;

import com.example.dsmapp.Adapters.GalleryRecycleViewAdapter;
import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.SingleImageViewer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragGalleryRec extends Fragment implements GalleryRecycleViewAdapter.OnGalleryClickListener{

    private String mToken;
    private ProgressBar progressBar;
    RecyclerView recyclerView;

    private List<String> imageIdList = new ArrayList<>();
    private ArrayList<Bitmap> imageList = new ArrayList<>();
    private List<String> authIdList = new ArrayList<>();
    private List<String> userNameList = new ArrayList<>();
    private List<String> grantedByList = new ArrayList<>();
    private List<String> grantedAuthDates = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.gallery_rec, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingGalleryRec);
        recyclerView = (RecyclerView) view.findViewById(R.id.rec_rec);

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext(), this);
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }


        return view;
    }

    public static FragGalleryRec newInstance(String mToken){
        FragGalleryRec fragmentTasks = new FragGalleryRec();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    @Override
    public void onGalleryItemClick(int position) {
        Fragment fragment = SingleImageViewer.newInstance(mToken, imageIdList.get(position));
        FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private GalleryRecycleViewAdapter.OnGalleryClickListener onGalleryClickListener;
        private String res;
        private Context ctx;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx, GalleryRecycleViewAdapter.OnGalleryClickListener onGalleryClickListener) {
            this.clientDataSource = clientDataSource;
            this.onGalleryClickListener = onGalleryClickListener;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            res = clientDataSource.getRecAuth();

            try {
                JSONArray jsonArr = new JSONArray(res);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String authId = jsonObj.getString("imageAuthId");
                    String grantedDate = jsonObj.getString("grantedDate");
                    String grantedByUserId = jsonObj.getString("grantedByUserID");

                    String id = clientDataSource.getAuthImage(authId);
                    String username = clientDataSource.getAuthUser(authId);
                    Bitmap myBitmap = clientDataSource.getImageFirstFrame(id);

                    String name =  clientDataSource.getName(grantedByUserId);

                    imageList.add(myBitmap);
                    authIdList.add(authId);
                    imageIdList.add(id);
                    grantedAuthDates.add(grantedDate);
                    userNameList.add(username);
                    grantedByList.add(name);
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
            GalleryRecycleViewAdapter adapter = new GalleryRecycleViewAdapter(this.ctx, this.onGalleryClickListener, imageList, grantedAuthDates, grantedByList, "from");
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.ctx));
        }
    }
}
