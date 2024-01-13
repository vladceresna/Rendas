package com.rula.rendas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.rula.rendas.adapters.ChatAdapter;
import com.rula.rendas.adapters.MessagesAdapter;
import com.rula.rendas.obj.Chat;
import com.rula.rendas.obj.Message;

import java.util.HashMap;

public class ChannelActivity extends AppCompatActivity {

    ListView listView;
    EditText message;
    ImageButton send;
    TextView text;

    MaterialToolbar toolbar;

    DatabaseReference reference;

    MessagesAdapter messagesAdapter;

    Chat chat;

    HashMap<String, Message> messageshash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        listView = findViewById(R.id.listView);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        text = findViewById(R.id.text);
        toolbar = findViewById(R.id.toolbar);

        reference = FirebaseDatabase.getInstance().getReference();

        reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    chat = snapshot.getValue(Chat.class);
                    if (chat.getChannels() != null) {
                        if (chat.getChannels().get(Integer.parseInt(getIntent().getStringExtra("item"))).getMessages() != null) {
                            messagesAdapter = new MessagesAdapter(ChannelActivity.this, chat.channels.get(Integer.parseInt(getIntent().getStringExtra("item"))).getMessages(), getIntent().getStringExtra("id"), getIntent().getStringExtra("key"));
                            listView.setAdapter(messagesAdapter);
                            toolbar.setTitle(chat.name);
                            toolbar.setSubtitle(chat.channels.get(Integer.parseInt(getIntent().getStringExtra("item"))).name);
                        }else{
                            listView.setVisibility(View.GONE);
                            text.setVisibility(View.VISIBLE);
                            toolbar.setTitle(chat.name);
                            toolbar.setSubtitle(chat.channels.get(Integer.parseInt(getIntent().getStringExtra("item"))).name);
                        }
                    }else{
                        listView.setVisibility(View.GONE);
                        text.setVisibility(View.VISIBLE);
                        text.setText("В этом чате ещё нет каналов");
                        toolbar.setTitle(chat.name);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).addChildEventListener(childEventListener());

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.chatinfo:
                        startActivity(new Intent(ChannelActivity.this, ChatInfoActivity.class).putExtra("key", getIntent().getStringExtra("key")).putExtra("id", getIntent().getStringExtra("id")).putExtra("item", getIntent().getStringExtra("item")));
                        return true;
                    default:
                        return false;
                }
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChannelActivity.this, ChatInfoActivity.class).putExtra("key", getIntent().getStringExtra("key")).putExtra("id", getIntent().getStringExtra("id")).putExtra("item", getIntent().getStringExtra("item")));
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu popupMenu = new PopupMenu(ChannelActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_messadapt_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).child("channels").child(getIntent().getStringExtra("item")).child("messages").child(messageshash.keySet().toArray()[i].toString()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(ChannelActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).child("channels").child(getIntent().getStringExtra("item")).child("messages").push().setValue(new Message(message.getText().toString(), getIntent().getStringExtra("id"), String.valueOf(System.currentTimeMillis())));
                message.setText("");
            }
        });
    }
    ChildEventListener childEventListener(){
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    updateChilds();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    updateChilds();
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    updateChilds();
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    updateChilds();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChannelActivity.this, PrimaryActivity.class));
        super.onBackPressed();
    }
    public void updateChilds(){
        reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    chat = snapshot.getValue(Chat.class);
                    if (chat.getChannels() != null) {
                        if (chat.channels.get(Integer.parseInt(getIntent().getStringExtra("item"))).getMessages() != null) {
                            messagesAdapter = new MessagesAdapter(ChannelActivity.this, chat.getChannels().get(Integer.parseInt(getIntent().getStringExtra("item"))).getMessages(), getIntent().getStringExtra("id"), getIntent().getStringExtra("key"));
                            listView.setAdapter(messagesAdapter);
                            messageshash = chat.getChannels().get(Integer.parseInt(getIntent().getStringExtra("item"))).getMessages();
                            listView.setVisibility(View.VISIBLE);
                            text.setVisibility(View.GONE);
                        } else {
                            listView.setVisibility(View.GONE);
                            text.setVisibility(View.VISIBLE);
                        }
                    }else{
                        listView.setVisibility(View.GONE);
                        text.setVisibility(View.VISIBLE);
                        text.setText("В этом чате ещё нет каналов");
                        toolbar.setTitle(chat.name);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}