# NBStom

## API

```java
NBSSong song = new NBSSong(Path.of("path/to/.nbs"));

// for a player
NBS.play(song, player);
NBS.stop(player);

// for an audience
UUID stopId = UUID.randomUUID();
NBS.play(song, audience, MinecraftServer.getSchedulerManager(), stopId);
NBS.stop(stopId)
```