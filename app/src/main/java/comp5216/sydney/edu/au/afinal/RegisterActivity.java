package comp5216.sydney.edu.au.afinal;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    private EditText id,name,password, vPassword;
    private ImageView profile;
    private Uri imageUri;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        id = findViewById(R.id.id);
        password=findViewById(R.id.password);
        vPassword=findViewById(R.id.vpw);
        name=findViewById(R.id.name);
        profile=findViewById(R.id.img_profile);
        mAuth = FirebaseAuth.getInstance();

        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                photoLauncher.launch(intent);
            }

        });
    }

    ActivityResultLauncher<Intent> photoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    profile.setImageURI(result.getData().getData());
                    imageUri = result.getData().getData();
                } else {
                    Toast.makeText(this, "Picture wasn't selected!", Toast.LENGTH_SHORT).show();
                }
            }
    );

    public void signup(View v) {
        String email = id.getText().toString();
        String username = name.getText().toString();
        String code = password.getText().toString();
        String vCode = vPassword.getText().toString();
        if (email.equals("")||username.equals("")){
            Toast.makeText(RegisterActivity.this, "name or email is empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!code.equals(vCode)){
            Toast.makeText(RegisterActivity.this, "Passwords are not same",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, code)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        FirebaseAuth.getInstance().createUserWithEmailAndPassword(id.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user").child(uid);
//
//                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            if(task.isSuccessful()){
//
//                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Uri> task) {
//
//                                        String imageurl = task.toString();
//
//                                        UserModel userModel=new UserModel();
//                                        userModel.name=name.getText().toString();
//                                        userModel.uid=uid;
//                                        userModel.imageurl=imageurl;
//
//                                    }
//                                });
//                            }else{
//
//                                Toast.makeText(RegisterActivity.this,"error",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//                }else{
//
//                    Toast.makeText(RegisterActivity.this,"error",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }


}