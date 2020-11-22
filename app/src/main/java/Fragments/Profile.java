package Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


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
import www.dheerajprajapati.blogspot.start_activity.R;

import static android.app.Activity.RESULT_OK;

public class Profile extends Fragment
{
    private CircleImageView circleImageView;
    TextView textView;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageurl;
    private StorageTask uploadTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        circleImageView = view.findViewById(R.id.profile);
        textView = view.findViewById(R.id.profile_username);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getActivity()==null){
                    return;
                }
                User user = snapshot.getValue(User.class);
                assert user != null;
                textView.setText(user.getUsername());
                if (user.getImageurl().equals("default")) {
                    circleImageView.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getContext()).load(user.getImageurl()).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* Change the profile image*/
        circleImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PopupMenu popupMenu=new PopupMenu(getContext(),circleImageView);
                popupMenu.getMenuInflater().inflate(R.menu.profile_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.changeimage) {
                            openImage();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        return view;
    }

    private void openImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        if(imageurl!=null){
            final StorageReference file=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageurl));
            uploadTask=file.putFile(imageurl);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
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
                            Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getContext(),"no image selected",Toast.LENGTH_SHORT).show();
        }
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData() !=null){
            imageurl=data.getData();
            if(uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(getContext(),"Upload in Progress",Toast.LENGTH_SHORT).show();
            }else
            {
                uploadImage();
            }
        }
    }
}