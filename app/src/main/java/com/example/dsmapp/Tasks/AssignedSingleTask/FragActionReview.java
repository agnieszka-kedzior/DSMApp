package com.example.dsmapp.Tasks.AssignedSingleTask;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;
import com.example.dsmapp.Tasks.FragTasksAssigned;
import com.example.dsmapp.Tasks.FragmentTasks;


public class FragActionReview extends Fragment {

    private String mToken;
    private String mTaskId;

    private Button closeButton;
    private Button reopenButton;
    private Button reviewButton;

    private String why;

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
        final View view = inflater.inflate(R.layout.single_task_review, container,false);
        closeButton = (Button) view.findViewById(R.id.closeButton);
        reopenButton = (Button) view.findViewById(R.id.reopenButton);
        reviewButton = (Button) view.findViewById(R.id.reviewButton);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClientDataSource clientDataSource = new ClientDataSource(mToken);
                    AsyncTask<Void, Void, String> execute = new CloseExecuteNetworkOperation(clientDataSource, getContext());
                    execute.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        reopenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(getContext());
                final View customView = factory.inflate(R.layout.single_task_image_review_reopen, null);
                final AlertDialog dialogReopen = new AlertDialog.Builder(getContext()).create();

                dialogReopen.setView(customView);
                dialogReopen.setTitle("Input Reason");

                final EditText text = (EditText) customView.findViewById(R.id.textReopen);

                customView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        why =  text.getText().toString();

                        try {
                            ClientDataSource clientDataSource = new ClientDataSource(mToken);
                            AsyncTask<Void, Void, String> execute = new ReopenExecuteNetworkOperation(clientDataSource, getContext());
                            execute.execute();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        dialogReopen.dismiss();
                    }
                });

                customView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(),"Canceled", Toast.LENGTH_SHORT).show();
                        dialogReopen.dismiss();
                    }
                });

                dialogReopen.show();
            }
        });

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = ImageViewerReview.newInstance(mToken,mTaskId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public static FragActionReview newInstance(String mToken, String mTaskId){
        FragActionReview fragmentTasks = new FragActionReview();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("task_id", mTaskId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
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
            clientDataSource.closeTask(mTaskId);
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
                Fragment fragment = FragmentTasks.newInstance(mToken, 0);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    }

    public class ReopenExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private Context ctx;

        public ReopenExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            clientDataSource.reopenTask(mTaskId, why);
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String s) {
            Toast toast = Toast.makeText(getActivity(),"Task reopened",Toast.LENGTH_SHORT);
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
