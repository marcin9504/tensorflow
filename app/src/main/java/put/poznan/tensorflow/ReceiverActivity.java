package put.poznan.tensorflow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

abstract class ReceiverActivity extends AppCompatActivity {

    Bitmap bitmap;
    final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
    final boolean QUANT = true;
    final String LABEL_PATH = "labels.txt";
    final int INPUT_SIZE = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void classify() {
        try {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(Objects.requireNonNull(this.bitmap), INPUT_SIZE, INPUT_SIZE, false);
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
            e.printStackTrace();
        }
    }
}
