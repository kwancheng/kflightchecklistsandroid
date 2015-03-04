package com.acproma.kflightchecklists.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.acproma.kflightchecklists.data.ActionItemsTable;
import com.acproma.kflightchecklists.data.KFlightChecklistDB;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Congee on 9/18/14.
 */
public class ActionItem implements Parcelable {
	static final public Creator<ActionItem> CREATOR = new ClassLoaderCreator<ActionItem>() {
		@Override
		public ActionItem createFromParcel(Parcel source, ClassLoader loader) {
			ActionItem a = new ActionItem();
			a.id = source.readLong();
			a.sectionId = source.readLong();
			a.displayOrder = source.readInt();
			a.messageType = source.readParcelable(loader);
			a.string1 = source.readString();
			a.string2 = source.readString();
			a.image = source.readString();
			return a;
		}

		@Override
		public ActionItem createFromParcel(Parcel source) {
			return null;
		}

		@Override
		public ActionItem[] newArray(int size) {
			return new ActionItem[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(sectionId);
		dest.writeInt(displayOrder);
		dest.writeParcelable(messageType, flags);
		dest.writeString(string1);
		dest.writeString(string2);
		dest.writeString(image);
		dest.writeParcelable(entryAction, flags);
		dest.writeParcelable(exitAction, flags);
	}

    // DTO
    public long id;
	public long sectionId;
	public int displayOrder;

    @SerializedName("message_type")
    public MessageType messageType;
	@SerializedName("string_1")
	public String string1;
	@SerializedName("string_2")
	public String string2;
	@SerializedName("entry_action")
	public ActionItemAction entryAction;
	@SerializedName("exit_action")
	public ActionItemAction exitAction;
	@SerializedName("image")
	public String image;

	// Transient Variables
	public boolean completed = false;

	static public List<ActionItem> fromCursor(Cursor c){
		List<ActionItem> actionItems = new ArrayList<ActionItem>();
		ContentValues cv = new ContentValues();
		String n = null;
		while(c.moveToNext()) {
			cv.clear();
			DatabaseUtils.cursorRowToContentValues(c,cv);
			ActionItem actionItem = new ActionItem();
			n = ActionItemsTable.COL_ID.getName();
			if(cv.containsKey(n)) actionItem.id = cv.getAsLong(n);

			n = ActionItemsTable.COL_SECTION_ID.getName();
			if(cv.containsKey(n)) actionItem.sectionId = cv.getAsLong(n);

			n = ActionItemsTable.COL_MSG_TYPE.getName();
			if(cv.containsKey(n)) {
				String msgTypeName = cv.getAsString(n);
				actionItem.messageType = MessageType.valueOf(msgTypeName);
			}

			n = ActionItemsTable.COL_STRING_1.getName();
			if(cv.containsKey(n)) actionItem.string1 = cv.getAsString(n);

			n = ActionItemsTable.COL_STRING_2.getName();
			if(cv.containsKey(n)) actionItem.string2 = cv.getAsString(n);

			n = ActionItemsTable.COL_DISPLAY_ORDER.getName();
			if(cv.containsKey(n))
				actionItem.displayOrder = cv.getAsInteger(n);

			n = ActionItemsTable.COL_IMAGE.getName();
			if(cv.containsKey(n))
				actionItem.image = cv.getAsString(n);

			n = ActionItemsTable.COL_ENTRY_ACTION_ID.getName();
			if(cv.containsKey(n)&& cv.getAsLong(n) != null) {
				long id = cv.getAsLong(n);
				actionItem.entryAction = KFlightChecklistDB.getInstance().ACTION_ITEM_ACTIONS_TABLE.getActionItemAction(id);
			}

			n = ActionItemsTable.COL_EXIT_ACTION_ID.getName();
			if(cv.containsKey(n) && cv.getAsLong(n) != null) {
				long id = cv.getAsLong(n);
				actionItem.exitAction = KFlightChecklistDB.getInstance().ACTION_ITEM_ACTIONS_TABLE.getActionItemAction(id);
			}
			actionItems.add(actionItem);
		}
		return actionItems;
	}

	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(ActionItemsTable.COL_ID.getName(), id);
		cv.put(ActionItemsTable.COL_SECTION_ID.getName(), sectionId);
		cv.put(ActionItemsTable.COL_MSG_TYPE.getName(), messageType.toString());
		cv.put(ActionItemsTable.COL_DISPLAY_ORDER.getName(), displayOrder);
		cv.put(ActionItemsTable.COL_STRING_1.getName(), string1);
		cv.put(ActionItemsTable.COL_STRING_2.getName(), string2);
		cv.put(ActionItemsTable.COL_IMAGE.getName(), image);
		cv.put(ActionItemsTable.COL_ENTRY_ACTION_ID.getName(), (entryAction==null)?null:entryAction.id);
		cv.put(ActionItemsTable.COL_EXIT_ACTION_ID.getName(), (exitAction==null)?null:exitAction.id);
		return cv;
	}
}
