package comp5216.sydney.edu.au.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.util.ImageTask;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        initView();
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
    }

    private void show(EventEntity event){
        username.setText(event.getUsername());
        date.setText(event.getDate().toString());
        title.setText(event.getTitle());
        description.setText(event.getDescription());
        location.setText(event.getAddress());
        likeNum.setText(event.getLikes());
        //show avatar of blogger
//        new ImageTask(new ImageTask.CallBack() {
//            @Override
//            public void getResults(Bitmap result, String url) {
//                userAvatar.setImageBitmap(result);
//            }
//        }).execute(event.getImageUrl());
    }

    private void follow( ){


    }





}