package comp5216.sydney.edu.au.afinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    EditText id,password;
    Button login,signup2;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        id = findViewById(R.id.id);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup2 = findViewById(R.id.signup2);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginEvent();
            }
        });

        signup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }
    private void loginEvent(){

       mAuth.signInWithEmailAndPassword(id.getText().toString(),password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                if (task.isSuccessful()){
                  //Sign in success.

                    startActivity((new Intent(LoginActivity.this,NavigatorBase.class)));

                }else{
                    //Sign in failed, display message to user

                    Toast.makeText(LoginActivity.this,"Login failed",
                            Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}