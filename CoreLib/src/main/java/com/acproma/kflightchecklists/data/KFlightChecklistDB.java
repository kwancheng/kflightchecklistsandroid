package com.acproma.kflightchecklists.data;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.acproma.kflightchecklists.AppController;

/**
 * Created by kwan.cheng on 9/13/2014.
 */
final public class KFlightChecklistDB extends SQLiteOpenHelper {
    final static String DATABASE_NAME = "kflightchecklists.db";
    final static int DB_VERSION = 1;

	final static public CraftsTable CRAFTS_TABLE = new CraftsTable();
	final static public ChecklistsTable CHECKLISTS_TABLE = new ChecklistsTable();
	final static public SectionsTable SECTIONS_TABLE = new SectionsTable();
	final static public ActionItemsTable ACTION_ITEMS_TABLE = new ActionItemsTable();
	final static public ActionItemActionsTable ACTION_ITEM_ACTIONS_TABLE = new ActionItemActionsTable();
	final static BaseTable[] tables;

	static private KFlightChecklistDB instance;

	static {
		tables = new BaseTable[] {
			CRAFTS_TABLE, CHECKLISTS_TABLE, SECTIONS_TABLE, ACTION_ITEMS_TABLE, ACTION_ITEM_ACTIONS_TABLE
		};
		instance = new KFlightChecklistDB();
	}

	synchronized static public KFlightChecklistDB getInstance() {
		return instance;
	}

	private Integer oldVersion = -1;
	private Integer newVersion = -1;

	private KFlightChecklistDB() {
		super(AppController.getInstance().getApplicationContext(), DATABASE_NAME, null, DB_VERSION);
	}

    @Override
    final public void onCreate(SQLiteDatabase db) {
	    for(BaseTable table : tables)
		    table.onCreate(db, oldVersion, DB_VERSION);
    }

    @Override
    final public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    this.oldVersion = oldVersion;
	    this.newVersion = newVersion;

		for(BaseTable table : tables) {
			table.onUpgrade(db, oldVersion, newVersion);
		}
        onCreate(db);
    }

    public void clear() {
		for(BaseTable table : tables)
			table.clear();
    }
}