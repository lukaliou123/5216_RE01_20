package comp5216.sydney.edu.au.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import comp5216.sydney.edu.au.afinal.database.Firebase;
import comp5216.sydney.edu.au.afinal.entity.Account;
import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.entity.Events;
import comp5216.sydney.edu.au.afinal.entity.FollowRelationItem;
import comp5216.sydney.edu.au.afinal.util.NetUtil;

public class HomePageActivity extends AppCompatActivity {
    private FirebaseFirestore mFireDB;
    Account loginAccount, tobefollowAccount;
    String loginAccountPath, tobefollowAccountPath;
    ArrayList<String> followerIDs = new ArrayList<>();
    ArrayList<String> followingIDs = new ArrayList<>();
    ArrayList<EventEntity> events = new ArrayList<>();
    File localFile = null;
    Button followBtn;
    private ImageButton goBack;
    boolean followed = false;
    public static final int  REQUEST_CODE_FOLLOWING = 527;
    public static final int REQUEST_CODE_EVENT = 528;
    EventAdapter eventAdapter;


    public void onFollowingClick(View view) {
        getAndShowAccounts(followingIDs);
    }

    public void onFollowerClick(View view) {
        getAndShowAccounts(followerIDs);
    }

    public void onFollowBtnClick(View view) {
        if (!followed)
            NetUtil.follow(loginAccountPath,
                    tobefollowAccountPath,
                    new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            refreshFollowingStatusUI();
                            refreshFollowerStatusUI();
                        }
                    });
        else
            NetUtil.unfollow(loginAccountPath,
                    tobefollowAccountPath,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            refreshFollowingStatusUI();
                            refreshFollowerStatusUI();
                        }
                    });
    }

    private void getAndShowAccounts(ArrayList<String> accountIDs)
    {
        mFireDB.collection("Accounts").whereIn("AccountID", accountIDs).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            ArrayList<String> accountIDs = new ArrayList<>();
                            ArrayList<String> accountNames = new ArrayList<>();
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                Account cAccount = doc.toObject(Account.class);
                                accountIDs.add(cAccount.getAccountID());
                                accountNames.add(cAccount.getUsername());
                            }

                            if(accountIDs.size() > 0) {
                                Intent intent = new Intent(HomePageActivity.this, AccountListActivity.class);

                                intent.putStringArrayListExtra("AccountIDs", accountIDs);
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

        // goback
        goBack = findViewById(R.id.profile_back);
        goBack.setOnClickListener(view -> onBackPressed());

        ImageView imageView = findViewById(R.id.IconImageView);
        followBtn = findViewById(R.id.FollowButton);
        eventAdapter = new EventAdapter(events, this);
        ListView listViewEvents = findViewById(R.id.lstViewEvent);
        listViewEvents.setAdapter(eventAdapter);
        listViewEvents.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        EventEntity event = (EventEntity) adapterView.getItemAtPosition(i);
                        Events.getSingleton().add(event);
                        Intent intent = new Intent(HomePageActivity.this, EventActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_EVENT);
                    }
                }
        );
        loginAccount = Firebase.getInstance().getLocalUser();
        loginAccountPath = loginAccount.getAccountID();
        Intent intent = getIntent();
        tobefollowAccountPath = intent.getStringExtra("ViewAccountID");
        refreshViewAccountUI();
    }

    void refreshViewAccountUI()
    {
        mFireDB.collection("Accounts").whereEqualTo("AccountID", tobefollowAccountPath).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc : task.getResult())
                            {
                                tobefollowAccount = doc.toObject(Account.class);
                                TextView textViewName = findViewById(R.id.NameTextView);
                                textViewName.setText(tobefollowAccount.getUsername());

                                try {
                                    localFile = File.createTempFile("images", "jpg");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                refreshFollowingStatusUI();
                                refreshFollowerStatusUI();
                                refreshEventsUI();

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

    private void refreshEventsUI() {
        mFireDB.collection("Events").whereEqualTo("blog_ref", tobefollowAccountPath).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            events.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                EventEntity event = EventEntity.FromQueryDocument(doc);
                                events.add(event);
                            }
                            TextView eventCount = findViewById(R.id.EventTextView);
                            eventCount.setText(Integer.toString(events.size()));
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
    }

    void refreshFollowingStatusUI()
    {
        mFireDB.collection("FollowRelation").whereEqualTo("followerID", tobefollowAccountPath).get().addOnCompleteListener(
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
        mFireDB.collection("FollowRelation").whereEqualTo("followeeID", tobefollowAccountPath).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            followerIDs.clear();
                            followed = false;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                FollowRelationItem item = doc.toObject(FollowRelationItem.class);
                                followerIDs.add(item.getFollowerID());
                                if(loginAccountPath.equals(item.getFollowerID())) {
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
                                followBtn.setText("FOLLOW");
                        }
                    }}
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_FOLLOWING) {
            if (data != null && data.hasExtra("AccountID")) {
                String newAccountID = data.getStringExtra("AccountID");
                if (tobefollowAccountPath != newAccountID) {
                    tobefollowAccountPath = newAccountID;
                    refreshViewAccountUI();
                }
            }
        }
        else if(requestCode == REQUEST_CODE_EVENT)
        {
            refreshEventsUI();
        }
    }

    public class EventAdapter extends ArrayAdapter<EventEntity> implements View.OnClickListener{

        public EventAdapter(ArrayList<EventEntity> data, Context context) {
            super(context, R.layout.activity_follow_event, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            EventEntity event = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_follow_event, parent, false);
            }
            TextView Title  = convertView.findViewById(R.id.eventTitle);
            Title.setText(event.getTitle());
            TextView Description = convertView.findViewById(R.id.eventDesciption);
            Description.setText(event.getDescription());
            TextView Like = convertView.findViewById(R.id.eventLike);
            Like.setText(Integer.toString(event.getLikes()));

            if(event.getImageUrl().size() > 0 && !event.getImageUrl().get(0).isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(event.getImageUrl().get(0));
                    ImageView iv = convertView.findViewById(R.id.eventImageView);
                    Picasso.get().load(imageUri).into(iv);
                } catch (Exception e) {
                    Log.e("Firebase", "Can't get image from url:" + event.getImageUrl().get(0));
                }
            }

            // Return the completed view to render on screen
            return convertView;
        }

        @Override
        public void onClick(View view) {

        }
    }


}