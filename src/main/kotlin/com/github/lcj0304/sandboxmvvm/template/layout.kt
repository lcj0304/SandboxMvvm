package com.github.lcj0304.sandboxmvvm.template


/**
 * @description ：
 * @author :liucj
 * @date : 2023/4/28 10:58
 */
fun layoutTemplate(
    packageName: String,
    modelName: String,
): String {

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
        
    </androidx.constraintlayout.widget.ConstraintLayout>

</layout>    
""".trimIndent()
}


