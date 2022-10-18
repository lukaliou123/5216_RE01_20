package comp5216.sydney.edu.au.afinal.entity;

import android.location.Location;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EventEntity implements Serializable {
    private String blogger;
    private Timestamp time;
    private String title;
    private String description;
    private List<String> imageUrl;
    private int likes;
    private String address;
    private GeoPoint location;

    public EventEntity(){}

    public EventEntity(String blogger, Timestamp time, String title, String description, List<String> imageUrl,
                       int likes, String address, GeoPoint location) {
        this.blogger = blogger;
        this.time = time;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.likes = likes;
        this.address = address;
        this.location = location;
    }

    public String getUsername() {
        return blogger;
    }

    public void setUsername(String username) {
        this.blogger = username;
    }

    public Timestamp getDate() {
        return time;
    }

    public void setDate(Timestamp time) {
        this.time = time;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEntity that = (EventEntity) o;
        return likes == that.likes && Objects.equals(blogger, that.blogger) && Objects.equals(time,
                that.time) && Objects.equals(title, that.title) && Objects.equals(description, that.description)
                && Objects.equals(imageUrl, that.imageUrl) && Objects.equals(address, that.address) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blogger, time, title, description, imageUrl, likes, address, location);
    }
}
