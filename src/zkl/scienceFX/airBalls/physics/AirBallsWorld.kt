package zkl.scienceFX.airBalls.physics

import java.util.*

class AirBallsWorld(var width:Double = 500.0, var height:Double = 500.0 ) {
	var airBalls:ArrayList<AirBall> = ArrayList()
	val ballsCount:Int get() = airBalls.size

	fun process(time: Double) {
		//处理位移
		for (ball in airBalls) {
			ball.process(time)
			
			if (ball.position.x < ball.radius && ball.velocity.x < 0) {
				ball.velocity.x *= -1.0
				ball.position.x = ball.radius
			} else if (ball.position.x > width - ball.radius && ball.velocity.x > 0) {
				ball.velocity.x *= -1.0
				ball.position.x =  width - ball.radius
			}
			if (ball.position.y < ball.radius && ball.velocity.y < 0) {
				ball.velocity.y *= -1.0
				ball.position.y = ball.radius
			} else if (ball.position.y > height - ball.radius && ball.velocity.y > 0) {
				ball.velocity.y *= -1.0
				ball.position.y = height - ball.radius
			}
			
			//ball.velocity.offset(0.0, 0.01)
			
		}
		//处理碰撞
		for (i in 0..airBalls.size-1) {
			for (j in i..airBalls.size-1) {
				val ball1: AirBall = airBalls[i]
				val ball2: AirBall = airBalls[j]

				//远距判断是否碰撞
				val dp=ball2.position-ball1.position
				val minL = ball1.radius + ball2.radius
				if (Math.abs(dp.x) >= minL || Math.abs(dp.y) >= minL) continue
				
				//近距判断是否碰撞
				val l = dp.absolute()
				if (l > minL) continue
				
				//判断它们是否在接近
				val dVelocity = ball2.velocity - ball1.velocity
				if (dVelocity * dp >= 0.0) continue
				
				
				//碰撞了！
				
				
				//准备相关量
				val cos = dp.x / l
				val sin = dp.y / l
				
				//建立相对于碰撞处的坐标系（x轴方向连接两球球心），切换到该坐标系
				var v1x = ball1.velocity.x * cos + ball1.velocity.y * sin
				var v2x = ball2.velocity.x * cos + ball2.velocity.y * sin
				val v1y = -ball1.velocity.x * sin + ball1.velocity.y * cos
				val v2y = -ball2.velocity.x * sin + ball2.velocity.y * cos
				
				//交换速度
				val vTemp = v1x
				v1x = v2x
				v2x = vTemp
				
				//切换回原坐标系
				ball1.velocity.set(
					x = v1x * cos - v1y * sin,
					y = v1x * sin + v1y * cos)
				ball2.velocity.set(
					x = v2x * cos - v2y * sin,
					y = v2x * sin + v2y * cos)
				
				//强制设置其位置使其不重叠
				val offsetP = dp / l * (minL - l) / 2.0
				ball1.position.offset(-offsetP)
				ball2.position.offset(offsetP)
			}
		}
		
	}
}

