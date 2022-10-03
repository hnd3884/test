package org.msgpack;

import org.msgpack.packer.Unconverter;
import org.msgpack.unpacker.Converter;
import org.msgpack.type.Value;
import java.io.IOException;
import org.msgpack.template.Template;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import org.msgpack.unpacker.MessagePackBufferUnpacker;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.MessagePackUnpacker;
import org.msgpack.unpacker.Unpacker;
import java.io.InputStream;
import org.msgpack.packer.MessagePackBufferPacker;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.MessagePackPacker;
import org.msgpack.packer.Packer;
import java.io.OutputStream;
import org.msgpack.template.TemplateRegistry;

public class MessagePack
{
    private TemplateRegistry registry;
    private static final MessagePack globalMessagePack;
    
    public MessagePack() {
        this.registry = new TemplateRegistry(null);
    }
    
    public MessagePack(final MessagePack msgpack) {
        this.registry = new TemplateRegistry(msgpack.registry);
    }
    
    protected MessagePack(final TemplateRegistry registry) {
        this.registry = registry;
    }
    
    public void setClassLoader(final ClassLoader cl) {
        this.registry.setClassLoader(cl);
    }
    
    public Packer createPacker(final OutputStream out) {
        return new MessagePackPacker(this, out);
    }
    
    public BufferPacker createBufferPacker() {
        return new MessagePackBufferPacker(this);
    }
    
    public BufferPacker createBufferPacker(final int bufferSize) {
        return new MessagePackBufferPacker(this, bufferSize);
    }
    
    public Unpacker createUnpacker(final InputStream in) {
        return new MessagePackUnpacker(this, in);
    }
    
    public BufferUnpacker createBufferUnpacker() {
        return new MessagePackBufferUnpacker(this);
    }
    
    public BufferUnpacker createBufferUnpacker(final byte[] bytes) {
        return this.createBufferUnpacker().wrap(bytes);
    }
    
    public BufferUnpacker createBufferUnpacker(final byte[] bytes, final int off, final int len) {
        return this.createBufferUnpacker().wrap(bytes, off, len);
    }
    
    public BufferUnpacker createBufferUnpacker(final ByteBuffer buffer) {
        return this.createBufferUnpacker().wrap(buffer);
    }
    
    public <T> byte[] write(final T v) throws IOException {
        final BufferPacker pk = this.createBufferPacker();
        if (v == null) {
            pk.writeNil();
        }
        else {
            final Template<T> tmpl = this.registry.lookup(v.getClass());
            tmpl.write(pk, v);
        }
        return pk.toByteArray();
    }
    
    public <T> byte[] write(final T v, final Template<T> template) throws IOException {
        final BufferPacker pk = this.createBufferPacker();
        template.write(pk, v);
        return pk.toByteArray();
    }
    
    public <T> void write(final OutputStream out, final T v) throws IOException {
        final Packer pk = this.createPacker(out);
        if (v == null) {
            pk.writeNil();
        }
        else {
            final Template<T> tmpl = this.registry.lookup(v.getClass());
            tmpl.write(pk, v);
        }
    }
    
    public <T> void write(final OutputStream out, final T v, final Template<T> template) throws IOException {
        final Packer pk = this.createPacker(out);
        template.write(pk, v);
    }
    
    public byte[] write(final Value v) throws IOException {
        final BufferPacker pk = this.createBufferPacker();
        pk.write(v);
        return pk.toByteArray();
    }
    
    public Value read(final byte[] bytes) throws IOException {
        return this.read(bytes, 0, bytes.length);
    }
    
    public Value read(final byte[] bytes, final int off, final int len) throws IOException {
        return this.createBufferUnpacker(bytes, off, len).readValue();
    }
    
    public Value read(final ByteBuffer buffer) throws IOException {
        return this.createBufferUnpacker(buffer).readValue();
    }
    
    public Value read(final InputStream in) throws IOException {
        return this.createUnpacker(in).readValue();
    }
    
    public <T> T read(final byte[] bytes, final T v) throws IOException {
        final Template<T> tmpl = this.registry.lookup(v.getClass());
        return this.read(bytes, v, tmpl);
    }
    
    public <T> T read(final byte[] bytes, final Template<T> tmpl) throws IOException {
        return this.read(bytes, (T)null, tmpl);
    }
    
    public <T> T read(final byte[] bytes, final Class<T> c) throws IOException {
        final Template<T> tmpl = this.registry.lookup(c);
        return this.read(bytes, (T)null, tmpl);
    }
    
    public <T> T read(final byte[] bytes, final T v, final Template<T> tmpl) throws IOException {
        final BufferUnpacker u = this.createBufferUnpacker(bytes);
        return tmpl.read(u, v);
    }
    
    public <T> T read(final ByteBuffer b, final T v) throws IOException {
        final Template<T> tmpl = this.registry.lookup(v.getClass());
        return this.read(b, v, tmpl);
    }
    
    public <T> T read(final ByteBuffer b, final Template<T> tmpl) throws IOException {
        return this.read(b, (T)null, tmpl);
    }
    
    public <T> T read(final ByteBuffer b, final Class<T> c) throws IOException {
        final Template<T> tmpl = this.registry.lookup(c);
        return this.read(b, (T)null, tmpl);
    }
    
    public <T> T read(final ByteBuffer b, final T v, final Template<T> tmpl) throws IOException {
        final BufferUnpacker u = this.createBufferUnpacker(b);
        return tmpl.read(u, v);
    }
    
    public <T> T read(final InputStream in, final T v) throws IOException {
        final Template<T> tmpl = this.registry.lookup(v.getClass());
        return this.read(in, v, tmpl);
    }
    
    public <T> T read(final InputStream in, final Template<T> tmpl) throws IOException {
        return this.read(in, (T)null, tmpl);
    }
    
    public <T> T read(final InputStream in, final Class<T> c) throws IOException {
        final Template<T> tmpl = this.registry.lookup(c);
        return this.read(in, (T)null, tmpl);
    }
    
    public <T> T read(final InputStream in, final T v, final Template<T> tmpl) throws IOException {
        final Unpacker u = this.createUnpacker(in);
        return tmpl.read(u, v);
    }
    
    public <T> T convert(final Value v, final T to) throws IOException {
        final Template<T> tmpl = this.registry.lookup(to.getClass());
        return tmpl.read(new Converter(this, v), to);
    }
    
    public <T> T convert(final Value v, final Class<T> c) throws IOException {
        final Template<T> tmpl = this.registry.lookup(c);
        return tmpl.read(new Converter(this, v), null);
    }
    
    public <T> Value unconvert(final T v) throws IOException {
        final Unconverter pk = new Unconverter(this);
        if (v == null) {
            pk.writeNil();
        }
        else {
            final Template<T> tmpl = this.registry.lookup(v.getClass());
            tmpl.write(pk, v);
        }
        return pk.getResult();
    }
    
    public void register(final Class<?> type) {
        this.registry.register(type);
    }
    
    public <T> void register(final Class<T> type, final Template<T> template) {
        this.registry.register(type, template);
    }
    
    public boolean unregister(final Class<?> type) {
        return this.registry.unregister(type);
    }
    
    public void unregister() {
        this.registry.unregister();
    }
    
    public <T> Template<T> lookup(final Class<T> type) {
        return this.registry.lookup(type);
    }
    
    public Template<?> lookup(final Type type) {
        return this.registry.lookup(type);
    }
    
    @Deprecated
    public static byte[] pack(final Object v) throws IOException {
        return MessagePack.globalMessagePack.write(v);
    }
    
    @Deprecated
    public static void pack(final OutputStream out, final Object v) throws IOException {
        MessagePack.globalMessagePack.write(out, v);
    }
    
    @Deprecated
    public static <T> byte[] pack(final T v, final Template<T> template) throws IOException {
        return MessagePack.globalMessagePack.write(v, (Template<Object>)template);
    }
    
    @Deprecated
    public static <T> void pack(final OutputStream out, final T v, final Template<T> template) throws IOException {
        MessagePack.globalMessagePack.write(out, v, (Template<Object>)template);
    }
    
    @Deprecated
    public static Value unpack(final byte[] bytes) throws IOException {
        return MessagePack.globalMessagePack.read(bytes);
    }
    
    @Deprecated
    public static <T> T unpack(final byte[] bytes, final Template<T> template) throws IOException {
        final BufferUnpacker u = new MessagePackBufferUnpacker(MessagePack.globalMessagePack).wrap(bytes);
        return template.read(u, null);
    }
    
    @Deprecated
    public static <T> T unpack(final byte[] bytes, final Template<T> template, final T to) throws IOException {
        final BufferUnpacker u = new MessagePackBufferUnpacker(MessagePack.globalMessagePack).wrap(bytes);
        return template.read(u, to);
    }
    
    @Deprecated
    public static <T> T unpack(final byte[] bytes, final Class<T> klass) throws IOException {
        return (T)MessagePack.globalMessagePack.read(bytes, (Class<Object>)klass);
    }
    
    @Deprecated
    public static <T> T unpack(final byte[] bytes, final T to) throws IOException {
        return (T)MessagePack.globalMessagePack.read(bytes, (Object)to);
    }
    
    @Deprecated
    public static Value unpack(final InputStream in) throws IOException {
        return MessagePack.globalMessagePack.read(in);
    }
    
    @Deprecated
    public static <T> T unpack(final InputStream in, final Template<T> tmpl) throws IOException, MessageTypeException {
        return tmpl.read(new MessagePackUnpacker(MessagePack.globalMessagePack, in), null);
    }
    
    @Deprecated
    public static <T> T unpack(final InputStream in, final Template<T> tmpl, final T to) throws IOException, MessageTypeException {
        return tmpl.read(new MessagePackUnpacker(MessagePack.globalMessagePack, in), to);
    }
    
    @Deprecated
    public static <T> T unpack(final InputStream in, final Class<T> klass) throws IOException {
        return (T)MessagePack.globalMessagePack.read(in, (Class<Object>)klass);
    }
    
    @Deprecated
    public static <T> T unpack(final InputStream in, final T to) throws IOException {
        return (T)MessagePack.globalMessagePack.read(in, (Object)to);
    }
    
    static {
        globalMessagePack = new MessagePack();
    }
}
