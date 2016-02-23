package com.nathansass.petal.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nathansass.petal.interfaces.GetEventsCallback;
import com.nathansass.petal.interfaces.GetImageCallback;
import com.nathansass.petal.interfaces.GetLikedEventsCallback;
import com.nathansass.petal.interfaces.GetUserCallback;
import com.nathansass.petal.interfaces.PostEventCallback;
import com.nathansass.petal.interfaces.PostUsersEventsCallback;
import com.nathansass.petal.models.EventCard;
import com.nathansass.petal.models.EventDeck;
import com.nathansass.petal.models.LikedDeck;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by nathansass on 2/10/16.
 */
public class ServerRequests {

    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://nathansass.comxa.com/";
    public static final String TAG = ServerRequests.class.getSimpleName();

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

    public void fetchEventDataInBackground(User currentUser, GetEventsCallback callback) {
        progressDialog.show();
        new FetchEventDataAsyncTask(currentUser, callback).execute();
    }

    public void storeEventDataInBackground(EventCard eventCard, PostEventCallback eventCallback) {
        new StoreEventDataAsyncTask(eventCard, eventCallback).execute();
    }

    public void storeUsersEventsDataInBackground(User currentUser, EventCard eventCard, Boolean created, Boolean attending, PostUsersEventsCallback callback){
        new StoreUsersEventsDataAsyncTask(currentUser, eventCard, created, attending, callback).execute();
    }

    public void fetchLikedEventsDataInBackground(User currentUser,GetLikedEventsCallback callback){
        new FetchLikedEventsDataAsyncTask(currentUser, callback).execute();
    }

    public void fetchImageInBackground(String url, GetImageCallback callback){
        new FetchImageAsyncTask(url, callback).execute();
    }

    /* Fetch Image */
    public class FetchImageAsyncTask extends AsyncTask<Void, Void, Bitmap>{
        String url;
        GetImageCallback callback;
        Bitmap bitmap;
        public FetchImageAsyncTask(String url, GetImageCallback callback) {
            this.url = url;
            this.callback = callback;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            callback.done(bitmap);
        }


    }

    /* Fetch Liked Events Data */
    public class FetchLikedEventsDataAsyncTask extends AsyncTask<Void, Void, LikedDeck> {
        GetLikedEventsCallback callback;
        User currentUser;

        public FetchLikedEventsDataAsyncTask(User currentUser, GetLikedEventsCallback callback) {
            this.currentUser = currentUser;
            this.callback    = callback;
        }

        @Override
        protected LikedDeck doInBackground(Void... params) {
            try {
                URL url = new URL(SERVER_ADDRESS + "FetchLikedEventsData.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("user_id", currentUser.id + "");

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.close();
                os.close();

                conn.connect();

                InputStream in  = new BufferedInputStream(conn.getInputStream());
                String response = IOUtils.toString(in, "UTF-8");

                JSONArray jResponse = new JSONArray(response);
                Log.v(TAG,response);

                LikedDeck.get().buildEventDeck(jResponse);

                return LikedDeck.get();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(LikedDeck likedDeck) {
            super.onPostExecute(likedDeck);
            callback.done(likedDeck);
        }
    }

    /* Store UsersEvents Relationship Data*/
    public class StoreUsersEventsDataAsyncTask extends AsyncTask<Void, Void, Integer> {

        User currentUser;
        EventCard eventCard;
        PostUsersEventsCallback callback;
        int created, attending;

        public StoreUsersEventsDataAsyncTask(User currentUser, EventCard eventCard, Boolean created, Boolean attending, PostUsersEventsCallback callback) {
            this.currentUser = currentUser;
            this.eventCard   = eventCard;
            this.callback    = callback;
            this.created     = 0;
            this.attending   = 0;

            if (created) {
                this.created = 1;
            }

            if (attending) {
                this.attending = 1;
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                URL url = new URL(SERVER_ADDRESS + "PostUsersEventsData.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(CONNECTION_TIMEOUT);


                Uri.Builder builder = new Uri.Builder().appendQueryParameter("user_id", currentUser.id + "")
                                                        .appendQueryParameter("event_id", eventCard.id + "")
                                                        .appendQueryParameter("created", created + "")
                                                        .appendQueryParameter("attending", attending + "");

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.close();
                os.close();

                conn.connect();

                InputStream in  = new BufferedInputStream(conn.getInputStream());
                String response = IOUtils.toString(in, "UTF-8");
                int lastId = Integer.parseInt(response);
                return lastId;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return -1;

        }

        @Override
        protected void onPostExecute(Integer returnedRecordId) {
            super.onPostExecute(returnedRecordId);
            callback.done(returnedRecordId);
        }
    }

    /* Store Event Card Data */
    public class StoreEventDataAsyncTask extends AsyncTask<Void, Void, EventCard> {
        PostEventCallback eventCallback;
        EventCard eventCard;

        public StoreEventDataAsyncTask(EventCard eventCard, PostEventCallback eventCallback) {
            this.eventCard     = eventCard;
            this.eventCallback = eventCallback;
        }

        @Override
        protected EventCard doInBackground(Void... params) {
            try {
                URL url = new URL(SERVER_ADDRESS + "PostEventData.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("title", eventCard.mTitle)
                                                        .appendQueryParameter("street", eventCard.street);
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.close();
                os.close();

                conn.connect();

                InputStream in  = new BufferedInputStream(conn.getInputStream());
                String response = IOUtils.toString(in, "UTF-8");

                int lastEventID = Integer.parseInt(response);

                eventCard.setId(lastEventID);

                return eventCard;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(EventCard returnedEvent) {
            super.onPostExecute(returnedEvent);
            eventCallback.done(returnedEvent);
        }
    }

    /* Fetch Event Card Data */
    public class FetchEventDataAsyncTask extends AsyncTask<Void, Void, Void> {
        GetEventsCallback userCallback;
        User currentUser;

        public FetchEventDataAsyncTask(User currentUser, GetEventsCallback userCallback) {
            this.userCallback = userCallback;
            this.currentUser = currentUser;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL(SERVER_ADDRESS + "FetchEventData.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("user_id", currentUser.id + "");

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.close();
                os.close();

                conn.connect();

                InputStream in  = new BufferedInputStream(conn.getInputStream());
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
                    int id      = jResponse.getInt("id");

                    returnedUser = new User(id, name, age, user.username, user.password);


                }

                return returnedUser;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);

            progressDialog.dismiss();
            userCallback.done(returnedUser);
        }

    }

}
