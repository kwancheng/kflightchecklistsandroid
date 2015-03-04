package com.acproma.kflightchecklists.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.acproma.kflightchecklists.models.Section;

import java.util.List;

/**
 * Created by kwan.cheng on 9/13/2014.
 */
public class SectionsTable extends BaseTable {
    static final public Column COL_ID = Column.createIdColumn("id");
    static final public Column COL_CHECKLIST_ID = Column.createStringColumn("checklist_id");
    static final public Column COL_NAME = Column.createStringColumn("name");
    static final public Column COL_DESCRIPTION = Column.createStringColumn("description");
    static final public Column COL_DISPLAY_ORDER = Column.createIntegerColumn("display_order");

	{
		tableName = "sections";
		columns = new Column[] {
			COL_ID, COL_CHECKLIST_ID, COL_NAME, COL_DESCRIPTION, COL_DISPLAY_ORDER
		};
		identityColumns= new Column[] {
				COL_CHECKLIST_ID, COL_NAME
		};
	}

	@Override
	void backupData(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	void restoreData(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public Section getSection(long id, boolean deepCopy) {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		List<Section> sections = Section.fromCursor(getElementsWithId(db, COL_ID, id));
		db.close();
		Section section = null;
		if(sections.size() > 0) {
			section = sections.get(0);
			if(deepCopy) {
				// TODO
			}
		}
		return section;
	}

	public List<Section> getSections(long checklistId, boolean deepCopy) {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		List<Section> sections = Section.fromCursor(getElementsWithId(db, COL_CHECKLIST_ID, checklistId));
		db.close();
		if(deepCopy) {
			// todo
		}
		return sections;
	}

	public void addSection(Section section, boolean deepCopy){
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		ContentValues cv = section.toContentValues();
		cv.remove(COL_ID.getName());

		section.id = db.insert(tableName, null, cv);

		if(deepCopy){
			// TODO
		}
		db.close();
	}

	public boolean contains(Section section){
		return contains(identityColumns, new String[]{
				Long.toString(section.checklistId), section.name
		});
	}
}