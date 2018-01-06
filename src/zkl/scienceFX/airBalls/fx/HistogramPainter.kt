package zkl.scienceFX.airBalls.fx

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import zkl.tools.math.Rect
import java.util.*

abstract class HistogramPainter<Canvas : Any> : Painter<Canvas, HistogramPainterSource>() {
	val plotBoard: Rect get() = source.plotBoard
	val histogram: Histogram get() = source.histogram
	
	override fun onInitialized() { }
	override fun onRelease() { }
	override fun paint() {
		val values = histogram.getValues()
		
		drawBoard()
		val w=plotBoard.width/ histogram.graduationCount
		for (i in 0..values.size-1) {
			val h=values[i] * histogram.graduationCount
			drawRect(
				x = plotBoard.left+w*i,
				y = plotBoard.bottom-h,
				w = w, h = h)
		}
		
	}
	
	abstract fun drawBoard()
	abstract fun drawRect(x:Double,y:Double,w:Double,h:Double)
}

class HistogramPainterSource(
	var histogram: Histogram,
	var plotBoard: Rect = Rect(50.0, 50.0, 100.0, 100.0)
)

/**
 * 直方图
 */
class Histogram(var min:Double=0.0, var max:Double, var graduationCount:Int, var maxBufferSize:Int=10,var scale:Double=1.0){
	private var buffer: LinkedList<DoubleArray> = LinkedList()
	private var sumValues: DoubleArray = DoubleArray(graduationCount)
	
	@Synchronized fun addValues(values: DoubleArray){
		buffer.add(values)
		if (buffer.size > maxBufferSize) {
			val removed=buffer.removeFirst()
			for (i in 0..graduationCount - 1) {
				sumValues[i]+=values[i]-removed[i]
			}
		}else{
			for (i in 0..graduationCount - 1) {
				sumValues[i]+=values[i]
			}
		}
	}
	@Synchronized fun getValues(): DoubleArray {
		val re = DoubleArray(graduationCount)
		for (i in 0..re.size-1) {
			re[i] = sumValues[i] / buffer.size * scale
		}
		return re
	}
	
	fun doStatistics(iterable: Iterator<Double>) {
		val newValues: DoubleArray = DoubleArray(graduationCount)
		val block=(max-min)/ graduationCount
		for (value in iterable) {
			val i=Math.floor((value-min)/block).toInt()
			if (i > -1 && i < graduationCount) {
				newValues[i]+=1.0
			}
		}
		addValues(newValues)
	}
	
}


class HistogramPainterFX : HistogramPainter<GraphicsContext>() {
	override fun drawBoard() {
		canvas.fill= Color.BLACK
		canvas.fillRect(plotBoard.x, plotBoard.y, plotBoard.width, plotBoard.height)
	}
	override fun drawRect(x: Double, y: Double, w: Double, h: Double) {
		canvas.fill= Color.WHITE
		canvas.fillRect(x, y, w, h)
	}
	
}