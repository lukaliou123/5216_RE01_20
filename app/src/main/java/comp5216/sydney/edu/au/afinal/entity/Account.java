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

    @ColumnInfo(name = "Username")
    private String Username;

    @ColumnInfo(name = "icon")
    private String Icon;

    @ColumnInfo(name = "Gender")
    private String Gender;

    @ColumnInfo(name = "Email")
    private String Email;

    @ColumnInfo(name = "Birth")
    private String Birth;

    public Account()
    {

    }

    public Account(@NonNull String accountID, String name, String icon, String gender, String email, String birthDate) {
        AccountID = accountID;
        Username = name;
        Icon = icon;
        Gender = gender;
        Email = email;
        Birth = birthDate;
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

    public String getBirth() {
        return Birth;
    }

    public void setBirth(String birth) {
        Birth = birth;
    }


    public String getAccountID() {
        return AccountID;
    }

    public void setAccountID(String accountID) {
        AccountID = accountID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }
}
