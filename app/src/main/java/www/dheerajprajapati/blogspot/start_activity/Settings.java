package www.dheerajprajapati.blogspot.start_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends AppCompatActivity {

    Button account_delete,change_password,update;
    FirebaseUser firebaseUser;
    EditText oldPswd,newPswd,newConfirmPswd;

    @Override
    protected void onStart() {
        super.onStart();
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("Caution!");
        alert.setMessage("1. Delete account not to be recovered!"+"\n"+"2. Be carefully to change your password");
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Settings.this,"Be Careful....",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        account_delete=findViewById(R.id.delete_button);
        change_password=findViewById(R.id.change_btn);
        oldPswd=findViewById(R.id.old_password);
        newPswd=findViewById(R.id.new_password);
        newConfirmPswd=findViewById(R.id.confirm_password);
        update=findViewById(R.id.update);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        // Get the editexts Values
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPswd.setVisibility(View.VISIBLE);
                newPswd.setVisibility(View.VISIBLE);
                newConfirmPswd.setVisibility(View.VISIBLE);
                change_password.setVisibility(View.GONE);
                update.setVisibility(View.VISIBLE);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old=oldPswd.getText().toString();
                String newpw=newPswd.getText().toString();
                String newconfirm=newConfirmPswd.getText().toString();
                if(TextUtils.isEmpty(old)){
                    oldPswd.setError("Please enter old password!");
                }else if(TextUtils.isEmpty(newpw) && TextUtils.isEmpty(newconfirm)){
                    newPswd.setError("Please enter password!");
                }else if(!newpw.equals(newconfirm)){
                    Toast.makeText(Settings.this,"Please check your password!",Toast.LENGTH_SHORT).show();
                    oldPswd.setText("");
                    newPswd.setText("");
                    newConfirmPswd.setText("");
                }else if(newpw.length()<8 && newconfirm.length()<8){
                    newPswd.setError("Password at least 8 digits ");
                    newConfirmPswd.setError("");
                }else {
                    changePassword(old,newconfirm);
                }
            }
        });

        account_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder delete=new AlertDialog.Builder(Settings.this);
                delete.setTitle("Account Delete?");
                delete.setMessage("Are you want to delete your account?");
                delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(Settings.this,Start.class));
                                Toast.makeText(Settings.this,"Account deleted successfully",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Settings.this,"Be careful",Toast.LENGTH_SHORT).show();
                    }
                });
                delete.show();
            }
        });
    }
    private void changePassword(String old,String confirmpas){
        AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(),old );
        firebaseUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseUser.updatePassword(confirmpas)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Settings.this,"Password Updated.....",Toast.LENGTH_SHORT).show();
                                oldPswd.setVisibility(View.GONE);
                                newPswd.setVisibility(View.GONE);
                                newConfirmPswd.setVisibility(View.GONE);
                                change_password.setVisibility(View.VISIBLE);
                                update.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Settings.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Settings.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}