package world.cepi.example

import net.minestom.server.extensions.Extension;

class ExampleExtension : Extension() {

    override fun initialize(): LoadStatus {
        logger().info("[ExampleExtension] has been enabled!")

        return LoadStatus.SUCCESS
    }

    override fun terminate() {
        logger().info("[ExampleExtension] has been disabled!")
    }

}
