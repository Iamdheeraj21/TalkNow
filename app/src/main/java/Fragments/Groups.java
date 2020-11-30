package Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapter.GroupAdapter;
import Project.Group;
import www.dheerajprajapati.blogspot.start_activity.R;

public class Groups extends Fragment
{
    private GroupAdapter groupAdapter;
    private RecyclerView recyclerView;
    private List<Group> groupList;
    FirebaseUser firebaseUser;
    EditText search_group;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        search_group=view.findViewById(R.id.search_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupList=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        search_group.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_group(s.toString().toUpperCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void search_group(String toLowerCase)
    {
        Query query=FirebaseDatabase.getInstance().getReference("Groups").orderByChild("search")
                .startAt(toLowerCase)
                .endAt(toLowerCase+"uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Group group=dataSnapshot.getValue(Group.class);
                        groupList.add(group);
                        if(TextUtils.isEmpty(toLowerCase)){
                        groupList.clear();
                    }

                }
                groupAdapter=new GroupAdapter(getContext(),groupList);
                recyclerView.setAdapter(groupAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}