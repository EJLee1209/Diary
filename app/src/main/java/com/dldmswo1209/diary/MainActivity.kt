package com.dldmswo1209.diary

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dldmswo1209.diary.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable
import java.time.LocalDate
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {
    var mBinding : ActivityMainBinding? = null
    val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MainPagerAdapter(this, 2)
        binding.mainPager.adapter = adapter


    }


}

class MainPagerAdapter(
    fragment: FragmentActivity,
    val tabCount: Int
): FragmentStateAdapter(fragment){
    override fun getItemCount(): Int {
        return tabCount
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                DiaryFragment()
            }
            else->{
                ToDoListFragment()
            }
        }
    }

}