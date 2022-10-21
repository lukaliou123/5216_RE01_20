package comp5216.sydney.edu.au.afinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import comp5216.sydney.edu.au.afinal.database.Firebase;

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
        imageUri = null;
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
        if (email.equals("")||username.equals("")||code.equals("")){
            Toast.makeText(RegisterActivity.this, "name or email is empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }else if(code.length()<6){
            Toast.makeText(RegisterActivity.this, "Passwords should be longer than 6",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!code.equals(vCode)){
            Toast.makeText(RegisterActivity.this, "Passwords are not same",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if (imageUri == null){
            Toast.makeText(RegisterActivity.this, "please select a profile photo",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, code)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseuser = Firebase.getInstance().getCurrentUser();
                            String uid = firebaseuser.getUid();
                            if(imageUri != null){
                                Task<Uri> uriTask = Firebase.getInstance().uploadPhotoToStorage(uid, imageUri);
                                uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Firebase.getInstance().createAccountSnapshotInFireStore(uid, username, task.getResult().toString(),email).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    finish();
                                                }
                                            });
                                        }else{
                                            Log.d("Firebase", "task failed");
                                        }
                                    }
                                });
                            }else{
                                Firebase.getInstance().createAccountSnapshotInFireStore(uid, username, "",email).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        finish();
                                    }
                                });
                            }

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}