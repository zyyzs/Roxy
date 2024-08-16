package net.netease.chunk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ChunkRandomAccessFile extends RandomAccessFile {
   private long currentPosition = 0L;

   public ChunkRandomAccessFile(String fileName, String mode) throws FileNotFoundException {
      super(fileName, mode);
   }

   public int read(byte[] byteArray) throws IOException {
      int bytesRead = super.read(byteArray);
      if (bytesRead != -1) {
         CustomChunkMethod.processData(byteArray, this.currentPosition);
         this.currentPosition += (long)bytesRead;
      }

      return bytesRead;
   }

   public ChunkRandomAccessFile(File file, String mode) throws FileNotFoundException {
      super(file, mode);
   }

   public int read(byte[] byteArray, int offset, int length) throws IOException {
      int bytesRead = super.read(byteArray, offset, length);
      if (bytesRead != -1) {
         CustomChunkMethod.processData(byteArray, this.currentPosition, offset, length);
         this.currentPosition += (long)bytesRead;
      }

      return bytesRead;
   }

   public int read() throws IOException {
      int byteRead = super.read();
      if (byteRead != -1) {
         int processedByte = CustomChunkMethod.processByte(byteRead, this.currentPosition);
         ++this.currentPosition;
         return processedByte;
      } else {
         return byteRead;
      }
   }

   public void seek(long position) throws IOException {
      super.seek(position);
      this.currentPosition = position;
   }
}
