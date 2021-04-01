package com.anhtong8x.loginappfb;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SubActivity extends AppCompatActivity {

    Button btnShareLink, btnShareImg, btnShareVideo, btnPickVideo;
    EditText edtTitle, edtDes, edtLink;
    ImageView imgShareImage;
    VideoView vidShareVideo;

    ShareDialog shareDialog;
    ShareLinkContent shareLinkContent;

    // share image
    public static int Select_Image = 1;
    Bitmap bitmapImage;

    // share video
    public static int Pick_Video = 2;
    Uri Select_Video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        // init view
        initView();

        shareDialog = new ShareDialog(SubActivity.this);

        // sharelink
        btnShareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareDialog.canShow(ShareLinkContent.class)){
                    shareLinkContent = new ShareLinkContent.Builder()
                            .setContentDescription(edtDes.getText().toString())
                            .setContentTitle(edtTitle.getText().toString())
                            .setContentUrl(Uri.parse(edtLink.getText().toString())).build();
                }
                //
                shareDialog.show(shareLinkContent);
            }
        });

        // share image
        imgShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Select_Image);
            }
        });

        btnShareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(bitmapImage)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                shareDialog.show(content);
            }
        });

        // share video
        btnPickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVideo = new Intent(Intent.ACTION_PICK);
                intentVideo.setType("video/*");
                startActivityForResult(intentVideo, Pick_Video);
            }
        });

        btnShareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareVideo mShareVideo = null;
                mShareVideo = new ShareVideo.Builder()
                        .setLocalUrl(Select_Video)
                        .build();
                ShareVideoContent content = new ShareVideoContent.Builder()
                        .setVideo(mShareVideo)
                        .build();

                shareDialog.show(content);
                vidShareVideo.stopPlayback();
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // resul share image
        if(requestCode == Select_Image && resultCode == RESULT_OK){
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                bitmapImage = BitmapFactory.decodeStream(inputStream);

                imgShareImage.setImageBitmap(bitmapImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // resul share video
        if(requestCode == Pick_Video && resultCode == RESULT_OK){
            Select_Video = data.getData();
            vidShareVideo.setVideoURI(Select_Video);
            vidShareVideo.start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        btnPickVideo = findViewById(R.id.btnPickVideo);
        btnShareLink = findViewById(R.id.btnShareLink);
        btnShareImg = findViewById(R.id.btnImg);
        btnShareVideo = findViewById(R.id.btnShareVideo);

        edtTitle = findViewById(R.id.edtTitle);
        edtDes = findViewById(R.id.edtDes);
        edtLink = findViewById(R.id.edtLink);

        imgShareImage = findViewById(R.id.imgShareImage);

        vidShareVideo = findViewById(R.id.vidShare);

    }


}