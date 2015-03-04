package com.acproma.kflightchecklists.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.acproma.kflightchecklists.models.ActionItemAction;

import java.util.List;

/**
 * Created by kwan.cheng on 9/23/2014.
 */
public class ActionItemActionsTable extends BaseTable {
	static final public Column COL_ID = Column.createIdColumn("id");
	static final public Column COL_ACTION_ITEM_ID = Column.createIntegerColumn("action_item_id");
	static final public Column COL_TYPE = Column.createStringColumn("type");
	static final public Column COL_ACTION_NAME = Column.createStringColumn("action_name");
	static final public Column COL_CONFIG = Column.createStringColumn("config");
	static final public Column[] identityColumns = new Column[]{
		COL_ACTION_ITEM_ID, COL_TYPE
	};

	{
		tableName = "action_item_actions";
		columns = new Column[]{
			COL_ID, COL_ACTION_ITEM_ID, COL_TYPE, COL_ACTION_NAME, COL_CONFIG
		};
	}

	@Override
	void backupData(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	void restoreData(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public ActionItemAction getActionItemAction(long id){
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		List<ActionItemAction> aias = ActionItemAction.fromCursor(getElementsWithId(db, COL_ID, id));
		db.close();
		ActionItemAction aia = null;
		if(aias.size()>0)
			aia = aias.get(0);
		return aia;
	}

	public List<ActionItemAction> getActionItemActions(long actionItemId) {
		return null;
	}

	public void addActionItemAction(ActionItemAction actionItemAction) {
		if(contains(actionItemAction)) {
			remove(identityColumns, new String[]{
					Long.toString(actionItemAction.actionItemId),
					actionItemAction.type.toString()
			});
		}

		ContentValues cv = actionItemAction.toContentValues();
		cv.remove(COL_ID.getName());

		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		actionItemAction.id = db.insert(tableName, null, cv);
		db.close();
	}

	public boolean contains(ActionItemAction actionItemAction) {
		return contains(identityColumns, new String[]{
				Long.toString(actionItemAction.actionItemId),
				actionItemAction.type.toString()
		});
	}
}
