package com.zchd.pdf

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.util.*

/**
 * Copyright (C), 2020-2021, 中传互动（湖北）信息技术有限公司
 * Author: HeChao
 * Date: 2021/12/21 11:20
 * Description:
 */
internal object Utils {

    private val mApplication: Application by lazy { getApplication() }
    internal var mActivity: WeakReference<Activity>? = null

    internal fun registerActivityCallbacks() {
        mApplication.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                mActivity = WeakReference(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                mActivity?.clear()
            }
        })
    }

    fun getApplication(): Application {
        val activityThread = Class.forName("android.app.ActivityThread")
        return try {
            val currentApplication = activityThread.getDeclaredMethod("currentApplication")
            val currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread")
            val current = currentActivityThread.invoke(null)
            currentApplication.invoke(current) as Application
        } catch (e: Exception) {
            e.printStackTrace()
            Application()
        }
    }

    internal fun showToast(message: String?) {
        val activity = mActivity?.get()
        if (TextUtils.isEmpty(message) || activity == null || activity.isFinishing) return
        try {
            val toast = Toast.makeText(
                activity, message, Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.show()
        } catch (e: Exception) {
            Looper.prepare()
            val toast = Toast.makeText(
                activity, message, Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.show()
            Looper.loop()
        }
    }

    fun formatVideoTime(time: Long): String {
        val totalSecond = time / 1000
        val second = totalSecond % 60
        val minute = (totalSecond / 60) % 60
        val hour = totalSecond / 3600
        val stringBuilder = StringBuilder()
        val formatter = Formatter(stringBuilder, Locale.CHINA)
        return if (hour > 0) {
            formatter.format("%d:%02d:%02d", hour, minute, second).toString()
        } else {
            formatter.format("%02d:%02d", minute, second).toString()
        }
    }

    fun startInstallApk(context: Context, file: File) {
        context.startActivity(getInstallApkIntent(context, file))
    }

    fun getInstallApkIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileProvider.getUriForFile(
                context, context.packageName + ".fileprovider", file
            )
        } else {
            Uri.fromFile(file)
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        val resolveLists =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resolveLists) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                apkUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
        return intent
    }

    fun openMarket(context: Context) {
        kotlin.runCatching {
            val uri: Uri = Uri.parse("market://details?id=${context.packageName}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.android.vending")/*google play*/
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun isSex(idCard: CharSequence): Int {
        if (!TextUtils.isEmpty(idCard) && idCard.length == 18) {
            return if (Integer.parseInt(idCard.substring(16, 17)) % 2 == 0) {
                2
            } else {
                1
            }
        }
        return 0
    }


    fun realNameDesensitized(name: String?): String? {
        if (name?.isNotBlank() == true && name.length > 1) {
            var stringBuilder = StringBuilder(name)
            return stringBuilder.replace(0, name.length - 1, "**").toString()
        }
        return name ?: ""
    }

    fun idCardDesensitized(card: String?): String {
        if (card?.isNotBlank() == true) {
            var stringBuilder = StringBuilder(card)
            return stringBuilder.replace(1, card.length - 1, "****************").toString()
        }
        return card ?: ""
    }

    fun isSuEnable(): Boolean {
        var file: File? = null
        val paths = arrayListOf<String>(
            "/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/", "/su/bin/"
        )
        runCatching {
            paths.forEach {
                file = File(it + "su")
                if (file?.exists() == true && file?.canExecute() == true) {
                    return true
                }
            }
        }
        return false
    }


    fun rotateIfRequired(bitmap: Bitmap, outputImage: File): Bitmap? {
        try {
            var exifInterface = outputImage.path?.let { ExifInterface(it) }
            var orientation = exifInterface?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            );
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) return rotateBitmap(bitmap, 90)
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) return rotateBitmap(
                bitmap, 180
            );
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) return rotateBitmap(
                bitmap, 270
            );
            return bitmap
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        var matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        var rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }

    fun isChinese(c: Char): Boolean {
        val ub = Character.UnicodeBlock.of(c)
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
            return true
        }
        return false
    }

    fun getDeviceId(): String {
        return Build.PRODUCT + "-" + Build.DISPLAY
    }

    fun isActivityRunning(mContext: Context): Boolean {
        //通过ActivityManager 获取正在运行的任务信息
        val activityManager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //用List 单列集合 参数化类型为ActivityManager.RunningTaskInfo 存储RunningTaskInfo
        val info = activityManager.getRunningTasks(1) //获取1 个任务栈列表,但返回的列表size可能会小于int
        if (info != null && info.size > 0) {
            val component = info[0].topActivity //获取当前正在运行的任务栈的顶端activity，通过这个activity可以获取包名、类名等等信息
            if (component!!.packageName == mContext.packageName) {
                return true
            }
        }
        return false
    }

    fun getAssetsCacheFile(context: Context, fileName: String): String {
        val cacheFile = File(context.cacheDir, fileName)
        try {
            val inputStream: InputStream = context.assets.open(fileName)
            try {
                val outputStream = FileOutputStream(cacheFile)
                try {
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(buf).also { len = it } > 0) {
                        outputStream.write(buf, 0, len)
                    }
                } finally {
                    outputStream.close()
                }
            } finally {
                inputStream.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cacheFile.absolutePath
    }

    fun isBackground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        for (appProcess in appProcesses) {
            if (appProcess.processName == context.packageName) {
                return appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        }
        return false
    }

    fun cutRoundedCornerBitmap(bitmap: Bitmap): Bitmap {
        var output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(output)
        var color = Color.RED
        var paint = Paint()
        var rect = Rect(0, 0, bitmap.width, bitmap.height)
        var rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, 20f, 40f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

}