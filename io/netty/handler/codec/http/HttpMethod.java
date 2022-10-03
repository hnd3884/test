package io.netty.handler.codec.http;

import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.AsciiString;

public class HttpMethod implements Comparable<HttpMethod>
{
    public static final HttpMethod OPTIONS;
    public static final HttpMethod GET;
    public static final HttpMethod HEAD;
    public static final HttpMethod POST;
    public static final HttpMethod PUT;
    public static final HttpMethod PATCH;
    public static final HttpMethod DELETE;
    public static final HttpMethod TRACE;
    public static final HttpMethod CONNECT;
    private static final EnumNameMap<HttpMethod> methodMap;
    private final AsciiString name;
    
    public static HttpMethod valueOf(final String name) {
        final HttpMethod result = HttpMethod.methodMap.get(name);
        return (result != null) ? result : new HttpMethod(name);
    }
    
    public HttpMethod(String name) {
        name = ObjectUtil.checkNonEmptyAfterTrim(name, "name");
        for (int i = 0; i < name.length(); ++i) {
            final char c = name.charAt(i);
            if (Character.isISOControl(c) || Character.isWhitespace(c)) {
                throw new IllegalArgumentException("invalid character in name");
            }
        }
        this.name = AsciiString.cached(name);
    }
    
    public String name() {
        return this.name.toString();
    }
    
    public AsciiString asciiName() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        return this.name().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpMethod)) {
            return false;
        }
        final HttpMethod that = (HttpMethod)o;
        return this.name().equals(that.name());
    }
    
    @Override
    public String toString() {
        return this.name.toString();
    }
    
    @Override
    public int compareTo(final HttpMethod o) {
        if (o == this) {
            return 0;
        }
        return this.name().compareTo(o.name());
    }
    
    static {
        OPTIONS = new HttpMethod("OPTIONS");
        GET = new HttpMethod("GET");
        HEAD = new HttpMethod("HEAD");
        POST = new HttpMethod("POST");
        PUT = new HttpMethod("PUT");
        PATCH = new HttpMethod("PATCH");
        DELETE = new HttpMethod("DELETE");
        TRACE = new HttpMethod("TRACE");
        CONNECT = new HttpMethod("CONNECT");
        methodMap = new EnumNameMap<HttpMethod>((EnumNameMap.Node<HttpMethod>[])new EnumNameMap.Node[] { new EnumNameMap.Node(HttpMethod.OPTIONS.toString(), (T)HttpMethod.OPTIONS), new EnumNameMap.Node(HttpMethod.GET.toString(), (T)HttpMethod.GET), new EnumNameMap.Node(HttpMethod.HEAD.toString(), (T)HttpMethod.HEAD), new EnumNameMap.Node(HttpMethod.POST.toString(), (T)HttpMethod.POST), new EnumNameMap.Node(HttpMethod.PUT.toString(), (T)HttpMethod.PUT), new EnumNameMap.Node(HttpMethod.PATCH.toString(), (T)HttpMethod.PATCH), new EnumNameMap.Node(HttpMethod.DELETE.toString(), (T)HttpMethod.DELETE), new EnumNameMap.Node(HttpMethod.TRACE.toString(), (T)HttpMethod.TRACE), new EnumNameMap.Node(HttpMethod.CONNECT.toString(), (T)HttpMethod.CONNECT) });
    }
    
    private static final class EnumNameMap<T>
    {
        private final Node<T>[] values;
        private final int valuesMask;
        
        EnumNameMap(final Node<T>... nodes) {
            this.values = new Node[MathUtil.findNextPositivePowerOfTwo(nodes.length)];
            this.valuesMask = this.values.length - 1;
            for (final Node<T> node : nodes) {
                final int i = hashCode(node.key) & this.valuesMask;
                if (this.values[i] != null) {
                    throw new IllegalArgumentException("index " + i + " collision between values: [" + this.values[i].key + ", " + node.key + ']');
                }
                this.values[i] = node;
            }
        }
        
        T get(final String name) {
            final Node<T> node = this.values[hashCode(name) & this.valuesMask];
            return (node == null || !node.key.equals(name)) ? null : node.value;
        }
        
        private static int hashCode(final String name) {
            return name.hashCode() >>> 6;
        }
        
        private static final class Node<T>
        {
            final String key;
            final T value;
            
            Node(final String key, final T value) {
                this.key = key;
                this.value = value;
            }
        }
    }
}
