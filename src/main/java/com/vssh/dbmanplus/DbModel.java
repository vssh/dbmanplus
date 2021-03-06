package com.vssh.dbmanplus;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by varun on 24.08.15.
 *
 * Helper class for DbManPlus. Extend this for each table in database.
 */
public abstract class DbModel<T extends DbItem> {
    protected DbManPlus mDbManager;

    public DbModel(DbManPlus dbManager) {
        this.mDbManager = dbManager;
    }

    /**
     * Get table name
     * @return Table name
     */
    public abstract String getTableName();

    /**
     * Check if values should be inserted into the database
     * @param values a map from column names to new column values. null is a
     *            valid value that will be translated to NULL.
     * @return true, if it should continue to insert
     */
    public abstract boolean continueInsert(ContentValues values);

    /**
     * Insert into this table
     * @param values a map from column names to new column values. null is a
     *            valid value that will be translated to NULL.
     * @return row ID if successful, else -1
     */
    public long insert(ContentValues values) {
        long result = -1;
        if(this.continueInsert(values)) {
            result = mDbManager.insert(this.getTableName(), values);
        }
        return result;
    }

    /**
     * Insert into this table
     * @param item an object of the appropriate type.
     * @return row ID if successful, else -1
     */
    public long insert(T item) {
        return insert(item.toContentValues(getTableColumns()));
    }

    /**
     * Insert multiple rows as one transaction
     * @param values a map from column names to new column values. null is a
     *            valid value that will be translated to NULL.
     * @return number of rows
     */
    public int bulkInsert(ContentValues[] values) {
        return mDbManager.bulkInsert(this.getTableName(), values);
    }

    /**
     * Update rows in this table
     * @param values a map from column names to new column values. null is a
     *            valid value that will be translated to NULL.
     * @param where the optional WHERE clause to apply when updating.
     *            Passing null will update all rows.
     * @param whereArgs You may include ?s in the where clause, which
     *            will be replaced by the values from whereArgs. The values
     *            will be bound as Strings.
     * @return number of rows
     */
    public int update(ContentValues values, String where, String[] whereArgs) {
        return mDbManager.update(this.getTableName(), values, where, whereArgs);
    }

    /**
     * Update rows in this table
     * @param item an object of appropriate type.
     * @param where the optional WHERE clause to apply when updating.
     *            Passing null will update all rows.
     * @param whereArgs You may include ?s in the where clause, which
     *            will be replaced by the values from whereArgs. The values
     *            will be bound as Strings.
     * @return number of rows
     */
    public int update(T item, String where, String[] whereArgs) {
        return mDbManager.update(this.getTableName(), item.toContentValues(getTableColumns()), where, whereArgs);
    }

    /**
     * Delete from this table
     * @param where the optional WHERE clause to apply when deleting.
     *            Passing null will update all rows.
     * @param whereArgs You may include ?s in the where clause, which
     *            will be replaced by the values from whereArgs. The values
     *            will be bound as Strings.
     * @return number of rows
     */
    public int delete(String where, String[] whereArgs) {
        return mDbManager.delete(this.getTableName(), where, whereArgs);
    }

    /**
     * Query this table
     * @param projection A list of which columns to return. Passing
     *   null will return all columns, which is discouraged to prevent
     *   reading data from storage that isn't going to be used.
     * @param where A filter declaring which rows to return,
     *   formatted as an SQL WHERE clause (excluding the WHERE
     *   itself). Passing null will return all rows for the given URL.
     * @param whereArgs You may include ?s in the where clause, which
     *            will be replaced by the values from whereArgs. The values
     *            will be bound as Strings.
     * @param sortOrder How to order the rows, formatted as an SQL
     *   ORDER BY clause (excluding the ORDER BY itself). Passing null
     *   will use the default sort order, which may be unordered.
     * @return Cursor
     */
    public Cursor query(String[] projection, String where, String[] whereArgs, String sortOrder) {
        return this.query(projection, where, whereArgs, null, null, sortOrder, null);
    }

    /**
     * Query this table
     * @param projection A list of which columns to return. Passing
     *   null will return all columns, which is discouraged to prevent
     *   reading data from storage that isn't going to be used.
     * @param where A filter declaring which rows to return,
     *   formatted as an SQL WHERE clause (excluding the WHERE
     *   itself). Passing null will return all rows for the given URL.
     * @param whereArgs You may include ?s in the where clause, which
     *            will be replaced by the values from whereArgs. The values
     *            will be bound as Strings.
     * @param groupBy A filter declaring how to group rows, formatted
     *   as an SQL GROUP BY clause (excluding the GROUP BY
     *   itself). Passing null will cause the rows to not be grouped.
     * @param having A filter declare which row groups to include in
     *   the cursor, if row grouping is being used, formatted as an
     *   SQL HAVING clause (excluding the HAVING itself).  Passing
     *   null will cause all row groups to be included, and is
     *   required when row grouping is not being used.
     * @param sortOrder How to order the rows, formatted as an SQL
     *   ORDER BY clause (excluding the ORDER BY itself). Passing null
     *   will use the default sort order, which may be unordered.
     * @param limit Limits the number of rows returned by the query,
     *   formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return Cursor
     */
    public Cursor query(String[] projection, String where, String[] whereArgs, @Nullable String groupBy,
                        @Nullable String having, String sortOrder, @Nullable String limit) {
        return mDbManager.query(this.getTableName(), projection, where, whereArgs, groupBy, having, sortOrder, limit);
    }

    /**
     * Query table (@link #query) and get a list of objects as defined in {@link #cursorToList}
     *@param projection A list of which columns to return. Passing
     *   null will return all columns, which is discouraged to prevent
     *   reading data from storage that isn't going to be used.
     * @param where A filter declaring which rows to return,
     *   formatted as an SQL WHERE clause (excluding the WHERE
     *   itself). Passing null will return all rows for the given URL.
     * @param whereArgs You may include ?s in the where clause, which
     *            will be replaced by the values from whereArgs. The values
     *            will be bound as Strings.
     * @param sortOrder How to order the rows, formatted as an SQL
     *   ORDER BY clause (excluding the ORDER BY itself). Passing null
     *   will use the default sort order, which may be unordered.
     * @return list of Objects returned by {@link #getModelItem}
     */
    public ArrayList<T> queryAsList(String[] projection, String where, String[] whereArgs, String sortOrder) {
        Cursor cursor = this.query(projection, where, whereArgs, null, null, sortOrder, null);
        ArrayList<T> list = cursorToList(cursor);
        cursor.close();

        return list;
    }

    /**
     * Query table (@link #query) and get a list of objects as defined in {@link #cursorToList}
     * @param projection A list of which columns to return. Passing
     *   null will return all columns, which is discouraged to prevent
     *   reading data from storage that isn't going to be used.
     * @param where A filter declaring which rows to return,
     *   formatted as an SQL WHERE clause (excluding the WHERE
     *   itself). Passing null will return all rows for the given URL.
     * @param whereArgs You may include ?s in the where clause, which
     *            will be replaced by the values from whereArgs. The values
     *            will be bound as Strings.
     * @param groupBy A filter declaring how to group rows, formatted
     *   as an SQL GROUP BY clause (excluding the GROUP BY
     *   itself). Passing null will cause the rows to not be grouped.
     * @param having A filter declare which row groups to include in
     *   the cursor, if row grouping is being used, formatted as an
     *   SQL HAVING clause (excluding the HAVING itself).  Passing
     *   null will cause all row groups to be included, and is
     *   required when row grouping is not being used.
     * @param sortOrder How to order the rows, formatted as an SQL
     *   ORDER BY clause (excluding the ORDER BY itself). Passing null
     *   will use the default sort order, which may be unordered.
     * @param limit Limits the number of rows returned by the query,
     *   formatted as LIMIT clause. Passing null denotes no LIMIT clause.
     * @return list of Objects returned by {@link #getModelItem}
     */
    public ArrayList<T> queryAsList(String[] projection, String where, String[] whereArgs, @Nullable String groupBy,
                        @Nullable String having, String sortOrder, @Nullable String limit) {
        Cursor cursor = mDbManager.query(this.getTableName(), projection, where, whereArgs, groupBy, having, sortOrder, limit);
        ArrayList<T> list = cursorToList(cursor);
        cursor.close();

        return list;
    }

    /**
     * Retrieve an Object list (as described in {@link #getModelItem})
     * @param cursor input cursor
     * @return list of Objects returned by {@link #getModelItem}
     */
    public ArrayList<T> cursorToList(Cursor cursor) {
        ArrayList<T> list = new ArrayList<>();
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int pos = cursor.getPosition();

                T item = getModelItem();
                if (item != null) {
                    item.fromCursor(cursor, getTableColumns());
                    list.add(item);
                }

                if(pos == cursor.getPosition()) {
                    cursor.moveToNext();
                }
                else {
                    cursor.moveToPosition(pos+1);
                }
            }
        }
        return list;
    }

    /**
     * Get a basic object of the correct type
     * @return object of desired type
     */
    protected abstract @Nullable T getModelItem();


    /**
     * Get table columns as an array
     * REMEMBER to include id column
     * @return array of column names
     */
    protected abstract @NonNull String[] getTableColumns();
}
