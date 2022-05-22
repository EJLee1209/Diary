package com.dldmswo1209.diary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dldmswo1209.diary.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    var mBinding: ActivityRegisterBinding? = null
    val binding get() = mBinding!!
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            val email = binding.registerId.text.toString()
            val pw = binding.registerPw.text.toString()

            auth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        db.collection("users")
                            .document(auth.currentUser!!.uid)
                            .set(User(email, pw, auth.currentUser!!.uid))

                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnCanceledListener {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

class User(
    val email: String,
    val pw: String,
    val uid: String
)