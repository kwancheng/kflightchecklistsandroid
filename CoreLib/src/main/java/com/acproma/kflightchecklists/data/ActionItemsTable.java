package com.acproma.kflightchecklists.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.acproma.kflightchecklists.models.ActionItem;
import com.acproma.kflightchecklists.models.ActionItemAction;

import java.util.List;

/**
 * Created by kwan.cheng on 9/13/2014.
 */
public class ActionItemsTable extends BaseTable {
    static final public Column COL_ID = Column.createIdColumn("id");
    static final public Column COL_SECTION_ID = Column.createIntegerColumn("section_id");
	static final public Column COL_MSG_TYPE = Column.createIntegerColumn("msg_type");
	static final public Column COL_DISPLAY_ORDER = Column.createIntegerColumn("display_order");
	static final public Column COL_STRING_1 = Column.createStringColumn("string_1");
	static final public Column COL_STRING_2 = Column.createStringColumn("string_2");
	static final public Column COL_IMAGE = new Column("image", "text", "allow nulls");
	static final public Column COL_ENTRY_ACTION_ID = new Column("entry_action_id", "integer", "allow nulls");
	static final public Column COL_EXIT_ACTION_ID = new Column("exit_action_id", "integer", "allow nulls");

	{
		tableName = "action_items";
		columns = new Column[] {
			COL_ID, COL_SECTION_ID, COL_MSG_TYPE, COL_DISPLAY_ORDER, COL_STRING_1, COL_STRING_2, COL_IMAGE, COL_ENTRY_ACTION_ID, COL_EXIT_ACTION_ID
		};
		identityColumns = new Column[] {
			COL_SECTION_ID, COL_MSG_TYPE, COL_STRING_1, COL_STRING_2, COL_ENTRY_ACTION_ID, COL_EXIT_ACTION_ID
		};
	}

	@Override
	void backupData(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	void restoreData(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public ActionItem getActionItem(long id) {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		List<ActionItem> actionItems = ActionItem.fromCursor(getElementsWithId(db, COL_ID, id));
		db.close();
		ActionItem actionItem = null;
		if(actionItems.size() > 0)
			actionItem = actionItems.get(0);
		return actionItem;
	}

	public List<ActionItem> getActionItems(long sectionId){
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		List<ActionItem> actionItems = ActionItem.fromCursor(getElementsWithId(db, COL_SECTION_ID, sectionId, COL_DISPLAY_ORDER));
		db.close();
		return actionItems;
	}

	public void addActionItem(ActionItem actionItem) {
		ContentValues cv = actionItem.toContentValues();
		cv.remove(COL_ID.getName());

		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		actionItem.id = db.insert(tableName, null, cv);
		db.close();

		if(actionItem.entryAction != null) {
			actionItem.entryAction.actionItemId = actionItem.id;
			actionItem.entryAction.type = ActionItemAction.Type.ENTRY;
			KFlightChecklistDB.getInstance().ACTION_ITEM_ACTIONS_TABLE.addActionItemAction(actionItem.entryAction);

			db = KFlightChecklistDB.getInstance().getWritableDatabase();
			cv.clear();
			cv.put(ActionItemsTable.COL_ENTRY_ACTION_ID.getName(), actionItem.entryAction.id);
			db.update(tableName,cv, ActionItemsTable.COL_ID.getName() + "=?", new String[]{Long.toString(actionItem.id)});
			db.close();
		}
		if(actionItem.exitAction != null) {
			actionItem.exitAction.actionItemId = actionItem.id;
			actionItem.exitAction.type = ActionItemAction.Type.EXIT;
			KFlightChecklistDB.getInstance().ACTION_ITEM_ACTIONS_TABLE.addActionItemAction(actionItem.exitAction);

			db = KFlightChecklistDB.getInstance().getWritableDatabase();
			cv.clear();
			cv.put(ActionItemsTable.COL_EXIT_ACTION_ID.getName(), actionItem.exitAction.id);
			db.update(tableName,cv, ActionItemsTable.COL_ID.getName() + "=?", new String[]{Long.toString(actionItem.id)});
			db.close();
		}
	}
}
