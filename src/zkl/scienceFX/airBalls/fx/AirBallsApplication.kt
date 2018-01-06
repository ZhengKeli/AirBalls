package zkl.scienceFX.airBalls.fx

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.Window
import zkl.scienceFX.airBalls.physics.AirBall
import zkl.scienceFX.airBalls.physics.AirBallsWorld
import zkl.tools.math.MT
import zkl.tools.math.Rect
import kotlin.concurrent.thread

fun main(args: Array<String>) {
	Application.launch(AirBallsApplication::class.java, *args)
}
class AirBallsApplication:Application(){
	override fun start(stage: Stage) {
		val root: Pane = FXMLLoader.load(this.javaClass.getResource("airBalls.fxml"))
		stage.title = "airBalls"
		stage.scene = Scene(root, root.prefWidth, root.prefHeight)
		stage.show()
	}
}

class AirBallsController {
	private val stage: Window?
		get() = root.scene.window
	@FXML private lateinit var root: Pane
	
	@FXML private lateinit var b_start: Button
	var prepared=false
	fun onStartButtonClicked() {
		if(!prepared){
			preparePhysics()
			prepareHistogram()
			prepareDraw()
			prepared=true
		}
		
		if (runningMovie) stopMovie()
		else startMovie()
	}
	
	@FXML private lateinit var canvas: Canvas
	lateinit var painterSource: AirBallsPainterSource
	lateinit var painter: AirBallsPainterFX
	lateinit var histogramPainterSource: HistogramPainterSource
	lateinit var histogramPainter: HistogramPainterFX
	fun prepareDraw(){
		painterSource = AirBallsPainterSource(world, Rect(50.0, 50.0, world.width, world.height))
		painter = AirBallsPainterFX()
		painter.initialize(canvas.graphicsContext2D, painterSource)
		histogramPainterSource = HistogramPainterSource(histogram, Rect(100.0+world.width,50.0,world.height,world.height))
		histogramPainter = HistogramPainterFX()
		histogramPainter.initialize(canvas.graphicsContext2D, histogramPainterSource)
	}
	fun backgroundDraw(){
		canvas.graphicsContext2D.apply {
			fill= Color.BLACK
			fillRect(0.0, 0.0, canvas.width, canvas.height)
		}
	}
	fun draw(){
		painter.paint()
	}
	fun drawHistogram(){
		histogramPainter.paint()
	}
	
	//movie
	var runningMovie=false
	get() = field && stage?.isShowing ?: false
	fun startMovie(){
		runningMovie=true
		backgroundDraw()
		thread{
			while (runningMovie) {
				for (i in 1..5) {
					synchronized(world) {
						processPhysics()
					}
				}
				Platform.runLater {
					synchronized(world) {
						draw()
					}
				}
				Thread.sleep(20)
			}
		}
		thread {
			while (runningMovie) {
				synchronized(world) {
					doStatistics()
				}
				Platform.runLater {
					drawHistogram()
				}
				Thread.sleep(20)
			}
		}
	}
	fun stopMovie(){
		runningMovie=false
	}
	
	
	//physics
	lateinit var world: AirBallsWorld
	fun preparePhysics(){
		world = AirBallsWorld(500.0,500.0)
		for (i in 1..300) {
			val ball= AirBall(radius = 5.0)
			ball.setPosition(
				x = ball.radius+ MT.random(world.width-ball.radius*2),
				y = ball.radius+ MT.random(world.width-ball.radius*2))
			ball.setVelocity(MT.randomMirror(3.0), MT.randomMirror(3.0))
			world.airBalls.add(ball)
		}
	}
	fun processPhysics(){
		world.process(0.2)
	}
	
	
	//histogram
	lateinit var histogram: Histogram
	fun prepareHistogram(){
		histogram = Histogram(
			max = 8.0,
			graduationCount = 100,
			maxBufferSize = 300,
			scale = 80.0/world.ballsCount)
	}
	fun doStatistics(){
		histogram.doStatistics(VelocityIterator(world))
	}
	
	class VelocityIterator(world: AirBallsWorld) :Iterator<Double>{
		var ballsIterator=world.airBalls.iterator()
		override fun hasNext(): Boolean= ballsIterator.hasNext()
		override fun next(): Double =ballsIterator.next().velocity.absolute()
	}
	
	
}
