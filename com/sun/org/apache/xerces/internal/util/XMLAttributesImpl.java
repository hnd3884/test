package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.xml.internal.stream.XMLBufferListener;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;

public class XMLAttributesImpl implements XMLAttributes, XMLBufferListener
{
    protected static final int TABLE_SIZE = 101;
    protected static final int MAX_HASH_COLLISIONS = 40;
    protected static final int MULTIPLIERS_SIZE = 32;
    protected static final int MULTIPLIERS_MASK = 31;
    protected static final int SIZE_LIMIT = 20;
    protected boolean fNamespaces;
    protected int fLargeCount;
    protected int fLength;
    protected Attribute[] fAttributes;
    protected Attribute[] fAttributeTableView;
    protected int[] fAttributeTableViewChainState;
    protected int fTableViewBuckets;
    protected boolean fIsTableViewConsistent;
    protected int[] fHashMultipliers;
    
    public XMLAttributesImpl() {
        this(101);
    }
    
    public XMLAttributesImpl(final int tableSize) {
        this.fNamespaces = true;
        this.fLargeCount = 1;
        this.fAttributes = new Attribute[4];
        this.fTableViewBuckets = tableSize;
        for (int i = 0; i < this.fAttributes.length; ++i) {
            this.fAttributes[i] = new Attribute();
        }
    }
    
    public void setNamespaces(final boolean namespaces) {
        this.fNamespaces = namespaces;
    }
    
    @Override
    public int addAttribute(final QName name, final String type, final String value) {
        return this.addAttribute(name, type, value, null);
    }
    
    public int addAttribute(final QName name, final String type, final String value, final XMLString valueCache) {
        int index;
        if (this.fLength < 20) {
            index = ((name.uri != null && !name.uri.equals("")) ? this.getIndexFast(name.uri, name.localpart) : this.getIndexFast(name.rawname));
            if (index == -1) {
                index = this.fLength;
                if (this.fLength++ == this.fAttributes.length) {
                    final Attribute[] attributes = new Attribute[this.fAttributes.length + 4];
                    System.arraycopy(this.fAttributes, 0, attributes, 0, this.fAttributes.length);
                    for (int i = this.fAttributes.length; i < attributes.length; ++i) {
                        attributes[i] = new Attribute();
                    }
                    this.fAttributes = attributes;
                }
            }
        }
        else if (name.uri == null || name.uri.length() == 0 || (index = this.getIndexFast(name.uri, name.localpart)) == -1) {
            if (!this.fIsTableViewConsistent || this.fLength == 20 || (this.fLength > 20 && this.fLength > this.fTableViewBuckets)) {
                this.prepareAndPopulateTableView();
                this.fIsTableViewConsistent = true;
            }
            final int bucket = this.getTableViewBucket(name.rawname);
            if (this.fAttributeTableViewChainState[bucket] != this.fLargeCount) {
                index = this.fLength;
                if (this.fLength++ == this.fAttributes.length) {
                    final Attribute[] attributes2 = new Attribute[this.fAttributes.length << 1];
                    System.arraycopy(this.fAttributes, 0, attributes2, 0, this.fAttributes.length);
                    for (int j = this.fAttributes.length; j < attributes2.length; ++j) {
                        attributes2[j] = new Attribute();
                    }
                    this.fAttributes = attributes2;
                }
                this.fAttributeTableViewChainState[bucket] = this.fLargeCount;
                this.fAttributes[index].next = null;
                this.fAttributeTableView[bucket] = this.fAttributes[index];
            }
            else {
                int collisionCount;
                Attribute found;
                for (collisionCount = 0, found = this.fAttributeTableView[bucket]; found != null && found.name.rawname != name.rawname; found = found.next, ++collisionCount) {}
                if (found == null) {
                    index = this.fLength;
                    if (this.fLength++ == this.fAttributes.length) {
                        final Attribute[] attributes3 = new Attribute[this.fAttributes.length << 1];
                        System.arraycopy(this.fAttributes, 0, attributes3, 0, this.fAttributes.length);
                        for (int k = this.fAttributes.length; k < attributes3.length; ++k) {
                            attributes3[k] = new Attribute();
                        }
                        this.fAttributes = attributes3;
                    }
                    if (collisionCount >= 40) {
                        this.fAttributes[index].name.setValues(name);
                        this.rebalanceTableView(this.fLength);
                    }
                    else {
                        this.fAttributes[index].next = this.fAttributeTableView[bucket];
                        this.fAttributeTableView[bucket] = this.fAttributes[index];
                    }
                }
                else {
                    index = this.getIndexFast(name.rawname);
                }
            }
        }
        final Attribute attribute = this.fAttributes[index];
        attribute.name.setValues(name);
        attribute.type = type;
        attribute.value = value;
        attribute.xmlValue = valueCache;
        attribute.nonNormalizedValue = value;
        attribute.specified = false;
        if (attribute.augs != null) {
            attribute.augs.removeAllItems();
        }
        return index;
    }
    
    @Override
    public void removeAllAttributes() {
        this.fLength = 0;
    }
    
    @Override
    public void removeAttributeAt(final int attrIndex) {
        this.fIsTableViewConsistent = false;
        if (attrIndex < this.fLength - 1) {
            final Attribute removedAttr = this.fAttributes[attrIndex];
            System.arraycopy(this.fAttributes, attrIndex + 1, this.fAttributes, attrIndex, this.fLength - attrIndex - 1);
            this.fAttributes[this.fLength - 1] = removedAttr;
        }
        --this.fLength;
    }
    
    @Override
    public void setName(final int attrIndex, final QName attrName) {
        this.fAttributes[attrIndex].name.setValues(attrName);
    }
    
    @Override
    public void getName(final int attrIndex, final QName attrName) {
        attrName.setValues(this.fAttributes[attrIndex].name);
    }
    
    @Override
    public void setType(final int attrIndex, final String attrType) {
        this.fAttributes[attrIndex].type = attrType;
    }
    
    @Override
    public void setValue(final int attrIndex, final String attrValue) {
        this.setValue(attrIndex, attrValue, null);
    }
    
    @Override
    public void setValue(final int attrIndex, final String attrValue, final XMLString value) {
        final Attribute attribute = this.fAttributes[attrIndex];
        attribute.value = attrValue;
        attribute.nonNormalizedValue = attrValue;
        attribute.xmlValue = value;
    }
    
    @Override
    public void setNonNormalizedValue(final int attrIndex, String attrValue) {
        if (attrValue == null) {
            attrValue = this.fAttributes[attrIndex].value;
        }
        this.fAttributes[attrIndex].nonNormalizedValue = attrValue;
    }
    
    @Override
    public String getNonNormalizedValue(final int attrIndex) {
        final String value = this.fAttributes[attrIndex].nonNormalizedValue;
        return value;
    }
    
    @Override
    public void setSpecified(final int attrIndex, final boolean specified) {
        this.fAttributes[attrIndex].specified = specified;
    }
    
    @Override
    public boolean isSpecified(final int attrIndex) {
        return this.fAttributes[attrIndex].specified;
    }
    
    @Override
    public int getLength() {
        return this.fLength;
    }
    
    @Override
    public String getType(final int index) {
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        return this.getReportableType(this.fAttributes[index].type);
    }
    
    @Override
    public String getType(final String qname) {
        final int index = this.getIndex(qname);
        return (index != -1) ? this.getReportableType(this.fAttributes[index].type) : null;
    }
    
    @Override
    public String getValue(final int index) {
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        if (this.fAttributes[index].value == null && this.fAttributes[index].xmlValue != null) {
            this.fAttributes[index].value = this.fAttributes[index].xmlValue.toString();
        }
        return this.fAttributes[index].value;
    }
    
    @Override
    public String getValue(final String qname) {
        final int index = this.getIndex(qname);
        if (index == -1) {
            return null;
        }
        if (this.fAttributes[index].value == null) {
            this.fAttributes[index].value = this.fAttributes[index].xmlValue.toString();
        }
        return this.fAttributes[index].value;
    }
    
    public String getName(final int index) {
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        return this.fAttributes[index].name.rawname;
    }
    
    @Override
    public int getIndex(final String qName) {
        for (int i = 0; i < this.fLength; ++i) {
            final Attribute attribute = this.fAttributes[i];
            if (attribute.name.rawname != null && attribute.name.rawname.equals(qName)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int getIndex(final String uri, final String localPart) {
        for (int i = 0; i < this.fLength; ++i) {
            final Attribute attribute = this.fAttributes[i];
            if (attribute.name.localpart != null && attribute.name.localpart.equals(localPart) && (uri == attribute.name.uri || (uri != null && attribute.name.uri != null && attribute.name.uri.equals(uri)))) {
                return i;
            }
        }
        return -1;
    }
    
    public int getIndexByLocalName(final String localPart) {
        for (int i = 0; i < this.fLength; ++i) {
            final Attribute attribute = this.fAttributes[i];
            if (attribute.name.localpart != null && attribute.name.localpart.equals(localPart)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public String getLocalName(final int index) {
        if (!this.fNamespaces) {
            return "";
        }
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        return this.fAttributes[index].name.localpart;
    }
    
    @Override
    public String getQName(final int index) {
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        final String rawname = this.fAttributes[index].name.rawname;
        return (rawname != null) ? rawname : "";
    }
    
    @Override
    public QName getQualifiedName(final int index) {
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        return this.fAttributes[index].name;
    }
    
    @Override
    public String getType(final String uri, final String localName) {
        if (!this.fNamespaces) {
            return null;
        }
        final int index = this.getIndex(uri, localName);
        return (index != -1) ? this.getType(index) : null;
    }
    
    public int getIndexFast(final String qName) {
        for (int i = 0; i < this.fLength; ++i) {
            final Attribute attribute = this.fAttributes[i];
            if (attribute.name.rawname == qName) {
                return i;
            }
        }
        return -1;
    }
    
    public void addAttributeNS(final QName name, final String type, final String value) {
        final int index = this.fLength;
        if (this.fLength++ == this.fAttributes.length) {
            Attribute[] attributes;
            if (this.fLength < 20) {
                attributes = new Attribute[this.fAttributes.length + 4];
            }
            else {
                attributes = new Attribute[this.fAttributes.length << 1];
            }
            System.arraycopy(this.fAttributes, 0, attributes, 0, this.fAttributes.length);
            for (int i = this.fAttributes.length; i < attributes.length; ++i) {
                attributes[i] = new Attribute();
            }
            this.fAttributes = attributes;
        }
        final Attribute attribute = this.fAttributes[index];
        attribute.name.setValues(name);
        attribute.type = type;
        attribute.value = value;
        attribute.nonNormalizedValue = value;
        attribute.specified = false;
        attribute.augs.removeAllItems();
    }
    
    public QName checkDuplicatesNS() {
        final int length = this.fLength;
        if (length <= 20) {
            final Attribute[] attributes = this.fAttributes;
            for (int i = 0; i < length - 1; ++i) {
                final Attribute att1 = attributes[i];
                for (int j = i + 1; j < length; ++j) {
                    final Attribute att2 = attributes[j];
                    if (att1.name.localpart == att2.name.localpart && att1.name.uri == att2.name.uri) {
                        return att2.name;
                    }
                }
            }
            return null;
        }
        return this.checkManyDuplicatesNS();
    }
    
    private QName checkManyDuplicatesNS() {
        this.fIsTableViewConsistent = false;
        this.prepareTableView();
        final int length = this.fLength;
        final Attribute[] attributes = this.fAttributes;
        final Attribute[] attributeTableView = this.fAttributeTableView;
        final int[] attributeTableViewChainState = this.fAttributeTableViewChainState;
        int largeCount = this.fLargeCount;
        for (int i = 0; i < length; ++i) {
            final Attribute attr = attributes[i];
            final int bucket = this.getTableViewBucket(attr.name.localpart, attr.name.uri);
            if (attributeTableViewChainState[bucket] != largeCount) {
                attributeTableViewChainState[bucket] = largeCount;
                attr.next = null;
                attributeTableView[bucket] = attr;
            }
            else {
                int collisionCount = 0;
                for (Attribute found = attributeTableView[bucket]; found != null; found = found.next, ++collisionCount) {
                    if (found.name.localpart == attr.name.localpart && found.name.uri == attr.name.uri) {
                        return attr.name;
                    }
                }
                if (collisionCount >= 40) {
                    this.rebalanceTableViewNS(i + 1);
                    largeCount = this.fLargeCount;
                }
                else {
                    attr.next = attributeTableView[bucket];
                    attributeTableView[bucket] = attr;
                }
            }
        }
        return null;
    }
    
    public int getIndexFast(final String uri, final String localPart) {
        for (int i = 0; i < this.fLength; ++i) {
            final Attribute attribute = this.fAttributes[i];
            if (attribute.name.localpart == localPart && attribute.name.uri == uri) {
                return i;
            }
        }
        return -1;
    }
    
    private String getReportableType(final String type) {
        if (type.charAt(0) == '(') {
            return "NMTOKEN";
        }
        return type;
    }
    
    protected int getTableViewBucket(final String qname) {
        return (this.hash(qname) & Integer.MAX_VALUE) % this.fTableViewBuckets;
    }
    
    protected int getTableViewBucket(final String localpart, final String uri) {
        if (uri == null) {
            return (this.hash(localpart) & Integer.MAX_VALUE) % this.fTableViewBuckets;
        }
        return (this.hash(localpart, uri) & Integer.MAX_VALUE) % this.fTableViewBuckets;
    }
    
    private int hash(final String localpart) {
        if (this.fHashMultipliers == null) {
            return localpart.hashCode();
        }
        return this.hash0(localpart);
    }
    
    private int hash(final String localpart, final String uri) {
        if (this.fHashMultipliers == null) {
            return localpart.hashCode() + uri.hashCode() * 31;
        }
        return this.hash0(localpart) + this.hash0(uri) * this.fHashMultipliers[32];
    }
    
    private int hash0(final String symbol) {
        int code = 0;
        final int length = symbol.length();
        final int[] multipliers = this.fHashMultipliers;
        for (int i = 0; i < length; ++i) {
            code = code * multipliers[i & 0x1F] + symbol.charAt(i);
        }
        return code;
    }
    
    protected void cleanTableView() {
        if (++this.fLargeCount < 0) {
            if (this.fAttributeTableViewChainState != null) {
                for (int i = this.fTableViewBuckets - 1; i >= 0; --i) {
                    this.fAttributeTableViewChainState[i] = 0;
                }
            }
            this.fLargeCount = 1;
        }
    }
    
    private void growTableView() {
        final int length = this.fLength;
        int tableViewBuckets = this.fTableViewBuckets;
        do {
            tableViewBuckets = (tableViewBuckets << 1) + 1;
            if (tableViewBuckets < 0) {
                tableViewBuckets = Integer.MAX_VALUE;
                break;
            }
        } while (length > tableViewBuckets);
        this.fTableViewBuckets = tableViewBuckets;
        this.fAttributeTableView = null;
        this.fLargeCount = 1;
    }
    
    protected void prepareTableView() {
        if (this.fLength > this.fTableViewBuckets) {
            this.growTableView();
        }
        if (this.fAttributeTableView == null) {
            this.fAttributeTableView = new Attribute[this.fTableViewBuckets];
            this.fAttributeTableViewChainState = new int[this.fTableViewBuckets];
        }
        else {
            this.cleanTableView();
        }
    }
    
    protected void prepareAndPopulateTableView() {
        this.prepareAndPopulateTableView(this.fLength);
    }
    
    private void prepareAndPopulateTableView(final int count) {
        this.prepareTableView();
        for (int i = 0; i < count; ++i) {
            final Attribute attr = this.fAttributes[i];
            final int bucket = this.getTableViewBucket(attr.name.rawname);
            if (this.fAttributeTableViewChainState[bucket] != this.fLargeCount) {
                this.fAttributeTableViewChainState[bucket] = this.fLargeCount;
                attr.next = null;
                this.fAttributeTableView[bucket] = attr;
            }
            else {
                attr.next = this.fAttributeTableView[bucket];
                this.fAttributeTableView[bucket] = attr;
            }
        }
    }
    
    @Override
    public String getPrefix(final int index) {
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        final String prefix = this.fAttributes[index].name.prefix;
        return (prefix != null) ? prefix : "";
    }
    
    @Override
    public String getURI(final int index) {
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        final String uri = this.fAttributes[index].name.uri;
        return uri;
    }
    
    @Override
    public String getValue(final String uri, final String localName) {
        final int index = this.getIndex(uri, localName);
        return (index != -1) ? this.getValue(index) : null;
    }
    
    @Override
    public Augmentations getAugmentations(final String uri, final String localName) {
        final int index = this.getIndex(uri, localName);
        return (index != -1) ? this.fAttributes[index].augs : null;
    }
    
    @Override
    public Augmentations getAugmentations(final String qName) {
        final int index = this.getIndex(qName);
        return (index != -1) ? this.fAttributes[index].augs : null;
    }
    
    @Override
    public Augmentations getAugmentations(final int attributeIndex) {
        if (attributeIndex < 0 || attributeIndex >= this.fLength) {
            return null;
        }
        return this.fAttributes[attributeIndex].augs;
    }
    
    @Override
    public void setAugmentations(final int attrIndex, final Augmentations augs) {
        this.fAttributes[attrIndex].augs = augs;
    }
    
    public void setURI(final int attrIndex, final String uri) {
        this.fAttributes[attrIndex].name.uri = uri;
    }
    
    public void setSchemaId(final int attrIndex, final boolean schemaId) {
        this.fAttributes[attrIndex].schemaId = schemaId;
    }
    
    public boolean getSchemaId(final int index) {
        return index >= 0 && index < this.fLength && this.fAttributes[index].schemaId;
    }
    
    public boolean getSchemaId(final String qname) {
        final int index = this.getIndex(qname);
        return index != -1 && this.fAttributes[index].schemaId;
    }
    
    public boolean getSchemaId(final String uri, final String localName) {
        if (!this.fNamespaces) {
            return false;
        }
        final int index = this.getIndex(uri, localName);
        return index != -1 && this.fAttributes[index].schemaId;
    }
    
    @Override
    public void refresh() {
        if (this.fLength > 0) {
            for (int i = 0; i < this.fLength; ++i) {
                this.getValue(i);
            }
        }
    }
    
    @Override
    public void refresh(final int pos) {
    }
    
    private void prepareAndPopulateTableViewNS(final int count) {
        this.prepareTableView();
        for (int i = 0; i < count; ++i) {
            final Attribute attr = this.fAttributes[i];
            final int bucket = this.getTableViewBucket(attr.name.localpart, attr.name.uri);
            if (this.fAttributeTableViewChainState[bucket] != this.fLargeCount) {
                this.fAttributeTableViewChainState[bucket] = this.fLargeCount;
                attr.next = null;
                this.fAttributeTableView[bucket] = attr;
            }
            else {
                attr.next = this.fAttributeTableView[bucket];
                this.fAttributeTableView[bucket] = attr;
            }
        }
    }
    
    private void rebalanceTableView(final int count) {
        if (this.fHashMultipliers == null) {
            this.fHashMultipliers = new int[33];
        }
        PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
        this.prepareAndPopulateTableView(count);
    }
    
    private void rebalanceTableViewNS(final int count) {
        if (this.fHashMultipliers == null) {
            this.fHashMultipliers = new int[33];
        }
        PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
        this.prepareAndPopulateTableViewNS(count);
    }
    
    static class Attribute
    {
        public QName name;
        public String type;
        public String value;
        public XMLString xmlValue;
        public String nonNormalizedValue;
        public boolean specified;
        public boolean schemaId;
        public Augmentations augs;
        public Attribute next;
        
        Attribute() {
            this.name = new QName();
            this.augs = new AugmentationsImpl();
        }
    }
}
