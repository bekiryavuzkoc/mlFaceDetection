package com.huawei.com.huawei.honorappmlkit

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.face.face3d.ML3DFaceAnalyzer
import com.huawei.hms.mlsdk.face.face3d.ML3DFaceAnalyzerSetting
import kotlinx.android.synthetic.main.activity_main.*

private lateinit var mAnalyzer: ML3DFaceAnalyzer
private lateinit var mLensEngine: LensEngine
private lateinit var mFaceAnalyzerTransactor: FaceAnalyzerTransactor

private var surfaceHolderCamera: SurfaceHolder? = null

private val requiredPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (hasPermissions(requiredPermissions))
            init()
        else
            ActivityCompat.requestPermissions(this, requiredPermissions, 0)
    }

    private fun hasPermissions(permissions: Array<String>) = permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0 && grantResults.isNotEmpty() && hasPermissions(requiredPermissions))
            init()
    }

    private fun init() {
        mAnalyzer = createAnalyzer()
        mFaceAnalyzerTransactor = FaceAnalyzerTransactor()
        mAnalyzer.setTransactor(mFaceAnalyzerTransactor)
        prepareViews()
    }

    private fun prepareViews() {
        surfaceHolderCamera = surfaceViewCamera.holder

        surfaceHolderCamera?.addCallback(surfaceHolderCallback)
    }

    private val surfaceHolderCallback = object : SurfaceHolder.Callback {

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            mLensEngine = createLensEngine(p1, p2)
            mLensEngine.run(p0)
        }

        override fun surfaceDestroyed(p0: SurfaceHolder) {
            mLensEngine.release()
        }

        override fun surfaceCreated(p0: SurfaceHolder) {

        }
    }

    private fun createLensEngine(width: Int, height: Int): LensEngine {
        val lensEngineCreator = LensEngine.Creator(this, mAnalyzer)
                .applyFps(20F)
                .setLensType(LensEngine.FRONT_LENS)
                .enableAutomaticFocus(true)

        return if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            lensEngineCreator.let {
                it.applyDisplayDimension(height, width)
                it.create()
            }
        } else {
            lensEngineCreator.let {
                it.applyDisplayDimension(width, height)
                it.create()
            }
        }
    }


    private fun createAnalyzer(): ML3DFaceAnalyzer {
        val settings = ML3DFaceAnalyzerSetting.Factory()
                .setTracingAllowed(true)
                .setPerformanceType(ML3DFaceAnalyzerSetting.TYPE_PRECISION)
                .create()

        return  MLAnalyzerFactory.getInstance().get3DFaceAnalyzer(settings)
    }


}