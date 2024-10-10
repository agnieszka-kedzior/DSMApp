package com.example.dsmapp.Tasks.CreatedSingleTask;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.Tasks.FragmentTasks;

import java.util.ArrayList;


public class FragAction extends Fragment {

    private String mToken;
    private String mTaskId;

    private Button remindButton;
    private Button historyButton;
    private Button closeButton;

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
        View view = inflater.inflate(R.layout.created_task_action, container,false);
        remindButton = (Button) view.findViewById(R.id.remindButton);
        historyButton = (Button) view.findViewById(R.id.historyButton);
        closeButton = (Button) view.findViewById(R.id.closeButton);

        remindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClientDataSource clientDataSource = new ClientDataSource(mToken);
                    AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, getContext());
                    execute.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = FragActionHistory.newInstance(mToken, mTaskId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Close")
                        .setMessage("Do you want to close this task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    ClientDataSource clientDataSource = new ClientDataSource(mToken);
                                    AsyncTask<Void, Void, String> execute = new CloseExecuteNetworkOperation(clientDataSource, getContext());
                                    execute.execute();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(),"Canceled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });


        return view;
    }

    public static FragAction newInstance(String mToken, String mTaskId){
        FragAction fragmentTasks = new FragAction();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("task_id", mTaskId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private Context ctx;

        public ExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String s) {
            Toast toast = Toast.makeText(getActivity(),"Reminder sent",Toast.LENGTH_SHORT);
            toast.show();
            /*if(toast == null || toast.getView().getWindowVisibility() != View.VISIBLE){
                Fragment fragment = AssignedSingleTask.newInstance(mToken, mTaskId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }*/
        }
    }

    public class CloseExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private Context ctx;

        public CloseExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            clientDataSource.forceCloseTask(mTaskId);
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String s) {
            Toast toast = Toast.makeText(getActivity(),"Task closed",Toast.LENGTH_SHORT);
            toast.show();
            if(toast == null || toast.getView().getWindowVisibility() != View.VISIBLE){
                Fragment fragment = FragmentTasks.newInstance(mToken, 1);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }
}
