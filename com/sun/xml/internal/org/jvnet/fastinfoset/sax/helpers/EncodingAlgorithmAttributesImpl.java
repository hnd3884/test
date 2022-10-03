package com.sun.xml.internal.org.jvnet.fastinfoset.sax.helpers;

import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import java.io.IOException;
import org.xml.sax.Attributes;
import java.util.Map;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;

public class EncodingAlgorithmAttributesImpl implements EncodingAlgorithmAttributes
{
    private static final int DEFAULT_CAPACITY = 8;
    private static final int URI_OFFSET = 0;
    private static final int LOCALNAME_OFFSET = 1;
    private static final int QNAME_OFFSET = 2;
    private static final int TYPE_OFFSET = 3;
    private static final int VALUE_OFFSET = 4;
    private static final int ALGORITHMURI_OFFSET = 5;
    private static final int SIZE = 6;
    private Map _registeredEncodingAlgorithms;
    private int _length;
    private String[] _data;
    private int[] _algorithmIds;
    private Object[] _algorithmData;
    private String[] _alphabets;
    private boolean[] _toIndex;
    
    public EncodingAlgorithmAttributesImpl() {
        this(null, null);
    }
    
    public EncodingAlgorithmAttributesImpl(final Attributes attributes) {
        this(null, attributes);
    }
    
    public EncodingAlgorithmAttributesImpl(final Map registeredEncodingAlgorithms, final Attributes attributes) {
        this._data = new String[48];
        this._algorithmIds = new int[8];
        this._algorithmData = new Object[8];
        this._alphabets = new String[8];
        this._toIndex = new boolean[8];
        this._registeredEncodingAlgorithms = registeredEncodingAlgorithms;
        if (attributes != null) {
            if (attributes instanceof EncodingAlgorithmAttributes) {
                this.setAttributes((EncodingAlgorithmAttributes)attributes);
            }
            else {
                this.setAttributes(attributes);
            }
        }
    }
    
    public final void clear() {
        for (int i = 0; i < this._length; ++i) {
            this._data[i * 6 + 4] = null;
            this._algorithmData[i] = null;
        }
        this._length = 0;
    }
    
    public void addAttribute(final String URI, final String localName, final String qName, final String type, final String value) {
        if (this._length >= this._algorithmData.length) {
            this.resize();
        }
        int i = this._length * 6;
        this._data[i++] = this.replaceNull(URI);
        this._data[i++] = this.replaceNull(localName);
        this._data[i++] = this.replaceNull(qName);
        this._data[i++] = this.replaceNull(type);
        this._data[i++] = this.replaceNull(value);
        this._toIndex[this._length] = false;
        this._alphabets[this._length] = null;
        ++this._length;
    }
    
    public void addAttribute(final String URI, final String localName, final String qName, final String type, final String value, final boolean index, final String alphabet) {
        if (this._length >= this._algorithmData.length) {
            this.resize();
        }
        int i = this._length * 6;
        this._data[i++] = this.replaceNull(URI);
        this._data[i++] = this.replaceNull(localName);
        this._data[i++] = this.replaceNull(qName);
        this._data[i++] = this.replaceNull(type);
        this._data[i++] = this.replaceNull(value);
        this._toIndex[this._length] = index;
        this._alphabets[this._length] = alphabet;
        ++this._length;
    }
    
    public void addAttributeWithBuiltInAlgorithmData(final String URI, final String localName, final String qName, final int builtInAlgorithmID, final Object algorithmData) {
        if (this._length >= this._algorithmData.length) {
            this.resize();
        }
        int i = this._length * 6;
        this._data[i++] = this.replaceNull(URI);
        this._data[i++] = this.replaceNull(localName);
        this._data[i++] = this.replaceNull(qName);
        this._data[i++] = "CDATA";
        this._data[i++] = "";
        this._data[i++] = null;
        this._algorithmIds[this._length] = builtInAlgorithmID;
        this._algorithmData[this._length] = algorithmData;
        this._toIndex[this._length] = false;
        this._alphabets[this._length] = null;
        ++this._length;
    }
    
    public void addAttributeWithAlgorithmData(final String URI, final String localName, final String qName, final String algorithmURI, final int algorithmID, final Object algorithmData) {
        if (this._length >= this._algorithmData.length) {
            this.resize();
        }
        int i = this._length * 6;
        this._data[i++] = this.replaceNull(URI);
        this._data[i++] = this.replaceNull(localName);
        this._data[i++] = this.replaceNull(qName);
        this._data[i++] = "CDATA";
        this._data[i++] = "";
        this._data[i++] = algorithmURI;
        this._algorithmIds[this._length] = algorithmID;
        this._algorithmData[this._length] = algorithmData;
        this._toIndex[this._length] = false;
        this._alphabets[this._length] = null;
        ++this._length;
    }
    
    public void replaceWithAttributeAlgorithmData(final int index, final String algorithmURI, final int algorithmID, final Object algorithmData) {
        if (index < 0 || index >= this._length) {
            return;
        }
        final int i = index * 6;
        this._data[i + 4] = null;
        this._data[i + 5] = algorithmURI;
        this._algorithmIds[index] = algorithmID;
        this._algorithmData[index] = algorithmData;
        this._toIndex[index] = false;
        this._alphabets[index] = null;
    }
    
    public void setAttributes(final Attributes atts) {
        this._length = atts.getLength();
        if (this._length > 0) {
            if (this._length >= this._algorithmData.length) {
                this.resizeNoCopy();
            }
            int index = 0;
            for (int i = 0; i < this._length; ++i) {
                this._data[index++] = atts.getURI(i);
                this._data[index++] = atts.getLocalName(i);
                this._data[index++] = atts.getQName(i);
                this._data[index++] = atts.getType(i);
                this._data[index++] = atts.getValue(i);
                ++index;
                this._toIndex[i] = false;
                this._alphabets[i] = null;
            }
        }
    }
    
    public void setAttributes(final EncodingAlgorithmAttributes atts) {
        this._length = atts.getLength();
        if (this._length > 0) {
            if (this._length >= this._algorithmData.length) {
                this.resizeNoCopy();
            }
            int index = 0;
            for (int i = 0; i < this._length; ++i) {
                this._data[index++] = atts.getURI(i);
                this._data[index++] = atts.getLocalName(i);
                this._data[index++] = atts.getQName(i);
                this._data[index++] = atts.getType(i);
                this._data[index++] = atts.getValue(i);
                this._data[index++] = atts.getAlgorithmURI(i);
                this._algorithmIds[i] = atts.getAlgorithmIndex(i);
                this._algorithmData[i] = atts.getAlgorithmData(i);
                this._toIndex[i] = false;
                this._alphabets[i] = null;
            }
        }
    }
    
    @Override
    public final int getLength() {
        return this._length;
    }
    
    @Override
    public final String getLocalName(final int index) {
        if (index >= 0 && index < this._length) {
            return this._data[index * 6 + 1];
        }
        return null;
    }
    
    @Override
    public final String getQName(final int index) {
        if (index >= 0 && index < this._length) {
            return this._data[index * 6 + 2];
        }
        return null;
    }
    
    @Override
    public final String getType(final int index) {
        if (index >= 0 && index < this._length) {
            return this._data[index * 6 + 3];
        }
        return null;
    }
    
    @Override
    public final String getURI(final int index) {
        if (index >= 0 && index < this._length) {
            return this._data[index * 6 + 0];
        }
        return null;
    }
    
    @Override
    public final String getValue(final int index) {
        if (index < 0 || index >= this._length) {
            return null;
        }
        final String value = this._data[index * 6 + 4];
        if (value != null) {
            return value;
        }
        if (this._algorithmData[index] == null || this._registeredEncodingAlgorithms == null) {
            return null;
        }
        try {
            return this._data[index * 6 + 4] = this.convertEncodingAlgorithmDataToString(this._algorithmIds[index], this._data[index * 6 + 5], this._algorithmData[index]).toString();
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
        for (int index = 0; index < this._length; ++index) {
            if (qName.equals(this._data[index * 6 + 2])) {
                return index;
            }
        }
        return -1;
    }
    
    @Override
    public final String getType(final String qName) {
        final int index = this.getIndex(qName);
        if (index >= 0) {
            return this._data[index * 6 + 3];
        }
        return null;
    }
    
    @Override
    public final String getValue(final String qName) {
        final int index = this.getIndex(qName);
        if (index >= 0) {
            return this.getValue(index);
        }
        return null;
    }
    
    @Override
    public final int getIndex(final String uri, final String localName) {
        for (int index = 0; index < this._length; ++index) {
            if (localName.equals(this._data[index * 6 + 1]) && uri.equals(this._data[index * 6 + 0])) {
                return index;
            }
        }
        return -1;
    }
    
    @Override
    public final String getType(final String uri, final String localName) {
        final int index = this.getIndex(uri, localName);
        if (index >= 0) {
            return this._data[index * 6 + 3];
        }
        return null;
    }
    
    @Override
    public final String getValue(final String uri, final String localName) {
        final int index = this.getIndex(uri, localName);
        if (index >= 0) {
            return this.getValue(index);
        }
        return null;
    }
    
    @Override
    public final String getAlgorithmURI(final int index) {
        if (index >= 0 && index < this._length) {
            return this._data[index * 6 + 5];
        }
        return null;
    }
    
    @Override
    public final int getAlgorithmIndex(final int index) {
        if (index >= 0 && index < this._length) {
            return this._algorithmIds[index];
        }
        return -1;
    }
    
    @Override
    public final Object getAlgorithmData(final int index) {
        if (index >= 0 && index < this._length) {
            return this._algorithmData[index];
        }
        return null;
    }
    
    @Override
    public final String getAlpababet(final int index) {
        if (index >= 0 && index < this._length) {
            return this._alphabets[index];
        }
        return null;
    }
    
    @Override
    public final boolean getToIndex(final int index) {
        return index >= 0 && index < this._length && this._toIndex[index];
    }
    
    private final String replaceNull(final String s) {
        return (s != null) ? s : "";
    }
    
    private final void resizeNoCopy() {
        final int newLength = this._length * 3 / 2 + 1;
        this._data = new String[newLength * 6];
        this._algorithmIds = new int[newLength];
        this._algorithmData = new Object[newLength];
    }
    
    private final void resize() {
        final int newLength = this._length * 3 / 2 + 1;
        final String[] data = new String[newLength * 6];
        final int[] algorithmIds = new int[newLength];
        final Object[] algorithmData = new Object[newLength];
        final String[] alphabets = new String[newLength];
        final boolean[] toIndex = new boolean[newLength];
        System.arraycopy(this._data, 0, data, 0, this._length * 6);
        System.arraycopy(this._algorithmIds, 0, algorithmIds, 0, this._length);
        System.arraycopy(this._algorithmData, 0, algorithmData, 0, this._length);
        System.arraycopy(this._alphabets, 0, alphabets, 0, this._length);
        System.arraycopy(this._toIndex, 0, toIndex, 0, this._length);
        this._data = data;
        this._algorithmIds = algorithmIds;
        this._algorithmData = algorithmData;
        this._alphabets = alphabets;
        this._toIndex = toIndex;
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
