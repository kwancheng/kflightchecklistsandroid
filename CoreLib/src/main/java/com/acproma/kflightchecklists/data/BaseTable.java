package com.acproma.kflightchecklists.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kwan.cheng on 9/24/2014.
 */
abstract public class BaseTable {
	final static public String DEL_STM = "DELETE FROM %s";
	final static public String DROP_STM = "DROP TABLE IF EXISTS %s";
	final static public String TABLE_CREATE = "CREATE TABLE %s(%s)";
	final static public String ID_QUERY_STM = "SELECT * FROM %s WHERE %s=?";
	final static public String ID_QUERY_WITH_ORDER_STM = "SELECT * FROM %s WHERE %s=? ORDER BY %s";

	static public String getColCreateStatement(Column ... columns) {
		StringBuilder sb = new StringBuilder();

		int ntl = columns.length-1;
		for(int i = 0; i < columns.length; i++) {
			sb.append(columns[i].getCreateStatement());
			if(i < ntl)
				sb.append(",");
		}

		return sb.toString();
	}

	static public String constructWhereClause(Column... columns) {
		StringBuilder sb = new StringBuilder();

		int ntl = columns.length-1;
		for(int i = 0; i < columns.length; i++) {
			sb.append(String.format("%s=?", columns[i].getName()));
			if(i < ntl)
				sb.append(" AND ");
		}

		return sb.toString();
	}

	String tableName;
	Column[] columns;
	Column[] identityColumns;

	abstract void backupData(SQLiteDatabase db, int oldVersion, int newVersion);
	abstract void restoreData(SQLiteDatabase db, int oldVersion, int newVersion);

	final public void onCreate(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(getTableCreateStatement());
		restoreData(db, oldVersion, newVersion);
	}

	final public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		backupData(db, oldVersion, newVersion);
		db.execSQL(getDropStatement());
	}

	public String getTableCreateStatement() {
		String colCreateStm = getColCreateStatement(columns);
		return String.format(TABLE_CREATE, tableName, colCreateStm);
	}

	public String getDropStatement() {
		return String.format(DROP_STM, tableName);
	}

	public void clear() {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		db.execSQL(String.format(DEL_STM, tableName));
		db.close();
	}

	public Cursor getElementsWithId(SQLiteDatabase db, Column idColumn, long id) {
		String sql = String.format(ID_QUERY_STM, tableName, idColumn.getName());
		return db.rawQuery(sql, new String[]{Long.toString(id)});
	}

	public Cursor getElementsWithId(SQLiteDatabase db, Column idColumn, long id, Column orderByColumn) {
		String sql = String.format(ID_QUERY_WITH_ORDER_STM, tableName, idColumn.getName(), orderByColumn.getName());
		return db.rawQuery(sql, new String[]{Long.toString(id)});
	}

	public boolean contains(Column[] idColumns, String[] args) {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		String whereClause = constructWhereClause(idColumns);
		boolean retVal = DatabaseUtils.queryNumEntries(db, tableName, whereClause, args) > 0;
		db.close();
		return retVal;
	}

	public void remove(Column[] idColumns, String[] args) {
		SQLiteDatabase db = KFlightChecklistDB.getInstance().getWritableDatabase();
		String whereClause = constructWhereClause(idColumns);
		db.delete(tableName, whereClause, args);
		db.close();
	}
}
