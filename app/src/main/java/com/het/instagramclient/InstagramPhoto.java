package com.het.instagramclient;

import java.util.ArrayList;

public class InstagramPhoto {
    public String id;
    public String username;
    public String userPicture;
    public String caption;
    public String captionCreatedTime;
    public String imageUrl;
    public String videoUrl;
//    public String comments;
//    public String commentsUsername;
    public ArrayList<Comments> acomments = new ArrayList<>();
    public int imageHeight;
    public int imageWidth;
    public int likesCount;
}
