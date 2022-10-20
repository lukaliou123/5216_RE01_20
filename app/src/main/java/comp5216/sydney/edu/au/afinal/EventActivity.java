package comp5216.sydney.edu.au.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.youth.banner.Banner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.afinal.database.Firebase;
import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.entity.Events;
import comp5216.sydney.edu.au.afinal.util.ImageTask;
import comp5216.sydney.edu.au.afinal.util.NetUtil;

public class EventActivity extends AppCompatActivity {
    public static int REQUEST_CODE_EVENT = 502;

    private ImageButton goBack;

    private ImageView userAvatar;
    private TextView username;
    private TextView date;
    private Button toFollow;
    private TextView title;
    private TextView description;
    private TextView location;
    private TextView likeNum;
    private Banner<Bitmap, MyBannerAdapter> banner;

    private List<Bitmap> imageList = new ArrayList<>();
    private EventEntity eventEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        Events events = Events.getSingleton();

        eventEntity = events.get();//(EventEntity)getIntent().getSerializableExtra("event");
        initView();
        show(eventEntity);
        loadImage();
    }


    private void initView(){
        goBack = findViewById(R.id.event_tit_back);
        goBack.setOnClickListener(view -> onBackPressed());
        userAvatar = findViewById(R.id.event_head_portrait);
        username = findViewById(R.id.event_text_username);
        date = findViewById(R.id.event_text_date);
        toFollow = findViewById(R.id.event_btn_follow);
        toFollow.setOnClickListener(view -> {});
        title = findViewById(R.id.event_title);
        description = findViewById(R.id.event_content);
        location = findViewById(R.id.event_text_location);
        likeNum = findViewById(R.id.event_favorite_num);
        banner = findViewById(R.id.event_banner);
        banner.setAdapter(new MyBannerAdapter(imageList));
    }

    private void show(EventEntity event){
        username.setText(event.getBlogger());
        date.setText(event.getTimeStamp().toString());
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        location.setText(event.getLocation());
        likeNum.setText(event.getLikes()+"");
        //show avatar of blogger
//// Reference to an image file in Cloud Storage
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("pig.jpeg");
        //System.out.println("look!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "+ref.getDownloadUrl().toString());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println("look!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "+uri);
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(userAvatar);
                // Got the download URL for 'users/me/profile.png'
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    private void loadAvatar(){


    }

    public void onAvatarClick(View view) {
        Intent intent = new Intent(EventActivity.this, HomePageActivity.class);
        intent.putExtra("ViewAccountID", eventEntity.getBlog_ref());

        startActivityForResult(intent, REQUEST_CODE_EVENT);
    }

    private void loadImage(){
        List<String> imageUrls = eventEntity.getImageUrl();
        if(imageUrls == null || imageUrls.size() == 0){
            return;
        }
        for(int i = 0; i < imageUrls.size(); i ++){
            new ImageTask(new ImageTask.CallBack() {
                @Override
                public void getResults(Bitmap result, String url) {
                    imageList.add(result);
                }
            }).execute(imageUrls.get(i));
        }
    }

    public  void onFollowClick(View view)
    {
        follow();
    }

    private void follow(){
        NetUtil.follow(Firebase.getInstance().getLocalUser().getAccountID(), eventEntity.getBlog_ref(), null);
    }







}