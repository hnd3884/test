package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.IOException;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import java.util.Map;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;

public class AttributesHolder implements EncodingAlgorithmAttributes
{
    private static final int DEFAULT_CAPACITY = 8;
    private Map _registeredEncodingAlgorithms;
    private int _attributeCount;
    private QualifiedName[] _names;
    private String[] _values;
    private String[] _algorithmURIs;
    private int[] _algorithmIds;
    private Object[] _algorithmData;
    
    public AttributesHolder() {
        this._names = new QualifiedName[8];
        this._values = new String[8];
        this._algorithmURIs = new String[8];
        this._algorithmIds = new int[8];
        this._algorithmData = new Object[8];
    }
    
    public AttributesHolder(final Map registeredEncodingAlgorithms) {
        this();
        this._registeredEncodingAlgorithms = registeredEncodingAlgorithms;
    }
    
    @Override
    public final int getLength() {
        return this._attributeCount;
    }
    
    @Override
    public final String getLocalName(final int index) {
        return this._names[index].localName;
    }
    
    @Override
    public final String getQName(final int index) {
        return this._names[index].getQNameString();
    }
    
    @Override
    public final String getType(final int index) {
        return "CDATA";
    }
    
    @Override
    public final String getURI(final int index) {
        return this._names[index].namespaceName;
    }
    
    @Override
    public final String getValue(final int index) {
        final String value = this._values[index];
        if (value != null) {
            return value;
        }
        if (this._algorithmData[index] == null || (this._algorithmIds[index] >= 32 && this._registeredEncodingAlgorithms == null)) {
            return null;
        }
        try {
            return this._values[index] = this.convertEncodingAlgorithmDataToString(this._algorithmIds[index], this._algorithmURIs[index], this._algorithmData[index]).toString();
        }
        catch (final IOException e) {
            return null;
        }
        catch (final FastInfosetException e2) {
            return null;
        }
    }
    
    @Override
    public final int getIndex(final String qName) {
        int i = qName.indexOf(58);
        String prefix = "";
        String localName = qName;
        if (i >= 0) {
            prefix = qName.substring(0, i);
            localName = qName.substring(i + 1);
        }
        for (i = 0; i < this._attributeCount; ++i) {
            final QualifiedName name = this._names[i];
            if (localName.equals(name.localName) && prefix.equals(name.prefix)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public final String getType(final String qName) {
        final int index = this.getIndex(qName);
        if (index >= 0) {
            return "CDATA";
        }
        return null;
    }
    
    @Override
    public final String getValue(final String qName) {
        final int index = this.getIndex(qName);
        if (index >= 0) {
            return this._values[index];
        }
        return null;
    }
    
    @Override
    public final int getIndex(final String uri, final String localName) {
        for (int i = 0; i < this._attributeCount; ++i) {
            final QualifiedName name = this._names[i];
            if (localName.equals(name.localName) && uri.equals(name.namespaceName)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public final String getType(final String uri, final String localName) {
        final int index = this.getIndex(uri, localName);
        if (index >= 0) {
            return "CDATA";
        }
        return null;
    }
    
    @Override
    public final String getValue(final String uri, final String localName) {
        final int index = this.getIndex(uri, localName);
        if (index >= 0) {
            return this._values[index];
        }
        return null;
    }
    
    public final void clear() {
        for (int i = 0; i < this._attributeCount; ++i) {
            this._values[i] = null;
            this._algorithmData[i] = null;
        }
        this._attributeCount = 0;
    }
    
    @Override
    public final String getAlgorithmURI(final int index) {
        return this._algorithmURIs[index];
    }
    
    @Override
    public final int getAlgorithmIndex(final int index) {
        return this._algorithmIds[index];
    }
    
    @Override
    public final Object getAlgorithmData(final int index) {
        return this._algorithmData[index];
    }
    
    @Override
    public String getAlpababet(final int index) {
        return null;
    }
    
    @Override
    public boolean getToIndex(final int index) {
        return false;
    }
    
    public final void addAttribute(final QualifiedName name, final String value) {
        if (this._attributeCount == this._names.length) {
            this.resize();
        }
        this._names[this._attributeCount] = name;
        this._values[this._attributeCount++] = value;
    }
    
    public final void addAttributeWithAlgorithmData(final QualifiedName name, final String URI, final int id, final Object data) {
        if (this._attributeCount == this._names.length) {
            this.resize();
        }
        this._names[this._attributeCount] = name;
        this._values[this._attributeCount] = null;
        this._algorithmURIs[this._attributeCount] = URI;
        this._algorithmIds[this._attributeCount] = id;
        this._algorithmData[this._attributeCount++] = data;
    }
    
    public final QualifiedName getQualifiedName(final int index) {
        return this._names[index];
    }
    
    public final String getPrefix(final int index) {
        return this._names[index].prefix;
    }
    
    private final void resize() {
        final int newLength = this._attributeCount * 3 / 2 + 1;
        final QualifiedName[] names = new QualifiedName[newLength];
        final String[] values = new String[newLength];
        final String[] algorithmURIs = new String[newLength];
        final int[] algorithmIds = new int[newLength];
        final Object[] algorithmData = new Object[newLength];
        System.arraycopy(this._names, 0, names, 0, this._attributeCount);
        System.arraycopy(this._values, 0, values, 0, this._attributeCount);
        System.arraycopy(this._algorithmURIs, 0, algorithmURIs, 0, this._attributeCount);
        System.arraycopy(this._algorithmIds, 0, algorithmIds, 0, this._attributeCount);
        System.arraycopy(this._algorithmData, 0, algorithmData, 0, this._attributeCount);
        this._names = names;
        this._values = values;
        this._algorithmURIs = algorithmURIs;
        this._algorithmIds = algorithmIds;
        this._algorithmData = algorithmData;
    }
    
    private final StringBuffer convertEncodingAlgorithmDataToString(final int identifier, final String URI, final Object data) throws FastInfosetException, IOException {
        EncodingAlgorithm ea = null;
        if (identifier < 9) {
            ea = BuiltInEncodingAlgorithmFactory.getAlgorithm(identifier);
        }
        else {
            if (identifier == 9) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
            }
            if (identifier < 32) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
            }
            if (URI == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent") + identifier);
            }
            ea = this._registeredEncodingAlgorithms.get(URI);
            if (ea == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmNotRegistered") + URI);
            }
        }
        final StringBuffer sb = new StringBuffer();
        ea.convertToCharacters(data, sb);
        return sb;
    }
}
