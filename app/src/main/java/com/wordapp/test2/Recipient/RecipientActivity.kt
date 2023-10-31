package com.wordapp.test2

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.parcelize.Parcelize

class RecipientActivity : AppCompatActivity() {

    private val categories: List<RequestCategory> = listOf(
        RequestCategory("Grains", listOf(RequestItem("Rice"), RequestItem("Wheat"))),
        RequestCategory("Canned Foods", listOf(RequestItem("Can Food 1"), RequestItem("Can Food 2"))),
        RequestCategory("Baking", listOf(RequestItem("Flour"), RequestItem("Sugar")))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient)

        val finalizeRequestButton: Button = findViewById(R.id.finalizeRequestButton)
        finalizeRequestButton.setOnClickListener {
            // Navigate to FinalizeRequestsActivity to finalize the request
            startActivity(Intent(this, FinalizeRequestsActivity::class.java))
        }

        val adapter = CategoryAdapter(categories)
        finalizeRequestButton.setOnClickListener {
            startActivity(Intent(this, FinalizeRequestsActivity::class.java))
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    @Parcelize
    data class RequestItem(
        var name: String = "",  // default value
        var requested: Boolean = false,  // default value
        var firebaseKey: String = ""  // unique identifier
    ) : Parcelable



    @Parcelize
    data class RequestCategory(val name: String, val items: List<RequestItem>) : Parcelable

    inner class CategoryAdapter(private val categories: List<RequestCategory>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            val categoryName: TextView = view.findViewById(R.id.category_name)

            init {
                view.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val category = categories[position]
                    val intent = Intent(this@RecipientActivity, RecipientItemsActivity::class.java).apply {
                        putParcelableArrayListExtra("items", ArrayList(category.items))
                    }
                    startActivity(intent)
                }

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val category = categories[position]
            holder.categoryName.text = category.name
        }

        override fun getItemCount() = categories.size
    }
}
