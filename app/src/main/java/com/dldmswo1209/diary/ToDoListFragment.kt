package com.dldmswo1209.diary

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.diary.databinding.FragmentDiaryBinding
import com.dldmswo1209.diary.databinding.FragmentToDoListBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.util.*

lateinit var auth: FirebaseAuth
lateinit var db: FirebaseFirestore
var date = ""
class ToDoListFragment : Fragment() {
    var mBinding: FragmentToDoListBinding? = null
    val binding get() = mBinding!!

    val todoList = mutableListOf<Todo>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentToDoListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        date = LocalDate.now().toString()
        binding.date.text = date

        binding.addTodo.setOnClickListener {
            // 추가 버튼을 눌렀을 때
            // 할일 추가 다이얼로그
            var builder = AlertDialog.Builder(context as MainActivity)
            builder.setTitle("할일 추가")
            builder.setIcon(R.drawable.to_do)

            var v1 = layoutInflater.inflate(R.layout.dialog, null)
            builder.setView(v1)

            var listener = DialogInterface.OnClickListener { dialogInterface, i ->
                var alert = dialogInterface as AlertDialog
                var todoContent = alert.findViewById<EditText>(R.id.todo_content)
                val todo = Todo(todoContent.text.toString())
                // db에 추가
                db.collection("todo")
                    .document(auth.currentUser!!.uid)
                    .collection(date)
                    .document(Timestamp.now().toString())
                    .set(todo)

                getTodoList()
            }
            builder.setPositiveButton("추가", listener)
            builder.setNegativeButton("취소", null)

            builder.show()

        }
        binding.date.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            val listener = DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                // 날짜 선택시
                if(m < 10){
                    date = "${y}-0${m+1}-${d}"
                }
                else{
                    date = "${y}-${m+1}-${d}"
                }
                binding.date.text = date // 날짜 변경
                getTodoList()
            }
            var picker = DatePickerDialog(context as MainActivity, listener, year, month, day)
            picker.show()

        }

        binding.deleteTodo.setOnClickListener {
            // 삭제 버튼을 눌렀을 때
        }
        getTodoList()
    }
    fun getTodoList(){
        todoList.clear()
        db.collection("todo")
            .document(auth.currentUser!!.uid)
            .collection(date)
            .get()
            .addOnSuccessListener {
                it.forEach {
                    val todo = Todo(it["content"].toString())
                    todoList.add(todo)
                }
                // 리사이클러 뷰의 어답터 적용
                val adapter = TodoListAdapter(todoList, LayoutInflater.from(context as MainActivity))
                binding.todoRecyclerview.adapter = adapter
                val itemTouchHelperCallback = ItemTouchHelperCallback(adapter)
                val helper = ItemTouchHelper(itemTouchHelperCallback)
                helper.attachToRecyclerView(binding.todoRecyclerview)

            }
            .addOnFailureListener {

            }
    }
}

// RecyclerView 의 Adapter 와 ItemTouchHelper.Callback 을 연결시켜 주는 리스너
interface ItemTouchHelperListener{
    fun onItemMove(from_position: Int, to_position: Int): Boolean
    fun onItemSwipe(position: Int)
}

class ItemTouchHelperCallback(
    val listener: ItemTouchHelperListener
) : ItemTouchHelper.Callback(){ // ItemTouchHelper.Callback 클래스를 상속

    // 활성화된 이동 방향을 정의하는 플래그를 반환하는 메소드
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // 드래그 방향
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        // 스와이프 방향
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        // 이동을 만드는 메소드
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    // 드래그된 item을 이전 위치에서 새로운 위치로 옮길 때 호출
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // 리스너의 onItemMove 메소드 호출
        return listener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
    }


    // 사용자에 의해 swipe 될 때 호출
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 리스너의 onItemSwipe 메소드 호출
        listener.onItemSwipe(viewHolder.adapterPosition)
    }
}

class Todo(
    var content: String
)

class TodoListAdapter(
    val todoList: MutableList<Todo>,
    val inflater: LayoutInflater
): RecyclerView.Adapter<TodoListAdapter.ViewHolder>(), ItemTouchHelperListener {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val todoListContent : TextView
        val todoCheck : ImageView

        init{
            todoListContent = itemView.findViewById<TextView>(R.id.todo_list_content)
            todoCheck = itemView.findViewById<ImageView>(R.id.todo_check)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListAdapter.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.todo_item, parent, false))
    }

    override fun onBindViewHolder(holder: TodoListAdapter.ViewHolder, position: Int) {
        val curTodo = todoList[position]
        holder.todoListContent.text = curTodo.content
        holder.todoCheck.setImageResource(R.drawable.check_mark)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    // 아이템을 드래그하면 호출되는 메소드
    override fun onItemMove(from_position: Int, to_position: Int): Boolean {
        val todo = todoList[from_position]
        // 리스트 갱신
        todoList.removeAt(from_position)
        todoList.add(to_position, todo)

        // fromPosition 에서 toPosition 으로 아이템이 이동했음을 공지
        notifyItemMoved(from_position, to_position)
        return true
    }

    // 아이템이 스와이프 되면 호출되는 메소드
    override fun onItemSwipe(position: Int) {
        val todo = todoList[position]
        // 리스트 아이템 삭제
        todoList.removeAt(position)
        // 아이템 삭제되었다고 공지

        notifyItemRemoved(position)
    }



}
