package put.poznan.tensorflow.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.util.Objects;

public class ReceiverBitmapActivity extends ReceiverActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.bitmap = (Bitmap) Objects.requireNonNull(intent.getExtras()).get("data");
        this.classify();
    }
}
