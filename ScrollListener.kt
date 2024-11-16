package com.mayad.instagram.android.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class ScrollListener(
    private val layoutManager: RecyclerView.LayoutManager,
    private val visibleThreshold: Int = 5 // The minimum amount of items to have below the current scroll position before loading more.
) : RecyclerView.OnScrollListener() {

    private var loading = true // True if we are still waiting for the last set of data to load.
    private var previousTotal = 0 // The total number of items in the data set after the last load.
    private var currentPage = 1

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = recyclerView.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItem = when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            is StaggeredGridLayoutManager -> {
                val firstVisibleItems = layoutManager.findFirstVisibleItemPositions(null)
                firstVisibleItems.minOrNull() ?: 0
            }

            else -> 0
        }

        if (loading && totalItemCount > previousTotal) {
            loading = false
            previousTotal = totalItemCount
        }

        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            currentPage++
            onLoadMore(currentPage)
            loading = true
        }
    }

    abstract fun onLoadMore(currentPage: Int)

    // Call this method to reset loading state externally.
    fun setLoading(loading: Boolean) {
        this.loading = loading
    }

    // Call this method to reset the pagination and data tracking.
    fun resetPagination() {
        previousTotal = 0
        currentPage = 1
        loading = true
    }
}