package comp5216.sydney.edu.au.afinal.ui.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import comp5216.sydney.edu.au.afinal.R;

public class EditProfileActivity extends Activity implements View.OnClickListener {

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        // Populate the screen using the layout
        setContentView(R.layout.fragment_edit_profile);

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
