package com.nathansass.petal.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.nathansass.petal.interfaces.GetEventsCallback;
import com.nathansass.petal.interfaces.GetUserCallback;
import com.nathansass.petal.models.EventDeck;
import com.nathansass.petal.models.User;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nathansass on 2/10/16.
 */
public class ServerRequests {

    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://nathansass.comxa.com/";

    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
    }

    public void storeUserDataInBackground(User user, GetUserCallback userCallback) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallback ).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallback callback) {
        progressDialog.show();
        new FetchUserDataAsyncTask(user, callback).execute();
    }

    public void fetchEventDataInBackground(GetEventsCallback callback) {
        progressDialog.show();
        new FetchEventDataAsyncTask(callback).execute();
    }

    /* Fetch Event Card Data */
    public class FetchEventDataAsyncTask extends AsyncTask<Void, Void, Void> {
        GetEventsCallback userCallback;

        public FetchEventDataAsyncTask(GetEventsCallback userCallback) {
            this.userCallback = userCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(SERVER_ADDRESS + "FetchEventData.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(conn.getInputStream());

                String response = IOUtils.toString(in, "UTF-8");
                JSONArray jResponse = new JSONArray(response);

                EventDeck.get().buildEventDeck(jResponse);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            userCallback.done(null);
        }
    }

    /* Store User Data */
    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(SERVER_ADDRESS + "Register.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("name", user.name)
                                                        .appendQueryParameter("age", user.age + "")
                                                        .appendQueryParameter("username", user.username)
                                                        .appendQueryParameter("password", user.password);

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

//For debugging
                InputStream in = new BufferedInputStream(conn.getInputStream());

                String response = IOUtils.toString(in, "UTF-8");
                System.out.println(response);
//

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            userCallback.done(null);
        }

    }

    /* Fetch User Data */
    public class FetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallback;

        public FetchUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);

            progressDialog.dismiss();
            userCallback.done(returnedUser);
        }

        @Override
        protected User doInBackground(Void... params) {
            User returnedUser;
            try {
                URL url = new URL(SERVER_ADDRESS + "FetchUserData.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("username", user.username)
                                                        .appendQueryParameter("password", user.password);

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                InputStream in = new BufferedInputStream(conn.getInputStream());

                String response = IOUtils.toString(in, "UTF-8");
                JSONObject jResponse = new JSONObject(response);

                if (jResponse.length() == 0) {
                    returnedUser = null;
                } else {
                    String name = jResponse.getString("name");
                    int age     = jResponse.getInt("age");

                    returnedUser = new User(name, age, user.username, user.password);


                }

                return returnedUser;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

}
