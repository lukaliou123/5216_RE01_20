package comp5216.sydney.edu.au.afinal.ui.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import comp5216.sydney.edu.au.afinal.R;
import comp5216.sydney.edu.au.afinal.database.Firebase;
import comp5216.sydney.edu.au.afinal.entity.Account;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username, code, vCode, birth;
    private ImageView imageView;
    private Spinner gender;

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
        if(!user.getIcon().equals(""))
            Glide.with(this)
                    .load(user.getIcon())
                    .into(imageView);

    }

    @Override
    public void onClick(View view) {

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
