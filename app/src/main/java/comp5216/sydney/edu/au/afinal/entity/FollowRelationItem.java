package comp5216.sydney.edu.au.afinal.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "FollowRelation")
public class FollowRelationItem {
    public String getFollowerID() {
        return followerID;
    }

    public void setFollowerID(String FollowerID) {
        this.followerID = FollowerID;
    }

    @ColumnInfo(name = "followerID")
    String followerID;

    public String getFolloweeID() {
        return followeeID;
    }

    public void setFolloweeID(String FolloweeID) {
        this.followeeID = FolloweeID;
    }

    @ColumnInfo(name = "followeeID")
    String followeeID;


}
