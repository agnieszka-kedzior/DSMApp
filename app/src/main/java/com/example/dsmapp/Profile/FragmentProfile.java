package com.example.dsmapp.Profile;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.Gallery.FragGalleryPatients;
import com.example.dsmapp.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class FragmentProfile extends Fragment {

    private String mToken;

    private TextView textViewFriends;
    private TextView textViewLogout;

    private TextView textViewDegree;
    private TextView textViewName;
    private TextView textViewWork;
    private TextView textViewPosition;

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
        View view = inflater.inflate(R.layout.frag_profile, container,false);

        textViewFriends = (TextView) view.findViewById(R.id.textContacts);
        textViewLogout = (TextView) view.findViewById(R.id.textLogout);

        textViewDegree = (TextView) view.findViewById(R.id.textDegree);
        textViewName = (TextView) view.findViewById(R.id.textName);
        textViewWork  = (TextView) view.findViewById(R.id.textWork);
        textViewPosition = (TextView) view.findViewById(R.id.textPosition);

        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                activity.finish();
            }
        });

        textViewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = FragProfileFriends.newInstance(mToken);
                FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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

    public static FragmentProfile newInstance(String mToken){
        FragmentProfile fragmentProfile = new FragmentProfile();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        fragmentProfile.setArguments(bundle);
        return fragmentProfile;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String res;
        private Context ctx;

        String degree;
        String name;
        String work;
        String position;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            res = clientDataSource.getUserDetails();

            try {
                JSONObject jsonObj = new JSONObject(res);

                degree = jsonObj.getString("userDegree");
                name = jsonObj.getString("userFullName");
                work = jsonObj.getString("userWork");
                position = jsonObj.getString("userPosition");

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            textViewDegree.setText(degree);
            textViewName.setText(name);
            textViewWork.setText(work);
            textViewPosition.setText(position);
        }
    }
}
