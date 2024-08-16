package net.netease.chunk;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkManager extends ChunkProviderClient {
   private final World world;
   private final LongHashMap<Chunk> chunkMap;
   private final Set<Long> loadedChunks = new HashSet();
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkLoader customCache;

   public ChunkManager(World world, Properties properties) {
      super(world);
      this.world = world;
      File file = new File(Minecraft.getMinecraft().mcDataDir, "GermCache/" + properties.getProperty("respath"));
      this.customCache = new ChunkLoader(file);
      this.chunkMap = this.chunkMapping;
   }

   public boolean isChunkLoaded(int x, int z) {
      return this.loadedChunks.contains(ChunkCoordIntPair.chunkXZ2Int(x, z));
   }

   public Chunk getLoadedChunk(long pos) {
      return (Chunk)this.chunkMap.getValueByKey(pos);
   }

   public void unloadChunk(int x, int z) {
      super.unloadChunk(x, z);
      this.loadedChunks.remove(ChunkCoordIntPair.chunkXZ2Int(x, z));
   }

   public Chunk loadChunk(int x, int z) {
      try {
         Chunk chunk = this.customCache.loadChunk(this.world, x, z);
         if (chunk != null) {
            chunk.setLastSaveTime(this.world.getTotalWorldTime());
            long pos = ChunkCoordIntPair.chunkXZ2Int(x, z);
            this.loadedChunks.add(pos);
            chunk.setChunkLoaded(true);
            this.chunkMap.add(pos, chunk);
            return chunk;
         }
      } catch (Exception var6) {
         LOGGER.error("Couldn't load res chunk", var6);
      }

      return super.loadChunk(x, z);
   }

   public World getWorld() {
      return this.world;
   }

   public Set<Long> getLoadedChunks() {
      return this.loadedChunks;
   }
}
