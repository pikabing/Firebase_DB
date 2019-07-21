package android.example.zersey;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Events implements Parcelable {

    public Events() {

    }

    private String title, desc, category, image;
    private long likes, comments;

    protected Events(Parcel in) {
        title = in.readString();
        desc = in.readString();
        category = in.readString();
        image = in.readString();
        likes = in.readLong();
        comments = in.readLong();
    }

    public static final Creator<Events> CREATOR = new Creator<Events>() {
        @Override
        public Events createFromParcel(Parcel in) {
            return new Events(in);
        }

        @Override
        public Events[] newArray(int size) {
            return new Events[size];
        }
    };

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(desc);
        parcel.writeString(category);
        parcel.writeString(image);
        parcel.writeLong(likes);
        parcel.writeLong(comments);
    }
}
