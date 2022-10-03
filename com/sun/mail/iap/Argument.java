package com.sun.mail.iap;

import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import com.sun.mail.util.ASCIIUtility;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class Argument
{
    protected List<Object> items;
    
    public Argument() {
        this.items = new ArrayList<Object>(1);
    }
    
    public Argument append(final Argument arg) {
        this.items.addAll(arg.items);
        return this;
    }
    
    public Argument writeString(final String s) {
        this.items.add(new AString(ASCIIUtility.getBytes(s)));
        return this;
    }
    
    public Argument writeString(final String s, final String charset) throws UnsupportedEncodingException {
        if (charset == null) {
            this.writeString(s);
        }
        else {
            this.items.add(new AString(s.getBytes(charset)));
        }
        return this;
    }
    
    public Argument writeString(final String s, final Charset charset) {
        if (charset == null) {
            this.writeString(s);
        }
        else {
            this.items.add(new AString(s.getBytes(charset)));
        }
        return this;
    }
    
    public Argument writeNString(final String s) {
        if (s == null) {
            this.items.add(new NString(null));
        }
        else {
            this.items.add(new NString(ASCIIUtility.getBytes(s)));
        }
        return this;
    }
    
    public Argument writeNString(final String s, final String charset) throws UnsupportedEncodingException {
        if (s == null) {
            this.items.add(new NString(null));
        }
        else if (charset == null) {
            this.writeString(s);
        }
        else {
            this.items.add(new NString(s.getBytes(charset)));
        }
        return this;
    }
    
    public Argument writeNString(final String s, final Charset charset) {
        if (s == null) {
            this.items.add(new NString(null));
        }
        else if (charset == null) {
            this.writeString(s);
        }
        else {
            this.items.add(new NString(s.getBytes(charset)));
        }
        return this;
    }
    
    public Argument writeBytes(final byte[] b) {
        this.items.add(b);
        return this;
    }
    
    public Argument writeBytes(final ByteArrayOutputStream b) {
        this.items.add(b);
        return this;
    }
    
    public Argument writeBytes(final Literal b) {
        this.items.add(b);
        return this;
    }
    
    public Argument writeAtom(final String s) {
        this.items.add(new Atom(s));
        return this;
    }
    
    public Argument writeNumber(final int i) {
        this.items.add(i);
        return this;
    }
    
    public Argument writeNumber(final long i) {
        this.items.add(i);
        return this;
    }
    
    public Argument writeArgument(final Argument c) {
        this.items.add(c);
        return this;
    }
    
    public void write(final Protocol protocol) throws IOException, ProtocolException {
        final int size = (this.items != null) ? this.items.size() : 0;
        final DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
        for (int i = 0; i < size; ++i) {
            if (i > 0) {
                os.write(32);
            }
            final Object o = this.items.get(i);
            if (o instanceof Atom) {
                os.writeBytes(((Atom)o).string);
            }
            else if (o instanceof Number) {
                os.writeBytes(o.toString());
            }
            else if (o instanceof AString) {
                this.astring(((AString)o).bytes, protocol);
            }
            else if (o instanceof NString) {
                this.nstring(((NString)o).bytes, protocol);
            }
            else if (o instanceof byte[]) {
                this.literal((byte[])o, protocol);
            }
            else if (o instanceof ByteArrayOutputStream) {
                this.literal((ByteArrayOutputStream)o, protocol);
            }
            else if (o instanceof Literal) {
                this.literal((Literal)o, protocol);
            }
            else if (o instanceof Argument) {
                os.write(40);
                ((Argument)o).write(protocol);
                os.write(41);
            }
        }
    }
    
    private void astring(final byte[] bytes, final Protocol protocol) throws IOException, ProtocolException {
        this.nastring(bytes, protocol, false);
    }
    
    private void nstring(final byte[] bytes, final Protocol protocol) throws IOException, ProtocolException {
        if (bytes == null) {
            final DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
            os.writeBytes("NIL");
        }
        else {
            this.nastring(bytes, protocol, true);
        }
    }
    
    private void nastring(final byte[] bytes, final Protocol protocol, final boolean doQuote) throws IOException, ProtocolException {
        final DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
        final int len = bytes.length;
        if (len > 1024) {
            this.literal(bytes, protocol);
            return;
        }
        boolean quote = len == 0 || doQuote;
        boolean escape = false;
        final boolean utf8 = protocol.supportsUtf8();
        for (final byte b : bytes) {
            if (b == 0 || b == 13 || b == 10 || (!utf8 && (b & 0xFF) > 127)) {
                this.literal(bytes, protocol);
                return;
            }
            if (b == 42 || b == 37 || b == 40 || b == 41 || b == 123 || b == 34 || b == 92 || (b & 0xFF) <= 32 || (b & 0xFF) > 127) {
                quote = true;
                if (b == 34 || b == 92) {
                    escape = true;
                }
            }
        }
        if (!quote && bytes.length == 3 && (bytes[0] == 78 || bytes[0] == 110) && (bytes[1] == 73 || bytes[1] == 105) && (bytes[2] == 76 || bytes[2] == 108)) {
            quote = true;
        }
        if (quote) {
            os.write(34);
        }
        if (escape) {
            for (final byte b : bytes) {
                if (b == 34 || b == 92) {
                    os.write(92);
                }
                os.write(b);
            }
        }
        else {
            os.write(bytes);
        }
        if (quote) {
            os.write(34);
        }
    }
    
    private void literal(final byte[] b, final Protocol protocol) throws IOException, ProtocolException {
        this.startLiteral(protocol, b.length).write(b);
    }
    
    private void literal(final ByteArrayOutputStream b, final Protocol protocol) throws IOException, ProtocolException {
        b.writeTo(this.startLiteral(protocol, b.size()));
    }
    
    private void literal(final Literal b, final Protocol protocol) throws IOException, ProtocolException {
        b.writeTo(this.startLiteral(protocol, b.size()));
    }
    
    private OutputStream startLiteral(final Protocol protocol, final int size) throws IOException, ProtocolException {
        final DataOutputStream os = (DataOutputStream)protocol.getOutputStream();
        final boolean nonSync = protocol.supportsNonSyncLiterals();
        os.write(123);
        os.writeBytes(Integer.toString(size));
        if (nonSync) {
            os.writeBytes("+}\r\n");
        }
        else {
            os.writeBytes("}\r\n");
        }
        os.flush();
        if (!nonSync) {
            while (true) {
                final Response r = protocol.readResponse();
                if (r.isContinuation()) {
                    break;
                }
                if (r.isTagged()) {
                    throw new LiteralException(r);
                }
            }
        }
        return os;
    }
}
