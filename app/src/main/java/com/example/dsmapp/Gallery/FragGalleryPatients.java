package com.example.dsmapp.Gallery;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;

import org.json.JSONArray;

import java.util.ArrayList;

public class FragGalleryPatients extends Fragment {

    private String mToken;
    private ProgressBar progressBar;
    private ArrayList<String> list = new ArrayList<>();

    ListView listView;
    

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
        View view = inflater.inflate(R.layout.gallery_pat, container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingMyGallery);
        listView = (ListView) view.findViewById(R.id.gallery_patient_list);

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext());
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = FragGalleryPatientsView.newInstance(mToken, list.get(position));
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public static FragGalleryPatients newInstance(String mToken){
        FragGalleryPatients fragmentTasks = new FragGalleryPatients();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String res;
        private Context ctx;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            res = clientDataSource.getPatientList();

            try {
                JSONArray jsonArr = new JSONArray(res);

                for (int i = 0; i < jsonArr.length(); i++)
                {
                    list.add(jsonArr.getString(i));
                }

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
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
            listView.setAdapter(arrayAdapter);
        }
    }
}
