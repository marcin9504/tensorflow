package put.poznan.tensorflow.service;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.Objects;

public class ReceiverUriActivity extends ReceiverActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri uri = (Uri) Objects.requireNonNull(intent.getExtras()).get("data");
        try {
            this.bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.classify();
    }
}
