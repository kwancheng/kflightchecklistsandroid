package com.acproma.kflightchecklists.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.acproma.kflightchecklists.models.Craft;

import java.util.List;

public final class CraftsTable extends BaseTable {
	static final public Column COL_ID = Column.createIdColumn("id");
	static final public Column COL_NAME = Column.createStringColumn("name");
	static final public Column COL_DESCRIPTION = Column.createStringColumn("description");
	static final public Column COL_VERSION = Column.createStringColumn("version");
	static final public Column COL_IMAGES_DIRECTORY = Column.createStringColumn("images_directory");
	static final public Column COL_IS_POH = Column.createBooleanColumn("is_pos");
	static final public Column COL_DISPLAY_ORDER = Column.createIntegerColumn("display_order");

	{
		tableName = "crafts";
		columns = new Column[]{
			COL_ID, COL_NAME, COL_DESCRIPTION, COL_VERSION, COL_IMAGES_DIRECTORY, COL_IS_POH, COL_DISPLAY_ORDER
		};
		identityColumns  = new Column[] {
				COL_NAME, COL_VERSION, COL_IS_POH
		};
	}

	@Override
	void backupData(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	void restoreData(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public Craft getCraft(long id, boolean deepCopy) {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		List<Craft> crafts = Craft.fromCursor(getElementsWithId(db, COL_ID, id));
		db.close();
		Craft craft = null;
		if(crafts.size() > 0) {
			craft = crafts.get(0);
			if(deepCopy) {
				// TODO
			}
		}
		return craft;
	}

	public List<Craft> getCrafts() {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		Cursor c = db.query(tableName, null, null, null, null, null, COL_DISPLAY_ORDER.getName());
		List<Craft> crafts = Craft.fromCursor(c);
		db.close();
		return crafts;
	}

	public void addCraft(Craft craft, boolean deepCopy) {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		ContentValues cv = craft.toContentValues();
		cv.remove(COL_ID.getName());

		craft.id = db.insert(tableName, null, cv);

		if(deepCopy) {
			// TODO
		}
		db.close();
	}

	public boolean contains(Craft craft) {
		return contains(identityColumns, new String[]{
			craft.name, craft.version, (craft.isPoh ? "1" : "0")
		});
	}
}
