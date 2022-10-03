package org.apache.xmlbeans.soap;

import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.PrefixResolver;
import java.util.Iterator;
import java.util.List;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.common.XmlWhitespace;
import javax.xml.namespace.QName;

public final class SOAPArrayType
{
    private QName _type;
    private int[] _ranks;
    private int[] _dimensions;
    private static int[] EMPTY_INT_ARRAY;
    
    public boolean isSameRankAs(final SOAPArrayType otherType) {
        if (this._ranks.length != otherType._ranks.length) {
            return false;
        }
        for (int i = 0; i < this._ranks.length; ++i) {
            if (this._ranks[i] != otherType._ranks[i]) {
                return false;
            }
        }
        return this._dimensions.length == otherType._dimensions.length;
    }
    
    public static int[] parseSoap11Index(String inbraces) {
        inbraces = XmlWhitespace.collapse(inbraces, 3);
        if (!inbraces.startsWith("[") || !inbraces.endsWith("]")) {
            throw new IllegalArgumentException("Misformed SOAP 1.1 index: must be contained in braces []");
        }
        return internalParseCommaIntString(inbraces.substring(1, inbraces.length() - 1));
    }
    
    private static int[] internalParseCommaIntString(final String csl) {
        final List dimStrings = new ArrayList();
        int i = 0;
        while (true) {
            final int j = csl.indexOf(44, i);
            if (j < 0) {
                break;
            }
            dimStrings.add(csl.substring(i, j));
            i = j + 1;
        }
        dimStrings.add(csl.substring(i));
        final int[] result = new int[dimStrings.size()];
        i = 0;
        final Iterator it = dimStrings.iterator();
        while (it.hasNext()) {
            final String dimString = XmlWhitespace.collapse(it.next(), 3);
            if (dimString.equals("*") || dimString.equals("")) {
                result[i] = -1;
            }
            else {
                try {
                    result[i] = Integer.parseInt(dimString);
                }
                catch (final Exception e) {
                    throw new XmlValueOutOfRangeException("Malformed integer in SOAP array index");
                }
            }
            ++i;
        }
        return result;
    }
    
    public SOAPArrayType(final String s, final PrefixResolver m) {
        final int firstbrace = s.indexOf(91);
        if (firstbrace < 0) {
            throw new XmlValueOutOfRangeException();
        }
        final String firstpart = XmlWhitespace.collapse(s.substring(0, firstbrace), 3);
        final int firstcolon = firstpart.indexOf(58);
        String prefix = "";
        if (firstcolon >= 0) {
            prefix = firstpart.substring(0, firstcolon);
        }
        final String uri = m.getNamespaceForPrefix(prefix);
        if (uri == null) {
            throw new XmlValueOutOfRangeException();
        }
        this._type = QNameHelper.forLNS(firstpart.substring(firstcolon + 1), uri);
        this.initDimensions(s, firstbrace);
    }
    
    public SOAPArrayType(final QName name, String dimensions) {
        final int firstbrace = dimensions.indexOf(91);
        if (firstbrace < 0) {
            this._type = name;
            this._ranks = SOAPArrayType.EMPTY_INT_ARRAY;
            dimensions = XmlWhitespace.collapse(dimensions, 3);
            final String[] dimStrings = dimensions.split(" ");
            for (int i = 0; i < dimStrings.length; ++i) {
                final String dimString = dimStrings[i];
                if (dimString.equals("*")) {
                    this._dimensions[i] = -1;
                }
                else {
                    try {
                        this._dimensions[i] = Integer.parseInt(dimStrings[i]);
                    }
                    catch (final Exception e) {
                        throw new XmlValueOutOfRangeException();
                    }
                }
            }
        }
        else {
            this._type = name;
            this.initDimensions(dimensions, firstbrace);
        }
    }
    
    public SOAPArrayType(final SOAPArrayType nested, final int[] dimensions) {
        this._type = nested._type;
        this._ranks = new int[nested._ranks.length + 1];
        System.arraycopy(nested._ranks, 0, this._ranks, 0, nested._ranks.length);
        this._ranks[this._ranks.length - 1] = nested._dimensions.length;
        System.arraycopy(dimensions, 0, this._dimensions = new int[dimensions.length], 0, dimensions.length);
    }
    
    private void initDimensions(final String s, final int firstbrace) {
        final List braces = new ArrayList();
        int lastbrace = -1;
        for (int i = firstbrace; i >= 0; i = s.indexOf(91, lastbrace)) {
            lastbrace = s.indexOf(93, i);
            if (lastbrace < 0) {
                throw new XmlValueOutOfRangeException();
            }
            braces.add(s.substring(i + 1, lastbrace));
        }
        final String trailer = s.substring(lastbrace + 1);
        if (!XmlWhitespace.isAllSpace(trailer)) {
            throw new XmlValueOutOfRangeException();
        }
        this._ranks = new int[braces.size() - 1];
        for (int j = 0; j < this._ranks.length; ++j) {
            final String commas = braces.get(j);
            int commacount = 0;
            for (int k = 0; k < commas.length(); ++k) {
                final char ch = commas.charAt(k);
                if (ch == ',') {
                    ++commacount;
                }
                else if (!XmlWhitespace.isSpace(ch)) {
                    throw new XmlValueOutOfRangeException();
                }
            }
            this._ranks[j] = commacount + 1;
        }
        this._dimensions = internalParseCommaIntString(braces.get(braces.size() - 1));
    }
    
    public QName getQName() {
        return this._type;
    }
    
    public int[] getRanks() {
        final int[] result = new int[this._ranks.length];
        System.arraycopy(this._ranks, 0, result, 0, result.length);
        return result;
    }
    
    public int[] getDimensions() {
        final int[] result = new int[this._dimensions.length];
        System.arraycopy(this._dimensions, 0, result, 0, result.length);
        return result;
    }
    
    public boolean containsNestedArrays() {
        return this._ranks.length > 0;
    }
    
    public String soap11DimensionString() {
        return this.soap11DimensionString(this._dimensions);
    }
    
    public String soap11DimensionString(final int[] actualDimensions) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this._ranks.length; ++i) {
            sb.append('[');
            for (int j = 1; j < this._ranks[i]; ++j) {
                sb.append(',');
            }
            sb.append(']');
        }
        sb.append('[');
        for (int i = 0; i < actualDimensions.length; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            if (actualDimensions[i] >= 0) {
                sb.append(actualDimensions[i]);
            }
        }
        sb.append(']');
        return sb.toString();
    }
    
    private SOAPArrayType() {
    }
    
    public static SOAPArrayType newSoap12Array(final QName itemType, String arraySize) {
        final int[] ranks = SOAPArrayType.EMPTY_INT_ARRAY;
        arraySize = XmlWhitespace.collapse(arraySize, 3);
        final String[] dimStrings = arraySize.split(" ");
        final int[] dimensions = new int[dimStrings.length];
        for (int i = 0; i < dimStrings.length; ++i) {
            final String dimString = dimStrings[i];
            if (i == 0 && dimString.equals("*")) {
                dimensions[i] = -1;
            }
            else {
                try {
                    dimensions[i] = Integer.parseInt(dimStrings[i]);
                }
                catch (final Exception e) {
                    throw new XmlValueOutOfRangeException();
                }
            }
        }
        final SOAPArrayType sot = new SOAPArrayType();
        sot._ranks = ranks;
        sot._type = itemType;
        sot._dimensions = dimensions;
        return sot;
    }
    
    public String soap12DimensionString(final int[] actualDimensions) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < actualDimensions.length; ++i) {
            if (i > 0) {
                sb.append(' ');
            }
            if (actualDimensions[i] >= 0) {
                sb.append(actualDimensions[i]);
            }
        }
        return sb.toString();
    }
    
    public SOAPArrayType nestedArrayType() {
        if (!this.containsNestedArrays()) {
            throw new IllegalStateException();
        }
        final SOAPArrayType result = new SOAPArrayType();
        result._type = this._type;
        result._ranks = new int[this._ranks.length - 1];
        System.arraycopy(this._ranks, 0, result._ranks, 0, result._ranks.length);
        result._dimensions = new int[this._ranks[this._ranks.length - 1]];
        for (int i = 0; i < result._dimensions.length; ++i) {
            result._dimensions[i] = -1;
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        return this._type.hashCode() + this._dimensions.length + this._ranks.length + ((this._dimensions.length == 0) ? 0 : this._dimensions[0]);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }
        final SOAPArrayType sat = (SOAPArrayType)obj;
        if (!this._type.equals(sat._type)) {
            return false;
        }
        if (this._ranks.length != sat._ranks.length) {
            return false;
        }
        if (this._dimensions.length != sat._dimensions.length) {
            return false;
        }
        for (int i = 0; i < this._ranks.length; ++i) {
            if (this._ranks[i] != sat._ranks[i]) {
                return false;
            }
        }
        for (int i = 0; i < this._dimensions.length; ++i) {
            if (this._dimensions[i] != sat._dimensions[i]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        SOAPArrayType.EMPTY_INT_ARRAY = new int[0];
    }
}
