package com.acproma.kflightchecklists.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.acproma.kflightchecklists.data.CraftsTable;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Congee on 9/18/14.
 */
public class Craft implements Parcelable{
	public static final Creator<Craft> CREATOR = new ClassLoaderCreator<Craft>() {
		@Override
		public Craft createFromParcel(Parcel source, ClassLoader loader) {
			return createFromParcel(source);
		}

		@Override
		public Craft createFromParcel(Parcel source) {
			Craft craft = new Craft();
			craft.id = source.readLong();
			craft.displayOrder = source.readInt();
			craft.name = source.readString();
			craft.description = source.readString();
			craft.version = source.readString();
			craft.imagesDirectory = source.readString();
			boolean[] bArr = new boolean[1];
			source.readBooleanArray(bArr);
			craft.isPoh = bArr[0];
			return craft;
		}

		@Override
		public Craft[] newArray(int size) {
			return new Craft[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeInt(displayOrder);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(version);
		dest.writeString(imagesDirectory);
		dest.writeBooleanArray(new boolean[]{isPoh});
	}

    // DTO Stuff
    public long id;
    public int displayOrder;
	public String imagesDirectory;

    // Json serializable
    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("version")
    public String version;
    @SerializedName("is_poh")
    public boolean isPoh;
    @SerializedName("checklists")
    public List<Checklist> checklists;

	static public List<Craft> fromCursor(Cursor c) {
		List<Craft> crafts = new ArrayList<Craft>();

		ContentValues cv = new ContentValues();
		while(c.moveToNext()) {
			cv.clear();
			DatabaseUtils.cursorRowToContentValues(c, cv);
			Craft craft = new Craft();
			if(cv.containsKey(CraftsTable.COL_ID.getName()))
				craft.id = cv.getAsLong(CraftsTable.COL_ID.getName());
			if(cv.containsKey(CraftsTable.COL_NAME.getName()))
				craft.name = cv.getAsString(CraftsTable.COL_NAME.getName());
			if(cv.containsKey(CraftsTable.COL_DESCRIPTION.getName()))
				craft.description = cv.getAsString(CraftsTable.COL_DESCRIPTION.getName());
			if(cv.containsKey(CraftsTable.COL_VERSION.getName()))
				craft.version = cv.getAsString(CraftsTable.COL_VERSION.getName());
			if(cv.containsKey(CraftsTable.COL_IMAGES_DIRECTORY.getName()))
				craft.imagesDirectory = cv.getAsString(CraftsTable.COL_IMAGES_DIRECTORY.getName());
			if(cv.containsKey(CraftsTable.COL_IS_POH.getName()))
				craft.isPoh = cv.getAsBoolean(CraftsTable.COL_IS_POH.getName());
			if(cv.containsKey(CraftsTable.COL_DISPLAY_ORDER.getName()))
				craft.displayOrder = cv.getAsInteger(CraftsTable.COL_DISPLAY_ORDER.getName());
			crafts.add(craft);
		}

		return crafts;
	}

	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(CraftsTable.COL_ID.getName(), id);
		cv.put(CraftsTable.COL_NAME.getName(), name);
		cv.put(CraftsTable.COL_DESCRIPTION.getName(), description);
		cv.put(CraftsTable.COL_VERSION.getName(), version);
		cv.put(CraftsTable.COL_IMAGES_DIRECTORY.getName(), imagesDirectory);
		cv.put(CraftsTable.COL_DISPLAY_ORDER.getName(), displayOrder);
		cv.put(CraftsTable.COL_IS_POH.getName(), isPoh);
		return cv;
	}
}