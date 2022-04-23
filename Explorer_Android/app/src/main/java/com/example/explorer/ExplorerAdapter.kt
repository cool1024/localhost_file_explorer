package com.example.explorer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.explorer.databinding.ExplorerItemBinding
import com.example.explorer.http.HttpRequest
import io.reactivex.android.schedulers.AndroidSchedulers

class ExplorerAdapter : RecyclerView.Adapter<ExplorerHolder>() {

    private var items: List<ExplorerHolder.FileItem> = emptyList()

    private var fitWith = 0

    val empty: Boolean
        get() = items.isEmpty()

    @SuppressLint("CheckResult")
    fun refreshData(path: String, callback: () -> Unit) {
        HttpRequest.instance().get("dir", mapOf("dir" to path))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { res ->
                items = res.apiData.getObjectList(ExplorerHolder.FileItem::class.java)
                this.notifyDataSetChanged()
                callback()
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExplorerHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ExplorerItemBinding.inflate(inflater, parent, false)
        fitWith = parent.width
        return ExplorerHolder(binding, binding.root)
    }

    override fun onBindViewHolder(holder: ExplorerHolder, position: Int) {
        holder.updateItem(items[position], fitWith / 2, items)
    }

    override fun getItemCount(): Int = items.size
}