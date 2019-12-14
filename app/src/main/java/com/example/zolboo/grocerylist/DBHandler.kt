package com.example.zolboo.grocerylist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.zolboo.grocerylist.DTO.Grocery
import com.example.zolboo.grocerylist.DTO.GroceryItem

class DBHandler(val context: Context) : SQLiteOpenHelper(context,
    DB_NAME, null,
    DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        val createGroceryTable = "  CREATE TABLE $TABLE_GROCERY (" +
                "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                "$COL_NAME varchar);"
        val createCroceryItemTable =
            "CREATE TABLE $TABLE_GROCERY_ITEM (" +
                    "$COL_ID integer PRIMARY KEY AUTOINCREMENT," +
                    "$COL_CREATED_AT datetime DEFAULT CURRENT_TIMESTAMP," +
                    "$COL_GROCERY_ID integer," +
                    "$COL_ITEM_NAME varchar," +
                    "$COL_IS_COMPLETED integer);"

        db.execSQL(createGroceryTable)
        db.execSQL(createCroceryItemTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }


    fun addGrocery(grocery: Grocery): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, grocery.name)
        val result = db.insert(TABLE_GROCERY, null, cv)
        return result != (-1).toLong()
    }

    fun updateGrocery(grocery: Grocery) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, grocery.name)
        db.update(
            TABLE_GROCERY,cv,"$COL_ID=?" , arrayOf(grocery.id
            .toString()))
    }

    fun deleteGrocery(groceryId: Long){
        val db = writableDatabase
        db.delete(TABLE_GROCERY_ITEM,"$COL_GROCERY_ID=?", arrayOf(groceryId.toString()))
        db.delete(TABLE_GROCERY,"$COL_ID=?", arrayOf(groceryId.toString()))
    }

    fun updateGroceryItemCompletedStatus(groceryId: Long, isCompleted: Boolean){
        val db = writableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_GROCERY_ITEM WHERE $COL_GROCERY_ID=$groceryId", null)

        if (queryResult.moveToFirst()) {
            do {
                val item = GroceryItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.groceryId = queryResult.getLong(queryResult.getColumnIndex(COL_GROCERY_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.isCompleted = isCompleted
                updateGroceryItem(item)
            } while (queryResult.moveToNext())
        }

        queryResult.close()
    }

    fun getGrocery(): MutableList<Grocery> {
        val result: MutableList<Grocery> = ArrayList()
        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * from $TABLE_GROCERY", null)
        if (queryResult.moveToFirst()) {
            do {
                val grocery = Grocery()
                grocery.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                grocery.name = queryResult.getString(queryResult.getColumnIndex(COL_NAME))
                result.add(grocery)
            } while (queryResult.moveToNext())
        }
        queryResult.close()
        return result
    }

    fun addGroceryItem(item: GroceryItem): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_GROCERY_ID, item.groceryId)
        cv.put(COL_IS_COMPLETED, item.isCompleted)

        val result = db.insert(TABLE_GROCERY_ITEM, null, cv)
        return result != (-1).toLong()
    }

    fun updateGroceryItem(item: GroceryItem) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(COL_ITEM_NAME, item.itemName)
        cv.put(COL_GROCERY_ID, item.groceryId)
        cv.put(COL_IS_COMPLETED, item.isCompleted)

        db.update(TABLE_GROCERY_ITEM, cv, "$COL_ID=?", arrayOf(item.id.toString()))
    }

    fun deleteGroceryItem(itemId : Long){
        val db = writableDatabase
        db.delete(TABLE_GROCERY_ITEM,"$COL_ID=?" , arrayOf(itemId.toString()))
    }

    fun getGroceryItems(groceryId: Long): MutableList<GroceryItem> {
        val result: MutableList<GroceryItem> = ArrayList()

        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * FROM $TABLE_GROCERY_ITEM WHERE $COL_GROCERY_ID=$groceryId", null)

        if (queryResult.moveToFirst()) {
            do {
                val item = GroceryItem()
                item.id = queryResult.getLong(queryResult.getColumnIndex(COL_ID))
                item.groceryId = queryResult.getLong(queryResult.getColumnIndex(COL_GROCERY_ID))
                item.itemName = queryResult.getString(queryResult.getColumnIndex(COL_ITEM_NAME))
                item.isCompleted = queryResult.getInt(queryResult.getColumnIndex(COL_IS_COMPLETED)) == 1
                result.add(item)
            } while (queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }

}