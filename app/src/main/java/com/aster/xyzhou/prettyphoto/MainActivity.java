package com.aster.xyzhou.prettyphoto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.aster.xyzhou.prettyphoto.R.id.pick_photo;
import static com.aster.xyzhou.prettyphoto.R.id.take_photo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public final static int TAKE_PHOTO = 1;
    public final static int PICK_PHOTO = 2;

    public Button mPichPhoto,mTakePhoto;
    public ImageView mPhoto;

    public Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoto = (ImageView) findViewById(R.id.photo_view);
        mPichPhoto = (Button) findViewById(R.id.pick_photo);
        mTakePhoto = (Button) findViewById(take_photo);
        mPichPhoto.setOnClickListener(this);
        mTakePhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case take_photo:
                takePhoto();
                break;
            case pick_photo:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    pickPhoto();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickPhoto();
                } else {
                    Toast.makeText(MainActivity.this, "You've denied this permission!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void pickPhoto() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent);
    }

    public void takePhoto() {
        File outputImage = new File(getExternalCacheDir(),
                "output_image.jpg");
        try {
            if(outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    "com.aster.xyzhou.prettyphoto",outputImage);

        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                        .openInputStream(imageUri));
                        mPhoto.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >=19) {
                        handleImageOnKitkat(data);
                    }
                } else {
                    handleImageBeforeKitkat(data);
                }
        }
    }

    public void handleImageOnKitkat(Intent intent) {

    }

    public void handleImageBeforeKitkat(Intent intent) {

    }
}
