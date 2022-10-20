package comp5216.sydney.edu.au.afinal.entity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class EventEntity implements Serializable {
    private String Blogger;
    private String blog_ref;
    private String description;
    private transient Timestamp timeStamp;
    private transient GeoPoint geo;
    private List<String> imageUrl;
    private Integer likes;
    private String location;
    private String title;

    public EventEntity(String blogger, String blog_ref, String description, Timestamp timeStamp,
                       GeoPoint geo, List<String> imageUrl, Integer likes, String location, String title) {
        Blogger = blogger;
        this.blog_ref = blog_ref;
        this.description = description;
        this.timeStamp = timeStamp;
        this.geo = geo;
        this.imageUrl = imageUrl;
        this.likes = likes;
        this.location = location;
        this.title = title;
    }

    public EventEntity(){}

    public String getBlogger() {
        return Blogger;
    }

    public void setBlogger(String blogger) {
        Blogger = blogger;
    }

    public String getBlog_ref() {
        return blog_ref;
    }

    public void setBlog_ref(String blog_ref) {
        this.blog_ref = blog_ref;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public GeoPoint getGeo() {
        return geo;
    }

    public void setGeo(GeoPoint geo) {
        this.geo = geo;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEntity that = (EventEntity) o;
        return likes == that.likes && Objects.equals(Blogger, that.Blogger) && Objects.equals(blog_ref,
                that.blog_ref) && Objects.equals(description, that.description) && Objects.equals(timeStamp,
                that.timeStamp) && Objects.equals(geo, that.geo) && Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(location, that.location) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Blogger, blog_ref, description, timeStamp, geo, imageUrl, likes, location, title);
    }

    public static EventEntity FromQueryDocument(QueryDocumentSnapshot document)
    {
        GeoPoint geo = (GeoPoint) document.getData().get("geo");
        String title = (String) document.getData().get("title");
        String blogRef = (String) document.getData().get("blog_ref");
        String blogger = (String) document.getData().get("Blogger");
        Timestamp timestamp = (Timestamp) document.getData().get("timeStamp");
        String description = (String) document.getData().get("description");
        List<String> imageUrl = (List<String>) document.getData().get("imageUrl");
        long likesInLong = (Long) document.getData().get("likes");
        Integer likes = (int) likesInLong;
        String address = (String) document.getData().get("location");
        return new EventEntity(blogger,blogRef,description,timestamp,geo,imageUrl,likes,address,title);
    }
}
