package com.nathansass.petal.models;

import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.nathansass.petal.R;

/**
 * Created by nathansass on 3/6/16.
 */
public abstract class EventDisplayer extends AppCompatActivity {
    /* UI Fields */
    ImageView eventBanner;
    TextView eventTitle, eventPrice, eventAddress, eventCityState,
            eventDate, eventAbout, eventTime, eventSize;

    public void getUIComponents() {
        this.eventTitle     = (TextView)  findViewById(R.id.eventTitle);
        this.eventBanner    = (ImageView) findViewById(R.id.eventBanner);
        this.eventAddress   = (TextView)  findViewById(R.id.eventAddress);
        this.eventCityState = (TextView)  findViewById(R.id.eventCityState);
        this.eventPrice     = (TextView)  findViewById(R.id.eventPrice);
        this.eventTitle     = (TextView)  findViewById(R.id.eventTitle);
        this.eventDate      = (TextView)  findViewById(R.id.eventDate);
        this.eventAbout     = (TextView)  findViewById(R.id.eventAbout);
        this.eventTime      = (TextView)  findViewById(R.id.eventTime);
        this.eventSize      = (TextView)  findViewById(R.id.eventSize);
    }

    public void updateEventUI(EventCard event) {
        this.eventAddress.setText( event.street );
        this.eventCityState.setText( event.getCityState() );
        this.eventPrice.setText( event.getPrice() );
        this.eventTitle.setText( event.title );
        this.eventAbout.setText( event.about );
        this.eventDate.setText( event.getEventStartDate() );
        this.eventTime.setText( event.getEventStartTimeToEndTime() );
        this.eventSize.setText( event.eventSize + "" );

        updateBackgroundImage();
    }

    public void setEmptyDeck() {
        this.eventTitle.setText(R.string.deck_empty_message);
    }

    public void updateBackgroundImage() {
//        final ServerRequests serverRequests = new ServerRequests(this);


            /* param not currently being used */
//            serverRequests.fetchImageUrlsInBackground("swingdance", new GetImageURLSCallback() {
//                @Override
//                public void done(JSONArray returnedUrls) {
//                    String imageUrl = null;
//                    try {
//                        JSONObject returnedUrl = (JSONObject) returnedUrls.get(new Random().nextInt(returnedUrls.length()));
//
//                        String farmId = returnedUrl.getInt("farm") + "";
//                        String serverId = returnedUrl.getString("server");
//                        String id = returnedUrl.getString("id");
//                        String secret = returnedUrl.getString("secret");
//                        String size = "n";
//
//                        imageUrl = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret + "_" + size + ".jpg";
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    serverRequests.fetchImageInBackground(imageUrl, new GetImageCallback() {
//                        @Override
//                        public void done(Bitmap returnedImage) {
//                            eventBanner.setImageBitmap(returnedImage);
//
//                            Toast toast = Toast.makeText(context, "Image loaded", duration);
//                            toast.show();
//                        }
//                    });
//
//
//                }
//            });
    }
}
