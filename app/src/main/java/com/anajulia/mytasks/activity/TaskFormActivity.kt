package com.anajulia.mytasks.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.anajulia.mytasks.databinding.ActivityTaskFormBinding
import com.anajulia.mytasks.entity.Task
import com.anajulia.mytasks.service.TaskService
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TaskFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskFormBinding

    private val taskService: TaskService by viewModels()

    private var taskId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("lifecycle", "TaskForm onCreate")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.extras?.getString(Intent.EXTRA_TEXT)?.let { text ->
            binding.etTitle.setText(text)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        initComponents()
        setValues()
    }

    private fun initComponents() {
        binding.btSave.setOnClickListener {
            if (validateTaskFields()) {
                val task = Task(
                    id = taskId,
                    title = binding.etTitle.text.toString(),
                    description = binding.etDescription.text.toString(),
                    date = convertToLocalDate(binding.etDate.text.toString()),
                    time = convertToLocalTime(binding.etTime.text.toString()))

                taskService.save(task).observe(this) { responseDto ->
                    if (responseDto.isError) {
                        Toast.makeText(this, "Erro com o servidor", Toast.LENGTH_SHORT).show()
                    } else {
                        finish()
                    }
                }
            }
        }
    }

    private fun validateTaskFields(): Boolean {
        if (binding.etTitle.text.toString().isEmpty()) {
            binding.etTitle.error = "This field is required"
            return false
        }

        val datePattern = Regex("\\d{2}/\\d{2}/\\d{4}")
        if (!datePattern.matches(binding.etDate.text.toString())) {
            binding.etDate.error = "Date format should be dd/MM/yyyy"
            return false
        }

        val timePattern = Regex("\\d{2}:\\d{2}")
        if (!timePattern.matches(binding.etTime.text.toString())) {
            binding.etTime.error = "Time format should be HH:mm"
            return false
        }

        return true
    }

    fun convertToLocalDate(dateString: String): LocalDate? {
        return try {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            LocalDate.parse(dateString, dateFormatter)
        } catch (e: DateTimeParseException) {
            println("Invalid date format. Expected format: dd/MM/yyyy")
            null
        }
    }

    fun convertToLocalTime(timeString: String): LocalTime? {
        return try {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            LocalTime.parse(timeString, timeFormatter)
        } catch (e: DateTimeParseException) {
            println("Invalid time format. Expected format: HH:mm")
            null
        }
    }

    fun formatLocalDateToString(date: LocalDate?): String? {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date?.format(dateFormatter)
    }

    @Suppress("deprecation")
    private fun setValues() {
        (intent.extras?.getSerializable("task") as Task?)?.let { task ->
            taskId = task.id
            binding.etTitle.setText(task.title)
            binding.etDescription.setText(task.description)
            binding.etDate.setText(formatLocalDateToString(task.date))
            binding.etTime.setText(task.time.toString())

            if (task.completed) {
                binding.btSave.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Log.e("lifecycle", "TaskForm onStart")
    }

    override fun onResume() {
        super.onResume()

        Log.e("lifecycle", "TaskForm onResume")
    }

    override fun onStop() {
        super.onStop()

        Log.e("lifecycle", "TaskForm onStop")
    }

    override fun onPause() {
        super.onPause()

        Log.e("lifecycle", "TaskForm onPause")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.e("lifecycle", "TaskForm onDestroy")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}