package comp5216.sydney.edu.au.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import comp5216.sydney.edu.au.afinal.entity.Account;
import comp5216.sydney.edu.au.afinal.entity.FollowRelationItem;

public class HomePageActivity extends AppCompatActivity {
    private FirebaseFirestore mFireDB;
    int loginAccountId = 1;
    int viewAccountId = 2;
    Account loginAccount, tobefollowAccount;
    ArrayList<Integer> followerIDs = new ArrayList<>();
    ArrayList<Integer> followingIDs = new ArrayList<>();
    File localFile = null;
    Button followBtn;
    boolean followed = false;
    public static final int  REQUEST_CODE_FOLLOWING = 527;

    public void onFollowingClick(View view) {
        getAndShowAccounts(followingIDs);
    }

    public void onFollowerClick(View view) {
        getAndShowAccounts(followerIDs);
    }

    public void onFollowBtnClick(View view) {
        if(!followed)
            follow(loginAccountId, viewAccountId);
        else
            unfollow(loginAccountId, viewAccountId);
    }

    private void unfollow(int loginAccountId, int viewAccountId) {
        if(loginAccountId == viewAccountId)
            return;

        mFireDB.collection("FollowRelation").whereEqualTo("followerID", loginAccountId).whereEqualTo("followeeID", viewAccountId).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                doc.getReference().delete().addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                refreshFollowingStatusUI();
                                                refreshFollowerStatusUI();
                                            }
                                        }
                                );
                            }
                        }
                    }}
        );

    }

    private void follow(int loginAccountId, int viewAccountId) {
        if(loginAccountId == viewAccountId)
            return;

        mFireDB.collection("FollowRelation").whereEqualTo("followerID", loginAccountId).whereEqualTo("followeeID", viewAccountId).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if (task.getResult().size() == 0)
                            {
                                FollowRelationItem item = new FollowRelationItem();
                                item.setFollowerID(loginAccountId);
                                item.setFolloweeID(viewAccountId);
                                mFireDB.collection("FollowRelation").add(item).addOnCompleteListener(
                                        new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                refreshFollowingStatusUI();
                                                refreshFollowerStatusUI();
                                            }
                                        }
                                );
                            }
                        }
                    }}
        );
    }

    private void getAndShowAccounts(ArrayList<Integer> accountIDs)
    {
        mFireDB.collection("Accounts").whereIn("AccountID", accountIDs).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            ArrayList<Integer> accountIDs = new ArrayList<>();
                            ArrayList<String> accountNames = new ArrayList<>();
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                Account cAccount = doc.toObject(Account.class);
                                accountIDs.add(Integer.parseInt(cAccount.getAccountID()));
                                accountNames.add(cAccount.getName());
                            }

                            if(accountIDs.size() > 0) {
                                Intent intent = new Intent(HomePageActivity.this, AccountListActivity.class);

                                intent.putIntegerArrayListExtra("AccountIDs", accountIDs);
                                intent.putStringArrayListExtra("AccountNames", accountNames);
                                startActivityForResult(intent, REQUEST_CODE_FOLLOWING);
                            }

                        }
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mFireDB = FirebaseFirestore.getInstance();
        ImageView imageView = findViewById(R.id.IconImageView);
        followBtn = findViewById(R.id.FollowButton);

        mFireDB.collection("Accounts").whereEqualTo("AccountID", loginAccountId).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                loginAccount = doc.toObject(Account.class);
                                break;
                            }
                        }
                    }});
        refreshViewAccountUI();
    }

    void refreshViewAccountUI()
    {
        mFireDB.collection("Accounts").whereEqualTo("AccountID", viewAccountId).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                tobefollowAccount = doc.toObject(Account.class);
                                TextView textViewName = findViewById(R.id.NameTextView);
                                textViewName.setText(tobefollowAccount.getName());

                                try {
                                    localFile = File.createTempFile("images", "jpg");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                refreshFollowingStatusUI();
                                refreshFollowerStatusUI();

                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(tobefollowAccount.getIcon());
                                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        ImageView iv = findViewById(R.id.IconImageView);

                                        iv.setImageURI(Uri.fromFile(localFile));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.e(" c", "cant get file");
                                    }
                                });
                                break;
                            }
                        }
                    }}
        );
    }
    void refreshFollowingStatusUI()
    {
        mFireDB.collection("FollowRelation").whereEqualTo("followerID", viewAccountId).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        followingIDs.clear();
                        for(QueryDocumentSnapshot doc : task.getResult())
                        {
                            FollowRelationItem item = doc.toObject(FollowRelationItem.class);
                            followingIDs.add(item.getFolloweeID());
                        }
                        TextView textView = findViewById(R.id.FollowingTextView);
                        if (followingIDs.size() != 0)
                            textView.setText("" +  followingIDs.size());
                        else
                            textView.setText("0");
                    }
                }
        );
    }

    void refreshFollowerStatusUI()
    {
        mFireDB.collection("FollowRelation").whereEqualTo("followeeID", viewAccountId).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            followerIDs.clear();
                            followed = false;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                FollowRelationItem item = doc.toObject(FollowRelationItem.class);
                                followerIDs.add(item.getFollowerID());
                                if(loginAccountId == item.getFollowerID()) {
                                    followed = true;
                                }
                            }

                            TextView textView = findViewById(R.id.FollowerTextView);
                            if (followerIDs.size() != 0)
                                textView.setText("" +  followerIDs.size());
                            else
                                textView.setText("0");

                            if(followed)
                                followBtn.setText("UNFOLLOW");
                            else
                                followBtn.setText("Follow");
                        }
                    }}
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        int newAccountID = data.getIntExtra("AccountID", viewAccountId);
        if(viewAccountId != newAccountID) {
            viewAccountId = newAccountID;
            refreshViewAccountUI();
        }
    }


}