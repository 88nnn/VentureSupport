package com.example.venturesupport

import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.venturesupport.databinding.SaleschartBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class SalesChartActivity: AppCompatActivity() {
    private val binding: SaleschartBinding by lazy {
        SaleschartBinding.inflate(layoutInflater)
    }
    /*
    private val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        Log.d("YearMonthPickerTest", "year = $year, month = $monthOfYear, day = $dayOfMonth")
    }
    */
    private lateinit var salesChartAdapter: SalesChartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)

        binding.yearButton.text = "${year}년"

        binding.yearButton.setOnClickListener {
            val pd = MyYearPickerDialog()
            pd.setListener { _, year, monthOfYear, _ ->
                val year = "${year}년"
                binding.yearButton.text = year
            }
            pd.show(supportFragmentManager, "YearPickerTest")
        }

        // 결제 수단과 오더 조회 후 빼기(수정 필요)
        binding.incomeButton.text = "345083원"
        binding.expenseButton.text = "250267원"

        binding.incomeButton.setOnClickListener {
            val intent = Intent(this, IncomeChartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        binding.expenseButton.setOnClickListener {
            val intent = Intent(this, ExpenseChartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val barChart: BarChart = binding.barchart
        // 데이터 생성
        val entriesForeground = ArrayList<BarEntry>()

        // 수정필요
        // 실제 값 (전경)
        entriesForeground.add(BarEntry(1f, 70f))
        entriesForeground.add(BarEntry(2f, 40f))
        entriesForeground.add(BarEntry(3f, 90f))
        entriesForeground.add(BarEntry(4f, 50f))
        entriesForeground.add(BarEntry(5f, 30f))
        entriesForeground.add(BarEntry(6f, 56f))
        entriesForeground.add(BarEntry(7f, 32f))
        entriesForeground.add(BarEntry(8f, 71f))
        entriesForeground.add(BarEntry(9f, 64f))
        entriesForeground.add(BarEntry(10f, 42f))
        entriesForeground.add(BarEntry(11f, 48f))
        entriesForeground.add(BarEntry(12f, 29f))

        // 데이터셋 생성
        val barDataSetForeground = BarDataSet(entriesForeground, "Foreground")
        barDataSetForeground.setColors(*ColorTemplate.COLORFUL_COLORS)

        // BarData 생성
        val barData = BarData(barDataSetForeground)
        barData.barWidth = 0.4f // 막대 너비 설정
        barChart.data = barData

        // X축 라벨 설정
        val labels = arrayOf("1월", "2월", "3월", "4월", "5월", "6월",
            "7월", "8월", "9월", "10월", "11월", "12월")

        barChart.xAxis.labelCount = labels.size // 모든 라벨 표시
        barChart.xAxis.setGranularity(1f) // 간격 설정

        barChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return if (value.toInt() >= 0 && value.toInt() < labels.size) {
                    labels[value.toInt()]
                } else {
                    ""
                }
            }
        }

        // X축 위치 설정
        barChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

        // 차트 갱신
        barChart.invalidate()


        salesChartAdapter = SalesChartAdapter(entriesForeground)
        binding.salesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.salesRecyclerView.adapter = salesChartAdapter
        salesChartAdapter.notifyDataSetChanged()
    }
}