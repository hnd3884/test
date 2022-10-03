package io.netty.handler.codec.redis;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import java.util.HashMap;
import io.netty.util.collection.LongObjectMap;
import io.netty.buffer.ByteBuf;
import java.util.Map;

public final class FixedRedisMessagePool implements RedisMessagePool
{
    private static final long MIN_CACHED_INTEGER_NUMBER = -1L;
    private static final long MAX_CACHED_INTEGER_NUMBER = 128L;
    private static final int SIZE_CACHED_INTEGER_NUMBER = 129;
    public static final FixedRedisMessagePool INSTANCE;
    private final Map<ByteBuf, SimpleStringRedisMessage> byteBufToSimpleStrings;
    private final Map<String, SimpleStringRedisMessage> stringToSimpleStrings;
    private final Map<RedisReplyKey, SimpleStringRedisMessage> keyToSimpleStrings;
    private final Map<ByteBuf, ErrorRedisMessage> byteBufToErrors;
    private final Map<String, ErrorRedisMessage> stringToErrors;
    private final Map<RedisErrorKey, ErrorRedisMessage> keyToErrors;
    private final Map<ByteBuf, IntegerRedisMessage> byteBufToIntegers;
    private final LongObjectMap<IntegerRedisMessage> longToIntegers;
    private final LongObjectMap<byte[]> longToByteBufs;
    
    private FixedRedisMessagePool() {
        this.keyToSimpleStrings = new HashMap<RedisReplyKey, SimpleStringRedisMessage>(RedisReplyKey.values().length, 1.0f);
        this.stringToSimpleStrings = new HashMap<String, SimpleStringRedisMessage>(RedisReplyKey.values().length, 1.0f);
        this.byteBufToSimpleStrings = new HashMap<ByteBuf, SimpleStringRedisMessage>(RedisReplyKey.values().length, 1.0f);
        for (final RedisReplyKey value : RedisReplyKey.values()) {
            final ByteBuf key = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(value.name().getBytes(CharsetUtil.UTF_8))).asReadOnly();
            final SimpleStringRedisMessage message = new SimpleStringRedisMessage(new String(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(value.name().getBytes(CharsetUtil.UTF_8))).array()));
            this.stringToSimpleStrings.put(value.name(), message);
            this.keyToSimpleStrings.put(value, message);
            this.byteBufToSimpleStrings.put(key, message);
        }
        this.keyToErrors = new HashMap<RedisErrorKey, ErrorRedisMessage>(RedisErrorKey.values().length, 1.0f);
        this.stringToErrors = new HashMap<String, ErrorRedisMessage>(RedisErrorKey.values().length, 1.0f);
        this.byteBufToErrors = new HashMap<ByteBuf, ErrorRedisMessage>(RedisErrorKey.values().length, 1.0f);
        for (final RedisErrorKey value2 : RedisErrorKey.values()) {
            final ByteBuf key = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(value2.toString().getBytes(CharsetUtil.UTF_8))).asReadOnly();
            final ErrorRedisMessage message2 = new ErrorRedisMessage(new String(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(value2.toString().getBytes(CharsetUtil.UTF_8))).array()));
            this.stringToErrors.put(value2.toString(), message2);
            this.keyToErrors.put(value2, message2);
            this.byteBufToErrors.put(key, message2);
        }
        this.byteBufToIntegers = new HashMap<ByteBuf, IntegerRedisMessage>(129, 1.0f);
        this.longToIntegers = new LongObjectHashMap<IntegerRedisMessage>(129, 1.0f);
        this.longToByteBufs = new LongObjectHashMap<byte[]>(129, 1.0f);
        for (long value3 = -1L; value3 < 128L; ++value3) {
            final byte[] keyBytes = RedisCodecUtil.longToAsciiBytes(value3);
            final ByteBuf keyByteBuf = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(keyBytes)).asReadOnly();
            final IntegerRedisMessage cached = new IntegerRedisMessage(value3);
            this.byteBufToIntegers.put(keyByteBuf, cached);
            this.longToIntegers.put(value3, cached);
            this.longToByteBufs.put(value3, keyBytes);
        }
    }
    
    @Override
    public SimpleStringRedisMessage getSimpleString(final String content) {
        return this.stringToSimpleStrings.get(content);
    }
    
    public SimpleStringRedisMessage getSimpleString(final RedisReplyKey key) {
        return this.keyToSimpleStrings.get(key);
    }
    
    @Override
    public SimpleStringRedisMessage getSimpleString(final ByteBuf content) {
        return this.byteBufToSimpleStrings.get(content);
    }
    
    @Override
    public ErrorRedisMessage getError(final String content) {
        return this.stringToErrors.get(content);
    }
    
    public ErrorRedisMessage getError(final RedisErrorKey key) {
        return this.keyToErrors.get(key);
    }
    
    @Override
    public ErrorRedisMessage getError(final ByteBuf content) {
        return this.byteBufToErrors.get(content);
    }
    
    @Override
    public IntegerRedisMessage getInteger(final long value) {
        return this.longToIntegers.get(value);
    }
    
    @Override
    public IntegerRedisMessage getInteger(final ByteBuf content) {
        return this.byteBufToIntegers.get(content);
    }
    
    @Override
    public byte[] getByteBufOfInteger(final long value) {
        return this.longToByteBufs.get(value);
    }
    
    static {
        INSTANCE = new FixedRedisMessagePool();
    }
    
    public enum RedisReplyKey
    {
        OK, 
        PONG, 
        QUEUED;
    }
    
    public enum RedisErrorKey
    {
        ERR("ERR"), 
        ERR_IDX("ERR index out of range"), 
        ERR_NOKEY("ERR no such key"), 
        ERR_SAMEOBJ("ERR source and destination objects are the same"), 
        ERR_SYNTAX("ERR syntax error"), 
        BUSY("BUSY Redis is busy running a script. You can only call SCRIPT KILL or SHUTDOWN NOSAVE."), 
        BUSYKEY("BUSYKEY Target key name already exists."), 
        EXECABORT("EXECABORT Transaction discarded because of previous errors."), 
        LOADING("LOADING Redis is loading the dataset in memory"), 
        MASTERDOWN("MASTERDOWN Link with MASTER is down and slave-serve-stale-data is set to 'no'."), 
        MISCONF("MISCONF Redis is configured to save RDB snapshots, but is currently not able to persist on disk. Commands that may modify the data set are disabled. Please check Redis logs for details about the error."), 
        NOREPLICAS("NOREPLICAS Not enough good slaves to write."), 
        NOSCRIPT("NOSCRIPT No matching script. Please use EVAL."), 
        OOM("OOM command not allowed when used memory > 'maxmemory'."), 
        READONLY("READONLY You can't write against a read only slave."), 
        WRONGTYPE("WRONGTYPE Operation against a key holding the wrong kind of value"), 
        NOT_AUTH("NOAUTH Authentication required.");
        
        private final String msg;
        
        private RedisErrorKey(final String msg) {
            this.msg = msg;
        }
        
        @Override
        public String toString() {
            return this.msg;
        }
    }
}
