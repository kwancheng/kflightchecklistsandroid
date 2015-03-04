package com.acproma.kflightchecklists.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.acproma.kflightchecklists.data.SectionsTable;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwan.cheng on 9/13/2014.
 */
public class Section implements Parcelable {
	static final public Creator<Section> CREATOR = new ClassLoaderCreator<Section>() {
		@Override
		public Section createFromParcel(Parcel source, ClassLoader loader) {
			return createFromParcel(source);
		}

		@Override
		public Section createFromParcel(Parcel source) {
			Section s = new Section();
			s.id = source.readLong();
			s.checklistId = source.readLong();
			s.displayOrder = source.readInt();
			s.name = source.readString();
			s.description = source.readString();
			return s;
		}

		@Override
		public Section[] newArray(int size) {
			return new Section[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(checklistId);
		dest.writeInt(displayOrder);
		dest.writeString(name);
		dest.writeString(description);
	}

    // DTO
    public long id;
    public long checklistId;
    public int displayOrder;

    // Json Stuff
    @SerializedName("name")
    public String name = null;
    @SerializedName("description")
    public String description = null;
    @SerializedName("action_items")
    public List<ActionItem> actionItems = null;

	static public List<Section> fromCursor(Cursor c){
		List<Section> sections = new ArrayList<Section>();
		ContentValues cv = new ContentValues();
		while(c.moveToNext()) {
			cv.clear();
			DatabaseUtils.cursorRowToContentValues(c, cv);
			Section section = new Section();
			if(cv.containsKey(SectionsTable.COL_ID.getName()))
				section.id = cv.getAsLong(SectionsTable.COL_ID.getName());
			if(cv.containsKey(SectionsTable.COL_CHECKLIST_ID.getName()))
				section.checklistId = cv.getAsLong(SectionsTable.COL_CHECKLIST_ID.getName());
			if(cv.containsKey(SectionsTable.COL_NAME.getName()))
				section.name = cv.getAsString(SectionsTable.COL_NAME.getName());
			if(cv.containsKey(SectionsTable.COL_DESCRIPTION.getName()))
				section.description = cv.getAsString(SectionsTable.COL_DESCRIPTION.getName());
			if(cv.containsKey(SectionsTable.COL_DISPLAY_ORDER.getName()))
				section.displayOrder = cv.getAsInteger(SectionsTable.COL_DISPLAY_ORDER.getName());
			sections.add(section);
		}
		return sections;
	}

	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(SectionsTable.COL_ID.getName(), id);
		cv.put(SectionsTable.COL_CHECKLIST_ID.getName(), checklistId);
		cv.put(SectionsTable.COL_NAME.getName(), name);
		cv.put(SectionsTable.COL_DESCRIPTION.getName(), description);
		cv.put(SectionsTable.COL_DISPLAY_ORDER.getName(), displayOrder);
		return cv;
	}
}
