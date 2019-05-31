package put.poznan.tensorflow;

import android.content.Intent;
import android.os.Bundle;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ReceiverURLActivity extends ReceiverActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        URL url = (URL) Objects.requireNonNull(intent.getExtras()).get("data");
        try {
            this.bitmap = new BitmapDownloader().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        this.classify();
    }
}

