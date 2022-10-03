package com.sun.security.sasl.util;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import sun.misc.HexDumpEncoder;
import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
import javax.security.sasl.SaslException;
import java.util.logging.Level;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractSaslImpl
{
    protected boolean completed;
    protected boolean privacy;
    protected boolean integrity;
    protected byte[] qop;
    protected byte allQop;
    protected byte[] strength;
    protected int sendMaxBufSize;
    protected int recvMaxBufSize;
    protected int rawSendSize;
    protected String myClassName;
    private static final String SASL_LOGGER_NAME = "javax.security.sasl";
    protected static final String MAX_SEND_BUF = "javax.security.sasl.sendmaxbuffer";
    protected static final Logger logger;
    protected static final byte NO_PROTECTION = 1;
    protected static final byte INTEGRITY_ONLY_PROTECTION = 2;
    protected static final byte PRIVACY_PROTECTION = 4;
    protected static final byte LOW_STRENGTH = 1;
    protected static final byte MEDIUM_STRENGTH = 2;
    protected static final byte HIGH_STRENGTH = 4;
    private static final byte[] DEFAULT_QOP;
    private static final String[] QOP_TOKENS;
    private static final byte[] QOP_MASKS;
    private static final byte[] DEFAULT_STRENGTH;
    private static final String[] STRENGTH_TOKENS;
    private static final byte[] STRENGTH_MASKS;
    
    protected AbstractSaslImpl(final Map<String, ?> map, final String myClassName) throws SaslException {
        this.completed = false;
        this.privacy = false;
        this.integrity = false;
        this.sendMaxBufSize = 0;
        this.recvMaxBufSize = 65536;
        this.myClassName = myClassName;
        if (map != null) {
            final String s;
            this.qop = parseQop(s = (String)map.get("javax.security.sasl.qop"));
            AbstractSaslImpl.logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL01:Preferred qop property: {0}", s);
            this.allQop = combineMasks(this.qop);
            if (AbstractSaslImpl.logger.isLoggable(Level.FINE)) {
                AbstractSaslImpl.logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL02:Preferred qop mask: {0}", new Byte(this.allQop));
                if (this.qop.length > 0) {
                    final StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < this.qop.length; ++i) {
                        sb.append(Byte.toString(this.qop[i]));
                        sb.append(' ');
                    }
                    AbstractSaslImpl.logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL03:Preferred qops : {0}", sb.toString());
                }
            }
            final String s2;
            this.strength = parseStrength(s2 = (String)map.get("javax.security.sasl.strength"));
            AbstractSaslImpl.logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL04:Preferred strength property: {0}", s2);
            if (AbstractSaslImpl.logger.isLoggable(Level.FINE) && this.strength.length > 0) {
                final StringBuffer sb2 = new StringBuffer();
                for (int j = 0; j < this.strength.length; ++j) {
                    sb2.append(Byte.toString(this.strength[j]));
                    sb2.append(' ');
                }
                AbstractSaslImpl.logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL05:Cipher strengths: {0}", sb2.toString());
            }
            final String s3 = (String)map.get("javax.security.sasl.maxbuffer");
            if (s3 != null) {
                try {
                    AbstractSaslImpl.logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL06:Max receive buffer size: {0}", s3);
                    this.recvMaxBufSize = Integer.parseInt(s3);
                }
                catch (final NumberFormatException ex) {
                    throw new SaslException("Property must be string representation of integer: javax.security.sasl.maxbuffer");
                }
            }
            final String s4 = (String)map.get("javax.security.sasl.sendmaxbuffer");
            if (s4 != null) {
                try {
                    AbstractSaslImpl.logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL07:Max send buffer size: {0}", s4);
                    this.sendMaxBufSize = Integer.parseInt(s4);
                }
                catch (final NumberFormatException ex2) {
                    throw new SaslException("Property must be string representation of integer: javax.security.sasl.sendmaxbuffer");
                }
            }
        }
        else {
            this.qop = AbstractSaslImpl.DEFAULT_QOP;
            this.allQop = 1;
            this.strength = AbstractSaslImpl.STRENGTH_MASKS;
        }
    }
    
    public boolean isComplete() {
        return this.completed;
    }
    
    public Object getNegotiatedProperty(final String s) {
        if (!this.completed) {
            throw new IllegalStateException("SASL authentication not completed");
        }
        switch (s) {
            case "javax.security.sasl.qop": {
                if (this.privacy) {
                    return "auth-conf";
                }
                if (this.integrity) {
                    return "auth-int";
                }
                return "auth";
            }
            case "javax.security.sasl.maxbuffer": {
                return Integer.toString(this.recvMaxBufSize);
            }
            case "javax.security.sasl.rawsendsize": {
                return Integer.toString(this.rawSendSize);
            }
            case "javax.security.sasl.sendmaxbuffer": {
                return Integer.toString(this.sendMaxBufSize);
            }
            default: {
                return null;
            }
        }
    }
    
    protected static final byte combineMasks(final byte[] array) {
        byte b = 0;
        for (int i = 0; i < array.length; ++i) {
            b |= array[i];
        }
        return b;
    }
    
    protected static final byte findPreferredMask(final byte b, final byte[] array) {
        for (int i = 0; i < array.length; ++i) {
            if ((array[i] & b) != 0x0) {
                return array[i];
            }
        }
        return 0;
    }
    
    private static final byte[] parseQop(final String s) throws SaslException {
        return parseQop(s, null, false);
    }
    
    protected static final byte[] parseQop(final String s, final String[] array, final boolean b) throws SaslException {
        if (s == null) {
            return AbstractSaslImpl.DEFAULT_QOP;
        }
        return parseProp("javax.security.sasl.qop", s, AbstractSaslImpl.QOP_TOKENS, AbstractSaslImpl.QOP_MASKS, array, b);
    }
    
    private static final byte[] parseStrength(final String s) throws SaslException {
        if (s == null) {
            return AbstractSaslImpl.DEFAULT_STRENGTH;
        }
        return parseProp("javax.security.sasl.strength", s, AbstractSaslImpl.STRENGTH_TOKENS, AbstractSaslImpl.STRENGTH_MASKS, null, false);
    }
    
    private static final byte[] parseProp(final String s, final String s2, final String[] array, final byte[] array2, final String[] array3, final boolean b) throws SaslException {
        final StringTokenizer stringTokenizer = new StringTokenizer(s2, ", \t\n");
        final byte[] array4 = new byte[array.length];
        int n = 0;
        while (stringTokenizer.hasMoreTokens() && n < array4.length) {
            final String nextToken = stringTokenizer.nextToken();
            int n2 = 0;
            for (int n3 = 0; n2 == 0 && n3 < array.length; ++n3) {
                if (nextToken.equalsIgnoreCase(array[n3])) {
                    n2 = 1;
                    array4[n++] = array2[n3];
                    if (array3 != null) {
                        array3[n3] = nextToken;
                    }
                }
            }
            if (n2 == 0 && !b) {
                throw new SaslException("Invalid token in " + s + ": " + s2);
            }
        }
        for (int i = n; i < array4.length; ++i) {
            array4[i] = 0;
        }
        return array4;
    }
    
    protected static final void traceOutput(final String s, final String s2, final String s3, final byte[] array) {
        traceOutput(s, s2, s3, array, 0, (array == null) ? 0 : array.length);
    }
    
    protected static final void traceOutput(final String s, final String s2, final String s3, final byte[] array, final int n, int min) {
        try {
            final int n2 = min;
            Level level;
            if (!AbstractSaslImpl.logger.isLoggable(Level.FINEST)) {
                min = Math.min(16, min);
                level = Level.FINER;
            }
            else {
                level = Level.FINEST;
            }
            String string;
            if (array != null) {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(min);
                new HexDumpEncoder().encodeBuffer(new ByteArrayInputStream(array, n, min), byteArrayOutputStream);
                string = byteArrayOutputStream.toString();
            }
            else {
                string = "NULL";
            }
            AbstractSaslImpl.logger.logp(level, s, s2, "{0} ( {1} ): {2}", new Object[] { s3, new Integer(n2), string });
        }
        catch (final Exception ex) {
            AbstractSaslImpl.logger.logp(Level.WARNING, s, s2, "SASLIMPL09:Error generating trace output: {0}", ex);
        }
    }
    
    protected static final int networkByteOrderToInt(final byte[] array, final int n, final int n2) {
        if (n2 > 4) {
            throw new IllegalArgumentException("Cannot handle more than 4 bytes");
        }
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            n3 = (n3 << 8 | (array[n + i] & 0xFF));
        }
        return n3;
    }
    
    protected static final void intToNetworkByteOrder(int n, final byte[] array, final int n2, final int n3) {
        if (n3 > 4) {
            throw new IllegalArgumentException("Cannot handle more than 4 bytes");
        }
        for (int i = n3 - 1; i >= 0; --i) {
            array[n2 + i] = (byte)(n & 0xFF);
            n >>>= 8;
        }
    }
    
    static {
        logger = Logger.getLogger("javax.security.sasl");
        DEFAULT_QOP = new byte[] { 1 };
        QOP_TOKENS = new String[] { "auth-conf", "auth-int", "auth" };
        QOP_MASKS = new byte[] { 4, 2, 1 };
        DEFAULT_STRENGTH = new byte[] { 4, 2, 1 };
        STRENGTH_TOKENS = new String[] { "low", "medium", "high" };
        STRENGTH_MASKS = new byte[] { 1, 2, 4 };
    }
}
