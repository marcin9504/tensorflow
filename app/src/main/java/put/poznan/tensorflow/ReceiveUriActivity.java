package put.poznan.tensorflow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

public class ReceiveUriActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
        final boolean QUANT = true;
        final String LABEL_PATH = "labels.txt";
        final int INPUT_SIZE = 224;

        try {
            Intent intent = getIntent();
            Uri uri = (Uri) Objects.requireNonNull(intent.getExtras()).get("data");
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(Objects.requireNonNull(bitmap), INPUT_SIZE, INPUT_SIZE, false);
            Classifier classifier = TFClassifier.create(getAssets(), MODEL_PATH, LABEL_PATH, INPUT_SIZE, QUANT);
            final List<Classifier.Recognition> results = classifier.recognizeImage(scaledBitmap);
            classifier.close();
            String result = results.toString();
            Intent rIntent = new Intent();
            rIntent.putExtra("result", result);
            setResult(Activity.RESULT_OK, rIntent);
            finish();

        } catch (Exception e) {
            System.out.println("Couldn't classify");
        }
    }
}
