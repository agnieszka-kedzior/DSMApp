package com.example.dsmapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ActivityMain extends AppCompatActivity {

    private EditText text_username;
    private EditText text_password;
    private ProgressBar progressBar;
    private Button button_login;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_username = (EditText) findViewById(R.id.username);
        text_password = (EditText) findViewById(R.id.password);
        button_login = (Button) findViewById(R.id.signin_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    username = text_username.getText().toString();
                    password = text_password.getText().toString();

                    ClientApiAuth clientApiAuth = new ClientApiAuth(username, password);
                    AsyncTask<Void, Void, String> execute = new ExecuteNetworkOperation(clientApiAuth);
                    execute.execute();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public class ExecuteNetworkOperation extends AsyncTask<Void, Void, String> {

        private ClientApiAuth clientApiAuth;
        private boolean isSignIn;
        private String token;

        ExecuteNetworkOperation(ClientApiAuth clientApiAuth){
            this.clientApiAuth = clientApiAuth;
        }

        @Override
        protected String doInBackground(Void... voids) {
            isSignIn = clientApiAuth.auth();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            if(isSignIn) {
                token = clientApiAuth.getAccessToken();
                goToHomeActivity(token);
            }else {
                Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
            super.onPostExecute(s);
        }

    }

    private void goToHomeActivity(String token){
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("access_token", token);

        Intent intent = new Intent(this, ActivityHome.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
