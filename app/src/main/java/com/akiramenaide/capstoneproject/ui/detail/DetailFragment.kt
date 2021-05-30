package com.akiramenaide.capstoneproject.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.akiramenaide.capstoneproject.core.domain.model.Fruit
import com.akiramenaide.capstoneproject.databinding.FragmentDetailBinding
import com.akiramenaide.capstoneproject.ui.util.MyColors
import com.akiramenaide.capstoneproject.ui.util.MyColors.darkCyan
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.ArrayList


class DetailFragment : Fragment() {
    private val brightColors = MyColors.brightColorArray
    private lateinit var binding: FragmentDetailBinding
    private lateinit var pieChart: PieChart
    private val detailViewModel: DetailViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pieChart = binding.pieChart
        detailViewModel.fruitList.observe(viewLifecycleOwner, {
            drawPieChart(it)
            drawRecyclerView(it)
        })
    }

    private fun drawPieChart(fruits: List<Fruit>){
        val pieValues = ArrayList<PieEntry>()
        for (element in fruits) {
            pieValues.add(PieEntry(element.total.toFloat(), element.name))
        }
        val dataSet = PieDataSet(pieValues, "")

        pieChart.apply {
            data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter())
                setValueTextSize(12f)
            }
            setDrawEntryLabels(false)
            setHoleColor(darkCyan)
            isRotationEnabled = false
            setUsePercentValues(true)
            animateY(1000)
            description.text = ""
        }
        dataSet.colors = brightColors
    }

    private fun drawRecyclerView(fruits: List<Fruit>){
        val fruitAdapter = DetailAdapter()
        fruitAdapter.setFruitsData(fruits)
        fruitAdapter.notifyDataSetChanged()

        with(binding.rvFruits) {
            layoutManager = LinearLayoutManager(context)
            adapter = fruitAdapter
            setHasFixedSize(true)
        }
    }

}