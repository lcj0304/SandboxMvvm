package com.github.lcj0304.sandboxmvvm.template


/**
 * @description ：
 * @author :liucj
 * @date : 2023/4/28 10:58
 */
fun layoutTemplate(
    packageName: String,
    modelName: String,
    isListLayout:Boolean = false
): String {
    val listLayout = if (isListLayout) listDataLayout else ""

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
fun listLayoutTemplate():String {
    return """<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:bind="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
            name="ViewModel"
            type="com.sandboxol.common.widget.rv.datarv.DataListViewModel" />
    </data>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.sandboxol.common.widget.PageLoadingView
            android:id="@+id/pageLoadingView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            />

        <com.scwang.smartrefresh.layout.SmartRefreshLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            bind:onSmartRefreshCommand="@{ViewModel.onRefreshCommand}">

            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/recyclerView"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                bind:itemBinding="@{ViewModel.itemBinding}"
                bind:items="@{ViewModel.itemViewModelList}"
                android:clipToPadding="false"
                tools:itemCount="20"
                />
        </com.scwang.smartrefresh.layout.SmartRefreshLayout>
    </FrameLayout>
</layout>"""
}



/**
 * list layout
 * @return String
 */
fun listItemLayoutTemplate(packageName:String, modelName:String):String {
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

