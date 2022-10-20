package comp5216.sydney.edu.au.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.afinal.database.Firebase;
import comp5216.sydney.edu.au.afinal.entity.Account;
import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.entity.Events;

public class EventActivity extends AppCompatActivity {


    private ImageButton goBack;

    private ImageView userAvatar;
    private TextView username;
    private TextView date;
    private Button toFollow;
    private TextView title;
    private TextView description;
    private TextView location;
    private TextView likeNum;
    private Banner<Bitmap, BannerImageAdapter<Bitmap>> banner;

    private List<Bitmap> imageList;
    private EventEntity eventEntity;
    private Account followUser;

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
        title = findViewById(R.id.event_title);
        description = findViewById(R.id.event_content);
        location = findViewById(R.id.event_text_location);
        likeNum = findViewById(R.id.event_favorite_num);
        banner = findViewById(R.id.event_banner);
    }

    private void show(EventEntity event){
        username.setText(event.getBlogger());
        date.setText(event.getTimeStamp().toDate().toString());
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        location.setText(event.getLocation());
        likeNum.setText(event.getLikes()+"");
        //show avatar of blogger
        String userId = eventEntity.getBlog_ref();
        Firebase FB = Firebase.getInstance();
        FB.setFollowUser(userId);
        followUser = FB.getFollowUserUser();
        String followIcon = followUser.getIcon();
        Glide.with(getApplicationContext())
                .load(followIcon)
                .into(userAvatar);

    }

    private void loadAvatar(){


    }

    private void loadImage(){
        List<String> imageUrls = eventEntity.getImageUrl();
        if(imageUrls == null || imageUrls.size() == 0){
            return;
        }
        imageList = new ArrayList<>(imageUrls.size());
        for(int i = 0; i < imageUrls.size(); i ++){
            int finalI = i;
            banner.setAdapter(new BannerImageAdapter<Bitmap>(imageList){
                @Override
                public void onBindView(BannerImageHolder holder, Bitmap data, int position, int size) {
                       Glide.with(holder.itemView).load(imageUrls.get(finalI)).apply(RequestOptions.bitmapTransform(new
                               RoundedCorners(30))).into(holder.imageView);
                }
            });
        }
    }

    private void follow(){

    }







}