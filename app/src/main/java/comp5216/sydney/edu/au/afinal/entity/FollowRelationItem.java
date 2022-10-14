package comp5216.sydney.edu.au.afinal.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "FollowRelation")
public class FollowRelationItem {
    public int getFollowerID() {
        return followerID;
    }

    public void setFollowerID(int FollowerID) {
        this.followerID = FollowerID;
    }

    @ColumnInfo(name = "followerID")
    int followerID;

    public int getFolloweeID() {
        return followeeID;
    }

    public void setFolloweeID(int FolloweeID) {
        this.followeeID = FolloweeID;
    }

    @ColumnInfo(name = "followeeID")
    int followeeID;


}
