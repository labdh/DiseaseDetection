package com.example.diseasedetection

import android.Manifest.permission.CAMERA
import android.Manifest.permission_group.CAMERA
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaRecorder.VideoSource.CAMERA
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.renderscript.Element
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.diseasedetection.ml.Potato
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class upload : AppCompatActivity() {

    private lateinit var camera : LinearLayout
    private lateinit var gallery : LinearLayout
    private lateinit var image : ImageView
    private lateinit var predict : Button
    private lateinit var ans : TextView
    private lateinit var bar : ProgressBar
    private lateinit var bitmap : Bitmap
//    private lateinit var binding : ActivityMainBinding
//    private lateinit var CAMERA_REQ_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)


        camera = findViewById(R.id.camera)
        gallery = findViewById(R.id.gallery)
        predict = findViewById(R.id.predict)
        ans = findViewById(R.id.prediction)
        image = findViewById(R.id.image)
        bar = findViewById(R.id.loading)
    var detect : TextView = findViewById(R.id.detect)

    var imageproc = ImageProcessor.Builder().add(ResizeOp(256,256,ResizeOp.ResizeMethod.BILINEAR)).build()

        gallery.setOnClickListener {
            var intent: Intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, 100)
        }

        predict.setOnClickListener {
            bar.visibility = View.VISIBLE

            val totalTimeInMillis: Long = 500 // total time for the progress bar to complete, in milliseconds
            val intervalInMillis: Long = 1000 // interval at which to update the progress bar, in milliseconds
            val timer = object : CountDownTimer(totalTimeInMillis, intervalInMillis) {
                override fun onTick(millisUntilFinished: Long) {
                    val progress = ((totalTimeInMillis - millisUntilFinished) / intervalInMillis).toInt()
                    bar.progress = progress
                }

                override fun onFinish() {
                    bar.visibility = View.GONE
                }
            }
            timer.start()

            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)
            tensorImage = imageproc.process(tensorImage)
            val model = Potato.newInstance(this)

// Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)

// Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            var maxIdx = 0
            outputFeature0.forEachIndexed { index, fl ->
                if(outputFeature0[maxIdx]<=fl){
                    maxIdx = index
                }
            }
            var array = arrayListOf<String>("Early Blight","Late Blight","Healthy")
            var per: TextView = findViewById(R.id.percent)
            var confi : TextView = findViewById(R.id.confidence)
            var valu = outputFeature0[maxIdx]*100
            ans.text = array[maxIdx]
            per.text = "${valu}%"
            detect.visibility = View.VISIBLE
            ans.visibility = View.VISIBLE
            confi.visibility = View.VISIBLE
            per.visibility = View.VISIBLE

            model.close()
        }

        camera.setOnClickListener {
            getpermission()
            var intent : Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,112)
        }
    }

    private fun getpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),101)
                }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==101 && grantResults.size>0 && grantResults[0]!=PackageManager.PERMISSION_GRANTED){
            this.getpermission()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100){
            var uri = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
            image.setImageBitmap(bitmap)
        }
        else if(requestCode == 112){

            if (data != null) {
                bitmap  = data.extras?.get("data") as Bitmap
            }
            image.setImageBitmap(bitmap)
        }
    }
}