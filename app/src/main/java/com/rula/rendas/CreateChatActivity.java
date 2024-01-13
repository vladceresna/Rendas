package com.rula.rendas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rula.rendas.obj.Channel;
import com.rula.rendas.obj.Chat;

import java.util.ArrayList;

public class CreateChatActivity extends AppCompatActivity {
    DatabaseReference reference;
    EditText name, rulaids;
    Button button;
    String id;
    String chatref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);

        name = findViewById(R.id.name);
        rulaids = findViewById(R.id.rulaids);
        button = findViewById(R.id.button);

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
                        }
                    }
                });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> members = new ArrayList<>();
                String[] starr = rulaids.getText().toString().split(",");
                members.add(id.trim());
                for (String str: starr){
                    members.add(str.trim());
                }
                ArrayList<String> owners = new ArrayList<>();
                owners.add(id);
                ArrayList<Channel> channels = new ArrayList<>();
                channels.add(new Channel("Общий", owners, members, null));
                Chat chat = new Chat(name.getText().toString(), owners, members,channels);

                String key = reference.child("rendas").child("chats").push().getKey();

                reference.child("rendas").child("chats").child(key).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            for (String rid:members){
                                reference.child("rendas").child("userschatreflist").child(rid).push().setValue(key);
                            }
                            finish();
                        }else{
                            Toast.makeText(CreateChatActivity.this, "Не удалось", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });

    }


}