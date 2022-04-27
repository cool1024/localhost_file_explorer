package com.example.explorer

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.androidx_example.fragments.album.PhotoPopupWindow
import com.example.explorer.databinding.ExplorerItemBinding

class ExplorerHolder(private val binding: ExplorerItemBinding, view: View) :
    RecyclerView.ViewHolder(view) {

    enum class FileType {
        DIR,
        LINK,
        IMAGE,
        VIDEO,
        PDF,
        DOCS,
        MUSIC,
        ZIP,
        TEXT,
        EXCEL,
        OTHER
    }

    data class FileItem(
        val fileName: String,
        val filePath: String,
        val fileType: String,
        val previewUrl: String,
        val downloadUrl: String,
        val parentDir: String,
        val previewSize: PreviewSize
    )

    data class PreviewSize(val width: Int, val height: Int)


    fun updateItem(item: FileItem, maxWidth: Int, items: List<FileItem>) {

        val fitSize = getFitSize(
            Size(item.previewSize.width, item.previewSize.height),
            Size(maxWidth, Int.MAX_VALUE)
        )

        itemView.apply {

            itemView.layoutParams.apply {
                width = fitSize.width
                height = fitSize.height
            }

            setOnClickListener {
                when (FileType.valueOf(item.fileType)) {
                    FileType.DIR -> {
                        it.findNavController()
                            .navigate(ExplorerFragmentDirections.actionExplorerFragmentSelf(item.filePath))
                    }
                    FileType.IMAGE -> {
                        val photos = items.filter { it.fileType.equals(FileType.IMAGE.name) }
                        val activeIndex = photos.indexOf(item)
                        PhotoPopupWindow.createAndShow(
                            activeIndex,
                            photos.map { it.previewUrl }.toTypedArray(),
                            itemView.context,
                            itemView as ViewGroup
                        )
                    }
                    FileType.VIDEO -> {
                        it.findNavController()
                            .navigate(
                                ExplorerFragmentDirections.actionExplorerFragmentToVideoActivity(
                                    item.downloadUrl
                                )
                            )
                    }
                    else -> {

                    }
                }
            }

            setOnLongClickListener {
                val clipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(
                    ClipData.newPlainText(
                        "DOWNLOAD_URL",
                        item.downloadUrl
                    )
                )
                Toast.makeText(context, item.downloadUrl, Toast.LENGTH_SHORT).show()
                true
            }
        }

        binding.fileName.text = item.fileName

        GlideApp.with(binding.fileImage)
            .load(item.previewUrl)
            .override(fitSize.width, fitSize.height)
            .into(binding.fileImage)
    }

    private fun getFitSize(originSize: Size, containerSize: Size): Size {
        val cwOW = 1f * containerSize.width / originSize.width
        val expW = containerSize.width
        val expH = (originSize.height * cwOW).toInt()
        return if (expH < containerSize.height)
            Size(expW, expH)
        else return Size(
            (containerSize.height * (1f * expW / expH)).toInt(),
            containerSize.height
        )
    }
}