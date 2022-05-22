package com.dldmswo1209.diary

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dldmswo1209.diary.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    var mBinding: ActivityLoginBinding? = null
    val binding get() = mBinding!!
    lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.inputId.text.toString()
            val pw = binding.inputPw.text.toString()

            auth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener {
                    sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("user_uid", auth.currentUser!!.uid)
                    editor.commit()

                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnCanceledListener {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }
}