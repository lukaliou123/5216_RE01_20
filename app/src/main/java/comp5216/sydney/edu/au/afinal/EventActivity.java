package comp5216.sydney.edu.au.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.afinal.database.Firebase;
import comp5216.sydney.edu.au.afinal.entity.Account;
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
    private MyBannerAdapter bannerAdapter;
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
        loadImage();
        show(eventEntity);

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
        bannerAdapter = new MyBannerAdapter(imageList);
        banner.setAdapter(bannerAdapter);
    }

    private void show(EventEntity event) {
        username.setText(event.getBlogger());
        date.setText(event.getTimeStamp().toDate().toString());
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        location.setText(event.getLocation());
        likeNum.setText(event.getLikes()+"");
        //show avatar of blogger
        String userId = eventEntity.getBlog_ref();
        Firebase FB = Firebase.getInstance();
        FB.getUser(userId).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Account followUser = setAccount(task);
                    String followIcon = followUser.getIcon();
                    Glide.with(getApplicationContext())
                            .load(followIcon)
                            .into(userAvatar);
                }

            }
        });
    }

    private Account setAccount(Task task){
        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
        String uid1 = (String) document.getData().get("AccountID");
        String username = (String) document.getData().get("Username");
        String icon = (String) document.getData().get("Icon");
        String email = (String) document.getData().get("Email");
        String gender = (String) document.getData().get("Gender");
        String birth = (String) document.getData().get("Birth");
        assert uid1 != null;
        return new Account(uid1, username, icon, gender, email, birth);
    }

    private void loadAvatar(){


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
                    if(result != null) {
                        imageList.add(result);
                        bannerAdapter.notifyDataSetChanged();
                    }
                }
            }).execute(imageUrls.get(i));
            Log.e(" comp5216.sydney.edu.au.afinal", imageUrls.get(i));
        }
    }

    public  void onFollowClick(View view)
    {
        follow();
    }

    private void follow(){
        NetUtil.follow(Firebase.getInstance().getLocalUser().getAccountID(), eventEntity.getBlog_ref(), null);

    }

    public void onAvatarClick(View view) {
        Intent intent = new Intent(EventActivity.this, HomePageActivity.class);
        intent.putExtra("ViewAccountID", eventEntity.getBlog_ref());

        startActivityForResult(intent, REQUEST_CODE_EVENT);
    }







}