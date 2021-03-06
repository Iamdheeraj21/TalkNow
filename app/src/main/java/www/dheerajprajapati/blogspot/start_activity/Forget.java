package www.dheerajprajapati.blogspot.start_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Forget extends AppCompatActivity
{
    EditText email;
    Button forget_password;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onStart() {
        super.onStart();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset your password!");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        email=findViewById(R.id.forget_email);
        forget_password=findViewById(R.id.forget_submit);

        firebaseAuth= FirebaseAuth.getInstance();
        forget_password.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String txt_email=email.getText().toString();
                if(txt_email.equals("")){
                    Toast.makeText(Forget.this,"Please enter the your email!",Toast.LENGTH_SHORT).show();
                }else
                {
                    firebaseAuth.sendPasswordResetEmail(txt_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Forget.this,"Please check your email!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Forget.this,Login.class));
                            }else {
                                String error=task.getException().getMessage();
                                Toast.makeText(Forget.this,error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}