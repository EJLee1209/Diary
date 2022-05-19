package com.dldmswo1209.diary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.diary.databinding.ActivityMainBinding
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {
    var mBinding : ActivityMainBinding? = null
    val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addItem.setOnClickListener{
            startActivity(Intent(this, WriteActivity::class.java))
        }



    }
}

class Item(
    val content: String,
    val date: String
)

class itemAdapter(
    val itemList: MutableList<Item>,
    val inflater: LayoutInflater
): RecyclerView.Adapter<itemAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val date: TextView
        init{
            date = itemView.findViewById(R.id.diary_item_date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemAdapter.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.diary_item, parent, false))
    }

    override fun onBindViewHolder(holder: itemAdapter.ViewHolder, position: Int) {
        val curItem = itemList[position]
        holder.date.text = curItem.date
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}