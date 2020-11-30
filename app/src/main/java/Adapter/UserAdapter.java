package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import Project.Chat;
import Project.User;
import www.dheerajprajapati.blogspot.start_activity.Message;
import www.dheerajprajapati.blogspot.start_activity.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private final Context mContext;
    private final List<User> mUsers;
    private final boolean isChat;
    String lastmessage;

    public UserAdapter(Context mContext,List<User> mUsers,boolean isChat)
    {
        this.isChat=isChat;
        this.mContext=mContext;
        this.mUsers=mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user =mUsers.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageurl().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else
        {
            Glide.with(mContext).load(user.getImageurl()).into(holder.profile_image);
        }
        if(isChat){
            lastmessage(user.getId(),holder.last_msg);
        }else {
            holder.last_msg.setVisibility(View.GONE);
        }
        if(isChat){
            if(user.getStatus().equals("Online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else
            {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        }else
        {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, Message.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView profile_image;
        public TextView username;
        private final ImageView img_on;
        private final ImageView img_off;
        private final TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image=itemView.findViewById(R.id.profile_pic2);
            username=itemView.findViewById(R.id.uname3);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
            last_msg=itemView.findViewById(R.id.last_msg);
        }
    }
    private void lastmessage(String userid,TextView last_msg){
        lastmessage="default";
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    assert chat != null;
                    assert firebaseUser != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        lastmessage=chat.getMessage();
                    }
                }
                if ("default".equals(lastmessage)) {
                    last_msg.setText("No message!");
                } else {
                    last_msg.setText(lastmessage);
                }
                lastmessage="default";
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
