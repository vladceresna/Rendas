package com.rula.rendas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rula.rendas.obj.Channel;
import com.rula.rendas.obj.Chat;

import java.util.ArrayList;

public class CreateChannelActivity extends AppCompatActivity {

    DatabaseReference reference;

    EditText name, rulaids;
    Button button;
    MaterialToolbar toolbar;

    Chat chat;
    String id;
    boolean owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        name = findViewById(R.id.name);
        rulaids = findViewById(R.id.rulaids);
        button = findViewById(R.id.button);
        toolbar = findViewById(R.id.toolbar);

        owner = false;

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chat = snapshot.getValue(Chat.class);
                toolbar.setTitle(chat.name);
                id = getIntent().getStringExtra("id");

                for (String ownerid:chat.getOwnerid()){
                    if (id.equals(ownerid)){
                        owner = true;
                        break;
                    }
                }
                if (!owner){
                    finishAffinity();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> owners = new ArrayList<>();
                owners.add(id);
                ArrayList<String> members = new ArrayList<>();
                members.add(id);

                String[] strs = rulaids.getText().toString().split(",");

                reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chat = snapshot.getValue(Chat.class);

                        for(String s:strs){
                            members.add(s.trim());
                            if (!s.trim().equals("@все")){
                                boolean estenchamem = false;
                                for (String mid:chat.getMembersid()){
                                    if (mid.equals(s.trim())){
                                        estenchamem = true;
                                        break;
                                    }
                                }
                                if (!estenchamem){
                                    if(!reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).child("membersid").child(String.valueOf(chat.getMembersid().size())).setValue(s.trim()).isSuccessful()){
                                        Toast.makeText(CreateChannelActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    if(!reference.child("rendas").child("userschatreflist").child(s.trim()).push().setValue(getIntent().getStringExtra("key")).isSuccessful()){
                                        //Toast.makeText(CreateChannelActivity.this, "Произошла ошибка 1", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        }

                        Channel channel = new Channel(name.getText().toString(), owners, members, null);

                        if(reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).child("channels").child(String.valueOf(chat.getChannels().size())).setValue(channel).isSuccessful()){
                            Toast.makeText(CreateChannelActivity.this, "Готово!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });

    }
}