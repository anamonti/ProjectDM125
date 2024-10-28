package com.anajulia.mytasks.listener

import com.anajulia.mytasks.entity.Task

interface TaskItemClickListener {

    fun onClick(task: Task)

    fun onMarkAsCompleteClick(position: Int, task: Task)

    fun onShareClick(task: Task)
}