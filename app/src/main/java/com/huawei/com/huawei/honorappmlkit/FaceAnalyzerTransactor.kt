package com.huawei.com.huawei.honorappmlkit

import android.util.Log
import android.util.SparseArray
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.common.MLAnalyzer.MLTransactor
import com.huawei.hms.mlsdk.face.face3d.ML3DFace

class FaceAnalyzerTransactor: MLTransactor<ML3DFace> {

    override fun transactResult(results: MLAnalyzer.Result<ML3DFace>?) {
        val items: SparseArray<ML3DFace> = results!!.analyseList

        Log.i("FaceOrientation:X ",items.get(0).get3DFaceEulerX().toString())
        Log.i("FaceOrientation:Y",items.get(0).get3DFaceEulerY().toString())
        Log.i("FaceOrientation:Z",items.get(0).get3DFaceEulerZ().toString())
    }

    override fun destroy() {

    }
}