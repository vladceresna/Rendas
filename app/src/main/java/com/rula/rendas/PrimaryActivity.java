package com.rula.rendas;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rula.rendas.adapters.ChatAdapter;
import com.rula.rendas.obj.Chat;

import java.util.ArrayList;
import java.util.HashMap;

public class PrimaryActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    FloatingActionButton fab;
    DatabaseReference reference;
    String id;
    ListView listView;
    ArrayList<Chat> chats;
    ChatAdapter chatAdapter;
    TextView label;
    boolean rostoest;
    boolean runupdate;

    HashMap<String, Object> str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);
        chats = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listView);
        fab = findViewById(R.id.floatbut);
        label = findViewById(R.id.label);
        runupdate = false;

        try {
            if(getPackageManager().getPackageInfo("com.rula.rosto",0) != null){
                rostoest = true;
            }else{
                rostoest = false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            rostoest = false;
            e.printStackTrace();
        }

        reference = FirebaseDatabase.getInstance().getReference();

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("id").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            id = String.valueOf(task.getResult().getValue());



                            reference.child("rendas").child("userschatreflist").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot != null) {
                                        str = (HashMap<String, Object>) snapshot.getValue();
                                        for (Object s:str.values()){
                                            if (s != null) {
                                                reference.child("rendas").child("chats").child(s.toString()).addChildEventListener(childEventListener());
                                            }
                                        }
                                        reference.child("rendas").child("userschatreflist").child(id).addChildEventListener(childEventListener());
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });



                        }
                    }
                });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(PrimaryActivity.this, ChannelActivity.class).putExtra("id", id).putExtra("item", "0").putExtra("key", str.values().toArray()[i].toString()));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PrimaryActivity.this, CreateChatActivity.class));
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile:
                        if(rostoest) {
                            startActivity(new Intent().setClassName("com.rula.rosto", "com.rula.rosto.ProfileActivity"));
                        }else{
                            new MaterialAlertDialogBuilder(PrimaryActivity.this)
                                    .setTitle("Приложение Rosto не обнаружено")
                                    .setMessage("Приложение Rosto нужно для нормального функционирования ретсистемы Rula. В нем находятся общие функции системы, такие как действия с профилями, и т.д. Установить его можна с нашего официального сайта rulav.repl.co")
                                    .setPositiveButton("Хорошо", null).show();
                        }
                        return true;
                    case R.id.checkupd:
                        startActivity(new Intent(PrimaryActivity.this, com.rula.rendas.CheckingUpdatesActivity.class));
                        return true;
                    case R.id.abouti:
                        startActivity(new Intent(PrimaryActivity.this, com.rula.rendas.AboutActivity.class));
                        return true;
                    case R.id.exitacc:
                        new MaterialAlertDialogBuilder(PrimaryActivity.this)
                                .setTitle("Really?")
                                .setMessage("Вы выйдете из аккаунта только в этом сервисе. Чтобы все сервисы Rula работали корректно, нужно чтобы во всех них был выполнен вход под одним и тем же аккаунтом. Вы действительно хотите выйти из вашего аккаунта?")
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseAuth.getInstance().signOut();
                                        finishAffinity();
                                    }
                                })
                                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {}
                                }).show();
                        return true;

                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
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
            Log.d("Tag", "updateChatList: ");
            reference.child("rendas").child("userschatreflist").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    str = (HashMap<String, Object>) snapshot.getValue();
                    if (str != null) {
                        for (Object s : str.values()) {
                            reference.child("rendas").child("chats").child(s.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        chats.add(snapshot.getValue(Chat.class));
                                        chatAdapter = new ChatAdapter(PrimaryActivity.this, chats, id);
                                        listView.setAdapter(null);
                                        listView.setAdapter(chatAdapter);
                                        Log.d("Tag", "AddElement");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                        chats.clear();
                        runupdate = false;

                    } else {
                        listView.setVisibility(View.GONE);
                        label.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}