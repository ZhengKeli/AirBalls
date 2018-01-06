package zkl.scienceFX.airBalls.physics

import zkl.tools.math.InstantPoint3D
import zkl.tools.math.Point3D

class AirBall(position: Point3D = InstantPoint3D(), velocity: Point3D = InstantPoint3D(), var radius: Double = 5.0) {
	val position: Point3D = position.getClone()
	fun setPosition(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0) {
		position.set(x, y, z)
	}
	
	fun process(time: Double) {
		position.set(
			x = position.x + velocity.x * time,
			y = position.y + velocity.y * time,
			z = position.z + velocity.z * time)
	}
	
	val velocity: Point3D = velocity.getClone()
	fun setVelocity(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0) {
		velocity.set(x, y, z)
	}
}