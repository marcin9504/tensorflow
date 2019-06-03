package put.poznan.tensorflow;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
    private static final String LABEL_PATH = "labels.txt";

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 1;

    private TFClassifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textView;
    private Button buttonShare;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        buttonShare = findViewById(R.id.buttonShare);
        buttonShare.setVisibility(View.INVISIBLE);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIt(((BitmapDrawable) imageView.getDrawable()).getBitmap(), textView.getText().toString());
            }
        });

        Button buttonClassify = findViewById(R.id.buttonClassify);
        buttonClassify.setVisibility(View.VISIBLE);
        buttonClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    Toast.makeText(getApplicationContext(), "Permission not granted", Toast.LENGTH_LONG).show();
                }

            }
        });

        Button buttonGallery = findViewById(R.id.buttonGallery);
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                    startActivity(intent);
                }
            }
        });

        initTensorFlowAndLoadModel();

        if (isStoragePermissionGranted()) {
            classifyImages();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TFClassifier.create(getAssets(), MODEL_PATH, LABEL_PATH);

                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_PERMISSION_CODE);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) throws Exception {
        if (isStoragePermissionGranted()) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        } else {
            throw new Exception("Couldn't share");
        }
    }

    void shareIt(Bitmap bitmap, String string) {
        try {
            Uri uri = getImageUri(getApplicationContext(), bitmap);
            Intent iShare = new Intent(Intent.ACTION_SEND);
            iShare.setType("image/*");
            iShare.putExtra(Intent.EXTRA_TEXT, string);
            iShare.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(iShare, "Choose app to share photo with"));
        } catch (Exception e) {
            Toast.makeText(this, "Couldn't share the result", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                classifyImages();
            } else {
                Toast.makeText(this, "External storage permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (isStoragePermissionGranted()) {
                Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                imageView.setImageBitmap(bitmap);
                final List<TFClassifier.Recognition> results = classifier.recognizeImage(bitmap);
                textView.setText(results.toString());
                buttonShare.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Couldn't get an image", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void classifyImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ClassifiedImage> allImages = AppDatabase.getDatabase(MainActivity.this.getApplicationContext()).classifiedImageDao().getAllFirstClasses();

                String path;
                Integer timestamp;
                Uri uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Uri uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

                String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATE_MODIFIED};

                Cursor cursorExternal = MainActivity.this.getContentResolver().query(uriExternal, projection, "_data IS NOT NULL", null, null);
                Cursor cursorInternal = MainActivity.this.getContentResolver().query(uriInternal, projection, "_data IS NOT NULL", null, null);
                Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});
                while (cursor.moveToNext()) {
                    try {
                        path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                        timestamp = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)));

                        if (findItem(path, timestamp, allImages)) {
                            continue;
                        }

                        Log.d(path, timestamp.toString());
                        Bitmap bitmap = getBitmapFromFilePath(path);
                        List<TFClassifier.Recognition> results = classifier.recognizeImage(bitmap);

                        List<ClassifiedImage> classifiedImages = new ArrayList<>();

                        int counter = 1;
                        for (TFClassifier.Recognition cr : results) {
                            ClassifiedImage classifiedImage = new ClassifiedImage();
                            classifiedImage.dataPath = path;
                            classifiedImage.className = cr.getTitle();
                            classifiedImage.fitnessPercent = cr.getConfidence();
                            classifiedImage.modifiedDate = timestamp;
                            classifiedImage.rankingPosition = counter;
                            counter++;

                            classifiedImages.add(classifiedImage);
                        }
                        AppDatabase.getDatabase(MainActivity.this.getApplicationContext()).classifiedImageDao().insertList(classifiedImages);
                    } catch (Exception ignored) {
                    }
                }
                cursor.close();

            }
        }).start();
    }

    private Boolean findItem(String path, Integer timestamp, List<ClassifiedImage> images) {
        for (ClassifiedImage img : images) {
            if (path.equals(img.dataPath) && timestamp.equals(img.modifiedDate)) {
                return true;
            }
        }
        return false;
    }

    private Bitmap getBitmapFromFilePath(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }
}
