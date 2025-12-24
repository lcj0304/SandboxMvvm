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
        """bind:onSmartLoadMoreCommand="@{ViewModel.onLoadMoreCommand}""""
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
fun listItemLayoutTemplate(modulePackageName:String, packageName: String, modelName: String, moreInfo: MoreInfo): String {
    var xmlChildren = ""
    var importR = ""
    if (moreInfo.isTaskList) {
        xmlChildren = taskListItemLayout
        importR = """<import type="${modulePackageName}.R" />"""
    }

    return """<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:bind="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable
        name="ViewModel"
        type="${packageName}.${modelName.getListItemViewModelName()}" />
        $importR
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        $xmlChildren
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


val taskListItemLayout = """
        <com.sandboxol.center.view.newtheme.widget.reward.CommonRewardView
        android:id="@+id/rewardView"
        android:layout_width="@dimen/dp_44"
        android:layout_height="@dimen/dp_44"
        android:layout_marginStart="@dimen/dp_6"
        bind:layout_constraintBottom_toBottomOf="parent"
        bind:layout_constraintStart_toStartOf="parent"
        bind:layout_constraintTop_toTopOf="parent"
        bind:onClickCommand="@{ViewModel.onItemClickCommand}"
        bind:quantityTextSizeRes="@{R.dimen.sp_11}"
        bind:reward="@{ViewModel.reward}"
        bind:rewardPaddingRes="@{R.dimen.dp_4}"
        bind:showQuantityWhen1="@{true}" />

    <TextView
        android:id="@+id/tvTaskDesc"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="@dimen/dp_6"
        android:layout_marginEnd="@dimen/dp_9"
        bind:autoSizeMaxTextSize="@dimen/sp_12"
        bind:autoSizeMinTextSize="@dimen/sp_9"
        bind:autoSizeTextType="uniform"
        android:gravity="start"
        android:text="@{ViewModel.taskText}"
        android:textColor="#334155"
        bind:layout_constraintBottom_toBottomOf="@+id/rewardView"
        bind:layout_constraintEnd_toStartOf="@+id/eventBtnView"
        bind:layout_constraintStart_toEndOf="@+id/rewardView"
        bind:layout_constraintTop_toTopOf="@+id/rewardView"
        tools:text="Complete 5 matches in any game"
        />

    <com.sandboxol.center.view.widget.progress.NinePatchProgressView
        android:id="@+id/progressView"
        android:layout_width="@dimen/dp_66"
        android:layout_height="@dimen/dp_12"
        android:layout_marginTop="@dimen/dp_2"
        bind:backgroundResId="@{ViewModel.progressBackgroundRedId}"
        bind:foregroundRedId="@{ViewModel.progressForegroundRedId}"
        bind:layout_constraintEnd_toEndOf="@+id/eventBtnView"
        bind:layout_constraintStart_toStartOf="@+id/eventBtnView"
        bind:layout_constraintTop_toTopOf="@+id/rewardView"
        bind:maxProgress="@{ViewModel.maxProgress}"
        bind:progress="@{ViewModel.progress}"
        bind:showProgressText="@{true}" />

    <com.sandboxol.center.view.widget.event.BaseEventButtonView
        android:id="@+id/eventBtnView"
        android:layout_width="@dimen/dp_72"
        android:layout_height="@dimen/dp_26"
        android:layout_marginEnd="@dimen/dp_7"
        bind:layout_constraintBottom_toBottomOf="@id/rewardView"
        bind:layout_constraintEnd_toEndOf="parent"
        bind:baseTask="@{ViewModel.item}"
        bind:onClickCommand="@{ViewModel.onButtonClickCommand}" />
        """.trimIndent()