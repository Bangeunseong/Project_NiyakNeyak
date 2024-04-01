package com.capstone.project_niyakneyak.main.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * This ItemDecorator is for decorating adapter vertical padding.
 * You need to enter Integer value when creating class.
 * Typed value will be set as padding size.
 */
class VerticalItemDecorator(private val divHeight: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = divHeight
    }
}