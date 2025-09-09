# Rounded Progress Bar Template

This template generates a custom Android progress bar control with rounded corners that don't get clipped during progress changes.

## Generated Files

When using the "Sandbox Rounded Progress Bar" template, the following files are generated:

1. **Custom View Class** (`RoundedProgressView.kt`)
2. **XML Attributes** (`roundedprogressview_attrs.xml`)  
3. **Example Layout** (optional - `activity_rounded_progress_example.xml`)
4. **Example Activity** (optional - `RoundedProgressViewExampleActivity.kt`)

## Key Features

### Problem Solved
Standard Android progress bars clip rounded corners when the progress is low, creating an inconsistent appearance. This custom view maintains perfect rounded corners at any progress value.

### Technical Solution
- Uses custom `onDraw()` with `Path` objects for background and progress
- Implements `canvas.clipPath()` to prevent overflow
- Dynamically adjusts corner radius for small progress values
- Proper stroke and padding handling

### XML Attributes Support
```xml
app:progress="30"           <!-- Current progress (0-maxProgress) -->
app:maxProgress="100"       <!-- Maximum progress value -->
app:backgroundColor="#E0E0E0"  <!-- Background color -->
app:progressColor="#4CAF50"    <!-- Progress fill color -->
app:cornerRadius="10dp"        <!-- Corner radius -->
app:strokeWidth="2dp"          <!-- Border width -->
app:strokeColor="#FF0000"      <!-- Border color -->
```

### Programmatic API
```kotlin
progressView.setProgress(75f)
progressView.setProgressColor(Color.BLUE)
progressView.setCornerRadius(15f)
progressView.setStroke(2f, Color.RED)
```

## Usage Examples

### Basic Usage
```xml
<com.yourpackage.RoundedProgressView
    android:layout_width="match_parent"
    android:layout_height="8dp"
    app:cornerRadius="4dp"
    app:progress="45"
    app:backgroundColor="#E0E0E0"
    app:progressColor="#4CAF50" />
```

### With Stroke Border
```xml
<com.yourpackage.RoundedProgressView
    android:layout_width="match_parent"  
    android:layout_height="12dp"
    app:cornerRadius="6dp"
    app:progress="60"
    app:backgroundColor="#F5F5F5"
    app:progressColor="#2196F3"
    app:strokeWidth="1dp"
    app:strokeColor="#1976D2" />
```

### Animation Support
```kotlin
val animator = ValueAnimator.ofFloat(0f, 100f)
animator.duration = 2000
animator.addUpdateListener { animation ->
    val progress = animation.animatedValue as Float
    progressView.setProgress(progress)
}
animator.start()
```

## Template Configuration

When creating the template in Android Studio:

1. **Class Name**: Name for your custom view (e.g., `RoundedProgressView`)
2. **Package Name**: Target package for the generated class
3. **Module Package Name**: Package name for R class imports
4. **Description**: Documentation comment for the class
5. **Example Layout Name**: Name for the demo layout file
6. **Create Example**: Whether to generate example Activity and layout

The template integrates seamlessly with the existing SandboxMvvm plugin architecture.