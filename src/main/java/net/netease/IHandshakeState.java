package net.netease;

import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;

public interface IHandshakeState<S> {
   void accept(int var1, ByteBuf var2, Consumer<? super S> var3);
}
