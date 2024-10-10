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
import android.widget.Button;
import android.widget.Toast;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.Tasks.FragmentTasks;


public class FragActionAccept extends Fragment {

    private String mToken;
    private String mTaskId;

    private Button acceptButton;
    private Button declineButton;


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
        View view = inflater.inflate(R.layout.single_task_accept, container,false);
        acceptButton = (Button) view.findViewById(R.id.acceptButton);
        declineButton = (Button) view.findViewById(R.id.declineButtom);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClientDataSource clientDataSource = new ClientDataSource(mToken);
                    AsyncTask<Void, Void, String> execute = new AcceptExecuteNetworkOperation(clientDataSource, getContext());
                    execute.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClientDataSource clientDataSource = new ClientDataSource(mToken);
                    AsyncTask<Void, Void, String> execute = new DeclineExecuteNetworkOperation(clientDataSource, getContext());
                    execute.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        return view;
    }

    public static FragActionAccept newInstance(String mToken, String mTaskId){
        FragActionAccept fragmentTasks = new FragActionAccept();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("task_id", mTaskId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class AcceptExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private Context ctx;

        public AcceptExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            System.out.println("Accept task: "+mTaskId);
            clientDataSource.acceptTask(mTaskId);
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String s) {
            Toast toast = Toast.makeText(getActivity(),"Task accepted",Toast.LENGTH_SHORT);
            toast.show();
            if(toast == null || toast.getView().getWindowVisibility() != View.VISIBLE){
                Fragment fragment = AssignedSingleTask.newInstance(mToken, mTaskId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }

    public class DeclineExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private Context ctx;

        public DeclineExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            clientDataSource.declineTask(mTaskId);
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String s) {
            Toast toast = Toast.makeText(getActivity(),"Task declined",Toast.LENGTH_SHORT);
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
