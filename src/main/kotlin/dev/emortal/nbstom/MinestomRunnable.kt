package dev.emortal.nbstom

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Creates a task using Java's scheduler instead of Minestom's
 * Allows for more accuracy, and appears to run faster
 *
 * @param delay How long to delay the task from running
 * @param repeat How long between intervals of running
 * @param iterations How many times to iterate, -1 being infinite (also default)
 * @param coroutineScope The coroutine scope to use, should be set to the game's scope if created inside a game as the tasks will be automatically cancelled on end, use GlobalScope otherwise
 */
internal abstract class MinestomRunnable(
    var delay: Duration = Duration.ZERO,
    var repeat: Duration = Duration.ZERO,
    var iterations: Int = -1,
    val coroutineScope: CoroutineScope
) {

    abstract suspend fun run()

    private val keepRunning = AtomicBoolean(true)
    private var job: Job? = null
    private val tryRun = suspend {
        try {
            run()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    var currentIteration = AtomicInteger(0)

    init {

        job = coroutineScope.launch {
            delay(delay.toMillis())
            if (repeat.toMillis() != 0L) {
                while (keepRunning.get()) {
                    tryRun()
                    delay(repeat.toMillis())
                    val currentIter = currentIteration.incrementAndGet()
                    if (iterations != -1 && currentIter >= iterations) {
                        cancelImmediate()
                        return@launch
                    }
                }
            } else {
                if (keepRunning.get()) {
                    tryRun()
                }
            }
        }
    }

    open fun cancelled() {}

    fun cancel() {
        keepRunning.set(false)
        cancelled()
    }

    fun cancelImmediate() {
        cancel()
        job?.cancel()
    }
}