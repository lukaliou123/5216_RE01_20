package comp5216.sydney.edu.au.afinal.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "FollowRelation")
public class FollowRelationItem {
    public int getFollowerID() {
        return FollowerID;
    }

    public void setFollowerID(int followerID) {
        FollowerID = followerID;
    }

    @ColumnInfo(name = "FollowerID")
    int FollowerID;

    public int getFolloweeID() {
        return FolloweeID;
    }

    public void setFolloweeID(int followeeID) {
        FolloweeID = followeeID;
    }

    @ColumnInfo(name = "FollowerID")
    int FolloweeID;


}
