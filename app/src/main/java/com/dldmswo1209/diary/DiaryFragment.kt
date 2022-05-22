package com.dldmswo1209.diary

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dldmswo1209.diary.databinding.FragmentDiaryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class DiaryFragment : Fragment() {
    var mBinding: FragmentDiaryBinding? = null
    val binding get() = mBinding!!
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentDiaryBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var date = ""
        auth=FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()

        binding.calendarView.setOnDateChangeListener { view, year, month, day ->
            // 날짜를 선택했을 때
            date = "${year}.${month+1}.${day}"
            binding.date.text = date

            getDiary(date)
        }

        binding.saveDiary.setOnClickListener {
            // 저장 버튼을 눌렀을 때
            val content = binding.diaryContent.text.toString()
            saveDiary(date, content)
            getDiary(date)
        }

        binding.deleteDiary.setOnClickListener {
            // 삭제 버튼을 눌렀을 때
            deleteDiary(date)
            getDiary(date)
        }

        binding.modifyDiary.setOnClickListener {
            // 수정 버튼을 눌렀을 때
            binding.completeModify.visibility = View.VISIBLE
            binding.diaryContent.visibility = View.VISIBLE
            binding.currentContent.visibility = View.INVISIBLE
            binding.modifyDiary.visibility = View.INVISIBLE
            binding.deleteDiary.visibility = View.INVISIBLE
        }

        binding.completeModify.setOnClickListener {
            // 수정 완료 버튼을 눌렀을 때
            binding.completeModify.visibility = View.INVISIBLE
            val content = binding.diaryContent.text.toString()
            saveDiary(date, content)
            getDiary(date)
        }
    }
    fun saveDiary(date: String, content: String){
        val diary = Diary(content, date)
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("diary")
            .document(date)
            .set(diary)

        Toast.makeText(context, "일기를 작성했습니다." ,Toast.LENGTH_SHORT).show()
    }
    fun deleteDiary(date: String){
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("diary")
            .document(date)
            .delete()

        Toast.makeText(context, "일기를 삭제했습니다.." ,Toast.LENGTH_SHORT).show()
    }

    fun getDiary(date: String){
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("diary")
            .document(date)
            .get()
            .addOnSuccessListener {
                if(it.exists()) { // 일기가 존재하면
                    binding.diaryContent.setText(it["content"].toString())
                    binding.currentContent.text = it["content"].toString()
                    binding.diaryContent.visibility = View.INVISIBLE // EditText를 가리고
                    binding.saveDiary.visibility = View.INVISIBLE // 저장 버튼 가리고
                    binding.currentContent.visibility = View.VISIBLE // TextView를 보이게
                    binding.modifyDiary.visibility = View.VISIBLE // 수정 버튼 보이게
                    binding.deleteDiary.visibility = View.VISIBLE // 삭제 버튼 보이게
                }
                else { // 일기가 존재하지 않으면
                    binding.diaryContent.setText("")
                    binding.diaryContent.visibility = View.VISIBLE
                    binding.saveDiary.visibility = View.VISIBLE
                    binding.currentContent.visibility = View.INVISIBLE
                    binding.modifyDiary.visibility = View.INVISIBLE
                }
            }
            .addOnFailureListener {
                Log.d("testt", "no data")
            }

    }

}

class Diary(
    val content: String,
    val date: String
)