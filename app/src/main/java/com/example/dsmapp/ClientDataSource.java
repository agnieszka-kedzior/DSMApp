package com.example.dsmapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class ClientDataSource {

    private String token;
    private String baseUrl = "http://192.168.43.185:43210/private/";

    public ClientDataSource(String token){
        this.token=token;
    }

    public Bitmap getImageFirstFrame(String id){ return getImageFrameResponse("f/id/"+ id); }

    public Bitmap getImageByFrame(String id, String frame){ return getImageFrameResponse("f/f/" + id + "/" + frame); }

    public String getUserDetails(){ return getResponse("user/det"); }

    public String getImagePatientName(String id){ return getResponse("image/pat/" + id); }

    public String getTaskFrameComment(String taskId, String frameId){ return getResponse("com/get/" + taskId + "/"+frameId); }

    public String getTaskHistory(String taskId){ return  getResponse("tasks/hist/all/" + taskId); }

    public String getImageFrames(String id){ return getResponse("f/all/"+id);}

    public String getTaskImageId(String id){ return getResponse("tasks/img/" + id);}

    public String getTaskUserName(String id){ return getResponse("tasks/user/" + id);}

    public String getCreatedBy(String id){ return getResponse("tasks/createdBy/" + id);}

    public String getCompletedBy(String id){ return getResponse("tasks/completedBy/" + id);}

    public String getOpenTasks(){ return getResponse("tasks/open");}

    public String getOneTask(String id){return getResponse("tasks/one/" + id);}

    public String getAssignedTasks(){ return getResponse("tasks/ass");}

    public String getClosedTasks() { return getResponse("tasks/closed");}

    public void deleteGrantedAuth(String authId){ deleteResponse("auth/del/"+authId);}

    public String getGrantedAuth(){ return  getResponse("auth/granted"); }

    public String getRecAuth(){ return  getResponse("auth/get"); }

    public String getAuthImage(String authId){return getResponse("auth/img/" + authId); }

    public String getAuthUser(String authId){ return getResponse("auth/user/" + authId); }

    public String getUserId(String name){ return  getResponse("user/id/" +name);}

    public String getName(String id){ return  getResponse("user/name/" +id);}

    public String getTaskTypes(){ return getResponse("tasks/types"); }

    public String getListOfUserImages(){ return getResponse("image/user"); }

    public String getListOfUserImagesForPatient(String pat){
        return getResponse("image/user/pat/" + pat);
    }

    public String getPatientList(){
        return getResponse("p/list");
    }

    public String getPatientDetails(String name){ return  getResponse("p/det/"+name); }

    public String getFriends(){
        return getResponse("user/fr");
    }

    public Bitmap getImageFrameResponse(String path){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(baseUrl + path + "?access_token=" +token);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "image/jpeg");
            connection.setRequestProperty("Authorization", "Basic dHJ1c3RlZC1jbGllbnQ6c2VjcmV0");
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return null;
    }

    public String getResponse(String path){
        URL url = null;
        HttpURLConnection connection = null;

        try {
            url = new URL(baseUrl + path +"?access_token=" +token);

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Basic dHJ1c3RlZC1jbGllbnQ6c2VjcmV0");

            connection.connect();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }

            return buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    public void deleteResponse(String path){
        URL url = null;
        HttpURLConnection connection = null;

        try {
            url = new URL( baseUrl+ path + "?access_token=" +token);

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");
            connection.connect();

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.flush();
            wr.close();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed");
        }
    }

    public void newTask(String userAss, String taskType, String dueDate, String title, String desc, String id) {
        String addUrl ="tasks/new";

        HashMap<String, String> params = new HashMap<>();

        params.put("access_token", token);
        params.put("userAssigned", userAss);
        params.put("taskType", taskType);
        params.put("dueDate", dueDate);
        params.put("title", title);
        params.put("desc", desc);
        params.put("image", id);

        StringBuilder sbParams = new StringBuilder();
        sbParams.append("?");

        try {
            sbParams.append("access_token").append("=")
                    .append(URLEncoder.encode(params.get("access_token"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("userAssigned").append("=")
                    .append(URLEncoder.encode(params.get("userAssigned"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("taskType").append("=")
                    .append(URLEncoder.encode(params.get("taskType"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("dueDate").append("=")
                    .append(URLEncoder.encode(params.get("dueDate"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("title").append("=")
                    .append(URLEncoder.encode(params.get("title"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("desc").append("=")
                    .append(URLEncoder.encode(params.get("desc"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("image").append("=")
                    .append(URLEncoder.encode(params.get("image"), "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String paramsString = sbParams.toString();

        URL url = null;
        HttpURLConnection connection = null;

        try {
            url = new URL( baseUrl+ addUrl + paramsString);

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

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

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed");
        }
    }

    public void assignTask(String taskId, String user) {
        String addUrl ="tasks/ass/user";

        HashMap<String, String> params = new HashMap<>();

        params.put("access_token", token);
        params.put("taskId", taskId);
        params.put("userAssigned",user);
        StringBuilder sbParams = new StringBuilder();
        sbParams.append("?");

        try {
            sbParams.append("access_token").append("=")
                    .append(URLEncoder.encode(params.get("access_token"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("taskId").append("=")
                    .append(URLEncoder.encode(params.get("taskId"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("userAssigned").append("=")
                    .append(URLEncoder.encode(params.get("userAssigned"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String paramsString = sbParams.toString();
        postRequest(addUrl, paramsString);
    }

    public void addFrameComment(String taskId, String frame, String comment) {
        String addUrl ="com/add";

        HashMap<String, String> params = new HashMap<>();

        params.put("access_token", token);
        params.put("taskId", taskId);
        params.put("imageFrameId",frame);
        params.put("comment",comment);
        StringBuilder sbParams = new StringBuilder();
        sbParams.append("?");

        try {
            sbParams.append("access_token").append("=")
                    .append(URLEncoder.encode(params.get("access_token"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("taskId").append("=")
                    .append(URLEncoder.encode(params.get("taskId"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("imageFrameId").append("=")
                    .append(URLEncoder.encode(params.get("imageFrameId"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("comment").append("=")
                    .append(URLEncoder.encode(params.get("comment"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String paramsString = sbParams.toString();
        postRequest(addUrl, paramsString);
    }

    public void reopenTask(String taskId, String why) {
        String addUrl ="tasks/reopen";
        HashMap<String, String> params = new HashMap<>();

        params.put("access_token", token);
        params.put("taskId", taskId);
        params.put("why", why);
        StringBuilder sbParams = new StringBuilder();
        sbParams.append("?");

        try {
            sbParams.append("access_token").append("=")
                    .append(URLEncoder.encode(params.get("access_token"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("taskId").append("=")
                    .append(URLEncoder.encode(params.get("taskId"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("why").append("=")
                    .append(URLEncoder.encode(params.get("why"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String paramsString = sbParams.toString();
        postRequest(addUrl, paramsString);
    }

    public void acceptTask(String taskId) {
        String addUrl ="tasks/accept";
        taskPostParams(taskId, addUrl);
    }

    public void declineTask(String taskId) {
        String addUrl ="tasks/decline";
        taskPostParams(taskId, addUrl);
    }

    public void submitTask(String taskId) {
        String addUrl ="tasks/submit";
        taskPostParams(taskId, addUrl);
    }

    public void cancelTask(String taskId) {
        String addUrl ="tasks/cancel";
        taskPostParams(taskId, addUrl);
    }

    public void closeTask(String taskId) {
        String addUrl ="tasks/close";
        taskPostParams(taskId, addUrl);
    }

    public void forceCloseTask(String taskId) {
        String addUrl ="tasks/forced/close";
        taskPostParams(taskId, addUrl);
    }

    public void postRequest(String addUrl, String paramsString){
        URL url = null;
        HttpURLConnection connection = null;

        try {
            url = new URL( baseUrl+ addUrl + paramsString);

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.connect();

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.flush();
            wr.close();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed");
        }
    }

    public void taskPostParams(String taskId, String addUrl){
        HashMap<String, String> params = new HashMap<>();

        params.put("access_token", token);
        params.put("taskId", taskId);
        StringBuilder sbParams = new StringBuilder();
        sbParams.append("?");

        try {
            sbParams.append("access_token").append("=")
                    .append(URLEncoder.encode(params.get("access_token"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("taskId").append("=")
                    .append(URLEncoder.encode(params.get("taskId"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String paramsString = sbParams.toString();
        postRequest(addUrl, paramsString);
    }

    public void addAuth(String userId, String imgId){
        String addUrl ="auth/add";

        HashMap<String, String> params = new HashMap<>();

        params.put("access_token", token);
        params.put("userId", userId);
        params.put("imgId", imgId);
        StringBuilder sbParams = new StringBuilder();
        sbParams.append("?");

        try {
            sbParams.append("access_token").append("=")
                    .append(URLEncoder.encode(params.get("access_token"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("userId").append("=")
                    .append(URLEncoder.encode(params.get("userId"), "UTF-8"));
            sbParams.append("&");
            sbParams.append("imgId").append("=")
                    .append(URLEncoder.encode(params.get("imgId"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String paramsString = sbParams.toString();
        postRequest(addUrl, paramsString);
    }
}
