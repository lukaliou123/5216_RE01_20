package comp5216.sydney.edu.au.afinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import comp5216.sydney.edu.au.afinal.database.Firebase;
import comp5216.sydney.edu.au.afinal.entity.Account;
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
    private MyBannerAdapter bannerAdapter;

    private String address;
    private Location l;
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
        title = findViewById(R.id.add_title);
        content = findViewById(R.id.add_content);
        location = findViewById(R.id.add_text_location);
        l = getLastKnownLocation();
        address = getAddress(l.getLatitude(),l.getLongitude()).getLocality();
        location.setText(address);
        cancel.setOnClickListener(view -> onBackPressed());
        confirm.setOnClickListener(view -> uploadData());
        selectImageBtn.setOnClickListener(view -> chooseImage());
        banner = findViewById(R.id.add_banner);
        bannerAdapter = new MyBannerAdapter(imageList);
        banner.setAdapter(bannerAdapter);
        bannerAdapter.setOnBannerListener(new OnBannerListener<Bitmap>() {
            @Override
            public void OnBannerClick(Bitmap data, int position) {
                 Log.e("TAG","点击"+position);
            }
        });
        banner.setDatas(imageList);
    }

    private void uploadData(){
        Account curUser = Firebase.getInstance().getLocalUser();
        String titleVal = title.getText().toString();
        String description = content.getText().toString();
        List<String> urls = new ArrayList<>();
        GeoPoint geo = new GeoPoint(l.getLatitude(),l.getLongitude());
        for(int i = 0; i < photoPath.size(); i ++){
            int finalI = i;
            NetUtil.uploadMediaFile(photoPath.get(i)).addOnCompleteListener(new OnCompleteListener<Uri>() {
               @Override
               public void onComplete(@NonNull Task<Uri> task) {
                   if(task.isSuccessful()){
                       urls.add(task.getResult().toString());
                       if(finalI == photoPath.size() - 1){
//                           EventEntity event = new EventEntity("test","??",description,new Timestamp(new Date()),geo,
//                                   urls,0,address,titleVal);
                           EventEntity event = new EventEntity(curUser.getUsername(),curUser.getAccountID(),description,
                                   new Timestamp(new Date()),geo, urls,0,address,titleVal);
                           NetUtil.uploadEvent(event, AddActivity.this);
                       }
                   }
               }
           });
        }


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
                image = BitmapFactory.decodeFile(picturePath);
                photoPath.add(picturePath);
                imageList.add(image);

            }

        }

    }

    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
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

    private Location getLastKnownLocation(){
        Location location = null;
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            return null;
        }
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            @SuppressLint("MissingPermission")
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (location == null || l.getAccuracy() < location.getAccuracy()) {
                // Found best last known location: %s", l);
                location = l;
            }
        }
        return location;
    }

    private Address getAddress(double latitude, double longitude) {
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(AddActivity.this);
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList != null) {
            return addressList.get(0);
        }
        return null;
    }


}