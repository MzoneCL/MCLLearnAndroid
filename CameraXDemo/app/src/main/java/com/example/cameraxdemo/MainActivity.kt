package com.example.cameraxdemo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


// 这是我们用来跟踪权限请求的数字,可以为任意值，
// 因为一个应用程序具有多个请求权限的上下文，
// 这个可以帮助区分不同的上下文。
private const val REQUEST_CODE_PERMISSIONS = 10

// 这个数组用于存放所有需要获得的权限。
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class MainActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    private fun startCamera() {
        // TODO: 实现 CameraX 的操作

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
                        Log.e("CameraXDemo", msg, exc)
                        viewFinder.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        Log.d("CameraXDemo", msg)
                        viewFinder.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }


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
        CameraX.bindToLifecycle(this, preview, imageCapture, analyzerUseCase)
    }

    private fun updateTransform() {
        // TODO: 实现相机取景器转换

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
                Log.d("CameraXDemo", "Average luminosity: $luma")
                // 更新时间戳
                lastAnalyzedTimestamp = currentTimestamp
            }
        }
    }
}
