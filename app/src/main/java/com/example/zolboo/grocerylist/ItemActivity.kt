package com.example.zolboo.grocerylist

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import com.example.zolboo.grocerylist.DTO.GroceryItem
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler
    var groceryId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(item_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = intent.getStringExtra(INTENT_GROCERY_NAME)
        groceryId = intent.getLongExtra(INTENT_GROCERY_ID, -1)
        dbHandler = DBHandler(this)

        rv_item.layoutManager = LinearLayoutManager(this)

        fab_item.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add Grocery Notes")
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val groceryName = view.findViewById<EditText>(R.id.ev_grocery)
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (groceryName.text.isNotEmpty()) {
                    val item = GroceryItem()
                    item.itemName = groceryName.text.toString()
                    item.groceryId = groceryId
                    item.isCompleted = false
                    dbHandler.addGroceryItem(item)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }

    }

    fun updateItem(item : GroceryItem){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Update Grocery Note")
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val groceryName = view.findViewById<EditText>(R.id.ev_grocery)
        groceryName.setText(item.itemName)
        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (groceryName.text.isNotEmpty()) {
                item.itemName = groceryName.text.toString()
                item.groceryId = groceryId
                item.isCompleted = false
                dbHandler.updateGroceryItem(item)
                refreshList()
            }
        }
        dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList() {
        rv_item.adapter = ItemAdapter(
            this,
            dbHandler.getGroceryItems(groceryId)
        )
    }

    class ItemAdapter(val activity: ItemActivity, val list: MutableList<GroceryItem>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(
                    activity
                ).inflate(R.layout.rv_child_item, p0, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.itemName.text = list[p1].itemName
            holder.itemName.isChecked = list[p1].isCompleted
            holder.itemName.setOnClickListener {
                list[p1].isCompleted = !list[p1].isCompleted
                activity.dbHandler.updateGroceryItem(list[p1])
            }
            holder.delete.setOnClickListener {
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("Are you sure")
                dialog.setMessage("Do you want to delete this note ?")
                dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                    activity.dbHandler.deleteGroceryItem(list[p1].id)
                    activity.refreshList()
                }
                dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

                }
                dialog.show()
            }
            holder.edit.setOnClickListener {
                activity.updateItem(list[p1])
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val itemName: CheckBox = v.findViewById(R.id.cb_item)
            val edit : ImageView = v.findViewById(R.id.iv_edit)
            val delete : ImageView = v.findViewById(R.id.iv_delete)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else
            super.onOptionsItemSelected(item)
    }

}
