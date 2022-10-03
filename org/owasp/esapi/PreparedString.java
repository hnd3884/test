package org.owasp.esapi;

import java.util.ArrayList;
import org.owasp.esapi.codecs.Codec;

public class PreparedString
{
    char parameterCharacter;
    Codec codec;
    String[] parameters;
    ArrayList parts;
    private static final char[] IMMUNE;
    
    public PreparedString(final String template, final Codec codec) {
        this.parameterCharacter = '?';
        this.codec = null;
        this.parameters = null;
        this.parts = new ArrayList();
        this.codec = codec;
        this.split(template, this.parameterCharacter);
    }
    
    public PreparedString(final String template, final char parameterCharacter, final Codec codec) {
        this.parameterCharacter = '?';
        this.codec = null;
        this.parameters = null;
        this.parts = new ArrayList();
        this.codec = codec;
        this.split(template, this.parameterCharacter = parameterCharacter);
    }
    
    private void split(final String str, final char c) {
        int index = 0;
        int pcount = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == c) {
                ++pcount;
                this.parts.add(str.substring(index, i));
                index = i + 1;
            }
        }
        this.parts.add(str.substring(index));
        this.parameters = new String[pcount];
    }
    
    public void set(final int index, final String value) {
        if (index < 1 || index > this.parameters.length) {
            throw new IllegalArgumentException("Attempt to set parameter " + index + " on a PreparedString with only " + this.parameters.length + " placeholders");
        }
        final String encoded = this.codec.encode(PreparedString.IMMUNE, value);
        this.parameters[index - 1] = encoded;
    }
    
    public void set(final int index, final String value, final Codec codec) {
        if (index < 1 || index > this.parameters.length) {
            throw new IllegalArgumentException("Attempt to set parameter " + index + " on a PreparedString with only " + this.parameters.length + " placeholders");
        }
        final String encoded = codec.encode(PreparedString.IMMUNE, value);
        this.parameters[index - 1] = encoded;
    }
    
    @Override
    public String toString() {
        for (int ix = 0; ix < this.parameters.length; ++ix) {
            if (this.parameters[ix] == null) {
                throw new RuntimeException("Attempt to render PreparedString without setting parameter " + (ix + 1));
            }
        }
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        for (int p = 0; p < this.parts.size(); ++p) {
            sb.append(this.parts.get(p));
            if (i < this.parameters.length) {
                sb.append(this.parameters[i++]);
            }
        }
        return sb.toString();
    }
    
    static {
        IMMUNE = new char[0];
    }
}
