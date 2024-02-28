package nay.kirill.columnchart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nay.kirill.columnchart.model.mockData

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        findViewById<ColumnChartView>(R.id.columnChart).items = mockData
    }

}