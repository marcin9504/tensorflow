package put.poznan.tensorflow.service;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import put.poznan.tensorflow.classifier.TFClassifier;

abstract class ReceiverActivity extends AppCompatActivity {

    Bitmap bitmap;
    final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
    final String LABEL_PATH = "labels.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void classify() {
        try {
            if(isStoragePermissionGranted()) {
                TFClassifier classifier = TFClassifier.create(getAssets(), MODEL_PATH, LABEL_PATH);
                final List<TFClassifier.Recognition> results = classifier.recognizeImage(this.bitmap);
                classifier.close();
                String result = results.toString();
                Intent rIntent = new Intent();
                rIntent.putExtra("result", result);
                setResult(Activity.RESULT_OK, rIntent);
                finish();
            }
            else{
                Toast.makeText(this, "Couldn't get an image", Toast.LENGTH_LONG).show();
            }


        } catch (Exception e) {
            System.out.println("Couldn't classify");
            e.printStackTrace();
        }
    }
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
