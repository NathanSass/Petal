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
import com.nathansass.petal.interfaces.GetImageURLSCallback;
import com.nathansass.petal.interfaces.GetUserCallback;
import com.nathansass.petal.interfaces.PostEventCallback;
import com.nathansass.petal.interfaces.PutEventAttendingCallback;
import com.nathansass.petal.models.EventCard;
import com.nathansass.petal.models.EventDeck;
import com.nathansass.petal.models.LikedDeck;
import com.nathansass.petal.models.User;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public static final String PETAL_API_ADDRESS = "http://" + "172.16.42.3" + ":8080/api";
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

    public void storeEventDataInBackground(User currentUser, EventCard eventCard, PostEventCallback eventCallback) {
        new StoreEventDataAsyncTask(currentUser, eventCard, eventCallback).execute();
    }

    public void storeEventAttendDataInBackground(User currentUser, EventCard eventCard, Boolean attending, PutEventAttendingCallback callback){
        new StoreEventAttendDataAsyncTask(currentUser, eventCard, attending, callback).execute();
    }

    public void fetchImageInBackground(String url, GetImageCallback callback){
        new FetchImageAsyncTask(url, callback).execute();
    }

    public void fetchImageUrlsInBackground(String searchTags, GetImageURLSCallback callback) {
        new FetchImageUrlsAsyncTask(searchTags, callback).execute();
    }

    /* Fetch Image Urls */
    public class FetchImageUrlsAsyncTask extends AsyncTask<Void, Void, JSONArray> {
        String searchTags;
        GetImageURLSCallback callback;
        JSONArray resultUrls;
        protected String apiKey = "53f0467d174b9080cbd9e2dd871e60d0";

        public FetchImageUrlsAsyncTask(String searchTags, GetImageURLSCallback callback) {
            this.searchTags = searchTags;
            this.callback = callback;
            //TODO: build something to format search tags
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            String urlStr = "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                    "&api_key=" + apiKey +
                    "&tags=" + "lindyhop" + //"swingdance%2C+dance%2C+party" +
                    "&safe_search=1&per_page=10&format=json&nojsoncallback=1";

            try {
                URL url = new URL(urlStr);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");


                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();


                JSONObject objResults = new JSONObject(response.toString());

                resultUrls = objResults.getJSONObject("photos").getJSONArray("photo");

                Log.v(TAG, resultUrls.toString());


                return resultUrls;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }


            return resultUrls;
        }

        @Override
        protected void onPostExecute(JSONArray resultUrls) {
            super.onPostExecute(resultUrls);
            callback.done(resultUrls);
        }
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

    /* Store whether a user is attending an event or not */
    public class StoreEventAttendDataAsyncTask extends AsyncTask<Void, Void, Void> {

        User currentUser;
        EventCard eventCard;
        PutEventAttendingCallback callback;
        int attending;

        public StoreEventAttendDataAsyncTask(User currentUser, EventCard eventCard, Boolean attending, PutEventAttendingCallback callback) {
            this.currentUser = currentUser;
            this.eventCard   = eventCard;
            this.callback    = callback;
            this.attending   = 0;


            if (attending) {
                this.attending = 1;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL(PETAL_API_ADDRESS + "/users/events/" + currentUser.id);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setConnectTimeout(CONNECTION_TIMEOUT);


                Uri.Builder builder = new Uri.Builder().appendQueryParameter("event_id", eventCard.id + "")
                                                        .appendQueryParameter("isAttending", attending + "");

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.close();
                os.close();

                conn.connect();

                InputStream in  = new BufferedInputStream(conn.getInputStream());
                String response = IOUtils.toString(in, "UTF-8");

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callback.done(aVoid);
        }
    }

    /* Store Event Card Data & Create Association with the user who created the event */
    public class StoreEventDataAsyncTask extends AsyncTask<Void, Void, EventCard> {
        PostEventCallback eventCallback;
        EventCard eventCard;
        User currentUser;

        public StoreEventDataAsyncTask(User currentUser, EventCard eventCard, PostEventCallback eventCallback) {
            this.eventCard     = eventCard;
            this.eventCallback = eventCallback;
            this.currentUser = currentUser;
        }

        @Override
        protected EventCard doInBackground(Void... params) {
            try {
                URL url = new URL(PETAL_API_ADDRESS + "/events");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(CONNECTION_TIMEOUT);

                Log.v(TAG, "startDate " + eventCard.startDateTime.toString() + " endTime: " + eventCard.endDateTime.toString());
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("user_id", currentUser.id)
                        .appendQueryParameter("title", eventCard.title)

                        .appendQueryParameter("street", eventCard.street)
                        .appendQueryParameter("city", eventCard.city)
                        .appendQueryParameter("state", eventCard.state)

                        .appendQueryParameter("startDateTime", eventCard.startDateTime.toString())
                        .appendQueryParameter("endDateTime", eventCard.endDateTime.toString())

                        .appendQueryParameter("about", eventCard.about)
                        .appendQueryParameter("price", eventCard.price + "")
                        .appendQueryParameter("eventSize", eventCard.eventSize + "")

                        .appendQueryParameter("lat", eventCard.lat + "")
                        .appendQueryParameter("lng", eventCard.lng + "");
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(query);
                writer.close();
                os.close();

                conn.connect();

                InputStream in  = new BufferedInputStream(conn.getInputStream());
                String response = IOUtils.toString(in, "UTF-8");

                JSONObject jResponse        = new JSONObject(response);

                String lastEventID          = jResponse.getJSONObject("success")
                                                        .getString("_id");

                eventCard.setId(lastEventID);

                return eventCard;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
                URL url = new URL(PETAL_API_ADDRESS + "/events/users/" + currentUser.id);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.connect();

                InputStream in  = new BufferedInputStream(conn.getInputStream());
                String response = IOUtils.toString(in, "UTF-8");

                JSONObject jResponse = new JSONObject(response);

                JSONArray userAttending = jResponse.getJSONArray("userAttending");
                JSONArray newEvents     = jResponse.getJSONArray("newEvents");

                EventDeck.get().buildEventDeck(newEvents);
                LikedDeck.get().buildEventDeck(userAttending);

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
                URL url = new URL(PETAL_API_ADDRESS + "/users");
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

                InputStream in = new BufferedInputStream(conn.getInputStream());

                String response = IOUtils.toString(in, "UTF-8");
                Log.v(TAG, response);

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
                URL url = new URL(PETAL_API_ADDRESS + "/users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");

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
                    String id   = jResponse.getString("_id");

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
