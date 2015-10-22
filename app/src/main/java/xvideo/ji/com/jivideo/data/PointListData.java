package xvideo.ji.com.jivideo.data;


import android.os.Parcel;
import android.os.Parcelable;

public class PointListData implements Parcelable {

    private String title;
    private int point;
    private String time;

    public PointListData() {

    }

    public PointListData(Parcel source) {
        title = source.readString();
        point = source.readInt();
        time = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(title);
        dest.writeInt(point);
        dest.writeString(time);
    }

    public static final Creator<PointListData> CREATOR = new Creator<PointListData>() {
        @Override
        public PointListData createFromParcel(Parcel parcel) {
            return new PointListData(parcel);
        }

        @Override
        public PointListData[] newArray(int i) {
            return new PointListData[i];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
