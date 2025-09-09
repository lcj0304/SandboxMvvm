package com.github.lcj0304.sandboxmvvm.template

/**
 * @description ：Custom progress bar template for rounded corner progress view
 * @author :
 * @date : 2023/11/20 15:30
 */

/**
 * Template for custom rounded progress bar view
 */
fun roundedProgressViewTemplate(
    packageName: String,
    className: String,
    desc: String = "Custom progress bar with rounded corners that don't get clipped"
): String {
    return """
package $packageName

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

${getFileComments(desc)}
class $className @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Progress properties
    private var progress: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 100f)
            invalidate()
        }

    private var maxProgress: Float = 100f
    
    // Appearance properties
    private var backgroundColor: Int = Color.LTGRAY
    private var progressColor: Int = Color.BLUE
    private var cornerRadius: Float = 0f
    private var strokeWidth: Float = 0f
    private var strokeColor: Int = Color.TRANSPARENT

    // Drawing objects
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    
    private val backgroundRect = RectF()
    private val progressRect = RectF()
    private val progressPath = Path()
    private val backgroundPath = Path()

    init {
        // Initialize paints
        backgroundPaint.style = Paint.Style.FILL
        progressPaint.style = Paint.Style.FILL
        
        // Parse attributes if provided
        attrs?.let { parseAttributes(context, it) }
        
        updatePaints()
    }

    private fun parseAttributes(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.$className)
        try {
            progress = typedArray.getFloat(R.styleable.${className}_progress, 0f)
            maxProgress = typedArray.getFloat(R.styleable.${className}_maxProgress, 100f)
            backgroundColor = typedArray.getColor(R.styleable.${className}_backgroundColor, Color.LTGRAY)
            progressColor = typedArray.getColor(R.styleable.${className}_progressColor, Color.BLUE)
            cornerRadius = typedArray.getDimension(R.styleable.${className}_cornerRadius, 0f)
            strokeWidth = typedArray.getDimension(R.styleable.${className}_strokeWidth, 0f)
            strokeColor = typedArray.getColor(R.styleable.${className}_strokeColor, Color.TRANSPARENT)
        } finally {
            typedArray.recycle()
        }
    }

    private fun updatePaints() {
        backgroundPaint.color = backgroundColor
        progressPaint.color = progressColor
        strokePaint.color = strokeColor
        strokePaint.strokeWidth = strokeWidth
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        val padding = strokeWidth / 2f
        backgroundRect.set(
            padding,
            padding,
            w.toFloat() - padding,
            h.toFloat() - padding
        )
        
        updateProgressRect()
    }

    private fun updateProgressRect() {
        val progressWidth = (backgroundRect.width() - strokeWidth) * (progress / maxProgress)
        val padding = strokeWidth / 2f
        
        progressRect.set(
            padding,
            padding,
            padding + progressWidth,
            backgroundRect.bottom - padding
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        updateProgressRect()
        
        // Create rounded rectangle paths
        backgroundPath.reset()
        backgroundPath.addRoundRect(backgroundRect, cornerRadius, cornerRadius, Path.Direction.CW)
        
        // Draw background
        canvas.drawPath(backgroundPath, backgroundPaint)
        
        // Draw progress only if there's progress and width > 0
        if (progress > 0 && progressRect.width() > 0) {
            progressPath.reset()
            
            // For progress less than corner radius width, we need special handling
            val adjustedCornerRadius = if (progressRect.width() < cornerRadius * 2) {
                progressRect.width() / 2f
            } else {
                cornerRadius
            }
            
            // Create progress path with properly rounded corners
            val progressRadii = floatArrayOf(
                adjustedCornerRadius, adjustedCornerRadius, // top-left
                min(adjustedCornerRadius, progressRect.width() / 2f), min(adjustedCornerRadius, progressRect.width() / 2f), // top-right
                min(adjustedCornerRadius, progressRect.width() / 2f), min(adjustedCornerRadius, progressRect.width() / 2f), // bottom-right
                adjustedCornerRadius, adjustedCornerRadius  // bottom-left
            )
            
            progressPath.addRoundRect(progressRect, progressRadii, Path.Direction.CW)
            
            // Clip the progress path to background bounds to ensure it doesn't overflow
            canvas.save()
            canvas.clipPath(backgroundPath)
            canvas.drawPath(progressPath, progressPaint)
            canvas.restore()
        }
        
        // Draw stroke if enabled
        if (strokeWidth > 0 && strokeColor != Color.TRANSPARENT) {
            canvas.drawPath(backgroundPath, strokePaint)
        }
    }

    // Public API methods
    fun setProgress(progress: Float) {
        this.progress = progress
    }

    fun getProgress(): Float = progress

    fun setMaxProgress(maxProgress: Float) {
        this.maxProgress = maxProgress.coerceAtLeast(1f)
        invalidate()
    }

    fun getMaxProgress(): Float = maxProgress

    fun setProgressColor(color: Int) {
        progressColor = color
        updatePaints()
        invalidate()
    }

    fun setBackgroundColor(color: Int) {
        backgroundColor = color
        updatePaints()
        invalidate()
    }

    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        invalidate()
    }

    fun setStroke(width: Float, color: Int) {
        strokeWidth = width
        strokeColor = color
        updatePaints()
        requestLayout() // Stroke affects the layout
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultWidth = (200 * resources.displayMetrics.density).toInt()
        val defaultHeight = (20 * resources.displayMetrics.density).toInt()
        
        val width = resolveSize(defaultWidth, widthMeasureSpec)
        val height = resolveSize(defaultHeight, heightMeasureSpec)
        
        setMeasuredDimension(width, height)
    }
}
""".trimIndent()
}

/**
 * Template for XML attributes definition
 */
fun progressBarAttrsTemplate(className: String): String {
    return """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="$className">
        <attr name="progress" format="float" />
        <attr name="maxProgress" format="float" />
        <attr name="backgroundColor" format="color" />
        <attr name="progressColor" format="color" />
        <attr name="cornerRadius" format="dimension" />
        <attr name="strokeWidth" format="dimension" />
        <attr name="strokeColor" format="color" />
    </declare-styleable>
</resources>
""".trimIndent()
}

/**
 * Template for example layout showing usage
 */
fun progressBarExampleLayoutTemplate(
    packageName: String,
    className: String
): String {
    return """
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="$className Example"
        android:textSize="18sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <$packageName.$className
        android:id="@+id/progressBar1"
        android:layout_width="0dp"
        android:layout_height="20dp"
        android:layout_marginTop="24dp"
        app:backgroundColor="#E0E0E0"
        app:cornerRadius="10dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvTitle"
        app:maxProgress="100"
        app:progress="30"
        app:progressColor="#4CAF50" />

    <TextView
        android:id="@+id/tvProgress1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="Basic Progress: 30%"
        app:layout_constraintStart_toStartOf="@id/progressBar1"
        app:layout_constraintTop_toBottomOf="@id/progressBar1" />

    <$packageName.$className
        android:id="@+id/progressBar2"
        android:layout_width="0dp"
        android:layout_height="30dp"
        android:layout_marginTop="24dp"
        app:backgroundColor="#FFE0E0"
        app:cornerRadius="15dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvProgress1"
        app:maxProgress="100"
        app:progress="75"
        app:progressColor="#FF5722"
        app:strokeColor="#FF0000"
        app:strokeWidth="2dp" />

    <TextView
        android:id="@+id/tvProgress2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="With Stroke: 75%"
        app:layout_constraintStart_toStartOf="@id/progressBar2"
        app:layout_constraintTop_toBottomOf="@id/progressBar2" />

    <$packageName.$className
        android:id="@+id/progressBar3"
        android:layout_width="0dp"
        android:layout_height="40dp"
        android:layout_marginTop="24dp"
        app:backgroundColor="#E3F2FD"
        app:cornerRadius="20dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvProgress2"
        app:maxProgress="100"
        app:progress="10"
        app:progressColor="#2196F3" />

    <TextView
        android:id="@+id/tvProgress3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="Low Progress (10%) - Rounded corners preserved"
        app:layout_constraintStart_toStartOf="@id/progressBar3"
        app:layout_constraintTop_toBottomOf="@id/progressBar3" />

    <Button
        android:id="@+id/btnAnimate"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:text="Animate Progress"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvProgress3" />

</androidx.constraintlayout.widget.ConstraintLayout>
""".trimIndent()
}

/**
 * Template for example activity showing how to use the progress bar
 */
fun progressBarExampleActivityTemplate(
    packageName: String,
    className: String,
    layoutName: String,
    modulePackageName: String
): String {
    // Convert layout name to binding class name (e.g., activity_example -> ActivityExampleBinding)
    val bindingClassName = layoutName.split("_").joinToString("") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    } + "Binding"
    
    return """
package $packageName

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ${modulePackageName}.R
import ${modulePackageName}.databinding.$bindingClassName

${getFileComments("Example activity demonstrating $className usage")}
class ${className}ExampleActivity : AppCompatActivity() {
    
    private lateinit var binding: $bindingClassName
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = $bindingClassName.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupProgressBars()
        setupAnimationButton()
    }
    
    private fun setupProgressBars() {
        // Set initial progress values
        binding.progressBar1.setProgress(30f)
        binding.progressBar2.setProgress(75f)
        binding.progressBar3.setProgress(10f)
    }
    
    private fun setupAnimationButton() {
        binding.btnAnimate.setOnClickListener {
            animateProgressBars()
        }
    }
    
    private fun animateProgressBars() {
        // Animate progress bar 1
        animateProgress(binding.progressBar1, 0f, 100f, 2000)
        
        // Animate progress bar 2 with delay
        binding.progressBar2.postDelayed({
            animateProgress(binding.progressBar2, 0f, 100f, 1500)
        }, 500)
        
        // Animate progress bar 3 with delay
        binding.progressBar3.postDelayed({
            animateProgress(binding.progressBar3, 0f, 100f, 1000)
        }, 1000)
    }
    
    private fun animateProgress(progressBar: $className, from: Float, to: Float, duration: Long) {
        val animator = ValueAnimator.ofFloat(from, to)
        animator.duration = duration
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            progressBar.setProgress(progress)
        }
        animator.start()
    }
}
""".trimIndent()
}