package com.example.dsmapp.Tasks.AssignedSingleTask;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.Tasks.FragmentTasks;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragActionAssign extends Fragment {

    private String mToken;
    private String mTaskId;
    private String mChosenUserFullName;

    private Button assignButton;
    private Button cancelButton;

    private Spinner userSpinner;
    private ArrayList<String> userList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mToken = getArguments().getString("access_token");
            mTaskId = getArguments().getString("task_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_task_assign, container,false);
        assignButton = (Button) view.findViewById(R.id.assignButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        userSpinner = (Spinner) view.findViewById(R.id.spinner_user);

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext());
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        assignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( userList.get(userSpinner.getSelectedItemPosition()) == null){
                    Toast.makeText(getActivity(),"Please select user",Toast.LENGTH_SHORT).show();
                }
                else {
                    mChosenUserFullName = userList.get(userSpinner.getSelectedItemPosition());
                    try {
                        ClientDataSource clientDataSource = new ClientDataSource(mToken);
                        AsyncTask<Void, Void, String> execute = new AssignExecuteNetworkOperation(clientDataSource, getContext());
                        execute.execute();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClientDataSource clientDataSource = new ClientDataSource(mToken);
                    AsyncTask<Void, Void, String> execute = new CancelExecuteNetworkOperation(clientDataSource, getContext());
                    execute.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public static FragActionAssign newInstance(String mToken, String mTaskId){
        FragActionAssign fragmentTasks = new FragActionAssign();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("task_id", mTaskId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String response;
        private Context ctx;
        private String res;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            response = clientDataSource.getFriends();

            try {
                JSONArray jsonArr = new JSONArray(response);
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String name = jsonObj.getString("userFullName");
                    userList.add(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            userSpinner.setAdapter(spinnerAdapter);
        }
    }

    public class AssignExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private Context ctx;

        public AssignExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            clientDataSource.assignTask(mTaskId, mChosenUserFullName);
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            Toast toast = Toast.makeText(getActivity(),"Task assigned",Toast.LENGTH_SHORT);
            toast.show();
            if(toast == null || toast.getView().getWindowVisibility() != View.VISIBLE){
                Fragment fragment = FragmentTasks.newInstance(mToken, 0);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }

    public class CancelExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private Context ctx;

        public CancelExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            clientDataSource.cancelTask(mTaskId);
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            Toast toast = Toast.makeText(getActivity(),"Task canceled",Toast.LENGTH_SHORT);
            toast.show();
            if(toast == null || toast.getView().getWindowVisibility() != View.VISIBLE){
                Fragment fragment = FragmentTasks.newInstance(mToken, 0);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }
}
