package com.github.lcj0304.sandboxmvvm.template


/**
 * @description ：
 * @author :liucj
 * @date : 2023/4/28 10:58
 */
fun layoutTemplate(
    packageName: String,
    modelName: String,
    isListLayout: Boolean = false,
    isDiff: Boolean = false,
    isPageList: Boolean = false,
): String {
    val listLayout = if (isListLayout) getListLayout(isDiff, isPageList) else ""

    return """
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:bind="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>

        <variable
            name="ViewModel"
            type="${packageName}.${modelName}VM" />
    </data>


    <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        $listLayout
    </androidx.constraintlayout.widget.ConstraintLayout>

</layout>    
""".trimIndent()
}

/**
 * list layout
 * @return String
 */
fun listLayoutTemplate(
    isDiff: Boolean = false,
    isPageList: Boolean = false,
): String {
    val loadMore = if (isPageList) {
        """bind:onLoadMoreCommand="@{ViewModel.onLoadMoreCommand}""""
    } else {
        ""
    }
    return """<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:bind="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="ViewModel"
            type="${getListViewModel(isDiff, isPageList)}" />
    </data>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.sandboxol.common.widget.PageLoadingView
            android:id="@+id/pageLoadingView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            />

        <com.scwang.smart.refresh.layout.SmartRefreshLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            bind:onSmartRefreshCommand="@{ViewModel.onRefreshCommand}"
            $loadMore
            >

            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/recyclerView"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                bind:itemBinding="@{ViewModel.itemBinding}"
                bind:items="@{ViewModel.itemViewModelList}"
                android:clipToPadding="false"
                tools:itemCount="20"
                />
        </com.scwang.smart.refresh.layout.SmartRefreshLayout>
    </FrameLayout>
</layout>"""
}


/**
 * list layout
 * @return String
 */
fun listItemLayoutTemplate(packageName: String, modelName: String): String {
    return """<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:bind="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools">

<data>

<variable
name="ViewModel"
type="${packageName}.${modelName.getListItemViewModelName()}" />
</data>

<androidx.constraintlayout.widget.ConstraintLayout
android:layout_width="match_parent"
android:layout_height="match_parent">

</androidx.constraintlayout.widget.ConstraintLayout>
</layout>"""
}

fun getListViewModel(isDiff: Boolean, isPage: Boolean): String {
    return if (isDiff) {
        if (isPage) {
            return "com.sandboxol.common.widget.rv.pagerv.DiffPageListViewModel"
        } else {
            return "com.sandboxol.common.widget.rv.datarv.DiffDataListViewModel"
        }
    } else {
        if (isPage) {
            return "com.sandboxol.common.widget.rv.pagerv.PageListViewModel"
        } else {
            return "com.sandboxol.common.widget.rv.datarv.DataListViewModel"
        }
    }
}


fun getListLayout(isDiff: Boolean, isPage: Boolean): String {
    return if (isDiff) {
        if (isPage) {
            diffPageDataLayout
        } else {
            diffListDataLayout
        }
    } else {
        if (isPage) {
            pageDataLayout
        } else {
            listDataLayout
        }
    }
}


val listDataLayout = """
<com.sandboxol.common.widget.rv.datarv.DataRecyclerView
    android:layout_width="0dp"
    android:layout_height="0dp"
    bind:layout_constraintTop_toTopOf="parent"
    bind:layout_constraintBottom_toBottomOf="parent"
    bind:layout_constraintEnd_toEndOf="parent"
    bind:layout_constraintStart_toStartOf="parent"
    bind:listLayout="@{ViewModel.listLayout}"
    bind:model="@{ViewModel.listModel}"
/>
""".trimIndent()


val diffListDataLayout = """
<com.sandboxol.common.widget.rv.datarv.DiffDataRecyclerView
    android:layout_width="0dp"
    android:layout_height="0dp"
    bind:layout_constraintTop_toTopOf="parent"
    bind:layout_constraintBottom_toBottomOf="parent"
    bind:layout_constraintEnd_toEndOf="parent"
    bind:layout_constraintStart_toStartOf="parent"
    bind:diffItemCallback="@{ViewModel.diffItemCallback}"
    bind:listLayout="@{ViewModel.listLayout}"
    bind:model="@{ViewModel.listModel}"
/>
""".trimIndent()


val pageDataLayout = """
<com.sandboxol.common.widget.rv.pagerv.PageRecyclerView
    android:layout_width="0dp"
    android:layout_height="0dp"
    bind:layout_constraintTop_toTopOf="parent"
    bind:layout_constraintBottom_toBottomOf="parent"
    bind:layout_constraintEnd_toEndOf="parent"
    bind:layout_constraintStart_toStartOf="parent"
    bind:listLayout="@{ViewModel.listLayout}"
    bind:model="@{ViewModel.listModel}"
/>
""".trimIndent()

val diffPageDataLayout = """
<com.sandboxol.common.widget.rv.pagerv.DiffPageRecyclerView
    android:layout_width="0dp"
    android:layout_height="0dp"
    bind:layout_constraintTop_toTopOf="parent"
    bind:layout_constraintBottom_toBottomOf="parent"
    bind:layout_constraintEnd_toEndOf="parent"
    bind:layout_constraintStart_toStartOf="parent"
    bind:diffItemCallback="@{ViewModel.diffItemCallback}"
    bind:listLayout="@{ViewModel.listLayout}"
    bind:model="@{ViewModel.listModel}"
/>
""".trimIndent()

