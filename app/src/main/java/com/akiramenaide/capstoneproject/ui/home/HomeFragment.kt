package com.akiramenaide.capstoneproject.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.akiramenaide.capstoneproject.core.data.source.remote.api.ApiService
import com.akiramenaide.capstoneproject.core.data.source.remote.response.PostedImage
import com.akiramenaide.capstoneproject.core.domain.model.Fruit
import com.akiramenaide.capstoneproject.databinding.FragmentHomeBinding
import com.akiramenaide.capstoneproject.ml.FruitModel
import com.akiramenaide.capstoneproject.ui.util.MyColors
import com.akiramenaide.capstoneproject.ui.util.PredictedObject
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.koin.android.viewmodel.ext.android.viewModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.ArrayList

class HomeFragment : Fragment() {
    private val brightColors = MyColors.brightColorArray
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var barChart: BarChart
    private lateinit var classList: List<String>
    private lateinit var fruitList: List<Fruit>
    private var insertedFruit: Fruit? = null
    private  var fruitInfo: PredictedObject? = null
    private var myBitmap: Bitmap? = null
    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)
        return fragmentHomeBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barChart = fragmentHomeBinding.barChart
        homeViewModel.getAllFruits().observe(viewLifecycleOwner, { fruits ->
            fruitList = fruits
            drawBarChart(fruitList)
        })

        classList = getClassList()

        fragmentHomeBinding.btnPickImg.setOnClickListener {
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            }
            else {
                getImage()
            }
        }

        fragmentHomeBinding.btnPredictImg.setOnClickListener {
            fruitInfo = predictImage()
        }

        fragmentHomeBinding.btnInsertDb.setOnClickListener {
            val fruit = fruitInfo
            var isInDb = false

            fruit?.let { myFruit ->
                var fruitName = classList[myFruit.index]
                var isFresh = false

                if (fruitName.contains("fresh")) {
                    fruitName = fruitName.removePrefix("fresh")
                    isFresh = true
                } else {
                    fruitName = fruitName.removePrefix("rotten")
                }
                Log.d(TAG, "$fruitName, $isFresh")

                for (element in fruitList) {
                    if (fruitName.lowercase() == element.name.lowercase()) {
                        element.total += 1
                        if (isFresh) {
                            element.freshTotal += 1
                        }
                        isInDb = true
                        Log.d(TAG, "updating ...")
                        Log.d(TAG, "${element.total}, ${element.freshTotal}")
                        homeViewModel.updateFruitInfo(element)
                    }
                }

                if(!isInDb) {
                    insertedFruit = Fruit(fruitList.size + 1, fruitName, 1, if(isFresh) 1 else 0)
                    Log.d(TAG, "inserting ...")
                    insertedFruit?.let {
                        homeViewModel.insertFruit(it)
                    }
                }

            }
            Log.d(TAG, "${insertedFruit?.id}, ${insertedFruit?.name}, ${insertedFruit?.total}, ${insertedFruit?.freshTotal}")

        }
    }

    private fun drawBarChart(fruits: List<Fruit>){
        val barValues = ArrayList<BarEntry>()
        val fruitNames = ArrayList<String>()
        var max = 0f
        var x = 0f
        for (element in fruits) {
            barValues.add(BarEntry(
                x,
                element.total.toFloat())
            )
            x++
            fruitNames.add(element.name)
            if (element.total.toFloat() > max) {
                max = element.total.toFloat()
            }
        }
        val dataSet = BarDataSet(barValues, "Fruits")

        barChart.apply {
            data = BarData(dataSet).apply {
                setValueTextSize(12f)
                setValueTextColor(Color.WHITE)
            }
            description = null
            setFitBars(true)
            setScaleEnabled(false)
            setPinchZoom(false)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(fruitNames)
                labelCount = fruitNames.size
                yOffset = 5f
            }
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f
            animateY(1000)
            invalidate()
        }
        dataSet.colors = brightColors
    }

    private fun getClassList(): List<String> {
        val filename = "fruit_labels.txt"
        val inputString = requireActivity().application.assets.open(filename)
            .bufferedReader()
            .use { it.readText() }
        return inputString.split("\n")
    }

    private fun getImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun predictImage(): PredictedObject? {
        myBitmap?.let { originalBitmap ->
            val resizedImg = Bitmap.createScaledBitmap(originalBitmap, 224, 224, true)
            fragmentHomeBinding.myImg.setImageBitmap(resizedImg)

            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedImg.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val imgString = Base64.encode(imageBytes, Base64.DEFAULT)
            val utfString = String(imgString, charset("UTF-8"))

            val input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).order(ByteOrder.nativeOrder())

            for (y in 0 until 224) {
                for (x in 0 until 224) {
                    val px = resizedImg.getPixel(x, y)

                    val r = Color.red(px)
                    val g = Color.green(px)
                    val b = Color.blue(px)

                    val rf = r / 255f
                    val gf = g / 255f
                    val bf = b / 255f

                    input.putFloat(rf)
                    input.putFloat(gf)
                    input.putFloat(bf)
                }
            }

            val model = FruitModel.newInstance(requireContext())
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)

            inputFeature0.loadBuffer(input)

//            val myData = PostedData(inputFeature0.floatArray)
//
//            val q = inputFeature0.floatArray
//            val arr = arrayListOf<Float>()
//            for (x in q){
//                arr.add(x)
//            }

            val jsonString = "{\"data\": \"$utfString\"}"

            //Log.d("InputString", jsonString)

            postString(jsonString)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val max = getMax(outputFeature0.floatArray)

            fragmentHomeBinding.predictionTxt.text = classList[max.index]
            fragmentHomeBinding.predictionNumTxt.text = max.similarity.toString()

            model.close()

            return max
        }

        return null
    }

    private fun postString(buffer: String) {
        ApiService.api.getPredict(buffer).enqueue(object : Callback<PostedImage> {
            override fun onResponse(
                call: retrofit2.Call<PostedImage>,
                response: Response<PostedImage>
            ) {
                val outputStream = "${response.body()?.percentage}, ${response.body()?.predClass}, ${response.body()?.prediction}, ${response.body()?.className}"
                Log.d("onResponse Success", response.code().toString())
                Log.d("Object", outputStream)
            }

            override fun onFailure(call: retrofit2.Call<PostedImage>, t: Throwable) {
                Log.e("onFailure", t.message.toString())
            }

        })
    }

    private fun getMax(arr: FloatArray): PredictedObject {
        var ind = 0
        var min = 0.0f

        for (i in 0..7) {
            if (arr[i] > min) {
                ind = i
                min = arr[i]
            }
        }

        return PredictedObject(ind, min)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImage()
                }
                else {
                    Toast.makeText(requireActivity(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val img = data?.data
            Log.d("URI", img.toString())
            myBitmap  = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, img)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val IMAGE_PICK_CODE = 1000
        const val PERMISSION_CODE = 1001
        private val TAG = HomeFragment::class.java.simpleName
    }

}