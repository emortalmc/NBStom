# NBStom

## API

```kotlin
val song = NBS(Path.of("./gaming.nbs"))

NBS.play(song, player)
NBS.playWithParticles(song, player)
NBS.stopPlaying(player)

// NBStom also has /music and /loop commands built in but simply not registered by default.
// Register them individually with:
MinecraftServer.getCommandManager().register(MusicCommand)
MinecraftServer.getCommandManager().register(LoopCommand)

// or alternatively
NBS.registerCommands()
```