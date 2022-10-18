package comp5216.sydney.edu.au.afinal;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.Banner;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import comp5216.sydney.edu.au.afinal.entity.EventEntity;
import comp5216.sydney.edu.au.afinal.util.FormatUtil;
import comp5216.sydney.edu.au.afinal.util.NetUtil;

public class AddActivity extends AppCompatActivity {

    private ImageButton cancel;
    private ImageButton confirm;
    private Button selectImageBtn;
    private RoundedImageView imageView;
    private EditText title;
    private EditText content;
    private TextView location;
    private Bitmap image;

    private List<String> photoPath = new ArrayList<>();
    private List<Bitmap> imageList = new ArrayList<>();
    private Banner<Bitmap, MyBannerAdapter> banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        //hide the action bar
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        checkPermission();
        initView();
    }

    private void initView(){
        cancel = findViewById(R.id.add_tit_cancel);
        confirm = findViewById(R.id.add_tit_confirm);
        selectImageBtn = findViewById(R.id.add_btn);
        //imageView = findViewById(R.id.add_image);
        title = findViewById(R.id.add_title);
        content = findViewById(R.id.add_content);
        location = findViewById(R.id.add_text_location);
        cancel.setOnClickListener(view -> onBackPressed());
        confirm.setOnClickListener(view -> uploadData());
        selectImageBtn.setOnClickListener(view -> chooseImage());
        banner = findViewById(R.id.add_banner);
        banner.setAdapter(new MyBannerAdapter(imageList));
        banner.setDatas(imageList);
    }


    private void uploadData(){
        String titleVal = title.getText().toString();
        String description = content.getText().toString();
        List<String> urls = NetUtil.uploadMediaFiles(photoPath, AddActivity.this);
//        EventEntity event = new EventEntity("test","???",description,new Timestamp(new Date()),null,
//                urls,0,"???",titleVal);
//        NetUtil.uploadEvent(event);
    }

    private void chooseImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            File file = new File(picturePath);
            if(file.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picturePath, options);
                options.inSampleSize = FormatUtil.calculateInSampleSize(options, 100, 100);
                options.inJustDecodeBounds = false;
                image = BitmapFactory.decodeFile(picturePath, options);
                photoPath.add(picturePath);
                imageList.add(image);
            }

        }

    }

    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET};
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission(){
        boolean flag = true;
        for(int i = 0; i < permissions.length; i++){
            if(!(ActivityCompat.checkSelfPermission(this,permissions[i]) == PackageManager.PERMISSION_GRANTED)){
                flag=false;
            }
        }
        if(!flag){
            requestPermissions(permissions,100);
        }
    }


}