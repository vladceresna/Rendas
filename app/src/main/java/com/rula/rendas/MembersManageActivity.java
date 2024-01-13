package com.rula.rendas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rula.rendas.obj.Chat;

public class MembersManageActivity extends AppCompatActivity {

    ListView listowners,listmembers;
    MaterialToolbar toolbar;
    DatabaseReference reference;
    Chat chat;
    String id;
    boolean owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_manage);

        listowners = findViewById(R.id.listowners);
        listmembers = findViewById(R.id.listmembers);
        toolbar = findViewById(R.id.toolbar);

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chat = snapshot.getValue(Chat.class);
                toolbar.setTitle(chat.name);
                toolbar.setSubtitle(chat.channels.get(0).name);

                id = getIntent().getStringExtra("id");
                for (String ownerid:chat.getOwnerid()){
                    if (id.equals(ownerid)){
                        owner = true;
                        break;
                    }
                }
                if (owner){

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}