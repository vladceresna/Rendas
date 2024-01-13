package com.rula.rendas.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rula.etime.EasyTime;
import com.rula.etime.TimeData;
import com.rula.rendas.R;
import com.rula.rendas.obj.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessagesAdapter extends BaseAdapter {
    private ArrayList<Message> messages;
    private HashMap<String, Message> messageshash;
    private Context context;
    private String myid;
    private DatabaseReference reference;
    private String key;

    public MessagesAdapter(Context context, HashMap<String, Message> messages, String myid, String key) {
        this.myid = myid;
        this.context = context;
        this.messages = new ArrayList<>();
        this.messages.addAll(messages.values());
        this.messageshash = messages;
        this.key = key;

        reference = FirebaseDatabase.getInstance().getReference();
    }
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(context).
                    inflate(R.layout.message_layout, viewGroup, false);
        }
        Message message = (Message) getItem(i);

        LinearLayout lspace = (LinearLayout) view.findViewById(R.id.lspace);
        LinearLayout rspace = (LinearLayout) view.findViewById(R.id.rspace);

        LinearLayout linlay = (LinearLayout) view.findViewById(R.id.linlay);

        TextView textname = (TextView) view.findViewById(R.id.textname);
        TextView textmessage = (TextView) view.findViewById(R.id.textmessage);
        TextView texttime = (TextView) view.findViewById(R.id.texttime);

        textname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(context.getPackageManager().getPackageInfo("com.rula.rosto",0) != null){
                        context.startActivity(new Intent().setClassName("com.rula.rosto", "com.rula.rosto.UserProfileActivity").putExtra("id", message.getSenderid()));
                    }else{
                        new MaterialAlertDialogBuilder(context)
                                .setTitle("Приложение Rosto не обнаружено")
                                .setMessage("Приложение Rosto нужно для нормального функционирования ретсистемы Rula. В нем находятся общие функции системы, такие как действия с профилями, и т.д. Установить его можна с нашего официального сайта rulav.repl.co")
                                .setPositiveButton("Хорошо", null).show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Приложение Rosto не обнаружено")
                            .setMessage("Приложение Rosto нужно для нормального функционирования ретсистемы Rula. В нем находятся общие функции системы, такие как действия с профилями, и т.д. Установить его можна с нашего официального сайта rulav.repl.co")
                            .setPositiveButton("Хорошо", null).show();
                    e.printStackTrace();
                }
            }
        });
        textname.setText(message.getSenderid());
        textmessage.setText(message.getMessage());


        String s = ".";
        TimeData td = new EasyTime().getConvTime(Long.parseLong(message.getTime()));
        texttime.setText(td.M() +s+ td.D() +" "+ td.H() +":"+ td.m());

        if (myid.equals(message.getSenderid())){
            rspace.setVisibility(View.GONE);
            lspace.setVisibility(View.VISIBLE);
        }else{
            lspace.setVisibility(View.GONE);
            rspace.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
