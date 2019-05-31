package put.poznan.tensorflow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

class BitmapDownloader extends AsyncTask<URL, Bitmap, Bitmap> {

    @Override
    protected Bitmap doInBackground(URL... urls) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
            publishProgress(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
