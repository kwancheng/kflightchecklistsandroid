package com.acproma.kflightchecklists.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.acproma.kflightchecklists.models.Checklist;

import java.util.List;

/**
 * Created by kwan.cheng on 9/11/2014.
 */
public class ChecklistsTable extends BaseTable{
    static final public Column COL_ID = Column.createIdColumn("id");
    static final public Column COL_CRAFT_ID = Column.createIntegerColumn("craft_id");
    static final public Column COL_NAME = Column.createStringColumn("name");
    static final public Column COL_DESCRIPTION = Column.createStringColumn("description");
    static final public Column COL_DISPLAY_ORDER = Column.createIntegerColumn("display_order");

	{
		tableName = "checklists";
		columns = new Column[] {
			COL_ID, COL_CRAFT_ID, COL_NAME, COL_DESCRIPTION, COL_DISPLAY_ORDER
		};
		identityColumns  = new Column[] {
			COL_CRAFT_ID, COL_NAME
		};
	}

	@Override
	void backupData(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	void restoreData(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public Checklist getChecklist(long id, boolean deepCopy) {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		List<Checklist> checklists = Checklist.fromCursor(getElementsWithId(db, COL_ID, id, COL_DISPLAY_ORDER));
		db.close();
		Checklist checklist = null;
		if(checklists.size() > 0)
			checklist = checklists.get(0);

		if(deepCopy) {
			// TODO
		}

		return checklist;
	}

	public List<Checklist> getChecklists(long craftId, boolean deepCopy) {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		List<Checklist> checklists = Checklist.fromCursor(getElementsWithId(db, COL_CRAFT_ID, craftId, COL_DISPLAY_ORDER));
		db.close();
		if(deepCopy) {
			// TODO
		}
		return checklists;
	}

	public void addChecklist(Checklist checklist, boolean deepCopy) {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		ContentValues cv = checklist.toContentValues();
		cv.remove(COL_ID.getName());

		checklist.id = db.insert(tableName, null, cv);

		if(deepCopy){
			// TODO add section
		}
		db.close();
	}

	public boolean contains(Checklist checklist) {
		return contains(identityColumns, new String[]{
			Long.toString(checklist.craftId), checklist.name
		});
	}
}

