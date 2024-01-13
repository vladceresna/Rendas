package com.rula.rendas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rula.rendas.obj.Chat;

import java.util.HashMap;

public class ChatSettingsActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    LinearLayout butdelete;

    DatabaseReference reference;

    Chat chat;

    HashMap<String, Object> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);

        toolbar = findViewById(R.id.toolbar);
        butdelete = findViewById(R.id.butdelete);

        reference = FirebaseDatabase.getInstance().getReference();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        butdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            chat = snapshot.getValue(Chat.class);

                            for (String s:chat.getMembersid()){
                                reference.child("rendas").child("userschatreflist").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        hashMap = (HashMap<String, Object>) snapshot.getValue();
                                        int it = 0;
                                        for (Object st:hashMap.values()){
                                            if (st.toString().equals(getIntent().getStringExtra("key"))){
                                                reference.child("rendas").child("userschatreflist").child(s).child(hashMap.keySet().toArray()[it].toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (!task.isSuccessful()){
                                                            Toast.makeText(ChatSettingsActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                                                            finishAffinity();
                                                        }
                                                    }
                                                });
                                            }
                                            it++;
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

                reference.child("rendas").child("chats").child(getIntent().getStringExtra("key")).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            finishAffinity();
                        }else{
                            Toast.makeText(ChatSettingsActivity.this, "Произошла ошибка 2", Toast.LENGTH_SHORT).show();
                            finishAffinity();
                        }
                    }
                });


            }
        });

    }
}