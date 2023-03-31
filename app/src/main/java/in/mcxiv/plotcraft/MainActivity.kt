package `in`.mcxiv.plotcraft

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val barGraph = findViewById<GraphAxisDescriber>(R.id.my_plot_describer)
        val button = findViewById<Button>(R.id.refresh_button)
        button.setOnClickListener {
            barGraph.plotData = (1..5).map { (20..80).random() }
            barGraph.invalidate()
        }
    }
}