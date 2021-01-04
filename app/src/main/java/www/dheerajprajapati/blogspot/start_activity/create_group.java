package www.dheerajprajapati.blogspot.start_activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import Project.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class create_group extends AppCompatActivity {
    TextView textView;
    EditText groupName,groupDesc;
    Button create,cancel;
    CircleImageView circleImageView;
    DatabaseReference databaseReference;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        textView=findViewById(R.id.textView6);
        groupName=findViewById(R.id.update_groupname);
        groupDesc=findViewById(R.id.update_groupdesc);
        create=findViewById(R.id.Update_btn);
        cancel=findViewById(R.id.cancel_btn);
        circleImageView=findViewById(R.id.groupimage);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(create_group.this,MainActivity.class));
                finish();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupFieldName=groupName.getText().toString();
                String groupFieldDescription=groupDesc.getText().toString();
                if(TextUtils.isEmpty(groupFieldName)){
                    groupName.setError("Enter group name!");
                }else if(TextUtils.isEmpty(groupFieldDescription)){
                    groupDesc.setError("Enter description");
                }else{
                    createGroup(groupFieldName,groupFieldDescription);
                }
            }
        });
    }

    private void createGroup(String groupfieldname, String groupfielddescription) {
        FirebaseUser firebaseUser;
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Groups").child(groupfieldname);
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("groupname",groupfieldname);
        hashMap.put("description",groupfielddescription);
        hashMap.put("groupimage","default");
        hashMap.put("createdby",firebaseUser.getUid());
        hashMap.put("search",groupfieldname.toUpperCase());
        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(create_group.this,"Group created..",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(create_group.this,MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(create_group.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}