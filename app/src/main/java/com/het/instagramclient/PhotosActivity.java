package com.het.instagramclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends AppCompatActivity {

    //    public static final String CLIENT_ID = "b472d9a428b84fd68992d9e5eb492e4d";
    public static final String ACCESS_TOKEN = "2182546991.5b9e1e6.c67e2484e49849118cda72b6e1c1fc01";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    public ListView lvPhotos = null;
    public InstagramPhoto photoCom;

    private SwipeRefreshLayout swipeContainer;

    JSONArray photosCommentsJSON = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

//        onRefresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });

        photos = new ArrayList<>();
        aPhotos = new InstagramPhotosAdapter(this, photos);


        lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);

        fetchPopularPhotos();

        lvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int pos, long id) {
                switch ((int) id) {
                    case 0: //show all comments
                        photoCom = (InstagramPhoto) adapterView.getItemAtPosition(pos);
                        onViewAllComments(photoCom);
                        break;
                    case 1: // show video in new activity
                        InstagramPhoto photoVideo = (InstagramPhoto) adapterView.getItemAtPosition(pos);
                        showRemoteVideo(PhotosActivity.this, photoVideo.videoUrl);
                }
            }
        });
    }

    public static void showRemoteVideo(Context ctx, String url) {
        Intent i = new Intent(ctx, VideoPlayerActivity.class);

        i.putExtra("url", url);
        ctx.startActivity(i);
    }


//    public void fetchPopularPhotosComments() {
//        for (int k = 0; k < photos.size(); k++) {
//
//            String id = photos.get(k).id;
//            AsyncHttpClient clientcom = new AsyncHttpClient();
//            String urlcom = "https://api.instagram.com/v1/media/" +
//                    id //                                {media-id}}
//                    + "/comments?access_token=" + ACCESS_TOKEN;
////
//
//            clientcom.get(urlcom, null, new JsonHttpResponseHandler() {
//                //
//
////            //            on success
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    photosCommentsJSON = null;
//                    try {
////
//                        photosCommentsJSON = response.getJSONArray("data");//array of comments
//
//                        for (int j = 0; j < photosCommentsJSON.length(); j++) {
//                            JSONObject photoCommentsJSON = photosCommentsJSON.getJSONObject(j);
//                            Comments comments = new Comments(photoCommentsJSON.getJSONObject("from").getString("username"),
//                                    photoCommentsJSON.getString("text"));
////                                    photos.get(k).acomments.clear();
////                                    photos.get(k).acomments.add(comments);
//
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                    String id = "";
//                }
//            });
//
//
//        }
//        ;
//    }

    //    Trigger API request
    public void fetchPopularPhotos() {

//        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        String url = "https://api.instagram.com/v1/media/popular?access_token=" + ACCESS_TOKEN;

//        create network client
        AsyncHttpClient client = new AsyncHttpClient();
//        SyncHttpClient client = new SyncHttpClient();

//        Trigger the GET request
        client.get(url, null, new JsonHttpResponseHandler() {

            //              on success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//Expecting JSON object
//        Iterate each of the foto and decode item into a java object
                JSONArray photosJSON = null;
//                JSONArray photosCommentsJSON = null;
                try {
                    photos.clear();
                    photosJSON = response.getJSONArray("data");//array of posts
//                    iterate array of posts
                    for (int i = 0; i < photosJSON.length(); i++) {
                        JSONObject photoJSON = photosJSON.getJSONObject(i);

                        InstagramPhoto photo = new InstagramPhoto();

                        photo.username = photoJSON.getJSONObject("user").getString("username");
                        photo.userPicture = photoJSON.getJSONObject("user").getString("profile_picture");
                        photo.caption = photoJSON.getJSONObject("caption").getString("text");
                        photo.captionCreatedTime = photoJSON.getJSONObject("caption").getString("created_time");
                        photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");

                        if (photoJSON.getString("type").equals("video")) {
                            photo.videoUrl = photoJSON.getJSONObject("videos").getJSONObject("standard_resolution").getString("url");
                        }
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.imageWidth = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("width");
                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        photo.id = photoJSON.getString("id");

                        photosCommentsJSON = photoJSON.getJSONObject("comments").getJSONArray("data"); //array of comments
                        for (int j = 0; j < photosCommentsJSON.length(); j++) {
                            JSONObject photoCommentsJSON = photosCommentsJSON.getJSONObject(j);
                            Comments comments = new Comments(photoCommentsJSON.getJSONObject("from").getString("username"),
                                    photoCommentsJSON.getString("text"));
                            photo.acomments.add(comments);
                        }
                        photos.add(photo);

                        swipeContainer.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                fetchPopularPhotosComments();
                aPhotos.notifyDataSetChanged();
            }

            //on fail
            @Override
            public void onFailure(int statusCode, Header[] headers, String
                    responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Please check your internet connection",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onViewAllComments(InstagramPhoto photoCom) {
        String comments = "";
        for (int i = 0; i < photoCom.acomments.size(); i++) {
            comments = comments +
                    "<font color=#2c72fa><strong>" + photoCom.acomments.get(i).commentsUsername + "</strong></font>  "
                                                   + photoCom.acomments.get(i).comments
                    + "<br>";
        }

//        Show comments in dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage(Html.fromHtml(comments));
        builder.setMessage(Html.fromHtml(comments + comments + comments + comments)); //just for check scrolling in dialog
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
