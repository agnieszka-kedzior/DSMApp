package com.example.dsmapp.Gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dsmapp.Adapters.GalleryRecycleViewAdapter;
import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.SingleImageViewer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragGalleryAccess extends Fragment implements GalleryRecycleViewAdapter.OnGalleryClickListener{

    private String mToken;
    private ProgressBar progressBar;
    private List<String> authIdList = new ArrayList<>();
    private List<String> imageIdList = new ArrayList<>();
    private List<String> userNameList = new ArrayList<>();
    private List<String> grantedAuthDates = new ArrayList<>();
    private ArrayList<Bitmap> imageList = new ArrayList<>();

    private GalleryRecycleViewAdapter galleryRecycleViewAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton buttonAdd;

    public static FragGalleryAccess newInstance(String mToken){
        FragGalleryAccess fragmentTasks = new FragGalleryAccess();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

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
        View view = inflater.inflate(R.layout.gallery_access, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingGalleryAccess);
        recyclerView = (RecyclerView) view.findViewById(R.id.rec_access);
        buttonAdd = (FloatingActionButton) view.findViewById(R.id.addButton);

        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView);

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext(), this );
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = FragGalleryAddAccess.newInstance(mToken,null);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
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
        private Context ctx;
        private String res;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx, GalleryRecycleViewAdapter.OnGalleryClickListener onGalleryClickListener) {
            this.clientDataSource = clientDataSource;
            this.onGalleryClickListener = onGalleryClickListener;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            res = clientDataSource.getGrantedAuth();

            try {
                JSONArray jsonArr = new JSONArray(res);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String authId = jsonObj.getString("imageAuthId");
                    String grantedDate = jsonObj.getString("grantedDate");

                    String id = clientDataSource.getAuthImage(authId);
                    String username = clientDataSource.getAuthUser(authId);
                    Bitmap myBitmap = clientDataSource.getImageFirstFrame(id);

                    imageList.add(myBitmap);
                    authIdList.add(authId);
                    imageIdList.add(id);
                    grantedAuthDates.add(grantedDate);
                    userNameList.add(username);
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
            galleryRecycleViewAdapter = new GalleryRecycleViewAdapter(this.ctx, onGalleryClickListener, imageList, grantedAuthDates, userNameList, "for");
            recyclerView.setAdapter(galleryRecycleViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        }
    }

    public class ExecuteDeleteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String authId;


        public ExecuteDeleteNetworkOperation(ClientDataSource clientDataSource, String authId) {
            this.clientDataSource = clientDataSource;
            this.authId = authId;
        }

        @Override
        protected String doInBackground(Void... voids) {
            clientDataSource.deleteGrantedAuth(authId);
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast toast = Toast.makeText(getActivity(),"Access deleted",Toast.LENGTH_LONG);
            toast.show();
            if(!toast.getView().isShown()) {
                Fragment fragment = FragmentGallery.newInstance(mToken, 2);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            try {
                ClientDataSource clientDataSource = new ClientDataSource(mToken);
                AsyncTask<Void, Void, String> execute = new ExecuteDeleteNetworkOperation(clientDataSource, authIdList.get(viewHolder.getAdapterPosition()));
                execute.execute();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    };
}
