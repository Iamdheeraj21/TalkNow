package www.dheerajprajapati.blogspot.start_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class show_profile extends AppCompatActivity {

    ImageView imageView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ImageButton imageButton;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageurl;
    private StorageTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        imageView=findViewById(R.id.user_image);
        imageButton=findViewById(R.id.change_image);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user=snapshot.getValue(User.class);
                assert user != null;
                if (user.getImageurl().equals("default")){
                        imageView.setImageResource(R.mipmap.ic_launcher);
                    }else{
                        Glide.with(show_profile.this).load(user.getImageurl()).into(imageView);
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(show_profile.this,imageButton);
                popupMenu.getMenuInflater().inflate(R.menu.profile_change,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.changeimage) {
                            final CharSequence[] options = {"Choose from Gallery","Cancel" };
                            AlertDialog.Builder builder = new AlertDialog.Builder(show_profile.this);
                            builder.setTitle("Add Photo!");
                            builder.setItems(options,new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    if(options[which].equals("Choose from Gallery"))
                                    {
                                        openImage();
                                    }
                                    else if(options[which].equals("Cancel"))
                                    {
                                        dialog.dismiss();
                                    }

                                }
                            });
                            builder.show();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
    private void openImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=show_profile.this.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        final ProgressDialog progressDialog=new ProgressDialog(show_profile.this);
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
                        Toast.makeText(show_profile.this,"Failed",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(show_profile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(show_profile.this,"no image selected",Toast.LENGTH_SHORT).show();
        }
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData() !=null){
            imageurl=data.getData();
            if(uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(show_profile.this,"Upload in Progress",Toast.LENGTH_SHORT).show();
            }else
            {
                uploadImage();
            }
        }
    }
}