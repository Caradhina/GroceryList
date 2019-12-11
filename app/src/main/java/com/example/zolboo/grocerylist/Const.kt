package com.example.zolboo.grocerylist

import android.os.FileObserver.CREATE

const val DB_NAME = "GroceryList"
const val DB_VERSION = 1
const val TABLE_GROCERY = "Grocery"
const val COL_ID = "id"
const val COL_CREATED_AT = "createdAt"
const val COL_NAME = "name"


const val TABLE_GROCERY_ITEM = "GroceryItem"
const val COL_TODO_ID = "toDoId"
const val COL_ITEM_NAME = "itemName"
const val COL_IS_COMPLETED = "isCompleted"




const val INTENT_GROCERY_ID = "GroceryId"
const val INTENT_GROCERY_NAME = "GroceryName"