package www.dheerajprajapati.blogspot.start_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import Project.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class User_Profile extends AppCompatActivity {

    private CircleImageView circleImageView,img_on,img_off;
    TextView username,about;
    EditText aboutchange;
    Button about_button;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageurl;
    private StorageTask uploadTask;

    @Override
    protected void onStart() {
        super.onStart();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        img_on=findViewById(R.id.img_on);
        img_off=findViewById(R.id.img_off);
        circleImageView =findViewById(R.id.profile);
        username =findViewById(R.id.profile_username);
        about=findViewById(R.id.about);
        aboutchange=findViewById(R.id.changeabout);
        about_button=findViewById(R.id.about_btn);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getApplicationContext()==null){
                    return;
                }
                User user = snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                about.setText(user.getAbout());
                if (user.getImageurl().equals("default")) {
                    circleImageView.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(User_Profile.this).load(user.getImageurl()).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        about_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aboutstatus=aboutchange .getText().toString();
                if(aboutstatus.equals(""))
                {
                    Toast.makeText(User_Profile.this,"Please type your status!",Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.child("about").setValue(aboutstatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(User_Profile.this,"Status update successfully",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(User_Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                aboutchange.setVisibility(View.GONE);
                about_button.setVisibility(View.GONE);
            }
        });
        /*Change the username*/
        username.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder=new AlertDialog.Builder(User_Profile.this).setTitle("Enter new username");
                final EditText input=new EditText(User_Profile.this);
                RelativeLayout.LayoutParams relativeLayout=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(relativeLayout);
                builder.setView(input);
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String user_name=input.getText().toString();
                        if(TextUtils.isEmpty(user_name)){
                            Toast.makeText(User_Profile.this,"Please enter new username!",Toast.LENGTH_SHORT).show();
                        }else{
                            databaseReference.child("username").setValue(user_name).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(User_Profile.this,"Username update successfully...",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(User_Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).setNegativeButton("Cancel",null).show();
            }
        });

        /* Change the profile image*/
        circleImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PopupMenu popupMenu=new PopupMenu(User_Profile.this,circleImageView);
                popupMenu.getMenuInflater().inflate(R.menu.profile_change,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.changeimage) {
                            openImage();
                            return true;
                        }else if (itemId==R.id.seeimage){
                            startActivity(new Intent(User_Profile.this,show_profile.class));
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        registerForContextMenu(about);
    }

    private void openImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver= User_Profile.this.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        final ProgressDialog progressDialog=new ProgressDialog(User_Profile.this);
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

                        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("imageurl",mUri);
                        databaseReference.updateChildren(map);

                        progressDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(User_Profile.this,"Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(User_Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(User_Profile.this,"no image selected",Toast.LENGTH_SHORT).show();
        }
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData() !=null){
            imageurl=data.getData();
            if(uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(User_Profile.this,"Upload in Progress",Toast.LENGTH_SHORT).show();
            }else
            {
                uploadImage();
            }
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,v.getId(),0,"Clear About");
        menu.add(0,v.getId(),0,"Change About");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle()=="Clear About")
        {
            databaseReference.child("about").setValue("Available").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(User_Profile.this,"Default Status update successfully",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(User_Profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        else  if (item.getTitle()=="Change About"){
            aboutchange.setVisibility(View.VISIBLE);
            about_button.setVisibility(View.VISIBLE);
            return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Setting) {
            startActivity(new Intent(getApplicationContext(),Settings.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }else if(item.getItemId()==R.id.AboutUs){
            startActivity(new Intent(getApplicationContext(), AboutUs.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }
        return false;
    }
}