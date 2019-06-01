package put.poznan.tensorflow;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
public interface ClassifiedImageDao {
    @Query("SELECT * FROM classifiedimage")
    List<ClassifiedImage> getAll();

    @Query("SELECT * FROM classifiedimage WHERE data_path IN (:dataPaths)")
    List<ClassifiedImage> getAllByDataPaths(String[] dataPaths);

    @Query("SELECT * FROM classifiedimage WHERE data_path = (:dataPath)")
    List<ClassifiedImage> getAllForDataPath(String dataPath);

    @Query("SELECT * FROM classifiedimage WHERE class_name = (:className) AND ranking_position = 1")
    List<ClassifiedImage> getAllForClassName(String className);

    @Query("SELECT * FROM classifiedimage WHERE ranking_position = 1")
    List<ClassifiedImage> getAllFirstClasses();

    @Insert
    void insertList(List<ClassifiedImage> classifiedImages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertClassified(ClassifiedImage image);

    @Delete
    void delete(ClassifiedImage classifiedImage);

    @Query("SELECT data_path AS dataPath, class_name AS className , count(*) AS countClassItems "
            + "FROM classifiedimage WHERE ranking_position = 1 GROUP BY class_name")
    public List<ClassCount> getClassCounts();

    static class ClassCount {
        public String dataPath;
        public String className;
        public Integer countClassItems;
    }
}