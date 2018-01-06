package zkl.scienceFX.airBalls.fx

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import zkl.scienceFX.airBalls.physics.AirBall
import zkl.scienceFX.airBalls.physics.AirBallsWorld
import zkl.tools.math.InstantPoint2D
import zkl.tools.math.Point3D
import zkl.tools.math.Rect

abstract class AirBallsPainter<Canvas:Any> : Painter<Canvas, AirBallsPainterSource>(){
	val world: AirBallsWorld get() = source.world
	
	override fun onInitialized() { }
	override fun onRelease() { }
	override fun paint() {
		drawBackground()
		world.airBalls.forEach { ball -> drawBall(ball) }
	}
	
	abstract fun drawBackground()
	abstract fun drawBall(ball: AirBall)
}

class AirBallsPainterSource(
	var world: AirBallsWorld,
	var paintBoard: Rect = Rect(50.0, 50.0, world.width, world.height)
){
	fun getMapped2D(point: Point3D): InstantPoint2D {
		return InstantPoint2D(
			x = paintBoard.left + (point.x / world.width * paintBoard.width),
			y = paintBoard.top + (point.y / world.height * paintBoard.height)
		)
	}
}

class AirBallsPainterFX : AirBallsPainter<GraphicsContext>() {
	override fun drawBackground() {
		canvas.fill= Color.BLACK
		canvas.fillRect(source.paintBoard.x, source.paintBoard.y, source.paintBoard.width, source.paintBoard.height)
		canvas.stroke= Color.WHITE
		canvas.strokeRect(source.paintBoard.x, source.paintBoard.y, source.paintBoard.width, source.paintBoard.height)
	}
	override fun drawBall(ball: AirBall) {
		val rate = Math.min(ball.velocity.absolute() / 5.0, 1.0)
		canvas.fill = colorMix(Color.WHITE,Color(0.2,0.2,0.2,1.0),  rate, 1 - rate)
		val mapped = source.getMapped2D(ball.position)
		canvas.fillOval(mapped.x - ball.radius, mapped.y - ball.radius, ball.radius * 2, ball.radius * 2)
	}
}

internal fun colorMix(color1: Color, color2: Color, weight1:Double=1.0, weight2:Double=1.0): Color {
	val weightSum=weight1+weight2
	return Color(
		(color1.red * weight1 + color2.red * weight2) / weightSum,
		(color1.green * weight1 + color2.green * weight2) / weightSum,
		(color1.blue * weight1 + color2.blue * weight2) / weightSum,
		(color1.opacity * weight1 + color2.opacity * weight2) / weightSum
	)
}
