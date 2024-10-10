package com.example.dsmapp.Profile;

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
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.dsmapp.Adapters.GalleryRecycleViewAdapter;
import com.example.dsmapp.Adapters.ProfileRecycleViewAdapter;
import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.SingleImageViewer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragProfileFriends extends Fragment implements ProfileRecycleViewAdapter.OnProfileClickListener{

    private String mToken;
    private ProgressBar progressBar;
    private ImageButton imageButtonSearch;
    RecyclerView recyclerView;

    private List<String> userIdList = new ArrayList<>();
    private List<String> userNameList = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.profile_friends, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        recyclerView = (RecyclerView) view.findViewById(R.id.rec_rec);
        imageButtonSearch = (ImageButton) view.findViewById(R.id.searchFriends);

        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = FragProfileSearch.newInstance(mToken);
                FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext(), this);
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }


        return view;
    }

    public static FragProfileFriends newInstance(String mToken){
        FragProfileFriends fragmentTasks = new FragProfileFriends();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    @Override
    public void onProfileItemClick(int position) {
        //Add user profile view
        /*
        Fragment fragment = SingleImageViewer.newInstance(mToken, imageIdList.get(position));
        FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private ProfileRecycleViewAdapter.OnProfileClickListener onProfileClickListener;
        private String res;
        private Context ctx;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx, ProfileRecycleViewAdapter.OnProfileClickListener onProfileClickListener) {
            this.clientDataSource = clientDataSource;
            this.onProfileClickListener = onProfileClickListener;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            res = clientDataSource.getFriends();

            try {
                JSONArray jsonArr = new JSONArray(res);

                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String userId = jsonObj.getString("userId");
                    String userName = jsonObj.getString("userFullName");

                    userIdList.add(userId);
                    userNameList.add(userName);
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
            ProfileRecycleViewAdapter adapter = new ProfileRecycleViewAdapter(this.ctx, this.onProfileClickListener, userIdList, userNameList);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.ctx));
        }
    }
}
