package com.example.dsmapp.Tasks;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dsmapp.ClientDataSource;
import com.example.dsmapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class FragTasksNewStepTwo extends Fragment {

    private static final String TAG = "FragTasksNewStepTwo";

    private String mToken;
    private String mChosenImgId;
    private String mChosenUserFullName;
    private String mChosenTaskType;
    private String mDueDate;
    private String mTitle;
    private String mDesc;

    private Button submitTaskButton;
    private Button backTaskButton;
    private Spinner userSpinner;
    private Spinner taskTypesSpinner;
    private TextView textViewDueDate;
    private EditText editTextTitle;
    private EditText editTextDesc;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private ArrayList<String> userList = new ArrayList<>();
    private ArrayList<String> listTaskTypes = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mToken = getArguments().getString("access_token");
            mChosenImgId = getArguments().getString("img_id");
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tasks_new_two, container,false);
        submitTaskButton = (Button) view.findViewById(R.id.submitTask);
        backTaskButton = (Button) view.findViewById(R.id.backTask);
        userSpinner = (Spinner) view.findViewById(R.id.spinner_user);
        taskTypesSpinner  = (Spinner) view.findViewById(R.id.spinner_task_type);
        textViewDueDate = (TextView) view.findViewById(R.id.setDueDate);
        editTextTitle = (EditText) view.findViewById(R.id.setTitle);
        editTextDesc = (EditText) view.findViewById(R.id.setTaskDesc);

        backTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = FragTasksNewStepOne.newInstance(mToken, mChosenImgId);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frag_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        submitTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( userList.get(userSpinner.getSelectedItemPosition()) == null
                        | listTaskTypes.get(taskTypesSpinner.getSelectedItemPosition()) == null
                        | textViewDueDate.getText().toString() == null
                        | editTextTitle.getText().toString() == null){
                    Toast.makeText(getActivity(),"Please select user, task type, due date and input title",Toast.LENGTH_LONG).show();
                }
                else{
                mTitle = editTextTitle.getText().toString();
                mDesc = editTextDesc.getText().toString();
                mDueDate = textViewDueDate.getText().toString();
                mChosenTaskType = listTaskTypes.get(taskTypesSpinner.getSelectedItemPosition());
                mChosenUserFullName = userList.get(userSpinner.getSelectedItemPosition());
                    try {
                        ClientDataSource clientDataSource = new ClientDataSource(mToken);
                        AsyncTask<Void, Void, String> execute = new NewTaskExecuteNetworkOperation(clientDataSource, getContext());
                        execute.execute();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        textViewDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;

                String day;
                String moth;
                String yr;

                if(dayOfMonth < 10){
                    day = "0" + dayOfMonth;
                } else {
                    day = "" + dayOfMonth;
                }

                if(month < 10){
                    moth = "0" + month;
                } else {
                    moth = "" + month;
                }

                String date = day+"-"+moth+"-"+year;
                textViewDueDate.setText(date);
            }
        };

        try {
            ClientDataSource clientDataSource = new ClientDataSource(mToken);
            AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientDataSource, this.getContext());
            execute.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    public static FragTasksNewStepTwo newInstance(String mToken, String chosenImgId){
        FragTasksNewStepTwo fragmentTasks = new FragTasksNewStepTwo();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        bundle.putString("img_id", chosenImgId);
        fragmentTasks.setArguments(bundle);
        return fragmentTasks;
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private String response;
        private String resTask;
        private Context ctx;

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

            resTask = clientDataSource.getTaskTypes();

            try {
                JSONArray jsonArr = new JSONArray(resTask);
                for (int i = 0; i < jsonArr.length(); i++)
                {
                    listTaskTypes.add(jsonArr.getString(i));
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String s) {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            userSpinner.setAdapter(spinnerAdapter);

            ArrayAdapter<String> taskSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listTaskTypes);
            taskSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            taskTypesSpinner.setAdapter(taskSpinnerAdapter);

        }
    }


    public class NewTaskExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientDataSource clientDataSource;
        private Context ctx;

        public NewTaskExecuteNetworkOperation(ClientDataSource clientDataSource, Context ctx) {
            this.clientDataSource = clientDataSource;
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... voids) {
            clientDataSource.newTask(mChosenUserFullName, mChosenTaskType, mDueDate, mTitle, mDesc, mChosenImgId);
            return null;
        }


        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String s) {
            Toast toast = Toast.makeText(getActivity(),"New task created",Toast.LENGTH_LONG);
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
