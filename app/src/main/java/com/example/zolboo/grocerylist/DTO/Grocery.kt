package com.example.zolboo.grocerylist.DTO

class Grocery {

    var id: Long = -1
    var name = ""
    var createdAt = ""
    var items: MutableList<GroceryItem> = ArrayList()

}