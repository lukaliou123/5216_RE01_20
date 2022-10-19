package comp5216.sydney.edu.au.afinal.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import comp5216.sydney.edu.au.afinal.R;
import comp5216.sydney.edu.au.afinal.RegisterActivity;
import comp5216.sydney.edu.au.afinal.database.Firebase;
import comp5216.sydney.edu.au.afinal.entity.Account;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username, code, vCode, birth;
    private ImageView imageView;
    private Spinner gender;
    private ArrayAdapter<CharSequence> adapter;
    private Uri imageUri;

    @SuppressLint("ResourceType")
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.fragment_edit_profile);
        username = findViewById(R.id.usernametext);
        code = findViewById(R.id.passwordtext);
        vCode = findViewById(R.id.vpsw);
        birth = findViewById(R.id.birth);
        imageView = findViewById(R.id.image);
        gender = findViewById(R.id.gender);
        Account user = Firebase.getInstance().getLocalUser();
        username.setText(user.getName());
        birth.setText(user.getBirthDate());
        imageUri = null;
        if(!user.getIcon().equals(""))
            Glide.with(this)
                    .load(user.getIcon())
                    .into(imageView);
        adapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        int pos = adapter.getPosition(user.getGender());
        gender.setSelection(pos);
        imageView.setOnClickListener(new View.OnClickListener(){
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
                    imageView.setImageURI(result.getData().getData());
                    imageUri = result.getData().getData();
                } else {
                    Toast.makeText(this, "Picture wasn't selected!", Toast.LENGTH_SHORT).show();
                }
            }
    );


    @Override
    public void onClick(View view) {
        String name = username.getText().toString();
        String password = code.getText().toString();
        String vpw = vCode.getText().toString();
        if (name.equals("")||password.equals("")){
            Toast.makeText(EditProfileActivity.this, "name is empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!password.equals(vpw)){
            Toast.makeText(EditProfileActivity.this, "Passwords are not same",
                    Toast.LENGTH_SHORT).show();
            return;
        }String uri = (imageUri == null) ? Firebase.getInstance().getLocalUser().getIcon() : imageUri.toString();
        String sex = (String) gender.getSelectedItem();
        String birthday = birth.getText().toString();
        FirebaseUser user = Firebase.getInstance().getCurrentUser();
        user.updatePassword(password);
        Firebase.getInstance().updateAccountSnapshotInFireStore(user.getUid(), name, uri, sex, birthday).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Firebase.getInstance().setLocalUser(user.getUid()).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        finish();
                    }
                });
            }
        });
    }

    public void onCancel(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle(R.string.dialog_cancel_title)
                .setMessage(R.string.dialog_cancel_msg)
                .setPositiveButton(R.string.Yes, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(EditProfileActivity.this, ProfileFragment.class);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(R.string.No, new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled the dialog
                                // Nothing happens
                            }
                        });
        builder.create().show();

    }
}
