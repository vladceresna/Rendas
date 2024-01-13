package com.rula.rendas.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rula.etime.EasyTime;
import com.rula.etime.TimeData;
import com.rula.rendas.R;
import com.rula.rendas.obj.Channel;
import com.rula.rendas.obj.Chat;
import com.rula.rendas.obj.Message;

import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private List<Chat> chats;
    private Context context;
    private String myid;
    String TAG = "Tag";
    public ChatAdapter(Context context, List<Chat> chats, String myid) {
        this.myid = myid;
        this.chats = chats;
        this.context = context;
        Log.d("Tag", "StartAdapt");
    }
    @Override
    public int getCount() {
        Log.d(TAG, "getCount: ");
        return chats.size();
    }

    @Override
    public Object getItem(int i) {
        Log.d(TAG, "getItem: ");
        return chats.get(i);
    }

    @Override
    public long getItemId(int i) {
        Log.d(TAG, "getItemId: ");
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            Log.d(TAG, "getView: ");
            view = LayoutInflater.from(context).
                    inflate(R.layout.chat_layout, viewGroup, false);
        }
        Log.d("Tag", "Adapt");
        Chat chat = (Chat) getItem(i);

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView lmess = (TextView) view.findViewById(R.id.lmess);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView senderid = (TextView) view.findViewById(R.id.senderid);

        name.setText(chat.getName());

        boolean estmess = false;

        Message lastmess = new Message("", myid, "0");
        if (chat.getChannels() != null) {
            for (Channel channel : chat.getChannels()) {
                if (channel.getMessages() != null) {
                    for (Message message : channel.getMessages().values()) {
                        if (Long.parseLong(message.getTime()) > Long.parseLong(lastmess.getTime())) {
                            lastmess = message;
                        }
                    }
                    estmess = true;
                }
            }
        }

        lmess.setText(lastmess.getMessage());

        TimeData td = new EasyTime().getConvTime(Long.parseLong(lastmess.getTime()));
        time.setText(td.M() +"."+ td.D() +" "+ td.H() +":"+ td.m());

        senderid.setText(lastmess.getSenderid()+": ");

        if(!estmess) {
            time.setVisibility(View.GONE);
            lmess.setText("Сообщений нет");
            senderid.setVisibility(View.GONE);
        }



        return view;
    }
}
