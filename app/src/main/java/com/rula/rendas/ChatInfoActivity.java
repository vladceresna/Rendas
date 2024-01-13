package com.rula.rendas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rula.rendas.adapters.ChannelAdapter;
import com.rula.rendas.adapters.ChatAdapter;
import com.rula.rendas.adapters.MessagesAdapter;
import com.rula.rendas.obj.Chat;

import java.util.HashMap;

public class ChatInfoActivity extends AppCompatActivity {

    DatabaseReference reference;
    MaterialToolbar toolbar;
    Chat chat;
    TextView countmemb;
    LinearLayout butmembers, butsettings, butnewchannel;
    ListView listView;

    String id;
    boolean owner;
    boolean runupdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);

        countmemb = findViewById(R.id.countmemb);
        toolbar = findViewById(R.id.toolbar);
        butmembers = findViewById(R.id.butmembers);
        butsettings = findViewById(R.id.butsettings);
        butnewchannel = findViewById(R.id.butnewchannel);
        listView = findViewById(R.id.listView);
        runupdate = false;

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    runupdate = true;
                    chat = snapshot.getValue(Chat.class);
                    toolbar.setTitle(chat.name);
                    id = getIntent().getStringExtra("id");
                    countmemb.setText(String.valueOf(chat.getMembersid().size()));

                    if (chat.getChannels() != null) {
                        toolbar.setSubtitle(chat.channels.get(Integer.parseInt(getIntent().getStringExtra("item"))).name);

                        listView.setAdapter(new ChannelAdapter(ChatInfoActivity.this, chat.channels, id));
                        runupdate = false;
                    }

                    reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).child("channels").addChildEventListener(childEventListener());

                    for (String ownerid:chat.getOwnerid()){
                        if (id.equals(ownerid)){
                            owner = true;
                            break;
                        }
                    }
                    if (owner){
                        butsettings.setVisibility(View.VISIBLE);
                        butnewchannel.setVisibility(View.VISIBLE);
                    }
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(ChatInfoActivity.this, ChannelActivity.class).putExtra("item", String.valueOf(i)).putExtra("id", getIntent().getStringExtra("id")).putExtra("key",getIntent().getStringExtra("key")));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu popupMenu = new PopupMenu(ChatInfoActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_messadapt_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:

                                reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).child("channels").child(String.valueOf(i)).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(ChatInfoActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
                return true;
            }
        });
        butmembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatInfoActivity.this, MembersManageActivity.class).putExtra("key", getIntent().getStringExtra("key")).putExtra("id", getIntent().getStringExtra("id")));
            }
        });
        butsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatInfoActivity.this, ChatSettingsActivity.class).putExtra("key", getIntent().getStringExtra("key")).putExtra("id", getIntent().getStringExtra("id")));
            }
        });
        butnewchannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatInfoActivity.this, CreateChannelActivity.class).putExtra("key", getIntent().getStringExtra("key")).putExtra("id", getIntent().getStringExtra("id")));
            }
        });
    }
    ChildEventListener childEventListener(){
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    updateChatList();
                }

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    updateChatList();
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    updateChatList();
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    updateChatList();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
    }
    private void updateChatList() {
        if (!runupdate) {
            runupdate = true;
            reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        chat = snapshot.getValue(Chat.class);
                        toolbar.setTitle(chat.name);
                        toolbar.setSubtitle(chat.channels.get(0).name);
                        countmemb.setText(String.valueOf(chat.getMembersid().size()));
                        id = getIntent().getStringExtra("id");

                        listView.setAdapter(new ChannelAdapter(ChatInfoActivity.this, chat.channels, id));

                        for (String ownerid:chat.getOwnerid()){
                            if (id.equals(ownerid)){
                                owner = true;
                                break;
                            }
                        }
                        if (owner){
                            butsettings.setVisibility(View.VISIBLE);
                        }
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            runupdate = false;
        }
    }
}