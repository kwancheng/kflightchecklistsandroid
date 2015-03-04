package com.acproma.kflightchecklists.utils;

import android.os.Message;

import com.acproma.kflightchecklists.models.MessageType;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by Congee on 9/19/14.
 */
public class MessageTypeTypeAdapter extends TypeAdapter<MessageType> {
    @Override
    public void write(JsonWriter out, MessageType value) throws IOException {

    }

    @Override
    public MessageType read(JsonReader in) throws IOException {
        MessageType mt = null;

	    try {
		    mt = MessageType.valueOf(in.nextString().toUpperCase());
	    } catch(Exception e){
		    mt = MessageType.ACTION_ITEM;
	    }

        return mt;
    }
}
