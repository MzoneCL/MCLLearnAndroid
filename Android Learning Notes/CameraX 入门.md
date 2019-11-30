# CameraX 入门

## 目录

### 1. 总览

### 2. 创建一个新项目

### 3. 添加依赖

### 4. 创建取景器布局

### 5. 获取相机权限

### 6. 实现取景器

### 7. 实现图像捕获

### 8. 实现图像分析

### 9. 测试 APP

### 10. 总结



## 正文

### 1. 总览

在这个教程中，我们将学习如何使用 CameraX 去创建一个 APP，去显示取景器、拍照以及从相机分析图像流。

为了实现这个目标，我们将介绍 CameraX 当中“用例（use cases）”的概念，它可以被用以实现各种各样的相机操作，例如从显示一个取景器到实时分析图像帧。

#### 我们将学到什么？

* 如何添加 CameraX 依赖
* 如何在 Activity 当中展示相机预览（Preview use case）
* 如何拍照并保存（ImageCapture use case）
* 如何实时地从相机分析图像帧（ImageAnalysis use case）

#### 需要的硬件设备

* 一台 Android 设备，尽管 Android 模拟器也可以正常运行（但还是建议使用物理设备进行测试）。支持的最低 API 级别为 21。

#### 需要的软件

* Android Studio 3.3 或更高版本。



### 2. 创建一个新项目

使用 Android Studio 创建一个新项目，并在出现提示时选择 Empty Activity。

![CameraX-1](D:\GithubRepos\MCLLearnAndroid\Android Learning Notes\images\CameraX-1.png)



接下来，我们给项目随便取一个名字，我这里就叫“CameraXDemo”了。

这里我选择的语言是 Kotlin （为了一致，我推荐你也这么选择），最小的 API level 选择 21（这是 CameraX 所需的最低版本），并且我们勾选“Use AndroidX aircrafts”。

![CameraX-2](D:\GithubRepos\MCLLearnAndroid\Android Learning Notes\images\CameraX-2.png)



### 3. 添加依赖

首先，让我们将 CameraX 的依赖添加到 app 的 Grade 文件中：

![CameraX-3](D:\GithubRepos\MCLLearnAndroid\Android Learning Notes\images\CameraX-3.png)



CameraX 需要使用 Java 8 中的某些方法，所以我们需要相应的设置编译选项。还是在当前 gradle 文件中，在 android 块末尾，即 buildTypes 下面，添加如下代码：

``

```groovy
compileOptions{
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```



最后，点击 Sync Now 进行同步，我们将准备在应用中使用 CameraX。



### 4. 创建取景器布局

我们将使用 SurfaceTexture 显示相机取景器。 在此教程中，我们将以固定大小的正方形显示取景器。 有关显示响应式取景器的更全面的示例，请查看官方示例（<https://github.com/android/camera-samples/tree/master/CameraXBasic>）。

接下来，我们编辑 activity_main.xml 文件：

``

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextureView
        android:id="@+id/view_finder"
        android:layout_width="640px"
        android:layout_height="640px"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```



### 5. 获取相机权限

在我们的这个项目中，如果想要使用任何相机的功能，就必须在清单文件中添加相机权限。首先，我们必须在清单文件中的 Application 标签之前声明它：

```xml
<uses-permission android:name="android.permission.CAMERA"/>
```

接下来，需要在 MainActivity 中申请运行时权限。在文件头部，MainActivity 类的外面，我们先定义一些常量，以及引入一些包：

```java
// 你的 IDE 一般会自动导入相关的包，但是鉴于还有其他的实现方式，
// 所以这里还是把它们列出来。
import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.graphics.Matrix
import android.view.TextureView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

// 这是我们用来跟踪权限请求的数字,可以为任意值， 
// 因为一个应用程序具有多个请求权限的上下文，
// 这个可以帮助区分不同的上下文。
private const val REQUEST_CODE_PERMISSIONS = 10

// 这个数组用于存放所有需要获得的权限。
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
```



在 MainActivity 类内部，添加下面的代码，用于动态权限请求：

```kotlin
class MainActivity : AppCompatActivity(), LifecycleOwner {

    override fun onCreate(savedInstanceState: Bundle?) {
        ...
    }

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

    private fun startCamera() {
        // TODO: 实现 CameraX 的操作
    }

    private fun updateTransform() {
        // TODO: 实现相机取景器转换
    }

    /**
     * 从权限请求对话框处理结果，是否已批准请求？ 如果是，启动相机。 否则显示一个 Toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.", 
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * 检查所需的权限是否都被批准了。
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
               baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}
```

最后，我们在 onCreate 方法中获取运行时权限：

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...

    // 将下面的代码添加在 onCreate 方法末尾

    viewFinder = findViewById(R.id.view_finder)

    // 请求相机权限
    if (allPermissionsGranted()) {
        viewFinder.post { startCamera() }
    } else {
        ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
    }

    // 每当提供的 texture view 改变时都重新计算布局
    viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        updateTransform()
    }
}
```

现在，当这个 APP 启动后，它将首先检查是否获得了相机权限，如果已获取相机权限，将直接调用 startCamera() 方法，否则将会请求获取权限，一旦权限被批准，将调用 startCamera() 方法。

[^注意]: 不用在主线程上调用 `startCamera（）`，而是使用 `viewFinder.post {...}`，这么做是为了确保在调用 `startCamera（）` 时已经将 `viewFinder` 加载到视图中。



### 6. 实现取景器

对于大多数相机应用程序而言，向用户展示取景器是非常重要的，否则用户很难将相机对准在正确的位置。 可以使用 CameraX 的 Preview 类来实现取景器。

要使用 Preview，我们首先需要定义一个“配置”，然后使用该配置来创建用例（Preview）的实例。 生成的实例就是我们需要绑定到 CameraX 的生命周期的实例。 我们将在 startCamera() 方法中执行此操作。代码如下：

```kotlin
private fun startCamera() {

    // 为取景器创建一个配置对象
    val previewConfig = PreviewConfig.Builder().apply {
        setTargetResolution(Size(640, 480))
    }.build()


    // 创建一个取景器用例
    val preview = Preview(previewConfig)

    // 每当取景器更新就重新计算布局
    preview.setOnPreviewOutputUpdateListener {

        // 为了更新 SurfaceTexture，我们必须先移除它，再重新添加
        val parent = viewFinder.parent as ViewGroup
        parent.removeView(viewFinder)
        parent.addView(viewFinder, 0)

        viewFinder.surfaceTexture = it.surfaceTexture
        updateTransform()
    }

    // 将我们的取景器实例（preview）绑定到生命周期
    // 如果 Android Studio 报错说 “this” 不是一个 LifeCycleOwner
    // 尝试重新构建项目或者将 appcompat 依赖更新到 1.1.0 或更高版本。
    CameraX.bindToLifecycle(this, preview)
}
```



[^注意]: 您可能在 bindToLifecycle() 方法调用中看到以下错误："Type mismatch: inferred type is MainActivity but LifecycleOwner! was expected"。Cleaning 和 rebuilding 可以解决此问题。 如果其他所有方法均失败，请确保您使用的是最新版本的 appcompat。 在撰写本文时，这意味着将 gradle 文件的 dependencies 部分中的 appcompat 条目更改为：`implementation 'androidx.appcompat：appcompat：1.1.0' `。



现在，我们需要实现  updateTransform() 方法。 在 updateTransform() 内部，目标是补偿设备方向的变化，以垂直旋转显示取景器：(At this point, we need to implement the mysterious `updateTransform()` method. Inside of `updateTransform()` the goal is to compensate for changes in device orientation to display our viewfinder in upright rotation:)

```kotlin
private fun updateTransform() {
    val matrix = Matrix()

    // 计算取景器的中心点
    val centerX = viewFinder.width / 2f
    val centerY = viewFinder.height / 2f

    // 纠正预览输出以适应显示旋转
    val rotationDegrees = when(viewFinder.display.rotation) {
        Surface.ROTATION_0 -> 0
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> return
    }
    matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

    // 最后，将转换应用于我们的 TextureView
    viewFinder.setTransform(matrix)
}
```

要实现可用于生产环境的应用程序，请查看官方示例（<https://github.com/android/camera/tree/master/CameraXBasic>）以了解需要处理的其他内容。 为了使此教程简短，我们偷了点儿懒。 例如，我们没有跟踪某些配置的更改，例如 180 度设备旋转，这些更改不会触发我们的布局更改监听器。 非方形取景器还需要补偿设备旋转时长宽比的变化。

如果我们构建并运行该应用程序，现在应该可以看到预览效果啦！Nice！

![CameraX-5](D:\GithubRepos\MCLLearnAndroid\Android Learning Notes\images\CameraX-5.png)



### 7. 实现图像捕获

为了让用户捕获图片，我们需要在 activity_main.xml 布局文件中添加一个按钮：

```xml
<ImageButton
        android:id="@+id/capture_button"
        android:layout_width="72dp"
        android:layout_height="72dp"
        android:layout_margin="24dp" 
        app:srcCompat="@android:drawable/ic_menu_camera"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"/>
```

与预览相比，其他用例的工作方式非常相似。首先，我们必须定义一个用于实例化实际用例对象的配置对象。为了捕获照片，当按下捕获按钮时，我们需要更新  startCamera() 方法，并在调用 CameraX.bindToLifecycle() 之前在最后添加几行代码：

```kotlin
private fun startCamera() {

    ...

    // 将下面的代码添加到 CameraX.bindToLifeCycle 之前

    // 为 image capture 用例创建配置对象
    val imageCaptureConfig = ImageCaptureConfig.Builder()
        .apply {
            // 我们没有为图像捕获设置分辨率； 
            // 取而代之的是，我们选择一种捕获模式，
            // 该模式将根据宽高比和请求的模式推断出适当的分辨率
            setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
        }.build()

    // 创建 image capture 用例，并在按钮的点击回调里使用它
    val imageCapture = ImageCapture(imageCaptureConfig)
    findViewById<ImageButton>(R.id.capture_button).setOnClickListener {
        val file = File(externalMediaDirs.first(),
            "${System.currentTimeMillis()}.jpg")

        imageCapture.takePicture(file, executor,
            object : ImageCapture.OnImageSavedListener {
                override fun onError(
                    imageCaptureError: ImageCapture.ImageCaptureError,
                    message: String,
                    exc: Throwable?
                ) {
                    val msg = "Photo capture failed: $message"
                    Log.e("CameraXApp", msg, exc)
                    viewFinder.post {
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onImageSaved(file: File) {
                    val msg = "Photo capture succeeded: ${file.absolutePath}"
                    Log.d("CameraXApp", msg)
                    viewFinder.post {
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    // 将我们的取景器实例（preview）绑定到生命周期
    // 如果 Android Studio 报错说 “this” 不是一个 LifeCycleOwner
    // 尝试重新构建项目或者将 appcompat 依赖更新到 1.1.0 或更高版本。
    CameraX.bindToLifecycle(this, preview)
}
```

然后，将 imageCapture 添加到 CameraX.bindToLifeCycle() 的参数列表中：

```kotlin
CameraX.bindToLifecycle(this, preview, imageCapture)
```

这样，我们就实现了一个具有拍照功能的按钮。

![CameraX-6](D:\GithubRepos\MCLLearnAndroid\Android Learning Notes\images\CameraX-6.png)



### 8. 实现图像分析

[^注意]: 同时实现预览，图像捕获和图像分析不适用于 Android Studio 设备模拟器。建议使用真实的物理设备来测试这一部分。

CameraX 之所以非常有趣在于它的 ImageAnalysis 类。它允许我们定义一个实现了 ImageAnalysis.Analyzer 接口的自定义类，它将与传入的相机帧一起被调用。与 CameraX 的核心愿景一致，我们不必担心管理摄像机的会话状态甚至处理图像。像其他支持生命周期的组件（<https://developer.android.com/topic/libraries/architecture/lifecycle>）一样，绑定到我们的应用程序所需的生命周期就足够了。

首先，我们将实现自定义图像分析器。我们的分析器非常简单，仅打印出图像的平均亮度（光度），但举例说明了对于其他复杂的用例需要执行的操作。我们需要做的就是在实现 ImageAnalysis.Analyzer 接口的类中重写 analyze() 函数。我们可以将分析器的实现定义为 MainActivity 中的内部类：

```kotlin
private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
    private var lastAnalyzedTimestamp = 0L

    /**
     * 定义一个帮助函数，用于从图像缓冲区获取字节数组
     */
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // 清零缓冲区
        val data = ByteArray(remaining())
        get(data)   // 将缓冲区的数据复制到字节数组
        return data // 返回
    }

    override fun analyze(image: ImageProxy, rotationDegrees: Int) {
        val currentTimestamp = System.currentTimeMillis()
        // 计算平均亮度的频率不超过一秒
        if (currentTimestamp - lastAnalyzedTimestamp >=
            TimeUnit.SECONDS.toMillis(1)) {
            // 由于 ImageAnalysis 中的格式为 YUV，因此 image.planes[0] 包含Y（亮度）平面
            val buffer = image.planes[0].buffer
            // 从回调对象中获取图像数据
            val data = buffer.toByteArray()
            // 将数据转化为像素数组
            val pixels = data.map { it.toInt() and 0xFF }
            // 计算图像的平均亮度
            val luma = pixels.average()
            // 打印平均亮度
            Log.d("CameraXApp", "Average luminosity: $luma")
            // 更新时间戳
            lastAnalyzedTimestamp = currentTimestamp
        }
    }
}
```

通过我们的类实现 ImageAnalysis.Analyzer 接口，我们需要做的就是像实例化所有其他用例一样实例化 ImageAnalysis 并在调用 CameraX.bindToLifecycle() 之前再次更新 startCamera() 函数：

```kotlin
private fun startCamera() {

    ...

    // 将下面的代码添加到 CameraX.bindToLifecycle 前面

    // 设置图像分析管道，以计算图像平均亮度
    val analyzerConfig = ImageAnalysisConfig.Builder().apply {
        // 在我们的分析中，相对于“每张”图像，我们更关注的是最新图像
        setImageReaderMode(
            ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
    }.build()

    // 创建图像分析用例并实例化我们的分析器
    val analyzerUseCase = ImageAnalysis(analyzerConfig).apply {
        setAnalyzer(executor, LuminosityAnalyzer())
    }

    // 将我们的取景器实例（preview）绑定到生命周期
    // 如果 Android Studio 报错说 “this” 不是一个 LifeCycleOwner
    // 尝试重新构建项目或者将 appcompat 依赖更新到 1.1.0 或更高版本。
    CameraX.bindToLifecycle(this, preview, imageCapture)
}
```

最后，同样的需要在 CameraX.bindToLifeCycle() 的参数列表中添加 analyzerUseCase：

```kotlin
CameraX.bindToLifecycle(this, preview, imageCapture, analyzerUseCase)
```

现在运行该应用程序，大约每隔一秒钟将在 Logcat 中产生一条类似于以下消息：

```
D/CameaXApp:Average luminosity：...
```



### 9. 测试 APP

要测试该应用程序，我们要做的就是点击 Android Studio 中的“运行”按钮，我们的项目将在选定的设备或模拟器上构建、部署和启动。应用加载后，我们应该就可以看到取景器，由于我们之前添加的方向处理代码，即使在旋转设备后，取景器也将保持直立，并且还应该能够使用按钮拍照：

![CameraX-6](D:\GithubRepos\MCLLearnAndroid\Android Learning Notes\images\CameraX-6.png)

![CameraX-7](D:\GithubRepos\MCLLearnAndroid\Android Learning Notes\images\CameraX-7.png)



### 10. 总结

通过这个教程，我们已经学会了 CameraX 的简单用法，包括：

- 添加 CameraX 的依赖到你的项目
- 显示一个相机取景器（使用 Preview 用例）
- 实现图像的获取，将图像保存到本地（使用 ImageCapture 用例）
- 实现实时的图像帧分析（使用 ImageAnalysis 用例）



如果您有兴趣阅读有关 CameraX 及其功能的更多信息，请查阅文档（https://developer.android.com/training/camerax）或官方示例（https://github.com/android/camera/tree/master/CameraXBasic）。



[^注意]: 如果您想了解 CameraX 开发人员社区的最新信息，请加入我们的开发人员论坛，网址为  <http://g.co/camerax/developers>


