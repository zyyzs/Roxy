package lol.tgformat.irc.network.packets;

import java.io.*;
import java.util.Objects;

/**
 * @author DiaoLing
 * @since 2/3/2024
 */
public class PacketBuffer {
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;

    public PacketBuffer(InputStream inputStream, OutputStream outputStream) {
        this.dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
        this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));
    }

    public byte readByte() throws IOException {
        return dataInputStream.readByte();
    }

    public int readInt() throws IOException {
        return dataInputStream.readInt();
    }

    public short readShort() throws IOException {
        return dataInputStream.readShort();
    }

    public long readLong() throws IOException {
        return dataInputStream.readLong();
    }

    public boolean readBoolean() throws IOException {
        return dataInputStream.readBoolean();
    }

    public String readString() throws IOException {
        return dataInputStream.readUTF();
    }

    public void writeByte(byte value) throws IOException {
        dataOutputStream.writeByte(value);
    }

    public void writeInt(int value) throws IOException {
        dataOutputStream.writeInt(value);
    }

    public void writeShort(short value) throws IOException {
        dataOutputStream.writeShort(value);
    }

    public void writeLong(long value) throws IOException {
        dataOutputStream.writeLong(value);
    }

    public void writeBoolean(boolean value) throws IOException {
        dataOutputStream.writeBoolean(value);
    }

    public void writeString(String value) throws IOException {
        dataOutputStream.writeUTF(Objects.requireNonNullElse(value, ""));
    }

    public int available() throws IOException {
        return dataInputStream.available();
    }

    public void flush() throws IOException {
        dataOutputStream.flush();
    }

    public void close() throws IOException {
        dataInputStream.close();
        dataOutputStream.close();
    }
}