package com.aster.xyzhou.prettyphoto;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aster.xyzhou.prettyphoto.adapter.MenuItemAdapter;
import com.aster.xyzhou.prettyphoto.model.FeatureList;
import com.aster.xyzhou.prettyphoto.util.ImageEffect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static com.aster.xyzhou.prettyphoto.R.id.pick_photo;
import static com.aster.xyzhou.prettyphoto.R.id.take_photo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
    ,MenuItemAdapter.dealBitmap{

    public final static int TAKE_PHOTO = 1;
    public final static int PICK_PHOTO = 2;

    public final static int REVERSE_BITMAP = 1;
    public final static int GRAY_EFFECT = 2;
    public final static int OLD_EFFECT = 3;
    public final static int QUSHE = 4;
    public final static int GAOBAOHE = 5;
    public final static int FUDIAO = 6;
    public final static int FLAG = 7;


    public Button mPichPhoto,mTakePhoto;
    public ImageView mPhoto;
    public RecyclerView mMenus;
    public MenuItemAdapter mAdapter;
    public Bitmap mBitmap = null;

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

        mMenus = (RecyclerView) findViewById(R.id.menu);
        List<String> features = new FeatureList().getFeatureList();
        mAdapter = new MenuItemAdapter(MainActivity.this, features);
        mMenus.setLayoutManager(new GridLayoutManager(this, 2));
        mMenus.setAdapter(mAdapter);
        mAdapter.setDealBitmapListener(this);
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
        startActivityForResult(intent, PICK_PHOTO);
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
                        mBitmap = BitmapFactory.decodeStream(getContentResolver()
                                        .openInputStream(imageUri));
                        mPhoto.setImageBitmap(mBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    handleImageOnKitkat(data);
                }
        }
    }

    public void handleImageOnKitkat(Intent intent) {
        String imagePath = null;
        Uri uri = intent.getData();
        if (DocumentsContract.isDocumentUri(MainActivity.this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID
                        + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.
                        EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {

        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.
                    Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            mBitmap = BitmapFactory.decodeFile(imagePath);
            mPhoto.setImageBitmap(mBitmap);
        } else {
            Toast.makeText(MainActivity.this, "failed to get photo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addEffect(int effectName) {
        if (mBitmap != null) {
            if (effectName <= 5) {
                Bitmap bmp = Bitmap.createBitmap(mBitmap.getWidth(),
                        mBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888);
                ColorMatrix colorMatrix = new ColorMatrix();
                switch (effectName) {
                    case GRAY_EFFECT:
                        colorMatrix.set(ImageEffect.GRAY_EFFECT);
                        break;
                    case REVERSE_BITMAP:
                        colorMatrix.set(ImageEffect.REVERSE_BITMAP);
                        break;
                    case OLD_EFFECT:
                        colorMatrix.set(ImageEffect.OLD_EFFECT);
                        break;
                    case QUSHE:
                        colorMatrix.set(ImageEffect.QUSHE);
                        break;
                    case GAOBAOHE:
                        colorMatrix.set(ImageEffect.GAOBAOHE);
                        break;
                }
                Canvas canvas = new Canvas(bmp);
                Paint paint = new Paint();
                paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                canvas.drawBitmap(mBitmap, 0, 0, paint);
                mPhoto.setImageBitmap(bmp);
            } else if (effectName == FUDIAO) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int width = mBitmap.getWidth();
                        int height = mBitmap.getHeight();
                        int color, color1;
                        int r,g,b,a;
                        int r1,g1,b1;
                        Bitmap bmp = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);

                        int[] oldPx = new int[width * height];
                        int[] newPx = new int[width * height];
                        mBitmap.getPixels(oldPx, 0, width, 0, 0, width, height);

                        for (int i = 0; i < width*height; i++) {
                            color = oldPx[i];
                            r = Color.red(color);
                            g = Color.green(color);
                            b = Color.blue(color);
                            a = Color.alpha(color);

                            int index = (i < (width * height -1)) ? i + 1 : i - 1;
                            color1 = oldPx[index];
                            r1 = Color.red(color1);
                            g1 = Color.green(color1);
                            b1 = Color.blue(color1);

                            r = r1 - r + 127;
                            g = g1 - g + 127;
                            b = b1 - b + 127;

                            if (r > 255) {
                                r = 255;
                            } else if (r < 0) {
                                r = 0;
                            }
                            if (g > 255) {
                                g = 255;
                            } else if (g < 0) {
                                g = 0;
                            }
                            if (b > 255) {
                                b = 255;
                            } else if (b < 0) {
                                b = 0;
                            }

                            newPx[i] = Color.argb(a, r, g, b);
                        }
                        bmp.setPixels(newPx, 0, width, 0, 0, width, height);
                        mPhoto.setImageBitmap(bmp);
                    }
                }).run();

            } else if (effectName == FLAG) {
                final int HEIGHT = 40;
                final int WIDTH = 40;
                float bitmapWidth = mBitmap.getWidth();
                float bitmapHeight = mBitmap.getHeight();
                int index = 0;
                float[] orig = new float[2 * (WIDTH+1) * (HEIGHT+1)];
                float[] verts = new float[2 * (WIDTH+1) * (HEIGHT+1)];
                for (int y = 0; y <= HEIGHT; y++) {
                    float fy = bitmapHeight * y / HEIGHT;
                    for (int x = 0; x <= WIDTH; x++) {
                        float fx = bitmapWidth * x / WIDTH;
                        orig[index * 2 + 0] = verts[index * 2 + 0] = fx;
                        orig[index * 2 + 1] = verts[index * 2 + 1] = fy + 60;
                        index += 1;
                    }
                }
                for (int j = 0; j <= HEIGHT; j++) {
                    for (int i = 0; i <= WIDTH; i++) {
                        verts[(j * (WIDTH + 1) + i) * 2 + 0] += 0;
                        float offsetY = (float) Math.sin((float)i / WIDTH * 2 * Math.PI);
                        verts[(j * (WIDTH + 1) + i) * 2 + 1] =
                                orig[(j * WIDTH + i) * 2 + 1] + offsetY * 60;
                    }
                }
                Bitmap bmp = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight() + 120, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmp);
                canvas.drawBitmapMesh(mBitmap, WIDTH, HEIGHT, verts, 0, null, 0, null);
                mPhoto.setImageBitmap(bmp);
            }
        }
    }
}
