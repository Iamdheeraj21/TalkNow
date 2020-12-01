package www.dheerajprajapati.blogspot.start_activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Iterator;

import Project.Group;
import Project.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class Group_Message extends AppCompatActivity
{
    CircleImageView group_profile;
    TextView group_name,group_desc,messages;
    ImageButton btn_send;
    EditText txt_send;
    Intent intent;
    String groups_name,Current_User;
    DatabaseReference UserRef,GroupRef,GroupMessageRef;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageurl;
    private StorageTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(".");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Group_Message.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        group_profile=findViewById(R.id.groupimage);
        group_name=findViewById(R.id.update_groupname);
        group_desc=findViewById(R.id.update_groupdesc);
        btn_send=findViewById(R.id.btn_send);
        txt_send=findViewById(R.id.txt_send);
        messages=findViewById(R.id.messages);
        intent=getIntent();
        groups_name=intent.getStringExtra("groupname");
        storageReference = FirebaseStorage.getInstance().getReference("groupimage");

        UserRef=FirebaseDatabase.getInstance().getReference("Users");
        GroupRef= FirebaseDatabase.getInstance().getReference("Groups").child(groups_name);
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group=snapshot.getValue(Group.class);
                assert group != null;
                group_name.setText(group.getGroupname());
                group_desc.setText(group.getDescription());
                if(group.getGroupimage().equals("default")){
                    group_profile.setImageResource(R.mipmap.group_image);
                }else {
                    Glide.with(getApplicationContext()).load(group.getGroupimage()).into(group_profile);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        UserInfo();
        group_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    openImage();
            }
        });
        GroupRef= FirebaseDatabase.getInstance().getReference("Groups").child(groups_name).child("groupchat");
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessageInGroup();
                txt_send.setText("");
            }
        });
    }
    private void UserInfo()
    {
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                   Current_User= dataSnapshot.getValue(User.class).getUsername();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendMessageInGroup() {
        String message=txt_send.getText().toString();
        String MessageKey=GroupRef.push().getKey();


        if(TextUtils.isEmpty(message)){
            txt_send.setError("type a message...");
        }
        else {
            HashMap<String, Object> groupmessageKey = new HashMap<>();
            GroupRef.updateChildren(groupmessageKey);

            GroupMessageRef=GroupRef.child(MessageKey);

            HashMap<String,Object> messageInfo=new HashMap<>();
            messageInfo.put("username",Current_User);
            messageInfo.put("message",message);

            GroupMessageRef.updateChildren(messageInfo);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        GroupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if(snapshot.exists()){
                        DisplayMessage(snapshot);
                    }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMessage(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Group Profile Picture change
    private void openImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=Group_Message.this.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        final ProgressDialog progressDialog=new ProgressDialog(Group_Message.this);
        progressDialog.setMessage("Uploading...");
        if(imageurl!=null){
            final StorageReference file=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageurl));
            uploadTask=file.putFile(imageurl);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return file.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if(task.isSuccessful()){
                        Uri downloaduri=task.getResult();
                        assert downloaduri != null;
                        String mUri= downloaduri.toString();

                        GroupRef=FirebaseDatabase.getInstance().getReference("Groups").child(groups_name);
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("groupimage",mUri);
                        GroupRef.updateChildren(map);

                        progressDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(Group_Message.this,"Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Group_Message.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(Group_Message.this,"no image selected",Toast.LENGTH_SHORT).show();
        }
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData() !=null){
            imageurl=data.getData();
            if(uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(Group_Message.this,"Upload in Progress",Toast.LENGTH_SHORT).show();
            }else
            {
                uploadImage();
            }
        }
    }
    private void DisplayMessage(DataSnapshot snapshot)
    {
        Iterator iterator=snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String message=(String) ((DataSnapshot)iterator.next()).getValue();
            String SenderName=(String) ((DataSnapshot)iterator.next()).getValue();
            messages.append("\n"+SenderName+":-"+message+"\n");
        }
    }
}