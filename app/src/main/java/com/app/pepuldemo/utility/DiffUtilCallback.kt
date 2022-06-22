package com.app.pepuldemo.utility

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.app.pepuldemo.model.ApiResponse
import com.app.pepuldemo.model.Data

class DiffUtilCallback(private val oldDataList: ArrayList<Data>, val newDataList: ArrayList<Data>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldDataList.size
    }

    override fun getNewListSize(): Int {
        return newDataList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldDataList[oldItemPosition].id === newDataList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee: Data = oldDataList[oldItemPosition]
        val newEmployee: Data = newDataList[newItemPosition]
        return oldEmployee.id.equals(newEmployee.id)
    }

}