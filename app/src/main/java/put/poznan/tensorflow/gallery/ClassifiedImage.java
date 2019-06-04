package put.poznan.tensorflow.gallery;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices={@Index(value = {"data_path", "ranking_position"}, unique = true)})
public class ClassifiedImage {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "data_path")
    public String dataPath;

    @ColumnInfo(name = "date_modified")
    public Integer modifiedDate;

    @ColumnInfo(name = "class_name")
    public String className;

    @ColumnInfo(name = "fitness_percent")
    public Float fitnessPercent;

    @ColumnInfo(name = "ranking_position")
    public int rankingPosition;
}