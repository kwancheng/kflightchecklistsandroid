package com.acproma.kflightchecklists.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.acproma.kflightchecklists.data.ChecklistsTable;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Congee on 9/18/14.
 */
public class Checklist implements Parcelable {
	static final public Creator<Checklist> CREATOR = new ClassLoaderCreator<Checklist>() {
		@Override
		public Checklist createFromParcel(Parcel source, ClassLoader loader) {
			Checklist c = new Checklist();

			c.id = source.readLong();
			c.craftId = source.readLong();
			c.displayOrder = source.readInt();
			c.name = source.readString();
			c.description = source.readString();
			return c;
		}

		@Override
		public Checklist createFromParcel(Parcel source) {
			return createFromParcel(source, null);
		}

		@Override
		public Checklist[] newArray(int size) {
			return new Checklist[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(craftId);
		dest.writeInt(displayOrder);
		dest.writeString(name);
		dest.writeString(description);
	}

    // DTO
    public long id;
    public long craftId;
    public int displayOrder;

    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("sections")
    public List<Section> sections;

	static public List<Checklist> fromCursor(Cursor c) {
		List<Checklist> checklists = new ArrayList<Checklist>();
		ContentValues cv = new ContentValues();
		while(c.moveToNext()) {
			cv.clear();
			DatabaseUtils.cursorRowToContentValues(c, cv);
			Checklist checklist = new Checklist();
			if(cv.containsKey(ChecklistsTable.COL_ID.getName()))
				checklist.id = cv.getAsLong(ChecklistsTable.COL_ID.getName());
			if(cv.containsKey(ChecklistsTable.COL_CRAFT_ID.getName()))
				checklist.craftId = cv.getAsLong(ChecklistsTable.COL_CRAFT_ID.getName());
			if(cv.containsKey(ChecklistsTable.COL_NAME.getName()))
				checklist.name = cv.getAsString(ChecklistsTable.COL_NAME.getName());
			if(cv.containsKey(ChecklistsTable.COL_DESCRIPTION.getName()))
				checklist.description = cv.getAsString(ChecklistsTable.COL_DESCRIPTION.getName());
			if(cv.containsKey(ChecklistsTable.COL_DISPLAY_ORDER.getName()))
				checklist.displayOrder = cv.getAsInteger(ChecklistsTable.COL_DISPLAY_ORDER.getName());
			checklists.add(checklist);
		}
		return checklists;
	}

	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(ChecklistsTable.COL_ID.getName(), id);
		cv.put(ChecklistsTable.COL_CRAFT_ID.getName(), craftId);
		cv.put(ChecklistsTable.COL_NAME.getName(), name);
		cv.put(ChecklistsTable.COL_DESCRIPTION.getName(), description);
		cv.put(ChecklistsTable.COL_DISPLAY_ORDER.getName(), displayOrder);
		return cv;
	}
}
