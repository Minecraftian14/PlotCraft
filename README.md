# Plot Craft

![image](https://user-images.githubusercontent.com/52451860/229164052-9f4b28e0-6d8d-4ee7-b45d-645975bd5ec5.png)

Made a simple bar graph plotter in Android.
I extended View class to draw only the bar graphs and the extended a ViewGroup to add the description part.

Most of the features are fully customisable, for instance:
 * The color of bars
 * The color of bar shadows
   * If not set, a darker version of the bar color will be calculated.
 * Background color of plot area
 * Experimental: If a grid color is set, draws a fine line between each bar.
 * Optionally, one can give minimum and maximum values to adjust the bar heights. If not given, minimum and maximum are calculated on the basis of given plotting data. The least value will not be drawn and the max value will fill the shadow.
 * Even the spacing between bars to bar width ratio can be adjusted!
 * Descriptor's title and axes's labels can also be modified via layout xmls.
 * Descriptor's title, axes's labels and tick's font size and color can be changed.
 
 
#### Example

activity_main.xml

```java
<TextView
  android:id="@+id/textView"
  .../>
  
<in.mcxiv.plotcraft.GraphAxisDescriber
  android:layout_width="350dp"
  android:layout_height="300dp"
  android:id="@+id/my_plot_describer"
  app:descBackgroundColor="#303030"
  app:descTitle="A Demo Bar Graph"
  app:layout_constraintTop_toBottomOf="@+id/textView"
  app:layout_constraintStart_toStartOf="parent"
  app:layout_constraintEnd_toEndOf="parent"
  android:layout_marginTop="32dp">

  <in.mcxiv.plotcraft.BarGraph
    android:layout_width="200dp"
    android:layout_height="300dp"
    android:id="@+id/my_plot_view"
    app:plotBarShadowColor="#3e3e3e"
    app:plotBarColor="#008EFF"
    app:plotBarWidth="2.5"
    app:plotBarSpacing="1"
    app:plotMinimumValue="0"
    app:plotMaximumValue="100" />

</in.mcxiv.plotcraft.GraphAxisDescriber>
```

MainActivity.java

```java
val barGraph = findViewById<GraphAxisDescriber>(R.id.my_plot_describer)
val button = findViewById<Button>(R.id.refresh_button)
button.setOnClickListener {
  barGraph.plotData = (1..5).map { (20..80).random() }
  barGraph.invalidate()
}
```