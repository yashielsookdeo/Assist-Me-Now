package com.wordapp.test2.Donors

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wordapp.test2.R
import com.wordapp.test2.databinding.ActivityDonorBinding
import kotlinx.parcelize.Parcelize

class DonorActivity : AppCompatActivity() {


    private val categories: List<FoodCategory> = listOf(
        FoodCategory("Grains", listOf(DonateItem("Rice"), DonateItem("Wheat"))),
        FoodCategory("Canned Foods", listOf(DonateItem("Can Food 1"), DonateItem("Can Food 2"))),
        FoodCategory("Baking", listOf(DonateItem("Flour"), DonateItem("Sugar")))
    )

    private lateinit var binding: ActivityDonorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = CategoryAdapter(categories)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.buttonFinalizeDonation.setOnClickListener {
            val intent = Intent(this, FinalizeDonationActivity::class.java)
            startActivity(intent)
        }

    }

    @Parcelize
    data class DonateItem(val name: String) : Parcelable

    @Parcelize
    data class FoodCategory(val name: String, val items: List<DonateItem>) : Parcelable
    inner class CategoryAdapter(private val categories: List<FoodCategory>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            val categoryName: TextView = view.findViewById(R.id.category_name)

            init {
                view.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val category = categories[position]
                    val intent = Intent(this@DonorActivity, ItemsActivity::class.java).apply {
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
