package com.het.instagramclient;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    private static class ViewHolder {
        ImageView UserPicture;
        ImageView Photo;
        TextView Caption;
        TextView CaptionCreatedTime;
        TextView Username;
        TextView Likes;
        TextView Comments;
        Button ViewAllComments;
        ImageButton WatchVideo;
    }

    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        InstagramPhoto photo = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_photo, parent, false);

            viewHolder.UserPicture = (ImageView) convertView.findViewById(R.id.ivUserPicture);
            viewHolder.Photo = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.Caption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.CaptionCreatedTime = (TextView) convertView.findViewById(R.id.tvCaptionCreatedTime);
            viewHolder.Username = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.Likes = (TextView) convertView.findViewById(R.id.tvLikes);
            viewHolder.Comments = (TextView) convertView.findViewById(R.id.tvComments);
            viewHolder.ViewAllComments = (Button) convertView.findViewById(R.id.btnViewAllComments);
            viewHolder.WatchVideo = (ImageButton) convertView.findViewById(R.id.btnWatchVideo);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ViewAllComments.setText("view all "
//                + photo.acomments.size()
                + " comments");
        viewHolder.ViewAllComments.setBackground(null);
        viewHolder.ViewAllComments.setTransformationMethod(null);

        viewHolder.WatchVideo.setBackground(null);

        viewHolder.Caption.setText(photo.caption);

        long dv = Long.valueOf(photo.captionCreatedTime) * 1000;
        viewHolder.CaptionCreatedTime.setText("⌛ " + getRelativeTimeSpanString(dv).toString());

        viewHolder.Username.setText(photo.username);
        viewHolder.Likes.setText("♡ " + photo.likesCount + " likes");

        String comments = "";
        for (int i = 0; i < 2 && i < photo.acomments.size(); i++) {
            comments = comments +
                    "<font color=#2c72fa><strong>" + photo.acomments.get(i).commentsUsername + "</strong></font>  "
                    + photo.acomments.get(i).comments
                    + "<br>";
        }
        viewHolder.Comments.setText(Html.fromHtml(comments));

        if (photo.videoUrl == null) {
            viewHolder.WatchVideo.setVisibility(View.INVISIBLE);
        }

        if (photo.videoUrl != null) {
            viewHolder.WatchVideo.setVisibility(View.VISIBLE);
            viewHolder.WatchVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView) parent).performItemClick(v, position, 1); // Let the event be handled in onItemClick()
//                onViewAllComments();
                }
            });
        }

        viewHolder.Photo.setImageResource(0);//reset

//        insert image using picasso
        Picasso.with(getContext()).load(photo.imageUrl)
                .placeholder(R.drawable.placeholder)
                .resize(photo.imageWidth, photo.imageHeight)
                .centerInside()
                .into(viewHolder.Photo);

        Transformation transformation = new RoundedTransformationBuilder()
//                .borderColor(Color.BLACK)
//                .borderWidthDp(3)
                .cornerRadiusDp(40)
                .oval(false)
                .build();
        Picasso.with(getContext()).load(photo.userPicture).transform(transformation).into(viewHolder.UserPicture);
//        }

        viewHolder.ViewAllComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
//                onViewAllComments();
            }
        });
        return convertView;
    }
}
