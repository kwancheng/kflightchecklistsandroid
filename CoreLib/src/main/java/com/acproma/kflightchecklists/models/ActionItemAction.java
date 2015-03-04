package com.acproma.kflightchecklists.models;

import android.app.Notification;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.acproma.kflightchecklists.data.ActionItemActionsTable;
import com.acproma.kflightchecklists.data.ActionItemsTable;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwan.cheng on 9/23/2014.
 */
public class ActionItemAction implements Parcelable {
	final static public Creator<ActionItemAction> CREATOR = new Creator<ActionItemAction>() {
		@Override
		public ActionItemAction createFromParcel(Parcel source) {
			ActionItemAction aia = new ActionItemAction();
			aia.id = source.readLong();
			aia.actionItemId = source.readLong();
			aia.type = source.readParcelable(ClassLoader.getSystemClassLoader());
			aia.actionName = source.readParcelable(ClassLoader.getSystemClassLoader());
			aia.config = source.readString();
			return aia;
		}

		@Override
		public ActionItemAction[] newArray(int size) {
			return new ActionItemAction[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(actionItemId);
		dest.writeParcelable(type, flags);
		dest.writeParcelable(actionName,flags);
		dest.writeString(config);
	}

	public enum ActionName implements Parcelable {
		TIMER;

		final static public Creator<ActionName> CREATOR = new Creator<ActionName>() {
			@Override
			public ActionName createFromParcel(Parcel source) {
				return ActionName.valueOf(source.readString());
			}

			@Override
			public ActionName[] newArray(int size) {
				return new ActionName[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(toString());
		}
	}

	public enum Type implements Parcelable {
		ENTRY, EXIT;

		final static public Creator<Type> CREATOR = new Creator<Type>() {
			@Override
			public Type createFromParcel(Parcel source) {
				return Type.valueOf(source.readString());
			}

			@Override
			public Type[] newArray(int size) {
				return new Type[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(toString());
		}
	}

	// DTO
	public long id;
	public long actionItemId;

	@SerializedName("type")
	public Type type;
	@SerializedName("action_name")
	public ActionName actionName;
	@SerializedName("config")
	public String config;

	static public List<ActionItemAction> fromCursor(Cursor c) {
		List<ActionItemAction> actionItemActions = new ArrayList<ActionItemAction>();
		ContentValues cv = new ContentValues();
		String n = null;
		while(c.moveToNext()) {
			cv.clear();
			DatabaseUtils.cursorRowToContentValues(c,cv);
			ActionItemAction aia = new ActionItemAction();

			n = ActionItemActionsTable.COL_ID.getName();
			if(cv.containsKey(n)) aia.id = cv.getAsLong(n);

			n = ActionItemActionsTable.COL_ACTION_ITEM_ID.getName();
			if(cv.containsKey(n)) aia.actionItemId = cv.getAsLong(n);

			n = ActionItemActionsTable.COL_TYPE.getName();
			if(cv.containsKey(n)) aia.type = Type.valueOf(cv.getAsString(n));

			n = ActionItemActionsTable.COL_ACTION_NAME.getName();
			if(cv.containsKey(n)) aia.actionName = ActionName.valueOf(cv.getAsString(n));

			n = ActionItemActionsTable.COL_CONFIG.getName();
			if(cv.containsKey(n)) aia.config = cv.getAsString(n);

			actionItemActions.add(aia);
		}
		return actionItemActions;
	}

	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();

		cv.put(ActionItemActionsTable.COL_ID.getName(), id);
		cv.put(ActionItemActionsTable.COL_ACTION_ITEM_ID.getName(), actionItemId);
		cv.put(ActionItemActionsTable.COL_TYPE.getName(), type.toString());
		cv.put(ActionItemActionsTable.COL_ACTION_NAME.getName(), actionName.toString());
		cv.put(ActionItemActionsTable.COL_CONFIG.getName(), config);

		return cv;
	}
}
