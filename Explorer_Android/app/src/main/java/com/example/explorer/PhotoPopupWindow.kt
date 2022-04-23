package com.example.androidx_example.fragments.album

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.viewpager.widget.PagerAdapter
import com.example.explorer.GlideApp
import com.example.explorer.databinding.WindowPhotoBinding
import com.github.chrisbanes.photoview.PhotoView


class PhotoPopupWindow(
    activeIndex: Int,
    photos: Array<String>,
    context: Context,
    rootView: ViewGroup
) :
    PopupWindow() {

    init {
        val binding = WindowPhotoBinding.inflate(LayoutInflater.from(context), rootView, false)
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        isFocusable = true
        contentView = binding.root
        binding.photoPager.apply {
            adapter = PhotoPagerAdapter(photos)
            currentItem = activeIndex
        }
        setBackgroundDrawable(ColorDrawable(Color.BLACK))
    }


    class PhotoPagerAdapter(private val items: Array<String>) : PagerAdapter() {

        override fun getCount(): Int {
            return items.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return PhotoView(container.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                GlideApp.with(context).load(items[position]).into(this)
                container.addView(this)
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as PhotoView)
        }
    }

    companion object {
        fun createAndShow(
            activeIndex: Int,
            photos: Array<String>,
            context: Context,
            rootView: ViewGroup
        ) {
            PhotoPopupWindow(activeIndex, photos, context, rootView)
                .showAtLocation(rootView, Gravity.CENTER, rootView.x.toInt(), rootView.y.toInt())
        }
    }
}