package net.netease.chunk;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class RegionFileManager {
   private static final Map<File, RegionFile> regionFileCache = Maps.newHashMap();

   public static boolean hasChunkData(File file, int chunkX, int chunkZ) {
      RegionFile regionFile = getRegionFile(file, chunkX, chunkZ);
      return regionFile != null && regionFile.method_7275(chunkX & 31, chunkZ & 31);
   }

   public static synchronized void clearRegionFileCache() {
      Iterator var0 = regionFileCache.values().iterator();

      while(var0.hasNext()) {
         RegionFile regionFile = (RegionFile)var0.next();

         try {
            if (regionFile != null) {
               regionFile.method_2558();
            }
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

      regionFileCache.clear();
   }

   public static synchronized RegionFile getRegionFile(File directory, int regionX, int regionZ) {
      File regionDir = new File(directory, "region");
      File regionFile = new File(regionDir, "r." + (regionX >> 5) + "." + (regionZ >> 5) + ".mca");
      RegionFile region = (RegionFile)regionFileCache.get(regionFile);
      if (region != null) {
         return region;
      } else if (regionDir.exists() && regionFile.exists()) {
         if (regionFileCache.size() >= 256) {
            clearRegionFileCache();
         }

         RegionFile newRegion = new RegionFile(regionFile);
         regionFileCache.put(regionFile, newRegion);
         return newRegion;
      } else {
         return null;
      }
   }

   public static DataInputStream getChunkInputStream(File directory, int chunkX, int chunkZ) {
      RegionFile regionFile = getRegionFile(directory, chunkX, chunkZ);
      return regionFile == null ? null : regionFile.method_6317(chunkX & 31, chunkZ & 31);
   }
}
