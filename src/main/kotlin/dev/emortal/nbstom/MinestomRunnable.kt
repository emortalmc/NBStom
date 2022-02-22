package dev.emortal.nbstom

import net.minestom.server.MinecraftServer
import java.time.Duration
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

abstract class MinestomRunnable(val delay: Duration = Duration.ZERO, val repeat: Duration = Duration.ZERO, var iterations: Int = -1, timer: Timer = defaultTimer) {

    companion object {
        val defaultTimer = Timer()
    }

    private var task: TimerTask? = null

    init {

        if (iterations < 2 && delay.toMillis() == 0L && repeat.toMillis() == 0L) {
            this@MinestomRunnable.run()
            this@MinestomRunnable.cancel()
        } else {
            task = timer.scheduleAtFixedRate(delay.toMillis(), repeat.toMillis()) {
                if (iterations != -1 && currentIteration >= iterations) {
                    this@MinestomRunnable.cancel()
                    cancelled()
                    return@scheduleAtFixedRate
                }

                try {

                    this@MinestomRunnable.run()
                } catch (e: Exception) {
                    MinecraftServer.getExceptionManager().handleException(e)
                }
                currentIteration++
            }
        }
    }
    var currentIteration = 0

    abstract fun run()
    open fun cancelled() {}

    fun cancel() {
        task?.cancel()
    }
}