package org.apache.xerces.util;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;

public class XMLAttributesImpl implements XMLAttributes
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
    
    public XMLAttributesImpl(final int fTableViewBuckets) {
        this.fNamespaces = true;
        this.fLargeCount = 1;
        this.fAttributes = new Attribute[4];
        this.fTableViewBuckets = fTableViewBuckets;
        for (int i = 0; i < this.fAttributes.length; ++i) {
            this.fAttributes[i] = new Attribute();
        }
    }
    
    public void setNamespaces(final boolean fNamespaces) {
        this.fNamespaces = fNamespaces;
    }
    
    public int addAttribute(final QName qName, final String type, final String s) {
        int n;
        if (this.fLength < 20) {
            n = ((qName.uri != null && qName.uri.length() != 0) ? this.getIndexFast(qName.uri, qName.localpart) : this.getIndexFast(qName.rawname));
            if (n == -1) {
                n = this.fLength;
                if (this.fLength++ == this.fAttributes.length) {
                    final Attribute[] fAttributes = new Attribute[this.fAttributes.length + 4];
                    System.arraycopy(this.fAttributes, 0, fAttributes, 0, this.fAttributes.length);
                    for (int i = this.fAttributes.length; i < fAttributes.length; ++i) {
                        fAttributes[i] = new Attribute();
                    }
                    this.fAttributes = fAttributes;
                }
            }
        }
        else if (qName.uri == null || qName.uri.length() == 0 || (n = this.getIndexFast(qName.uri, qName.localpart)) == -1) {
            if (!this.fIsTableViewConsistent || this.fLength == 20 || (this.fLength > 20 && this.fLength > this.fTableViewBuckets)) {
                this.prepareAndPopulateTableView();
                this.fIsTableViewConsistent = true;
            }
            final int tableViewBucket = this.getTableViewBucket(qName.rawname);
            if (this.fAttributeTableViewChainState[tableViewBucket] != this.fLargeCount) {
                n = this.fLength;
                if (this.fLength++ == this.fAttributes.length) {
                    final Attribute[] fAttributes2 = new Attribute[this.fAttributes.length << 1];
                    System.arraycopy(this.fAttributes, 0, fAttributes2, 0, this.fAttributes.length);
                    for (int j = this.fAttributes.length; j < fAttributes2.length; ++j) {
                        fAttributes2[j] = new Attribute();
                    }
                    this.fAttributes = fAttributes2;
                }
                this.fAttributeTableViewChainState[tableViewBucket] = this.fLargeCount;
                this.fAttributes[n].next = null;
                this.fAttributeTableView[tableViewBucket] = this.fAttributes[n];
            }
            else {
                int n2;
                Attribute next;
                for (n2 = 0, next = this.fAttributeTableView[tableViewBucket]; next != null && next.name.rawname != qName.rawname; next = next.next, ++n2) {}
                if (next == null) {
                    n = this.fLength;
                    if (this.fLength++ == this.fAttributes.length) {
                        final Attribute[] fAttributes3 = new Attribute[this.fAttributes.length << 1];
                        System.arraycopy(this.fAttributes, 0, fAttributes3, 0, this.fAttributes.length);
                        for (int k = this.fAttributes.length; k < fAttributes3.length; ++k) {
                            fAttributes3[k] = new Attribute();
                        }
                        this.fAttributes = fAttributes3;
                    }
                    if (n2 >= 40) {
                        this.fAttributes[n].name.setValues(qName);
                        this.rebalanceTableView(this.fLength);
                    }
                    else {
                        this.fAttributes[n].next = this.fAttributeTableView[tableViewBucket];
                        this.fAttributeTableView[tableViewBucket] = this.fAttributes[n];
                    }
                }
                else {
                    n = this.getIndexFast(qName.rawname);
                }
            }
        }
        final Attribute attribute = this.fAttributes[n];
        attribute.name.setValues(qName);
        attribute.type = type;
        attribute.value = s;
        attribute.nonNormalizedValue = s;
        attribute.specified = false;
        attribute.augs.removeAllItems();
        return n;
    }
    
    public void removeAllAttributes() {
        this.fLength = 0;
    }
    
    public void removeAttributeAt(final int n) {
        this.fIsTableViewConsistent = false;
        if (n < this.fLength - 1) {
            final Attribute attribute = this.fAttributes[n];
            System.arraycopy(this.fAttributes, n + 1, this.fAttributes, n, this.fLength - n - 1);
            this.fAttributes[this.fLength - 1] = attribute;
        }
        --this.fLength;
    }
    
    public void setName(final int n, final QName values) {
        this.fAttributes[n].name.setValues(values);
    }
    
    public void getName(final int n, final QName qName) {
        qName.setValues(this.fAttributes[n].name);
    }
    
    public void setType(final int n, final String type) {
        this.fAttributes[n].type = type;
    }
    
    public void setValue(final int n, final String s) {
        final Attribute attribute = this.fAttributes[n];
        attribute.value = s;
        attribute.nonNormalizedValue = s;
    }
    
    public void setNonNormalizedValue(final int n, String value) {
        if (value == null) {
            value = this.fAttributes[n].value;
        }
        this.fAttributes[n].nonNormalizedValue = value;
    }
    
    public String getNonNormalizedValue(final int n) {
        return this.fAttributes[n].nonNormalizedValue;
    }
    
    public void setSpecified(final int n, final boolean specified) {
        this.fAttributes[n].specified = specified;
    }
    
    public boolean isSpecified(final int n) {
        return this.fAttributes[n].specified;
    }
    
    public int getLength() {
        return this.fLength;
    }
    
    public String getType(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.getReportableType(this.fAttributes[n].type);
    }
    
    public String getType(final String s) {
        final int index = this.getIndex(s);
        return (index != -1) ? this.getReportableType(this.fAttributes[index].type) : null;
    }
    
    public String getValue(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fAttributes[n].value;
    }
    
    public String getValue(final String s) {
        final int index = this.getIndex(s);
        return (index != -1) ? this.fAttributes[index].value : null;
    }
    
    public String getName(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fAttributes[n].name.rawname;
    }
    
    public int getIndex(final String s) {
        for (int i = 0; i < this.fLength; ++i) {
            final Attribute attribute = this.fAttributes[i];
            if (attribute.name.rawname != null && attribute.name.rawname.equals(s)) {
                return i;
            }
        }
        return -1;
    }
    
    public int getIndex(final String s, final String s2) {
        for (int i = 0; i < this.fLength; ++i) {
            final Attribute attribute = this.fAttributes[i];
            if (attribute.name.localpart != null && attribute.name.localpart.equals(s2) && (s == attribute.name.uri || (s != null && attribute.name.uri != null && attribute.name.uri.equals(s)))) {
                return i;
            }
        }
        return -1;
    }
    
    public String getLocalName(final int n) {
        if (!this.fNamespaces) {
            return "";
        }
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fAttributes[n].name.localpart;
    }
    
    public String getQName(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        final String rawname = this.fAttributes[n].name.rawname;
        return (rawname != null) ? rawname : "";
    }
    
    public String getType(final String s, final String s2) {
        if (!this.fNamespaces) {
            return null;
        }
        final int index = this.getIndex(s, s2);
        return (index != -1) ? this.getReportableType(this.fAttributes[index].type) : null;
    }
    
    public String getPrefix(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        final String prefix = this.fAttributes[n].name.prefix;
        return (prefix != null) ? prefix : "";
    }
    
    public String getURI(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fAttributes[n].name.uri;
    }
    
    public String getValue(final String s, final String s2) {
        final int index = this.getIndex(s, s2);
        return (index != -1) ? this.getValue(index) : null;
    }
    
    public Augmentations getAugmentations(final String s, final String s2) {
        final int index = this.getIndex(s, s2);
        return (index != -1) ? this.fAttributes[index].augs : null;
    }
    
    public Augmentations getAugmentations(final String s) {
        final int index = this.getIndex(s);
        return (index != -1) ? this.fAttributes[index].augs : null;
    }
    
    public Augmentations getAugmentations(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fAttributes[n].augs;
    }
    
    public void setAugmentations(final int n, final Augmentations augs) {
        this.fAttributes[n].augs = augs;
    }
    
    public void setURI(final int n, final String uri) {
        this.fAttributes[n].name.uri = uri;
    }
    
    public int getIndexFast(final String s) {
        for (int i = 0; i < this.fLength; ++i) {
            if (this.fAttributes[i].name.rawname == s) {
                return i;
            }
        }
        return -1;
    }
    
    public void addAttributeNS(final QName values, final String type, final String s) {
        final int fLength = this.fLength;
        if (this.fLength++ == this.fAttributes.length) {
            Attribute[] fAttributes;
            if (this.fLength < 20) {
                fAttributes = new Attribute[this.fAttributes.length + 4];
            }
            else {
                fAttributes = new Attribute[this.fAttributes.length << 1];
            }
            System.arraycopy(this.fAttributes, 0, fAttributes, 0, this.fAttributes.length);
            for (int i = this.fAttributes.length; i < fAttributes.length; ++i) {
                fAttributes[i] = new Attribute();
            }
            this.fAttributes = fAttributes;
        }
        final Attribute attribute = this.fAttributes[fLength];
        attribute.name.setValues(values);
        attribute.type = type;
        attribute.value = s;
        attribute.nonNormalizedValue = s;
        attribute.specified = false;
        attribute.augs.removeAllItems();
    }
    
    public QName checkDuplicatesNS() {
        final int fLength = this.fLength;
        if (fLength <= 20) {
            final Attribute[] fAttributes = this.fAttributes;
            for (int i = 0; i < fLength - 1; ++i) {
                final Attribute attribute = fAttributes[i];
                for (int j = i + 1; j < fLength; ++j) {
                    final Attribute attribute2 = fAttributes[j];
                    if (attribute.name.localpart == attribute2.name.localpart && attribute.name.uri == attribute2.name.uri) {
                        return attribute2.name;
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
        final int fLength = this.fLength;
        final Attribute[] fAttributes = this.fAttributes;
        final Attribute[] fAttributeTableView = this.fAttributeTableView;
        final int[] fAttributeTableViewChainState = this.fAttributeTableViewChainState;
        int n = this.fLargeCount;
        for (int i = 0; i < fLength; ++i) {
            final Attribute attribute = fAttributes[i];
            final int tableViewBucket = this.getTableViewBucket(attribute.name.localpart, attribute.name.uri);
            if (fAttributeTableViewChainState[tableViewBucket] != n) {
                fAttributeTableViewChainState[tableViewBucket] = n;
                attribute.next = null;
                fAttributeTableView[tableViewBucket] = attribute;
            }
            else {
                int n2 = 0;
                for (Attribute next = fAttributeTableView[tableViewBucket]; next != null; next = next.next, ++n2) {
                    if (next.name.localpart == attribute.name.localpart && next.name.uri == attribute.name.uri) {
                        return attribute.name;
                    }
                }
                if (n2 >= 40) {
                    this.rebalanceTableViewNS(i + 1);
                    n = this.fLargeCount;
                }
                else {
                    attribute.next = fAttributeTableView[tableViewBucket];
                    fAttributeTableView[tableViewBucket] = attribute;
                }
            }
        }
        return null;
    }
    
    public int getIndexFast(final String s, final String s2) {
        for (int i = 0; i < this.fLength; ++i) {
            final Attribute attribute = this.fAttributes[i];
            if (attribute.name.localpart == s2 && attribute.name.uri == s) {
                return i;
            }
        }
        return -1;
    }
    
    private String getReportableType(final String s) {
        if (s.charAt(0) == '(') {
            return "NMTOKEN";
        }
        return s;
    }
    
    protected int getTableViewBucket(final String s) {
        return (this.hash(s) & Integer.MAX_VALUE) % this.fTableViewBuckets;
    }
    
    protected int getTableViewBucket(final String s, final String s2) {
        if (s2 == null) {
            return (this.hash(s) & Integer.MAX_VALUE) % this.fTableViewBuckets;
        }
        return (this.hash(s, s2) & Integer.MAX_VALUE) % this.fTableViewBuckets;
    }
    
    private int hash(final String s) {
        if (this.fHashMultipliers == null) {
            return s.hashCode();
        }
        return this.hash0(s);
    }
    
    private int hash(final String s, final String s2) {
        if (this.fHashMultipliers == null) {
            return s.hashCode() + s2.hashCode() * 31;
        }
        return this.hash0(s) + this.hash0(s2) * this.fHashMultipliers[32];
    }
    
    private int hash0(final String s) {
        int n = 0;
        final int length = s.length();
        final int[] fHashMultipliers = this.fHashMultipliers;
        for (int i = 0; i < length; ++i) {
            n = n * fHashMultipliers[i & 0x1F] + s.charAt(i);
        }
        return n;
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
        final int i = this.fLength;
        int fTableViewBuckets = this.fTableViewBuckets;
        do {
            fTableViewBuckets = (fTableViewBuckets << 1) + 1;
            if (fTableViewBuckets < 0) {
                fTableViewBuckets = Integer.MAX_VALUE;
                break;
            }
        } while (i > fTableViewBuckets);
        this.fTableViewBuckets = fTableViewBuckets;
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
    
    private void prepareAndPopulateTableView(final int n) {
        this.prepareTableView();
        for (int i = 0; i < n; ++i) {
            final Attribute attribute = this.fAttributes[i];
            final int tableViewBucket = this.getTableViewBucket(attribute.name.rawname);
            if (this.fAttributeTableViewChainState[tableViewBucket] != this.fLargeCount) {
                this.fAttributeTableViewChainState[tableViewBucket] = this.fLargeCount;
                attribute.next = null;
                this.fAttributeTableView[tableViewBucket] = attribute;
            }
            else {
                attribute.next = this.fAttributeTableView[tableViewBucket];
                this.fAttributeTableView[tableViewBucket] = attribute;
            }
        }
    }
    
    private void prepareAndPopulateTableViewNS(final int n) {
        this.prepareTableView();
        for (int i = 0; i < n; ++i) {
            final Attribute attribute = this.fAttributes[i];
            final int tableViewBucket = this.getTableViewBucket(attribute.name.localpart, attribute.name.uri);
            if (this.fAttributeTableViewChainState[tableViewBucket] != this.fLargeCount) {
                this.fAttributeTableViewChainState[tableViewBucket] = this.fLargeCount;
                attribute.next = null;
                this.fAttributeTableView[tableViewBucket] = attribute;
            }
            else {
                attribute.next = this.fAttributeTableView[tableViewBucket];
                this.fAttributeTableView[tableViewBucket] = attribute;
            }
        }
    }
    
    private void rebalanceTableView(final int n) {
        if (this.fHashMultipliers == null) {
            this.fHashMultipliers = new int[33];
        }
        PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
        this.prepareAndPopulateTableView(n);
    }
    
    private void rebalanceTableViewNS(final int n) {
        if (this.fHashMultipliers == null) {
            this.fHashMultipliers = new int[33];
        }
        PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
        this.prepareAndPopulateTableViewNS(n);
    }
    
    static class Attribute
    {
        public final QName name;
        public String type;
        public String value;
        public String nonNormalizedValue;
        public boolean specified;
        public Augmentations augs;
        public Attribute next;
        
        Attribute() {
            this.name = new QName();
            this.augs = new AugmentationsImpl();
        }
    }
}
