package com.dldmswo1209.diary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dldmswo1209.diary.databinding.ActivityWriteBinding
import java.time.LocalDate

class WriteActivity : AppCompatActivity() {
    var mBinding: ActivityWriteBinding? = null
    val binding get() = mBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addContent.setOnClickListener{
            val content = binding.diaryContent.text.toString()
            val date = LocalDate.now().toString()
            val newItem = Item(content, date)

            val sharedPreferences = getSharedPreferences(date, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            
            finish()
        }
    }
}