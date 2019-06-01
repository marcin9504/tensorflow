package put.poznan.tensorflow;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ClassifiedImage.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    private static final String DB_NAME = "TensorflowApp.db";

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DB_NAME)
                            // .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ClassifiedImageDao classifiedImageDao();

    public static void destroyInstance() {
        INSTANCE = null;
    }
}