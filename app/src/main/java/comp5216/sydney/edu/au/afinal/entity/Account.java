package comp5216.sydney.edu.au.afinal.entity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "AccountID")
    private String AccountID;

    @ColumnInfo(name = "Name")
    private String Name;

    @ColumnInfo(name = "icon")
    private String Icon;

    private String Gender;

    private String Email;

    private String BirthDate;

    public Account(@NonNull String accountID, String name, String icon, String gender, String email, String birthDate) {
        AccountID = accountID;
        Name = name;
        Icon = icon;
        Gender = gender;
        Email = email;
        BirthDate = birthDate;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String birthDate) {
        BirthDate = birthDate;
    }


    public String getAccountID() {
        return AccountID;
    }

    public void setAccountID(String accountID) {
        AccountID = accountID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }
}
