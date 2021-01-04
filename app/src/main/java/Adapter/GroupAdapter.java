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

import java.util.List;

import Project.Group;
import www.dheerajprajapati.blogspot.start_activity.Group_Message;
import www.dheerajprajapati.blogspot.start_activity.R;
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>{
    private final Context mContext;
    private final List<Group> mGroups;

    public GroupAdapter(Context mContext, List<Group> mGroups) {
        this.mContext = mContext;
        this.mGroups = mGroups;
    }


    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.group_name,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
            Group group=mGroups.get(position);
            holder.group_name.setText(group.getGroupname());
            if(group.getGroupimage().equals("default")){
                holder.group_image.setImageResource(R.mipmap.group_image);
            }else{
                Glide.with(mContext).load(group.getGroupimage()).into(holder.group_image);
            }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, Group_Message.class);
                intent.putExtra("groupname",group.getGroupname());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView group_image;
        public TextView group_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            group_image=itemView.findViewById(R.id.groupimage);
            group_name=itemView.findViewById(R.id.update_groupname);
        }
    }
}
