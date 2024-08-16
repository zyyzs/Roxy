package net.netease;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;
import net.minecraft.network.PacketBuffer;

public enum FMLHandshakeClientState implements IHandshakeState<FMLHandshakeClientState> {
   START {
      public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
         cons.accept(HELLO);
      }
   },
   HELLO {
      public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
         cons.accept(WAITINGSERVERDATA);
         PacketProcessor.INSTANCE.getForgeChannel().sendToServer("REGISTER", new PacketBuffer(Unpooled.buffer().writeBytes(PacketProcessor.HYT_REGISTER)));
         PacketBuffer helloBuffer = new PacketBuffer(Unpooled.buffer());
         helloBuffer.writeByte(1);
         helloBuffer.writeByte(2);
         PacketProcessor.INSTANCE.getForgeChannel().sendToServer("FML|HS", helloBuffer);
         PacketProcessor.INSTANCE.getForgeChannel().sendToServer("FML|HS", new PacketBuffer(Unpooled.buffer().writeBytes(PacketProcessor.MOD_LIST)));
      }
   },
   WAITINGSERVERDATA {
      public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
         cons.accept(WAITINGSERVERCOMPLETE);
         PacketProcessor.INSTANCE.getForgeChannel().sendToServer("FML|HS", new PacketBuffer(Unpooled.buffer().writeByte(-1).writeByte(2)));
      }
   },
   WAITINGSERVERCOMPLETE {
      public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
         new HashMap();
         new HashSet();
         boolean hasMore = payload.readBoolean();
         if (hasMore) {
            cons.accept(WAITINGSERVERCOMPLETE);
         } else {
            cons.accept(PENDINGCOMPLETE);
            PacketProcessor.INSTANCE.getForgeChannel().sendToServer("FML|HS", new PacketBuffer(Unpooled.buffer().writeByte(-1).writeByte(3)));
         }
      }
   },
   PENDINGCOMPLETE {
      public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
         cons.accept(COMPLETE);
         PacketProcessor.INSTANCE.getForgeChannel().sendToServer("FML|HS", new PacketBuffer(Unpooled.buffer().writeByte(-1).writeByte(4)));
      }
   },
   COMPLETE {
      public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
         cons.accept(DONE);
         PacketProcessor.INSTANCE.getForgeChannel().sendToServer("FML|HS", new PacketBuffer(Unpooled.buffer().writeByte(-1).writeByte(5)));
      }
   },
   DONE {
      public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
         if (id == -2) {
            cons.accept(HELLO);
         }

      }
   },
   ERROR {
      public void accept(int id, ByteBuf payload, Consumer<? super FMLHandshakeClientState> cons) {
      }
   };

   FMLHandshakeClientState() {
   }

}
