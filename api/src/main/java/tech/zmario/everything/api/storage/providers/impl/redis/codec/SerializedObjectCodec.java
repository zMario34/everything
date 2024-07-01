package tech.zmario.everything.api.storage.providers.impl.redis.codec;

import io.lettuce.core.codec.RedisCodec;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SerializedObjectCodec implements RedisCodec<String, Object> {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return CHARSET.decode(bytes).toString();
    }

    @Override
    public Object decodeValue(ByteBuffer bytes) {
        try {
            byte[] array = new byte[bytes.remaining()];
            bytes.get(array);

            try (ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(array))) {
                return is.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return CHARSET.encode(key);
    }

    @Override
    public ByteBuffer encodeValue(Object value) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ObjectOutputStream os = new ObjectOutputStream(bytes)) {

            os.writeObject(value);

            return ByteBuffer.wrap(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return ByteBuffer.wrap(new byte[0]);
        }
    }
}