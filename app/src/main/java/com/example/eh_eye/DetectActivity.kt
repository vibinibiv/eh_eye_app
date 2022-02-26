package com.example.eh_eye

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.example.eh_eye.databinding.ActivityDetectBinding
import com.example.eh_eye.utils.Draw
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import java.util.*


class DetectActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityDetectBinding
    private lateinit var objectDetector: ObjectDetector
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

    private var tts: TextToSpeech? = null

    var objectDetected = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this@DetectActivity)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_detect)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // get() is used to get the instance of the future.
            val cameraProvider = cameraProviderFuture.get()
            // Here, we will bind the preview
            bindPreview(cameraProvider=cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        val localModel = LocalModel.Builder()
            .setAssetFilePath("labeler.tflite")
            .build()
        val customObjectDetectorOptions =
            CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                .enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
                .setMaxPerObjectLabelCount(3)
                .build()
        objectDetector =
            ObjectDetection.getClient(customObjectDetectorOptions)


    }


    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                //buttonSpeak!!.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }

    }

    private fun speakOut(text :String) {
        tts!!.speak(text, TextToSpeech.QUEUE_ADD, null,"")
    }

    public override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider){
        val preview : Preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280,720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val image = imageProxy.image

            if (image != null) {
                val inputImage = InputImage.fromMediaImage(image, rotationDegrees)

                objectDetector
                    .process(inputImage)
                    .addOnSuccessListener { objects ->
                        for( i in objects) {
                            val label = i.labels.firstOrNull()?.text ?: "Undefined"
                            print(label)
                            if(objectDetected!=label && label !="Undefined"){
                                speakOut(label)
                                objectDetected=label
                            }

                            //toast(i.labels.firstOrNull()?.text ?: "Undefined")
                        if(binding.layout.childCount > 1)  binding.layout.removeViewAt(1)
                            val element = Draw(this, rect = i.boundingBox, text=label)
                            binding.layout.addView(element,1)
                            //speakOut(label)

                        }

                        imageProxy.close()
                    }
                    .addOnFailureListener {
                        imageProxy.close()
                        //toast("bo")
                    }
            }
        }

        cameraProvider.bindToLifecycle(this as LifecycleOwner,cameraSelector,imageAnalysis,preview)
    }



    private fun toast(s: String) {
        Toast.makeText(
            this@DetectActivity,
            s,
            Toast.LENGTH_SHORT
        ).show()

    }

}
