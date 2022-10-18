package comp5216.sydney.edu.au.afinal.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;
import java.util.Objects;
@Entity(tableName = "Events")
public class EventEntity {
    @ColumnInfo(name = "Blogger")
    private String Blogger;
    @ColumnInfo(name = "timeStamp")
    private Timestamp timeStamp;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "imageUrl")
    private List<String> imageUrl;
    @ColumnInfo(name = "likes")
    private int likes;
    @ColumnInfo(name = "geo")
    private GeoPoint geo;
    @ColumnInfo(name = "location")
    private String location;
    @ColumnInfo(name = "blog_ref")
    private String blog_ref;

    public String getBlog_ref() {
        return blog_ref;
    }

    public void setBlog_ref(String blog_ref) {
        this.blog_ref = blog_ref;
    }

    public String getBlogger() {
        return Blogger;
    }

    public void setBlogger(String blogger) {
        this.Blogger = blogger;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public GeoPoint getGeo() {
        return geo;
    }

    public void setGeo(GeoPoint geo) {
        this.geo = geo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEntity that = (EventEntity) o;
        return likes == that.likes && Objects.equals(Blogger, that.Blogger) && Objects.equals(timeStamp,
                that.timeStamp) && Objects.equals(title, that.title) && Objects.equals(description, that.description)
                && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(geo, that.geo) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Blogger, timeStamp, title, description, imageUrl, likes, geo, location);
    }
}
