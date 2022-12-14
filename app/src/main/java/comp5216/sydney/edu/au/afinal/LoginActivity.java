package comp5216.sydney.edu.au.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import comp5216.sydney.edu.au.afinal.database.Firebase;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private EditText emailWidget,passwordWidget;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        emailWidget = (EditText) findViewById(R.id.id);
        passwordWidget = (EditText) findViewById(R.id.password);

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(LoginActivity.this, NavigatorBase.class);
            Firebase.getInstance().setLocalUser(Firebase.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    startActivity(intent);
                }
            });
        }
    }

    public void Login(View v) {
        String email = emailWidget.getText().toString();
        String password = passwordWidget.getText().toString();
        if (email.equals("") || password.equals("")) {
            Toast.makeText(LoginActivity.this, "empty email or password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(LoginActivity.this, NavigatorBase.class);
                            Log.d(TAG, Firebase.getInstance().getCurrentUser().getUid());
                            Firebase.getInstance().setLocalUser(Firebase.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Incorrect email or password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void register(View v){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

}