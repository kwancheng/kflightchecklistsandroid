package com.acproma.kflightchecklists.utils;

import com.acproma.kflightchecklists.models.ActionItemAction;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by kwan.cheng on 9/26/2014.
 */
public class ActionItemActionTypeAdapter extends TypeAdapter<ActionItemAction>{

	@Override
	public void write(JsonWriter out, ActionItemAction value) throws IOException {

	}

	@Override
	public ActionItemAction read(JsonReader in) throws IOException {
		in.beginObject();
		ActionItemAction actionItemAction = new ActionItemAction();
		while(in.hasNext()) {
			String nodeName = in.nextName();

			if(nodeName.equalsIgnoreCase("action_name")) {
				actionItemAction.actionName = ActionItemAction.ActionName.valueOf(in.nextString().toUpperCase());
			} else if(nodeName.equalsIgnoreCase("config")) {
				actionItemAction.config = in.nextString();
			} else {
				in.skipValue();
			}
		}
		in.endObject();

		return actionItemAction;
	}
}
