package com.anajulia.mytasks.adapter

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.anajulia.mytasks.R
import com.anajulia.mytasks.databinding.TaskListItemBinding
import com.anajulia.mytasks.entity.Task
import com.anajulia.mytasks.listener.TaskItemClickListener
import com.anajulia.mytasks.utils.Utils
import java.time.LocalDate

class TaskViewHolder(
    private val context: Context,
    private val binding: TaskListItemBinding,
    private val listener: TaskItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun setValues(task: Task) {
        binding.tvTitle.text = task.title

        if (task.completed) {
            binding.colorView.setBackgroundResource(R.color.green_700)
        } else {
            binding.colorView.setBackgroundResource(R.color.purple_700)
        }

        task.date?.let { date ->
            if (date.isAfter(LocalDate.now())) {
                binding.colorView.setBackgroundResource(R.color.blue)
            } else if (date.isBefore(LocalDate.now().minusDays(1))) {
                binding.colorView.setBackgroundResource(R.color.red)
            } else if (date.isEqual(LocalDate.now())) {
                binding.colorView.setBackgroundResource(R.color.yellow)
            }
        } ?: run {
            binding.colorView.setBackgroundResource(R.color.blue)
        }

        binding.tvDate.text = task.date?.let {
            checkDateFormat(task.date!!)
        } ?: run {
            "-"
        }

        binding.tvTime.text = task.time?.let {
            task.time.toString()
        } ?: run {
            "-"
        }

        binding.root.setOnClickListener {
            listener.onClick(task)
        }

        binding.root.setOnCreateContextMenuListener { menu, _, _ ->
            menu.add(ContextCompat.getString(context, R.string.mark_as_completed)).setOnMenuItemClickListener {
                listener.onMarkAsCompleteClick(adapterPosition, task)
                true
            }
            menu.add(ContextCompat.getString(context, R.string.share)).setOnMenuItemClickListener {
                listener.onShareClick(task)
                true
            }
        }
    }

    fun checkDateFormat(date: LocalDate): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val selectedFormat = listOf(
            "date_format_standard",
            "date_format_long"
        ).find { key ->
            sharedPreferences.getBoolean(key, false)
        }

        return if (selectedFormat.equals("date_format_standard")) {
            Utils.formatLocalDateToStandard(date)
        } else {
            Utils.formatLocalDateToFull(date)
        }
    }
}