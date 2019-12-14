package com.example.zolboo.grocerylist

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.example.zolboo.grocerylist.DTO.Grocery
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    lateinit var dbHandler: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(dashboard_toolbar)
        title = "Grocery List"
        dbHandler = DBHandler(this)
        rv_dashboard.layoutManager = LinearLayoutManager(this)

        fab_dashboard.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Add Grocery")
            val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
            val groceryName = view.findViewById<EditText>(R.id.ev_grocery)
            dialog.setView(view)
            dialog.setPositiveButton("Add") { _: DialogInterface, _: Int ->
                if (groceryName.text.isNotEmpty()) {
                    val grocery = Grocery()
                    grocery.name = groceryName.text.toString()
                    dbHandler.addGrocery(grocery)
                    refreshList()
                }
            }
            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }

    }

    fun updateGrocery(grocery: Grocery){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Update Grocery")
        val view = layoutInflater.inflate(R.layout.dialog_dashboard, null)
        val groceryName = view.findViewById<EditText>(R.id.ev_grocery)
        groceryName.setText(grocery.name)
        dialog.setView(view)
        dialog.setPositiveButton("Update") { _: DialogInterface, _: Int ->
            if (groceryName.text.isNotEmpty()) {
                grocery.name = groceryName.text.toString()
                dbHandler.updateGrocery(grocery)
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

    private fun refreshList(){
        rv_dashboard.adapter = DashboardAdapter(
            this,
            dbHandler.getGrocery()
        )
    }


    class DashboardAdapter(val activity: DashboardActivity, val list: MutableList<Grocery>) :
        RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(activity).inflate(
                    R.layout.rv_child_dashboard,
                    p0,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.groceryName.text = list[p1].name

            holder.groceryName.setOnClickListener {
                val intent = Intent(activity, ItemActivity::class.java)
                intent.putExtra(INTENT_GROCERY_ID,list[p1].id)
                intent.putExtra(INTENT_GROCERY_NAME,list[p1].name)
                activity.startActivity(intent)
            }

            holder.menu.setOnClickListener {
                val popup = PopupMenu(activity,holder.menu)
                popup.inflate(R.menu.dashboard_child)
                popup.setOnMenuItemClickListener {

                    when(it.itemId){
                        R.id.menu_edit ->{
                            activity.updateGrocery(list[p1])
                        }
                        R.id.menu_delete ->{
                            val dialog = AlertDialog.Builder(activity)
                            dialog.setTitle("Are you sure")
                            dialog.setMessage("Do you want to delete this grocery ?")
                            dialog.setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                                activity.dbHandler.deleteGrocery(list[p1].id)
                                activity.refreshList()
                            }
                            dialog.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

                            }
                            dialog.show()
                        }
                        R.id.menu_mark_as_completed ->{
                            activity.dbHandler.updateGroceryItemCompletedStatus(list[p1].id,true)
                        }
                    }

                    true
                }
                popup.show()
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val groceryName: TextView = v.findViewById(R.id.tv_grocery_name)
            val menu : ImageView = v.findViewById(R.id.iv_menu)
        }
    }
}
