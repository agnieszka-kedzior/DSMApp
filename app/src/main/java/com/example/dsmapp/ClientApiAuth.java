package com.example.dsmapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class ClientApiAuth {

    private String username;
    private String password;

    private String accessToken;
    private JSONObject jsonObj;
    private boolean isLoginIn;

    ClientApiAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean auth() {
        HashMap<String, String> params = new HashMap<>();

        params.put("grant_type", "password");
        params.put("username", username);
        params.put("password", password);

        StringBuilder sbParams = new StringBuilder();
        sbParams.append("?");

        try {
            sbParams.append("grant_type").append("=")
                    .append(URLEncoder.encode(params.get("grant_type"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("username").append("=")
                    .append(URLEncoder.encode(params.get("username"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("password").append("=")
                    .append(URLEncoder.encode(params.get("password"), "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String paramsString = sbParams.toString();

        URL url = null;
        HttpURLConnection connection = null;

        try {
            url = new URL( "http://192.168.43.185:43210/oauth/token" + paramsString);

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Basic dHJ1c3RlZC1jbGllbnQ6c2VjcmV0");

            connection.connect();

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            //wr.writeBytes(paramsString);
            wr.flush();
            wr.close();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            jsonObj = (JSONObject) new JSONTokener(result.toString()).nextValue();
            accessToken = jsonObj.getString("access_token");
            isLoginIn = true;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Login failed");
            isLoginIn = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("Login status: " + isLoginIn);
        return isLoginIn;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
