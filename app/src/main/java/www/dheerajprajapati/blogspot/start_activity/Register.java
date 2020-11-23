package www.dheerajprajapati.blogspot.start_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity
{
    Button btn3;
    EditText uname,email1,createpassword,confirmpassword;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn3=findViewById(R.id.btn3);
        uname=findViewById(R.id.uname);
        email1=findViewById(R.id.email1);
        createpassword=findViewById(R.id.createpassword);
        confirmpassword=findViewById(R.id.confirmpassword);
        firebaseAuth=FirebaseAuth.getInstance();
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String txt_username=uname.getText().toString();
                String txt_email=email1.getText().toString();
                String create_password=createpassword.getText().toString();
                String confirm_password=createpassword.getText().toString();
                if(TextUtils.isEmpty(txt_username)){
                    Toast.makeText(Register.this,"Please enter username",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(txt_email)){
                    Toast.makeText(Register.this,"Please enter email",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(create_password) && TextUtils.isEmpty(confirm_password)){
                    Toast.makeText(Register.this,"Please enter password",Toast.LENGTH_SHORT).show();
                }else if(create_password.length()<7){
                    Toast.makeText(Register.this,"Password atleast 8 digits",Toast.LENGTH_SHORT).show();
                }else if(!confirm_password.equals(create_password)){
                    Toast.makeText(Register.this,"Password doesn't match!",Toast.LENGTH_SHORT).show();
                }else {
                    RegisterUser(txt_username,txt_email,confirm_password);
                }
            }
        });
    }

    private void RegisterUser(String username,String email,String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                    String UserId=firebaseUser.getUid();
                    databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("id",UserId);
                    hashMap.put("username",username);
                    hashMap.put("imageurl","default");
                    hashMap.put("status","Offline");
                    hashMap.put("about","Available");
                    hashMap.put("search",username.toUpperCase());

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                               firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task)
                                   {
                                        if(task.isSuccessful())
                                        {
                                            Intent intent=new Intent(Register.this,Login.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Toast.makeText(Register.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                   }
                               });
                            }
                        }
                    });
                }else
                {
                    Toast.makeText(Register.this,"Please Check your email and password!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}