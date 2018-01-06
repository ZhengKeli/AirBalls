package zkl.scienceFX.airBalls.fx

abstract class Painter<Canvas:Any,Source:Any> {
	lateinit var canvas:Canvas
	lateinit var source:Source
	
	fun initialize(canvas: Canvas, source: Source){
		this.canvas = canvas
		this.source = source
		onInitialized()
	}
	fun release() {
		onRelease()
	}
	
	
	abstract fun onInitialized()
	abstract fun paint()
	abstract fun onRelease()
}