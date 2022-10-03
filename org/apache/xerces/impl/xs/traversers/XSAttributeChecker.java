package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import java.util.Iterator;
import java.util.Map;
import org.apache.xerces.impl.xs.XSGrammarBucket;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Attr;
import org.apache.xerces.impl.xs.SchemaNamespaceSupport;
import java.util.StringTokenizer;
import org.w3c.dom.Node;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.w3c.dom.Element;
import java.util.Vector;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.dv.XSSimpleType;
import java.util.Hashtable;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.util.XIntPool;

public class XSAttributeChecker
{
    private static final String ELEMENT_N = "element_n";
    private static final String ELEMENT_R = "element_r";
    private static final String ATTRIBUTE_N = "attribute_n";
    private static final String ATTRIBUTE_R = "attribute_r";
    private static final String UNIQUE_R = "unique_r";
    private static final String KEY_R = "key_r";
    private static final String KEYREF_R = "keyref_r";
    private static int ATTIDX_COUNT;
    public static final int ATTIDX_ABSTRACT;
    public static final int ATTIDX_AFORMDEFAULT;
    public static final int ATTIDX_BASE;
    public static final int ATTIDX_BLOCK;
    public static final int ATTIDX_BLOCKDEFAULT;
    public static final int ATTIDX_DEFAULT;
    public static final int ATTIDX_EFORMDEFAULT;
    public static final int ATTIDX_FINAL;
    public static final int ATTIDX_FINALDEFAULT;
    public static final int ATTIDX_FIXED;
    public static final int ATTIDX_FORM;
    public static final int ATTIDX_ID;
    public static final int ATTIDX_ITEMTYPE;
    public static final int ATTIDX_MAXOCCURS;
    public static final int ATTIDX_MEMBERTYPES;
    public static final int ATTIDX_MINOCCURS;
    public static final int ATTIDX_MIXED;
    public static final int ATTIDX_NAME;
    public static final int ATTIDX_NAMESPACE;
    public static final int ATTIDX_NAMESPACE_LIST;
    public static final int ATTIDX_NILLABLE;
    public static final int ATTIDX_NONSCHEMA;
    public static final int ATTIDX_PROCESSCONTENTS;
    public static final int ATTIDX_PUBLIC;
    public static final int ATTIDX_REF;
    public static final int ATTIDX_REFER;
    public static final int ATTIDX_SCHEMALOCATION;
    public static final int ATTIDX_SOURCE;
    public static final int ATTIDX_SUBSGROUP;
    public static final int ATTIDX_SYSTEM;
    public static final int ATTIDX_TARGETNAMESPACE;
    public static final int ATTIDX_TYPE;
    public static final int ATTIDX_USE;
    public static final int ATTIDX_VALUE;
    public static final int ATTIDX_ENUMNSDECLS;
    public static final int ATTIDX_VERSION;
    public static final int ATTIDX_XML_LANG;
    public static final int ATTIDX_XPATH;
    public static final int ATTIDX_FROMDEFAULT;
    public static final int ATTIDX_ISRETURNED;
    public static final int ATTIDX_APPLIESTOEMPTY;
    public static final int ATTIDX_DEFAULTATTRAPPLY;
    public static final int ATTIDX_DEFAULTATTRIBUTES;
    public static final int ATTIDX_MODE;
    public static final int ATTIDX_NOTNAMESPACE;
    public static final int ATTIDX_NOTQNAME;
    public static final int ATTIDX_XPATHDEFAULTNS;
    public static final int ATTIDX_INHERITABLE;
    public static final int ATTIDX_XPATHDEFAULTNS_TWOPOUNDDFLT;
    private static final XIntPool fXIntPool;
    private static final XInt INT_QUALIFIED;
    private static final XInt INT_UNQUALIFIED;
    private static final XInt INT_EMPTY_SET;
    private static final XInt INT_ANY_STRICT;
    private static final XInt INT_ANY_LAX;
    private static final XInt INT_ANY_SKIP;
    private static final XInt INT_ANY_ANY;
    private static final XInt INT_ANY_LIST;
    private static final XInt INT_ANY_NOT;
    private static final XInt INT_USE_OPTIONAL;
    private static final XInt INT_USE_REQUIRED;
    private static final XInt INT_USE_PROHIBITED;
    private static final XInt INT_WS_PRESERVE;
    private static final XInt INT_WS_REPLACE;
    private static final XInt INT_WS_COLLAPSE;
    private static final XInt INT_ET_OPTION;
    private static final XInt INT_ET_REQUIRED;
    private static final XInt INT_ET_PROHIBITED;
    private static final XInt INT_UNBOUNDED;
    private static final XInt INT_MODE_NONE;
    private static final XInt INT_MODE_INTERLEAVE;
    private static final XInt INT_MODE_SUFFIX;
    private static final Hashtable fEleAttrsMapG;
    private static final Hashtable fEleAttrsMapL;
    private static final Hashtable fEleAttrs11MapG;
    private static final Hashtable fEleAttrs11MapL;
    protected static final int DT_ANYURI = 0;
    protected static final int DT_ID = 1;
    protected static final int DT_QNAME = 2;
    protected static final int DT_STRING = 3;
    protected static final int DT_TOKEN = 4;
    protected static final int DT_NCNAME = 5;
    protected static final int DT_XPATH = 6;
    protected static final int DT_XPATH1 = 7;
    protected static final int DT_LANGUAGE = 8;
    protected static final int DT_COUNT = 9;
    private static final XSSimpleType[] fExtraDVs;
    protected static final int DT_BLOCK = -1;
    protected static final int DT_BLOCK1 = -2;
    protected static final int DT_FINAL = -3;
    protected static final int DT_FINAL1 = -4;
    protected static final int DT_FINAL2 = -5;
    protected static final int DT_FORM = -6;
    protected static final int DT_MAXOCCURS = -7;
    protected static final int DT_MAXOCCURS1 = -8;
    protected static final int DT_MEMBERTYPES = -9;
    protected static final int DT_MINOCCURS1 = -10;
    protected static final int DT_NAMESPACE = -11;
    protected static final int DT_PROCESSCONTENTS = -12;
    protected static final int DT_USE = -13;
    protected static final int DT_WHITESPACE = -14;
    protected static final int DT_BOOLEAN = -15;
    protected static final int DT_NONNEGINT = -16;
    protected static final int DT_POSINT = -17;
    protected static final int DT_XPATH_DEFAULT_NS = -18;
    protected static final int DT_MODE = -19;
    protected static final int DT_MODE1 = -20;
    protected static final int DT_NOTNAMESPACE = -21;
    protected static final int DT_NOTQNAME = -22;
    protected static final int DT_INT = -23;
    protected static final int DT_EXPLICITTIMEZONE = -24;
    protected XSDHandler fSchemaHandler;
    protected SymbolTable fSymbolTable;
    protected Hashtable fNonSchemaAttrs;
    protected Vector fNamespaceList;
    protected boolean[] fSeen;
    private static boolean[] fSeenTemp;
    static final int INIT_POOL_SIZE = 10;
    static final int INC_POOL_SIZE = 10;
    Object[][] fArrayPool;
    private static Object[] fTempArray;
    int fPoolPos;
    
    public XSAttributeChecker(final XSDHandler fSchemaHandler) {
        this.fSchemaHandler = null;
        this.fSymbolTable = null;
        this.fNonSchemaAttrs = new Hashtable();
        this.fNamespaceList = new Vector();
        this.fSeen = new boolean[XSAttributeChecker.ATTIDX_COUNT];
        this.fArrayPool = new Object[10][XSAttributeChecker.ATTIDX_COUNT];
        this.fPoolPos = 0;
        this.fSchemaHandler = fSchemaHandler;
    }
    
    public void reset(final SymbolTable fSymbolTable) {
        this.fSymbolTable = fSymbolTable;
        this.fNonSchemaAttrs.clear();
    }
    
    public String checkTargetNamespace(final Element element, final XSDocumentInfo xsDocumentInfo) {
        if (DOMUtil.getAttr(element, SchemaSymbols.ATT_TARGETNAMESPACE) != null) {
            final String attrValueTrimmed = DOMUtil.getAttrValueTrimmed(element, SchemaSymbols.ATT_TARGETNAMESPACE);
            try {
                XSAttributeChecker.fExtraDVs[0].validate(attrValueTrimmed, xsDocumentInfo.fValidationContext, null);
                return this.fSymbolTable.addSymbol(attrValueTrimmed);
            }
            catch (final InvalidDatatypeValueException ex) {}
        }
        return null;
    }
    
    public Object[] checkAttributes(final Element element, final boolean b, final XSDocumentInfo xsDocumentInfo) {
        return this.checkAttributes(element, b, xsDocumentInfo, false);
    }
    
    public Object[] checkAttributes(final Element element, final boolean b, final XSDocumentInfo xsDocumentInfo, final boolean b2) {
        if (element == null) {
            return null;
        }
        final Attr[] attrs = DOMUtil.getAttrs(element);
        this.resolveNamespace(element, attrs, xsDocumentInfo.fNamespaceSupport);
        final String namespaceURI = DOMUtil.getNamespaceURI(element);
        final String localName = DOMUtil.getLocalName(element);
        if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(namespaceURI)) {
            this.reportSchemaError("s4s-elt-schema-ns", new Object[] { localName }, element);
        }
        Hashtable hashtable = (this.fSchemaHandler.fSchemaVersion < 4) ? XSAttributeChecker.fEleAttrsMapG : XSAttributeChecker.fEleAttrs11MapG;
        String s = localName;
        if (!b) {
            hashtable = ((this.fSchemaHandler.fSchemaVersion < 4) ? XSAttributeChecker.fEleAttrsMapL : XSAttributeChecker.fEleAttrs11MapL);
            if (localName.equals(SchemaSymbols.ELT_ELEMENT)) {
                if (DOMUtil.getAttr(element, SchemaSymbols.ATT_REF) != null) {
                    s = "element_r";
                }
                else {
                    s = "element_n";
                }
            }
            else if (localName.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
                if (DOMUtil.getAttr(element, SchemaSymbols.ATT_REF) != null) {
                    s = "attribute_r";
                }
                else {
                    s = "attribute_n";
                }
            }
            else if (this.fSchemaHandler.fSchemaVersion == 4 && DOMUtil.getAttr(element, SchemaSymbols.ATT_REF) != null) {
                if (localName.equals(SchemaSymbols.ELT_UNIQUE)) {
                    s = "unique_r";
                }
                else if (localName.equals(SchemaSymbols.ELT_KEY)) {
                    s = "key_r";
                }
                else if (localName.equals(SchemaSymbols.ELT_KEYREF)) {
                    s = "keyref_r";
                }
            }
        }
        final Container container = hashtable.get(s);
        if (container == null) {
            this.reportSchemaError("s4s-elt-invalid", new Object[] { localName }, element);
            return null;
        }
        final Object[] availableArray = this.getAvailableArray();
        long n = 0L;
        System.arraycopy(XSAttributeChecker.fSeenTemp, 0, this.fSeen, 0, XSAttributeChecker.ATTIDX_COUNT);
        for (final Attr attr : attrs) {
            final String name = attr.getName();
            String namespaceURI2 = DOMUtil.getNamespaceURI(attr);
            final String value = DOMUtil.getValue(attr);
            Label_0957: {
                if (name.startsWith("xml")) {
                    if ("xmlns".equals(DOMUtil.getPrefix(attr))) {
                        break Label_0957;
                    }
                    if ("xmlns".equals(name)) {
                        break Label_0957;
                    }
                    if (SchemaSymbols.ATT_XML_LANG.equals(name) && (SchemaSymbols.ELT_SCHEMA.equals(localName) || SchemaSymbols.ELT_DOCUMENTATION.equals(localName))) {
                        namespaceURI2 = null;
                    }
                }
                if (namespaceURI2 != null && namespaceURI2.length() != 0) {
                    if (namespaceURI2.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) {
                        this.reportSchemaError("s4s-att-not-allowed", new Object[] { localName, name }, element);
                    }
                    else {
                        if (availableArray[XSAttributeChecker.ATTIDX_NONSCHEMA] == null) {
                            availableArray[XSAttributeChecker.ATTIDX_NONSCHEMA] = new Vector(4, 2);
                        }
                        ((Vector)availableArray[XSAttributeChecker.ATTIDX_NONSCHEMA]).addElement(name);
                        ((Vector)availableArray[XSAttributeChecker.ATTIDX_NONSCHEMA]).addElement(value);
                    }
                }
                else {
                    final OneAttr value2 = container.get(name);
                    if (value2 == null) {
                        this.reportSchemaError("s4s-att-not-allowed", new Object[] { localName, name }, element);
                    }
                    else {
                        this.fSeen[value2.valueIndex] = true;
                        try {
                            if (value2.dvIndex >= 0) {
                                if (value2.dvIndex != 3 && value2.dvIndex != 6 && value2.dvIndex != 7) {
                                    final XSSimpleType xsSimpleType = XSAttributeChecker.fExtraDVs[value2.dvIndex];
                                    if (value2.valueIndex == XSAttributeChecker.ATTIDX_SUBSGROUP) {
                                        if (availableArray[XSAttributeChecker.ATTIDX_SUBSGROUP] == null) {
                                            availableArray[XSAttributeChecker.ATTIDX_SUBSGROUP] = new Vector();
                                        }
                                        if (this.fSchemaHandler.fSchemaVersion == 4) {
                                            final StringTokenizer stringTokenizer = new StringTokenizer(value, " \n\t\r");
                                            while (stringTokenizer.hasMoreTokens()) {
                                                final Object validate = xsSimpleType.validate(stringTokenizer.nextToken(), xsDocumentInfo.fValidationContext, null);
                                                this.modifyQNameForChameleonProcessing(xsDocumentInfo, validate);
                                                ((Vector)availableArray[XSAttributeChecker.ATTIDX_SUBSGROUP]).addElement(validate);
                                            }
                                        }
                                        else {
                                            final Object validate2 = xsSimpleType.validate(value, xsDocumentInfo.fValidationContext, null);
                                            this.modifyQNameForChameleonProcessing(xsDocumentInfo, validate2);
                                            ((Vector)availableArray[XSAttributeChecker.ATTIDX_SUBSGROUP]).addElement(validate2);
                                        }
                                    }
                                    else {
                                        final Object validate3 = xsSimpleType.validate(value, xsDocumentInfo.fValidationContext, null);
                                        if (value2.dvIndex == 2) {
                                            this.modifyQNameForChameleonProcessing(xsDocumentInfo, validate3);
                                        }
                                        availableArray[value2.valueIndex] = validate3;
                                    }
                                }
                                else {
                                    availableArray[value2.valueIndex] = value;
                                }
                            }
                            else {
                                availableArray[value2.valueIndex] = this.validate(availableArray, name, value, value2.dvIndex, xsDocumentInfo);
                            }
                        }
                        catch (final InvalidDatatypeValueException ex) {
                            this.reportSchemaError("s4s-att-invalid-value", new Object[] { localName, name, ex.getMessage() }, element);
                            if (value2.dfltValue != null) {
                                availableArray[value2.valueIndex] = value2.dfltValue;
                            }
                        }
                        if (localName.equals(SchemaSymbols.ELT_ENUMERATION) && b2) {
                            availableArray[XSAttributeChecker.ATTIDX_ENUMNSDECLS] = new SchemaNamespaceSupport(xsDocumentInfo.fNamespaceSupport);
                        }
                    }
                }
            }
        }
        final OneAttr[] values = container.values;
        for (int j = 0; j < values.length; ++j) {
            final OneAttr oneAttr = values[j];
            if (oneAttr.dfltValue != null && !this.fSeen[oneAttr.valueIndex]) {
                availableArray[oneAttr.valueIndex] = oneAttr.dfltValue;
                n |= 1 << oneAttr.valueIndex;
            }
        }
        availableArray[XSAttributeChecker.ATTIDX_FROMDEFAULT] = new Long(n);
        if (availableArray[XSAttributeChecker.ATTIDX_MAXOCCURS] != null) {
            final int intValue = ((XInt)availableArray[XSAttributeChecker.ATTIDX_MINOCCURS]).intValue();
            final int intValue2 = ((XInt)availableArray[XSAttributeChecker.ATTIDX_MAXOCCURS]).intValue();
            if (intValue2 != -1 && intValue > intValue2) {
                this.reportSchemaError("p-props-correct.2.1", new Object[] { localName, availableArray[XSAttributeChecker.ATTIDX_MINOCCURS], availableArray[XSAttributeChecker.ATTIDX_MAXOCCURS] }, element);
                availableArray[XSAttributeChecker.ATTIDX_MINOCCURS] = availableArray[XSAttributeChecker.ATTIDX_MAXOCCURS];
            }
        }
        return availableArray;
    }
    
    private void modifyQNameForChameleonProcessing(final XSDocumentInfo xsDocumentInfo, final Object o) {
        final QName qName = (QName)o;
        if (qName.prefix == XMLSymbols.EMPTY_STRING && qName.uri == null && xsDocumentInfo.fIsChameleonSchema) {
            qName.uri = xsDocumentInfo.fTargetNamespace;
        }
    }
    
    private Object validate(final Object[] array, final String s, final String s2, final int n, final XSDocumentInfo xsDocumentInfo) throws InvalidDatatypeValueException {
        if (s2 == null) {
            return null;
        }
        String s3 = XMLChar.trim(s2);
        Object o = null;
        switch (n) {
            case -15: {
                if (s3.equals("false") || s3.equals("0")) {
                    o = Boolean.FALSE;
                    break;
                }
                if (s3.equals("true") || s3.equals("1")) {
                    o = Boolean.TRUE;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s3, "boolean" });
            }
            case -16: {
                try {
                    if (s3.length() > 0 && s3.charAt(0) == '+') {
                        s3 = s3.substring(1);
                    }
                    o = XSAttributeChecker.fXIntPool.getXInt(Integer.parseInt(s3));
                }
                catch (final NumberFormatException ex) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s3, "nonNegativeInteger" });
                }
                if (((XInt)o).intValue() < 0) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s3, "nonNegativeInteger" });
                }
                break;
            }
            case -17: {
                try {
                    if (s3.length() > 0 && s3.charAt(0) == '+') {
                        s3 = s3.substring(1);
                    }
                    o = XSAttributeChecker.fXIntPool.getXInt(Integer.parseInt(s3));
                }
                catch (final NumberFormatException ex2) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s3, "positiveInteger" });
                }
                if (((XInt)o).intValue() <= 0) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s3, "positiveInteger" });
                }
                break;
            }
            case -1: {
                int n2 = 0;
                if (s3.equals("#all")) {
                    n2 = 31;
                }
                else {
                    final StringTokenizer stringTokenizer = new StringTokenizer(s3, " \n\t\r");
                    while (stringTokenizer.hasMoreTokens()) {
                        final String nextToken = stringTokenizer.nextToken();
                        if (nextToken.equals("extension")) {
                            n2 |= 0x1;
                        }
                        else if (nextToken.equals("restriction")) {
                            n2 |= 0x2;
                        }
                        else {
                            if (!nextToken.equals("substitution")) {
                                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { s3, "(#all | List of (extension | restriction | substitution))" });
                            }
                            n2 |= 0x4;
                        }
                    }
                }
                o = XSAttributeChecker.fXIntPool.getXInt(n2);
                break;
            }
            case -3:
            case -2: {
                int n3 = 0;
                if (s3.equals("#all")) {
                    n3 = 31;
                }
                else {
                    final StringTokenizer stringTokenizer2 = new StringTokenizer(s3, " \n\t\r");
                    while (stringTokenizer2.hasMoreTokens()) {
                        final String nextToken2 = stringTokenizer2.nextToken();
                        if (nextToken2.equals("extension")) {
                            n3 |= 0x1;
                        }
                        else {
                            if (!nextToken2.equals("restriction")) {
                                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { s3, "(#all | List of (extension | restriction))" });
                            }
                            n3 |= 0x2;
                        }
                    }
                }
                o = XSAttributeChecker.fXIntPool.getXInt(n3);
                break;
            }
            case -4: {
                int n4 = 0;
                if (s3.equals("#all")) {
                    n4 = 31;
                }
                else {
                    final StringTokenizer stringTokenizer3 = new StringTokenizer(s3, " \n\t\r");
                    while (stringTokenizer3.hasMoreTokens()) {
                        final String nextToken3 = stringTokenizer3.nextToken();
                        if (nextToken3.equals("list")) {
                            n4 |= 0x10;
                        }
                        else if (nextToken3.equals("union")) {
                            n4 |= 0x8;
                        }
                        else {
                            if (!nextToken3.equals("restriction")) {
                                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { s3, "(#all | List of (list | union | restriction))" });
                            }
                            n4 |= 0x2;
                        }
                    }
                }
                o = XSAttributeChecker.fXIntPool.getXInt(n4);
                break;
            }
            case -5: {
                int n5 = 0;
                if (s3.equals("#all")) {
                    n5 = 31;
                }
                else {
                    final StringTokenizer stringTokenizer4 = new StringTokenizer(s3, " \n\t\r");
                    while (stringTokenizer4.hasMoreTokens()) {
                        final String nextToken4 = stringTokenizer4.nextToken();
                        if (nextToken4.equals("extension")) {
                            n5 |= 0x1;
                        }
                        else if (nextToken4.equals("restriction")) {
                            n5 |= 0x2;
                        }
                        else if (nextToken4.equals("list")) {
                            n5 |= 0x10;
                        }
                        else {
                            if (!nextToken4.equals("union")) {
                                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { s3, "(#all | List of (extension | restriction | list | union))" });
                            }
                            n5 |= 0x8;
                        }
                    }
                }
                o = XSAttributeChecker.fXIntPool.getXInt(n5);
                break;
            }
            case -6: {
                if (s3.equals("qualified")) {
                    o = XSAttributeChecker.INT_QUALIFIED;
                    break;
                }
                if (s3.equals("unqualified")) {
                    o = XSAttributeChecker.INT_UNQUALIFIED;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { s3, "(qualified | unqualified)" });
            }
            case -7: {
                if (s3.equals("unbounded")) {
                    o = XSAttributeChecker.INT_UNBOUNDED;
                    break;
                }
                try {
                    o = this.validate(array, s, s3, -16, xsDocumentInfo);
                    break;
                }
                catch (final NumberFormatException ex3) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { s3, "(nonNegativeInteger | unbounded)" });
                }
            }
            case -8: {
                if (s3.equals("1")) {
                    o = XSAttributeChecker.fXIntPool.getXInt(1);
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { s3, "(1)" });
            }
            case -9: {
                final Vector vector = new Vector<QName>();
                try {
                    final StringTokenizer stringTokenizer5 = new StringTokenizer(s3, " \n\t\r");
                    while (stringTokenizer5.hasMoreTokens()) {
                        final QName qName = (QName)XSAttributeChecker.fExtraDVs[2].validate(stringTokenizer5.nextToken(), xsDocumentInfo.fValidationContext, null);
                        if (qName.prefix == XMLSymbols.EMPTY_STRING && qName.uri == null && xsDocumentInfo.fIsChameleonSchema) {
                            qName.uri = xsDocumentInfo.fTargetNamespace;
                        }
                        vector.addElement((Object)qName);
                    }
                    o = vector;
                    break;
                }
                catch (final InvalidDatatypeValueException ex4) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.2", new Object[] { s3, "(List of QName)" });
                }
            }
            case -10: {
                if (s3.equals("0")) {
                    o = XSAttributeChecker.fXIntPool.getXInt(0);
                    break;
                }
                if (s3.equals("1")) {
                    o = XSAttributeChecker.fXIntPool.getXInt(1);
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { s3, "(0 | 1)" });
            }
            case -11: {
                if (s3.equals("##any")) {
                    o = XSAttributeChecker.INT_ANY_ANY;
                    break;
                }
                if (s3.equals("##other")) {
                    o = XSAttributeChecker.INT_ANY_NOT;
                    array[XSAttributeChecker.ATTIDX_NAMESPACE_LIST] = new String[] { xsDocumentInfo.fTargetNamespace, null };
                    break;
                }
                o = XSAttributeChecker.INT_ANY_LIST;
                array[XSAttributeChecker.ATTIDX_NAMESPACE_LIST] = this.processNamespaceList(s3, xsDocumentInfo);
                break;
            }
            case -12: {
                if (s3.equals("strict")) {
                    o = XSAttributeChecker.INT_ANY_STRICT;
                    break;
                }
                if (s3.equals("lax")) {
                    o = XSAttributeChecker.INT_ANY_LAX;
                    break;
                }
                if (s3.equals("skip")) {
                    o = XSAttributeChecker.INT_ANY_SKIP;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { s3, "(lax | skip | strict)" });
            }
            case -13: {
                if (s3.equals("optional")) {
                    o = XSAttributeChecker.INT_USE_OPTIONAL;
                    break;
                }
                if (s3.equals("required")) {
                    o = XSAttributeChecker.INT_USE_REQUIRED;
                    break;
                }
                if (s3.equals("prohibited")) {
                    o = XSAttributeChecker.INT_USE_PROHIBITED;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { s3, "(optional | prohibited | required)" });
            }
            case -20: {
                if (s3.equals("none")) {
                    o = XSAttributeChecker.INT_MODE_NONE;
                    break;
                }
            }
            case -19: {
                if (s3.equals("interleave")) {
                    o = XSAttributeChecker.INT_MODE_INTERLEAVE;
                    break;
                }
                if (s3.equals("suffix")) {
                    o = XSAttributeChecker.INT_MODE_SUFFIX;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", (n == -20) ? new Object[] { s3, "(none | interleave | suffix)" } : new Object[] { s3, "(interleave | sufix)" });
            }
            case -22: {
                if (array[XSAttributeChecker.ATTIDX_NOTQNAME] == null) {
                    array[XSAttributeChecker.ATTIDX_NOTQNAME] = new Vector();
                }
                final Vector vector2 = (Vector)array[XSAttributeChecker.ATTIDX_NOTQNAME];
                final StringTokenizer stringTokenizer6 = new StringTokenizer(s3, " \n\t\r");
                Boolean b = Boolean.FALSE;
                Boolean b2 = Boolean.FALSE;
                try {
                    while (stringTokenizer6.hasMoreTokens()) {
                        final String nextToken5 = stringTokenizer6.nextToken();
                        if (nextToken5.equals("##defined")) {
                            b = Boolean.TRUE;
                        }
                        else if (nextToken5.equals("##definedSibling")) {
                            b2 = Boolean.TRUE;
                        }
                        else {
                            final QName qName2 = (QName)XSAttributeChecker.fExtraDVs[2].validate(nextToken5, xsDocumentInfo.fValidationContext, null);
                            if (vector2.contains(qName2)) {
                                continue;
                            }
                            vector2.addElement(qName2);
                        }
                    }
                    final QName[] array2 = new QName[vector2.size()];
                    vector2.copyInto(array2);
                    vector2.clear();
                    vector2.add(array2);
                    vector2.add(b);
                    vector2.add(b2);
                }
                catch (final InvalidDatatypeValueException ex5) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { s3, "(List of (QName | ##defined) )" });
                }
                o = vector2;
                break;
            }
            case -21: {
                o = this.processNamespaceList(s3, xsDocumentInfo);
                break;
            }
            case -14: {
                if (s3.equals("preserve")) {
                    o = XSAttributeChecker.INT_WS_PRESERVE;
                    break;
                }
                if (s3.equals("replace")) {
                    o = XSAttributeChecker.INT_WS_REPLACE;
                    break;
                }
                if (s3.equals("collapse")) {
                    o = XSAttributeChecker.INT_WS_COLLAPSE;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { s3, "(preserve | replace | collapse)" });
            }
            case -18: {
                o = null;
                if (s3.equals("##targetNamespace")) {
                    o = xsDocumentInfo.fTargetNamespace;
                    break;
                }
                if (s3.equals("##defaultNamespace")) {
                    o = xsDocumentInfo.fValidationContext.getURI(XMLSymbols.EMPTY_STRING);
                    if (o != null) {
                        o = this.fSymbolTable.addSymbol((String)o);
                    }
                    array[XSAttributeChecker.ATTIDX_XPATHDEFAULTNS_TWOPOUNDDFLT] = Boolean.TRUE;
                    break;
                }
                if (!s3.equals("##local")) {
                    try {
                        XSAttributeChecker.fExtraDVs[0].validate(s3, xsDocumentInfo.fValidationContext, null);
                        o = this.fSymbolTable.addSymbol(s3);
                        break;
                    }
                    catch (final InvalidDatatypeValueException ex6) {
                        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { s3, "anyURI | ##defaultNamespace | ##targetNamespace | ##local" });
                    }
                }
                break;
            }
            case -23: {
                boolean b3 = false;
                try {
                    if (s3.length() > 0 && s3.charAt(0) == '+') {
                        b3 = true;
                        s3 = s3.substring(1);
                    }
                    o = XSAttributeChecker.fXIntPool.getXInt(Integer.parseInt(s3));
                }
                catch (final NumberFormatException ex7) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s3, "Integer" });
                }
                if (b3 && ((XInt)o).intValue() < 0) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { "+" + s3, "Integer" });
                }
                break;
            }
            case -24: {
                if (s3.equals("optional")) {
                    o = XSAttributeChecker.INT_ET_OPTION;
                    break;
                }
                if (s3.equals("required")) {
                    o = XSAttributeChecker.INT_ET_REQUIRED;
                    break;
                }
                if (s3.equals("prohibited")) {
                    o = XSAttributeChecker.INT_ET_PROHIBITED;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[] { s3, "(optional | required | prohibited)" });
            }
        }
        return o;
    }
    
    private String[] processNamespaceList(final String s, final XSDocumentInfo xsDocumentInfo) throws InvalidDatatypeValueException {
        this.fNamespaceList.removeAllElements();
        final StringTokenizer stringTokenizer = new StringTokenizer(s, " \n\t\r");
        try {
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                String s2;
                if (nextToken.equals("##local")) {
                    s2 = null;
                }
                else if (nextToken.equals("##targetNamespace")) {
                    s2 = xsDocumentInfo.fTargetNamespace;
                }
                else {
                    XSAttributeChecker.fExtraDVs[0].validate(nextToken, xsDocumentInfo.fValidationContext, null);
                    s2 = this.fSymbolTable.addSymbol(nextToken);
                }
                if (!this.fNamespaceList.contains(s2)) {
                    this.fNamespaceList.addElement(s2);
                }
            }
        }
        catch (final InvalidDatatypeValueException ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[] { s, "((##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) )" });
        }
        final String[] array = new String[this.fNamespaceList.size()];
        this.fNamespaceList.copyInto(array);
        return array;
    }
    
    void reportSchemaError(final String s, final Object[] array, final Element element) {
        this.fSchemaHandler.reportSchemaError(s, array, element);
    }
    
    public void checkNonSchemaAttributes(final XSGrammarBucket xsGrammarBucket) {
        final Iterator iterator = this.fNonSchemaAttrs.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = (Map.Entry)iterator.next();
            final String s = (String)entry.getKey();
            final String substring = s.substring(0, s.indexOf(44));
            final String substring2 = s.substring(s.indexOf(44) + 1);
            final SchemaGrammar grammar = xsGrammarBucket.getGrammar(substring);
            if (grammar == null) {
                continue;
            }
            final XSAttributeDecl globalAttributeDecl = grammar.getGlobalAttributeDecl(substring2);
            if (globalAttributeDecl == null) {
                continue;
            }
            final XSSimpleType xsSimpleType = (XSSimpleType)globalAttributeDecl.getTypeDefinition();
            if (xsSimpleType == null) {
                continue;
            }
            final Vector vector = (Vector)entry.getValue();
            final String s2 = (String)vector.elementAt(0);
            for (int size = vector.size(), i = 1; i < size; i += 2) {
                final String s3 = (String)vector.elementAt(i);
                try {
                    xsSimpleType.validate((String)vector.elementAt(i + 1), null, null);
                }
                catch (final InvalidDatatypeValueException ex) {
                    this.reportSchemaError("s4s-att-invalid-value", new Object[] { s3, s2, ex.getMessage() }, null);
                }
            }
        }
    }
    
    public static String normalize(final String s, final short n) {
        final int n2 = (s == null) ? 0 : s.length();
        if (n2 == 0 || n == 0) {
            return s;
        }
        final StringBuffer sb = new StringBuffer();
        if (n == 1) {
            for (int i = 0; i < n2; ++i) {
                final char char1 = s.charAt(i);
                if (char1 != '\t' && char1 != '\n' && char1 != '\r') {
                    sb.append(char1);
                }
                else {
                    sb.append(' ');
                }
            }
        }
        else {
            boolean b = true;
            for (int j = 0; j < n2; ++j) {
                final char char2 = s.charAt(j);
                if (char2 != '\t' && char2 != '\n' && char2 != '\r' && char2 != ' ') {
                    sb.append(char2);
                    b = false;
                }
                else {
                    while (j < n2 - 1) {
                        final char char3 = s.charAt(j + 1);
                        if (char3 != '\t' && char3 != '\n' && char3 != '\r' && char3 != ' ') {
                            break;
                        }
                        ++j;
                    }
                    if (j < n2 - 1 && !b) {
                        sb.append(' ');
                    }
                }
            }
        }
        return sb.toString();
    }
    
    protected Object[] getAvailableArray() {
        if (this.fArrayPool.length == this.fPoolPos) {
            this.fArrayPool = new Object[this.fPoolPos + 10][];
            for (int i = this.fPoolPos; i < this.fArrayPool.length; ++i) {
                this.fArrayPool[i] = new Object[XSAttributeChecker.ATTIDX_COUNT];
            }
        }
        final Object[] array = this.fArrayPool[this.fPoolPos];
        this.fArrayPool[this.fPoolPos++] = null;
        System.arraycopy(XSAttributeChecker.fTempArray, 0, array, 0, XSAttributeChecker.ATTIDX_COUNT - 1);
        array[XSAttributeChecker.ATTIDX_ISRETURNED] = Boolean.FALSE;
        array[XSAttributeChecker.ATTIDX_XPATHDEFAULTNS_TWOPOUNDDFLT] = Boolean.FALSE;
        return array;
    }
    
    public void returnAttrArray(final Object[] array, final XSDocumentInfo xsDocumentInfo) {
        if (xsDocumentInfo != null) {
            xsDocumentInfo.fNamespaceSupport.popContext();
        }
        if (this.fPoolPos == 0 || array == null || array.length != XSAttributeChecker.ATTIDX_COUNT || (boolean)array[XSAttributeChecker.ATTIDX_ISRETURNED]) {
            return;
        }
        array[XSAttributeChecker.ATTIDX_ISRETURNED] = Boolean.TRUE;
        if (array[XSAttributeChecker.ATTIDX_NONSCHEMA] != null) {
            ((Vector)array[XSAttributeChecker.ATTIDX_NONSCHEMA]).clear();
        }
        if (array[XSAttributeChecker.ATTIDX_SUBSGROUP] != null) {
            ((Vector)array[XSAttributeChecker.ATTIDX_SUBSGROUP]).clear();
        }
        if (array[XSAttributeChecker.ATTIDX_NOTQNAME] != null) {
            ((Vector)array[XSAttributeChecker.ATTIDX_NOTQNAME]).clear();
        }
        this.fArrayPool[--this.fPoolPos] = array;
    }
    
    public void resolveNamespace(final Element element, final Attr[] array, final SchemaNamespaceSupport schemaNamespaceSupport) {
        schemaNamespaceSupport.pushContext();
        for (final Attr attr : array) {
            final String name = DOMUtil.getName(attr);
            String s = null;
            if (name.equals(XMLSymbols.PREFIX_XMLNS)) {
                s = XMLSymbols.EMPTY_STRING;
            }
            else if (name.startsWith("xmlns:")) {
                s = this.fSymbolTable.addSymbol(DOMUtil.getLocalName(attr));
            }
            if (s != null) {
                final String addSymbol = this.fSymbolTable.addSymbol(DOMUtil.getValue(attr));
                schemaNamespaceSupport.declarePrefix(s, (addSymbol.length() != 0) ? addSymbol : null);
            }
        }
    }
    
    static {
        XSAttributeChecker.ATTIDX_COUNT = 0;
        ATTIDX_ABSTRACT = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_AFORMDEFAULT = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_BASE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_BLOCK = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_BLOCKDEFAULT = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_DEFAULT = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_EFORMDEFAULT = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_FINAL = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_FINALDEFAULT = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_FIXED = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_FORM = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_ID = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_ITEMTYPE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_MAXOCCURS = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_MEMBERTYPES = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_MINOCCURS = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_MIXED = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_NAME = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_NAMESPACE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_NAMESPACE_LIST = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_NILLABLE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_NONSCHEMA = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_PROCESSCONTENTS = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_PUBLIC = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_REF = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_REFER = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_SCHEMALOCATION = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_SOURCE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_SUBSGROUP = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_SYSTEM = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_TARGETNAMESPACE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_TYPE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_USE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_VALUE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_ENUMNSDECLS = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_VERSION = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_XML_LANG = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_XPATH = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_FROMDEFAULT = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_ISRETURNED = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_APPLIESTOEMPTY = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_DEFAULTATTRAPPLY = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_DEFAULTATTRIBUTES = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_MODE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_NOTNAMESPACE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_NOTQNAME = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_XPATHDEFAULTNS = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_INHERITABLE = XSAttributeChecker.ATTIDX_COUNT++;
        ATTIDX_XPATHDEFAULTNS_TWOPOUNDDFLT = XSAttributeChecker.ATTIDX_COUNT++;
        fXIntPool = new XIntPool();
        INT_QUALIFIED = XSAttributeChecker.fXIntPool.getXInt(1);
        INT_UNQUALIFIED = XSAttributeChecker.fXIntPool.getXInt(0);
        INT_EMPTY_SET = XSAttributeChecker.fXIntPool.getXInt(0);
        INT_ANY_STRICT = XSAttributeChecker.fXIntPool.getXInt(1);
        INT_ANY_LAX = XSAttributeChecker.fXIntPool.getXInt(3);
        INT_ANY_SKIP = XSAttributeChecker.fXIntPool.getXInt(2);
        INT_ANY_ANY = XSAttributeChecker.fXIntPool.getXInt(1);
        INT_ANY_LIST = XSAttributeChecker.fXIntPool.getXInt(3);
        INT_ANY_NOT = XSAttributeChecker.fXIntPool.getXInt(2);
        INT_USE_OPTIONAL = XSAttributeChecker.fXIntPool.getXInt(0);
        INT_USE_REQUIRED = XSAttributeChecker.fXIntPool.getXInt(1);
        INT_USE_PROHIBITED = XSAttributeChecker.fXIntPool.getXInt(2);
        INT_WS_PRESERVE = XSAttributeChecker.fXIntPool.getXInt(0);
        INT_WS_REPLACE = XSAttributeChecker.fXIntPool.getXInt(1);
        INT_WS_COLLAPSE = XSAttributeChecker.fXIntPool.getXInt(2);
        INT_ET_OPTION = XSAttributeChecker.fXIntPool.getXInt(0);
        INT_ET_REQUIRED = XSAttributeChecker.fXIntPool.getXInt(1);
        INT_ET_PROHIBITED = XSAttributeChecker.fXIntPool.getXInt(2);
        INT_UNBOUNDED = XSAttributeChecker.fXIntPool.getXInt(-1);
        INT_MODE_NONE = XSAttributeChecker.fXIntPool.getXInt(0);
        INT_MODE_INTERLEAVE = XSAttributeChecker.fXIntPool.getXInt(1);
        INT_MODE_SUFFIX = XSAttributeChecker.fXIntPool.getXInt(2);
        fEleAttrsMapG = new Hashtable(29);
        fEleAttrsMapL = new Hashtable(79);
        fEleAttrs11MapG = new Hashtable(31);
        fEleAttrs11MapL = new Hashtable(102);
        fExtraDVs = new XSSimpleType[9];
        final SchemaGrammar s4SGrammar = SchemaGrammar.getS4SGrammar((short)1);
        XSAttributeChecker.fExtraDVs[0] = (XSSimpleType)s4SGrammar.getGlobalTypeDecl("anyURI");
        XSAttributeChecker.fExtraDVs[1] = (XSSimpleType)s4SGrammar.getGlobalTypeDecl("ID");
        XSAttributeChecker.fExtraDVs[2] = (XSSimpleType)s4SGrammar.getGlobalTypeDecl("QName");
        XSAttributeChecker.fExtraDVs[3] = (XSSimpleType)s4SGrammar.getGlobalTypeDecl("string");
        XSAttributeChecker.fExtraDVs[4] = (XSSimpleType)s4SGrammar.getGlobalTypeDecl("token");
        XSAttributeChecker.fExtraDVs[5] = (XSSimpleType)s4SGrammar.getGlobalTypeDecl("NCName");
        XSAttributeChecker.fExtraDVs[6] = XSAttributeChecker.fExtraDVs[3];
        XSAttributeChecker.fExtraDVs[6] = XSAttributeChecker.fExtraDVs[3];
        XSAttributeChecker.fExtraDVs[8] = (XSSimpleType)s4SGrammar.getGlobalTypeDecl("language");
        int n = 0;
        final int n2 = n++;
        final int n3 = n++;
        final int n4 = n++;
        final int n5 = n++;
        final int n6 = n++;
        final int n7 = n++;
        final int n8 = n++;
        final int n9 = n++;
        final int n10 = n++;
        final int n11 = n++;
        final int n12 = n++;
        final int n13 = n++;
        final int n14 = n++;
        final int n15 = n++;
        final int n16 = n++;
        final int n17 = n++;
        final int n18 = n++;
        final int n19 = n++;
        final int n20 = n++;
        final int n21 = n++;
        final int n22 = n++;
        final int n23 = n++;
        final int n24 = n++;
        final int n25 = n++;
        final int n26 = n++;
        final int n27 = n++;
        final int n28 = n++;
        final int n29 = n++;
        final int n30 = n++;
        final int n31 = n++;
        final int n32 = n++;
        final int n33 = n++;
        final int n34 = n++;
        final int n35 = n++;
        final int n36 = n++;
        final int n37 = n++;
        final int n38 = n++;
        final int n39 = n++;
        final int n40 = n++;
        final int n41 = n++;
        final int n42 = n++;
        final int n43 = n++;
        final int n44 = n++;
        final int n45 = n++;
        final int n46 = n++;
        final int n47 = n++;
        final int n48 = n++;
        final int n49 = n++;
        final int n50 = n++;
        final int n51 = n++;
        final int n52 = n++;
        final int n53 = n++;
        final int n54 = n++;
        final int n55 = n++;
        final int n56 = n++;
        final int n57 = n++;
        final int n58 = n++;
        final int n59 = n++;
        final int n60 = n++;
        final int n61 = n++;
        final int n62 = n++;
        final OneAttr[] array = new OneAttr[n];
        array[n2] = new OneAttr(SchemaSymbols.ATT_ABSTRACT, -15, XSAttributeChecker.ATTIDX_ABSTRACT, Boolean.FALSE);
        array[n3] = new OneAttr(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, -6, XSAttributeChecker.ATTIDX_AFORMDEFAULT, XSAttributeChecker.INT_UNQUALIFIED);
        array[n4] = new OneAttr(SchemaSymbols.ATT_BASE, 2, XSAttributeChecker.ATTIDX_BASE, null);
        array[n5] = new OneAttr(SchemaSymbols.ATT_BASE, 2, XSAttributeChecker.ATTIDX_BASE, null);
        array[n6] = new OneAttr(SchemaSymbols.ATT_BLOCK, -1, XSAttributeChecker.ATTIDX_BLOCK, null);
        array[n7] = new OneAttr(SchemaSymbols.ATT_BLOCK, -2, XSAttributeChecker.ATTIDX_BLOCK, null);
        array[n8] = new OneAttr(SchemaSymbols.ATT_BLOCKDEFAULT, -1, XSAttributeChecker.ATTIDX_BLOCKDEFAULT, XSAttributeChecker.INT_EMPTY_SET);
        array[n9] = new OneAttr(SchemaSymbols.ATT_DEFAULT, 3, XSAttributeChecker.ATTIDX_DEFAULT, null);
        array[n10] = new OneAttr(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, -6, XSAttributeChecker.ATTIDX_EFORMDEFAULT, XSAttributeChecker.INT_UNQUALIFIED);
        array[n11] = new OneAttr(SchemaSymbols.ATT_FINAL, -3, XSAttributeChecker.ATTIDX_FINAL, null);
        array[n12] = new OneAttr(SchemaSymbols.ATT_FINAL, -4, XSAttributeChecker.ATTIDX_FINAL, null);
        array[n13] = new OneAttr(SchemaSymbols.ATT_FINALDEFAULT, -5, XSAttributeChecker.ATTIDX_FINALDEFAULT, XSAttributeChecker.INT_EMPTY_SET);
        array[n14] = new OneAttr(SchemaSymbols.ATT_FIXED, 3, XSAttributeChecker.ATTIDX_FIXED, null);
        array[n15] = new OneAttr(SchemaSymbols.ATT_FIXED, -15, XSAttributeChecker.ATTIDX_FIXED, Boolean.FALSE);
        array[n16] = new OneAttr(SchemaSymbols.ATT_FORM, -6, XSAttributeChecker.ATTIDX_FORM, null);
        array[n17] = new OneAttr(SchemaSymbols.ATT_ID, 1, XSAttributeChecker.ATTIDX_ID, null);
        array[n18] = new OneAttr(SchemaSymbols.ATT_ITEMTYPE, 2, XSAttributeChecker.ATTIDX_ITEMTYPE, null);
        array[n19] = new OneAttr(SchemaSymbols.ATT_MAXOCCURS, -7, XSAttributeChecker.ATTIDX_MAXOCCURS, XSAttributeChecker.fXIntPool.getXInt(1));
        array[n20] = new OneAttr(SchemaSymbols.ATT_MAXOCCURS, -8, XSAttributeChecker.ATTIDX_MAXOCCURS, XSAttributeChecker.fXIntPool.getXInt(1));
        array[n21] = new OneAttr(SchemaSymbols.ATT_MEMBERTYPES, -9, XSAttributeChecker.ATTIDX_MEMBERTYPES, null);
        array[n22] = new OneAttr(SchemaSymbols.ATT_MINOCCURS, -16, XSAttributeChecker.ATTIDX_MINOCCURS, XSAttributeChecker.fXIntPool.getXInt(1));
        array[n23] = new OneAttr(SchemaSymbols.ATT_MINOCCURS, -10, XSAttributeChecker.ATTIDX_MINOCCURS, XSAttributeChecker.fXIntPool.getXInt(1));
        array[n24] = new OneAttr(SchemaSymbols.ATT_MIXED, -15, XSAttributeChecker.ATTIDX_MIXED, Boolean.FALSE);
        array[n25] = new OneAttr(SchemaSymbols.ATT_MIXED, -15, XSAttributeChecker.ATTIDX_MIXED, null);
        array[n26] = new OneAttr(SchemaSymbols.ATT_NAME, 5, XSAttributeChecker.ATTIDX_NAME, null);
        array[n27] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, -11, XSAttributeChecker.ATTIDX_NAMESPACE, XSAttributeChecker.INT_ANY_ANY);
        array[n28] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, 0, XSAttributeChecker.ATTIDX_NAMESPACE, null);
        array[n29] = new OneAttr(SchemaSymbols.ATT_NILLABLE, -15, XSAttributeChecker.ATTIDX_NILLABLE, Boolean.FALSE);
        array[n30] = new OneAttr(SchemaSymbols.ATT_PROCESSCONTENTS, -12, XSAttributeChecker.ATTIDX_PROCESSCONTENTS, XSAttributeChecker.INT_ANY_STRICT);
        array[n31] = new OneAttr(SchemaSymbols.ATT_PUBLIC, 4, XSAttributeChecker.ATTIDX_PUBLIC, null);
        array[n32] = new OneAttr(SchemaSymbols.ATT_REF, 2, XSAttributeChecker.ATTIDX_REF, null);
        array[n33] = new OneAttr(SchemaSymbols.ATT_REFER, 2, XSAttributeChecker.ATTIDX_REFER, null);
        array[n34] = new OneAttr(SchemaSymbols.ATT_SCHEMALOCATION, 0, XSAttributeChecker.ATTIDX_SCHEMALOCATION, null);
        array[n35] = new OneAttr(SchemaSymbols.ATT_SCHEMALOCATION, 0, XSAttributeChecker.ATTIDX_SCHEMALOCATION, null);
        array[n36] = new OneAttr(SchemaSymbols.ATT_SOURCE, 0, XSAttributeChecker.ATTIDX_SOURCE, null);
        array[n37] = new OneAttr(SchemaSymbols.ATT_SUBSTITUTIONGROUP, 2, XSAttributeChecker.ATTIDX_SUBSGROUP, null);
        array[n38] = new OneAttr(SchemaSymbols.ATT_SYSTEM, 0, XSAttributeChecker.ATTIDX_SYSTEM, null);
        array[n39] = new OneAttr(SchemaSymbols.ATT_TARGETNAMESPACE, 0, XSAttributeChecker.ATTIDX_TARGETNAMESPACE, null);
        array[n40] = new OneAttr(SchemaSymbols.ATT_TYPE, 2, XSAttributeChecker.ATTIDX_TYPE, null);
        array[n41] = new OneAttr(SchemaSymbols.ATT_USE, -13, XSAttributeChecker.ATTIDX_USE, XSAttributeChecker.INT_USE_OPTIONAL);
        array[n42] = new OneAttr(SchemaSymbols.ATT_VALUE, -16, XSAttributeChecker.ATTIDX_VALUE, null);
        array[n43] = new OneAttr(SchemaSymbols.ATT_VALUE, -17, XSAttributeChecker.ATTIDX_VALUE, null);
        array[n44] = new OneAttr(SchemaSymbols.ATT_VALUE, 3, XSAttributeChecker.ATTIDX_VALUE, null);
        array[n45] = new OneAttr(SchemaSymbols.ATT_VALUE, -14, XSAttributeChecker.ATTIDX_VALUE, null);
        array[n46] = new OneAttr(SchemaSymbols.ATT_VERSION, 4, XSAttributeChecker.ATTIDX_VERSION, null);
        array[n47] = new OneAttr(SchemaSymbols.ATT_XML_LANG, 8, XSAttributeChecker.ATTIDX_XML_LANG, null);
        array[n48] = new OneAttr(SchemaSymbols.ATT_XPATH, 6, XSAttributeChecker.ATTIDX_XPATH, null);
        array[n49] = new OneAttr(SchemaSymbols.ATT_XPATH, 7, XSAttributeChecker.ATTIDX_XPATH, null);
        array[n50] = new OneAttr(SchemaSymbols.ATT_VALUE, -23, XSAttributeChecker.ATTIDX_VALUE, null);
        array[n51] = new OneAttr(SchemaSymbols.ATT_APPLIESTOEMPTY, -15, XSAttributeChecker.ATTIDX_APPLIESTOEMPTY, Boolean.FALSE);
        array[n52] = new OneAttr(SchemaSymbols.ATT_DEFAULTATTRIBUTES, 2, XSAttributeChecker.ATTIDX_DEFAULTATTRIBUTES, null);
        array[n53] = new OneAttr(SchemaSymbols.ATT_DEFAULTATTRIBUTESAPPLY, -15, XSAttributeChecker.ATTIDX_DEFAULTATTRAPPLY, Boolean.TRUE);
        array[n54] = new OneAttr(SchemaSymbols.ATT_XPATH_DEFAULT_NS, -18, XSAttributeChecker.ATTIDX_XPATHDEFAULTNS, null);
        array[n55] = new OneAttr(SchemaSymbols.ATT_MODE, -19, XSAttributeChecker.ATTIDX_MODE, XSAttributeChecker.INT_MODE_INTERLEAVE);
        array[n56] = new OneAttr(SchemaSymbols.ATT_MODE, -20, XSAttributeChecker.ATTIDX_MODE, XSAttributeChecker.INT_MODE_INTERLEAVE);
        array[n57] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, -11, XSAttributeChecker.ATTIDX_NAMESPACE, null);
        array[n58] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, -21, XSAttributeChecker.ATTIDX_NOTNAMESPACE, null);
        array[n59] = new OneAttr(SchemaSymbols.ATT_NOTQNAME, -22, XSAttributeChecker.ATTIDX_NOTQNAME, null);
        array[n60] = new OneAttr(SchemaSymbols.ATT_TEST, 7, XSAttributeChecker.ATTIDX_XPATH, null);
        array[n61] = new OneAttr(SchemaSymbols.ATT_INHERITABLE, -15, XSAttributeChecker.ATTIDX_INHERITABLE, Boolean.FALSE);
        array[n62] = new OneAttr(SchemaSymbols.ATT_VALUE, -24, XSAttributeChecker.ATTIDX_VALUE, null);
        final Container container = Container.getContainer(5);
        container.put(SchemaSymbols.ATT_DEFAULT, array[n9]);
        container.put(SchemaSymbols.ATT_FIXED, array[n14]);
        container.put(SchemaSymbols.ATT_ID, array[n17]);
        container.put(SchemaSymbols.ATT_NAME, array[n26]);
        container.put(SchemaSymbols.ATT_TYPE, array[n40]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_ATTRIBUTE, container);
        final Container container2 = Container.getContainer(6);
        container2.put(SchemaSymbols.ATT_DEFAULT, array[n9]);
        container2.put(SchemaSymbols.ATT_FIXED, array[n14]);
        container2.put(SchemaSymbols.ATT_ID, array[n17]);
        container2.put(SchemaSymbols.ATT_NAME, array[n26]);
        container2.put(SchemaSymbols.ATT_TYPE, array[n40]);
        container2.put(SchemaSymbols.ATT_INHERITABLE, array[n61]);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_ATTRIBUTE, container2);
        final Container container3 = Container.getContainer(7);
        container3.put(SchemaSymbols.ATT_DEFAULT, array[n9]);
        container3.put(SchemaSymbols.ATT_FIXED, array[n14]);
        container3.put(SchemaSymbols.ATT_FORM, array[n16]);
        container3.put(SchemaSymbols.ATT_ID, array[n17]);
        container3.put(SchemaSymbols.ATT_NAME, array[n26]);
        container3.put(SchemaSymbols.ATT_TYPE, array[n40]);
        container3.put(SchemaSymbols.ATT_USE, array[n41]);
        XSAttributeChecker.fEleAttrsMapL.put("attribute_n", container3);
        final Container container4 = Container.getContainer(5);
        container4.put(SchemaSymbols.ATT_DEFAULT, array[n9]);
        container4.put(SchemaSymbols.ATT_FIXED, array[n14]);
        container4.put(SchemaSymbols.ATT_ID, array[n17]);
        container4.put(SchemaSymbols.ATT_REF, array[n32]);
        container4.put(SchemaSymbols.ATT_USE, array[n41]);
        XSAttributeChecker.fEleAttrsMapL.put("attribute_r", container4);
        final Container container5 = Container.getContainer(6);
        container5.put(SchemaSymbols.ATT_DEFAULT, array[n9]);
        container5.put(SchemaSymbols.ATT_FIXED, array[n14]);
        container5.put(SchemaSymbols.ATT_ID, array[n17]);
        container5.put(SchemaSymbols.ATT_REF, array[n32]);
        container5.put(SchemaSymbols.ATT_USE, array[n41]);
        container5.put(SchemaSymbols.ATT_INHERITABLE, array[n61]);
        XSAttributeChecker.fEleAttrs11MapL.put("attribute_r", container5);
        final Container container6 = Container.getContainer(10);
        container6.put(SchemaSymbols.ATT_ABSTRACT, array[n2]);
        container6.put(SchemaSymbols.ATT_BLOCK, array[n6]);
        container6.put(SchemaSymbols.ATT_DEFAULT, array[n9]);
        container6.put(SchemaSymbols.ATT_FINAL, array[n11]);
        container6.put(SchemaSymbols.ATT_FIXED, array[n14]);
        container6.put(SchemaSymbols.ATT_ID, array[n17]);
        container6.put(SchemaSymbols.ATT_NAME, array[n26]);
        container6.put(SchemaSymbols.ATT_NILLABLE, array[n29]);
        container6.put(SchemaSymbols.ATT_SUBSTITUTIONGROUP, array[n37]);
        container6.put(SchemaSymbols.ATT_TYPE, array[n40]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_ELEMENT, container6);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_ELEMENT, container6);
        final Container container7 = Container.getContainer(10);
        container7.put(SchemaSymbols.ATT_BLOCK, array[n6]);
        container7.put(SchemaSymbols.ATT_DEFAULT, array[n9]);
        container7.put(SchemaSymbols.ATT_FIXED, array[n14]);
        container7.put(SchemaSymbols.ATT_FORM, array[n16]);
        container7.put(SchemaSymbols.ATT_ID, array[n17]);
        container7.put(SchemaSymbols.ATT_MAXOCCURS, array[n19]);
        container7.put(SchemaSymbols.ATT_MINOCCURS, array[n22]);
        container7.put(SchemaSymbols.ATT_NAME, array[n26]);
        container7.put(SchemaSymbols.ATT_NILLABLE, array[n29]);
        container7.put(SchemaSymbols.ATT_TYPE, array[n40]);
        XSAttributeChecker.fEleAttrsMapL.put("element_n", container7);
        final Container container8 = Container.getContainer(4);
        container8.put(SchemaSymbols.ATT_ID, array[n17]);
        container8.put(SchemaSymbols.ATT_MAXOCCURS, array[n19]);
        container8.put(SchemaSymbols.ATT_MINOCCURS, array[n22]);
        container8.put(SchemaSymbols.ATT_REF, array[n32]);
        XSAttributeChecker.fEleAttrsMapL.put("element_r", container8);
        XSAttributeChecker.fEleAttrs11MapL.put("element_r", container8);
        final Container container9 = Container.getContainer(6);
        container9.put(SchemaSymbols.ATT_ABSTRACT, array[n2]);
        container9.put(SchemaSymbols.ATT_BLOCK, array[n7]);
        container9.put(SchemaSymbols.ATT_FINAL, array[n11]);
        container9.put(SchemaSymbols.ATT_ID, array[n17]);
        container9.put(SchemaSymbols.ATT_MIXED, array[n24]);
        container9.put(SchemaSymbols.ATT_NAME, array[n26]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_COMPLEXTYPE, container9);
        final Container container10 = Container.getContainer(4);
        container10.put(SchemaSymbols.ATT_ID, array[n17]);
        container10.put(SchemaSymbols.ATT_NAME, array[n26]);
        container10.put(SchemaSymbols.ATT_PUBLIC, array[n31]);
        container10.put(SchemaSymbols.ATT_SYSTEM, array[n38]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_NOTATION, container10);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_NOTATION, container10);
        final Container container11 = Container.getContainer(2);
        container11.put(SchemaSymbols.ATT_ID, array[n17]);
        container11.put(SchemaSymbols.ATT_MIXED, array[n24]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_COMPLEXTYPE, container11);
        final Container container12 = Container.getContainer(1);
        container12.put(SchemaSymbols.ATT_ID, array[n17]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_SIMPLECONTENT, container12);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_SIMPLECONTENT, container12);
        final Container container13 = Container.getContainer(2);
        container13.put(SchemaSymbols.ATT_BASE, array[n5]);
        container13.put(SchemaSymbols.ATT_ID, array[n17]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_RESTRICTION, container13);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_RESTRICTION, container13);
        final Container container14 = Container.getContainer(2);
        container14.put(SchemaSymbols.ATT_BASE, array[n4]);
        container14.put(SchemaSymbols.ATT_ID, array[n17]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_EXTENSION, container14);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_EXTENSION, container14);
        final Container container15 = Container.getContainer(2);
        container15.put(SchemaSymbols.ATT_ID, array[n17]);
        container15.put(SchemaSymbols.ATT_REF, array[n32]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, container15);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, container15);
        final Container container16 = Container.getContainer(3);
        container16.put(SchemaSymbols.ATT_ID, array[n17]);
        container16.put(SchemaSymbols.ATT_NAMESPACE, array[n27]);
        container16.put(SchemaSymbols.ATT_PROCESSCONTENTS, array[n30]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_ANYATTRIBUTE, container16);
        final Container container17 = Container.getContainer(2);
        container17.put(SchemaSymbols.ATT_ID, array[n17]);
        container17.put(SchemaSymbols.ATT_MIXED, array[n25]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_COMPLEXCONTENT, container17);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_COMPLEXCONTENT, container17);
        final Container container18 = Container.getContainer(2);
        container18.put(SchemaSymbols.ATT_ID, array[n17]);
        container18.put(SchemaSymbols.ATT_NAME, array[n26]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, container18);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, container18);
        final Container container19 = Container.getContainer(2);
        container19.put(SchemaSymbols.ATT_ID, array[n17]);
        container19.put(SchemaSymbols.ATT_NAME, array[n26]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_GROUP, container19);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_GROUP, container19);
        final Container container20 = Container.getContainer(4);
        container20.put(SchemaSymbols.ATT_ID, array[n17]);
        container20.put(SchemaSymbols.ATT_MAXOCCURS, array[n19]);
        container20.put(SchemaSymbols.ATT_MINOCCURS, array[n22]);
        container20.put(SchemaSymbols.ATT_REF, array[n32]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_GROUP, container20);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_GROUP, container20);
        final Container container21 = Container.getContainer(3);
        container21.put(SchemaSymbols.ATT_ID, array[n17]);
        container21.put(SchemaSymbols.ATT_MAXOCCURS, array[n20]);
        container21.put(SchemaSymbols.ATT_MINOCCURS, array[n23]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_ALL, container21);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_ALL, container21);
        final Container container22 = Container.getContainer(3);
        container22.put(SchemaSymbols.ATT_ID, array[n17]);
        container22.put(SchemaSymbols.ATT_MAXOCCURS, array[n19]);
        container22.put(SchemaSymbols.ATT_MINOCCURS, array[n22]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_CHOICE, container22);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_SEQUENCE, container22);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_CHOICE, container22);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_SEQUENCE, container22);
        final Container container23 = Container.getContainer(5);
        container23.put(SchemaSymbols.ATT_ID, array[n17]);
        container23.put(SchemaSymbols.ATT_MAXOCCURS, array[n19]);
        container23.put(SchemaSymbols.ATT_MINOCCURS, array[n22]);
        container23.put(SchemaSymbols.ATT_NAMESPACE, array[n27]);
        container23.put(SchemaSymbols.ATT_PROCESSCONTENTS, array[n30]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_ANY, container23);
        final Container container24 = Container.getContainer(2);
        container24.put(SchemaSymbols.ATT_ID, array[n17]);
        container24.put(SchemaSymbols.ATT_NAME, array[n26]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_UNIQUE, container24);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_KEY, container24);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_UNIQUE, container24);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_KEY, container24);
        final Container container25 = Container.getContainer(2);
        container25.put(SchemaSymbols.ATT_ID, array[n17]);
        container25.put(SchemaSymbols.ATT_REF, array[n32]);
        XSAttributeChecker.fEleAttrs11MapL.put("unique_r", container25);
        XSAttributeChecker.fEleAttrs11MapL.put("key_r", container25);
        XSAttributeChecker.fEleAttrs11MapL.put("keyref_r", container25);
        final Container container26 = Container.getContainer(3);
        container26.put(SchemaSymbols.ATT_ID, array[n17]);
        container26.put(SchemaSymbols.ATT_NAME, array[n26]);
        container26.put(SchemaSymbols.ATT_REFER, array[n33]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_KEYREF, container26);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_KEYREF, container26);
        final Container container27 = Container.getContainer(2);
        container27.put(SchemaSymbols.ATT_ID, array[n17]);
        container27.put(SchemaSymbols.ATT_REF, array[n32]);
        XSAttributeChecker.fEleAttrs11MapL.put("unique_r", container27);
        XSAttributeChecker.fEleAttrs11MapL.put("key_r", container27);
        XSAttributeChecker.fEleAttrs11MapL.put("keyref_r", container27);
        final Container container28 = Container.getContainer(2);
        container28.put(SchemaSymbols.ATT_ID, array[n17]);
        container28.put(SchemaSymbols.ATT_XPATH, array[n48]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_SELECTOR, container28);
        final Container container29 = Container.getContainer(2);
        container29.put(SchemaSymbols.ATT_ID, array[n17]);
        container29.put(SchemaSymbols.ATT_XPATH, array[n49]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_FIELD, container29);
        final Container container30 = Container.getContainer(1);
        container30.put(SchemaSymbols.ATT_ID, array[n17]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_ANNOTATION, container30);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_ANNOTATION, container30);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_ANNOTATION, container30);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_ANNOTATION, container30);
        final Container container31 = Container.getContainer(1);
        container31.put(SchemaSymbols.ATT_SOURCE, array[n36]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_APPINFO, container31);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_APPINFO, container31);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_APPINFO, container31);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_APPINFO, container31);
        final Container container32 = Container.getContainer(2);
        container32.put(SchemaSymbols.ATT_SOURCE, array[n36]);
        container32.put(SchemaSymbols.ATT_XML_LANG, array[n47]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_DOCUMENTATION, container32);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_DOCUMENTATION, container32);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_DOCUMENTATION, container32);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_DOCUMENTATION, container32);
        final Container container33 = Container.getContainer(3);
        container33.put(SchemaSymbols.ATT_FINAL, array[n12]);
        container33.put(SchemaSymbols.ATT_ID, array[n17]);
        container33.put(SchemaSymbols.ATT_NAME, array[n26]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_SIMPLETYPE, container33);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_SIMPLETYPE, container33);
        final Container container34 = Container.getContainer(2);
        container34.put(SchemaSymbols.ATT_FINAL, array[n12]);
        container34.put(SchemaSymbols.ATT_ID, array[n17]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_SIMPLETYPE, container34);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_SIMPLETYPE, container34);
        final Container container35 = Container.getContainer(2);
        container35.put(SchemaSymbols.ATT_ID, array[n17]);
        container35.put(SchemaSymbols.ATT_ITEMTYPE, array[n18]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_LIST, container35);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_LIST, container35);
        final Container container36 = Container.getContainer(2);
        container36.put(SchemaSymbols.ATT_ID, array[n17]);
        container36.put(SchemaSymbols.ATT_MEMBERTYPES, array[n21]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_UNION, container36);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_UNION, container36);
        final Container container37 = Container.getContainer(8);
        container37.put(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, array[n3]);
        container37.put(SchemaSymbols.ATT_BLOCKDEFAULT, array[n8]);
        container37.put(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, array[n10]);
        container37.put(SchemaSymbols.ATT_FINALDEFAULT, array[n13]);
        container37.put(SchemaSymbols.ATT_ID, array[n17]);
        container37.put(SchemaSymbols.ATT_TARGETNAMESPACE, array[n39]);
        container37.put(SchemaSymbols.ATT_VERSION, array[n46]);
        container37.put(SchemaSymbols.ATT_XML_LANG, array[n47]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_SCHEMA, container37);
        final Container container38 = Container.getContainer(2);
        container38.put(SchemaSymbols.ATT_ID, array[n17]);
        container38.put(SchemaSymbols.ATT_SCHEMALOCATION, array[n34]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_INCLUDE, container38);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_REDEFINE, container38);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_INCLUDE, container38);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_REDEFINE, container38);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_OVERRIDE, container38);
        final Container container39 = Container.getContainer(3);
        container39.put(SchemaSymbols.ATT_ID, array[n17]);
        container39.put(SchemaSymbols.ATT_NAMESPACE, array[n28]);
        container39.put(SchemaSymbols.ATT_SCHEMALOCATION, array[n35]);
        XSAttributeChecker.fEleAttrsMapG.put(SchemaSymbols.ELT_IMPORT, container39);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_IMPORT, container39);
        final Container container40 = Container.getContainer(3);
        container40.put(SchemaSymbols.ATT_ID, array[n17]);
        container40.put(SchemaSymbols.ATT_VALUE, array[n42]);
        container40.put(SchemaSymbols.ATT_FIXED, array[n15]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_LENGTH, container40);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_MINLENGTH, container40);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_MAXLENGTH, container40);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_FRACTIONDIGITS, container40);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_LENGTH, container40);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_MINLENGTH, container40);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_MAXLENGTH, container40);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_FRACTIONDIGITS, container40);
        final Container container41 = Container.getContainer(3);
        container41.put(SchemaSymbols.ATT_ID, array[n17]);
        container41.put(SchemaSymbols.ATT_VALUE, array[n43]);
        container41.put(SchemaSymbols.ATT_FIXED, array[n15]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_TOTALDIGITS, container41);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_TOTALDIGITS, container41);
        final Container container42 = Container.getContainer(2);
        container42.put(SchemaSymbols.ATT_ID, array[n17]);
        container42.put(SchemaSymbols.ATT_VALUE, array[n44]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_PATTERN, container42);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_PATTERN, container42);
        final Container container43 = Container.getContainer(2);
        container43.put(SchemaSymbols.ATT_ID, array[n17]);
        container43.put(SchemaSymbols.ATT_VALUE, array[n44]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_ENUMERATION, container43);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_ENUMERATION, container43);
        final Container container44 = Container.getContainer(3);
        container44.put(SchemaSymbols.ATT_ID, array[n17]);
        container44.put(SchemaSymbols.ATT_VALUE, array[n45]);
        container44.put(SchemaSymbols.ATT_FIXED, array[n15]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_WHITESPACE, container44);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_WHITESPACE, container44);
        final Container container45 = Container.getContainer(3);
        container45.put(SchemaSymbols.ATT_ID, array[n17]);
        container45.put(SchemaSymbols.ATT_VALUE, array[n44]);
        container45.put(SchemaSymbols.ATT_FIXED, array[n15]);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_MAXINCLUSIVE, container45);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_MAXEXCLUSIVE, container45);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_MININCLUSIVE, container45);
        XSAttributeChecker.fEleAttrsMapL.put(SchemaSymbols.ELT_MINEXCLUSIVE, container45);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_MAXINCLUSIVE, container45);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_MAXEXCLUSIVE, container45);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_MININCLUSIVE, container45);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_MINEXCLUSIVE, container45);
        final Container container46 = Container.getContainer(3);
        container46.put(SchemaSymbols.ATT_ID, array[n17]);
        container46.put(SchemaSymbols.ATT_VALUE, array[n50]);
        container46.put(SchemaSymbols.ATT_FIXED, array[n15]);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_MAXSCALE, container46);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_MINSCALE, container46);
        final Container container47 = Container.getContainer(3);
        container47.put(SchemaSymbols.ATT_ID, array[n17]);
        container47.put(SchemaSymbols.ATT_VALUE, array[n62]);
        container47.put(SchemaSymbols.ATT_FIXED, array[n15]);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_EXPLICITTIMEZONE, container47);
        final Container container48 = Container.getContainer(10);
        container48.put(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, array[n3]);
        container48.put(SchemaSymbols.ATT_DEFAULTATTRIBUTES, array[n52]);
        container48.put(SchemaSymbols.ATT_BLOCKDEFAULT, array[n8]);
        container48.put(SchemaSymbols.ATT_XPATH_DEFAULT_NS, array[n54]);
        container48.put(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, array[n10]);
        container48.put(SchemaSymbols.ATT_FINALDEFAULT, array[n13]);
        container48.put(SchemaSymbols.ATT_ID, array[n17]);
        container48.put(SchemaSymbols.ATT_TARGETNAMESPACE, array[n39]);
        container48.put(SchemaSymbols.ATT_VERSION, array[n46]);
        container48.put(SchemaSymbols.ATT_XML_LANG, array[n47]);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_SCHEMA, container48);
        final Container container49 = Container.getContainer(9);
        container49.put(SchemaSymbols.ATT_DEFAULT, array[n9]);
        container49.put(SchemaSymbols.ATT_FIXED, array[n14]);
        container49.put(SchemaSymbols.ATT_FORM, array[n16]);
        container49.put(SchemaSymbols.ATT_ID, array[n17]);
        container49.put(SchemaSymbols.ATT_NAME, array[n26]);
        container49.put(SchemaSymbols.ATT_TARGETNAMESPACE, array[n39]);
        container49.put(SchemaSymbols.ATT_TYPE, array[n40]);
        container49.put(SchemaSymbols.ATT_USE, array[n41]);
        container49.put(SchemaSymbols.ATT_INHERITABLE, array[n61]);
        XSAttributeChecker.fEleAttrs11MapL.put("attribute_n", container49);
        final Container container50 = Container.getContainer(11);
        container50.put(SchemaSymbols.ATT_BLOCK, array[n6]);
        container50.put(SchemaSymbols.ATT_DEFAULT, array[n9]);
        container50.put(SchemaSymbols.ATT_FIXED, array[n14]);
        container50.put(SchemaSymbols.ATT_FORM, array[n16]);
        container50.put(SchemaSymbols.ATT_ID, array[n17]);
        container50.put(SchemaSymbols.ATT_MAXOCCURS, array[n19]);
        container50.put(SchemaSymbols.ATT_MINOCCURS, array[n22]);
        container50.put(SchemaSymbols.ATT_NAME, array[n26]);
        container50.put(SchemaSymbols.ATT_NILLABLE, array[n29]);
        container50.put(SchemaSymbols.ATT_TARGETNAMESPACE, array[n39]);
        container50.put(SchemaSymbols.ATT_TYPE, array[n40]);
        XSAttributeChecker.fEleAttrs11MapL.put("element_n", container50);
        final Container container51 = Container.getContainer(7);
        container51.put(SchemaSymbols.ATT_ABSTRACT, array[n2]);
        container51.put(SchemaSymbols.ATT_BLOCK, array[n7]);
        container51.put(SchemaSymbols.ATT_FINAL, array[n11]);
        container51.put(SchemaSymbols.ATT_ID, array[n17]);
        container51.put(SchemaSymbols.ATT_MIXED, array[n24]);
        container51.put(SchemaSymbols.ATT_NAME, array[n26]);
        container51.put(SchemaSymbols.ATT_DEFAULTATTRIBUTESAPPLY, array[n53]);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_COMPLEXTYPE, container51);
        final Container container52 = Container.getContainer(3);
        container52.put(SchemaSymbols.ATT_ID, array[n17]);
        container52.put(SchemaSymbols.ATT_MIXED, array[n24]);
        container52.put(SchemaSymbols.ATT_DEFAULTATTRIBUTESAPPLY, array[n53]);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_COMPLEXTYPE, container52);
        final Container container53 = Container.getContainer(3);
        container53.put(SchemaSymbols.ATT_ID, array[n17]);
        container53.put(SchemaSymbols.ATT_XPATH, array[n48]);
        container53.put(SchemaSymbols.ATT_XPATH_DEFAULT_NS, array[n54]);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_SELECTOR, container53);
        final Container container54 = Container.getContainer(3);
        container54.put(SchemaSymbols.ATT_ID, array[n17]);
        container54.put(SchemaSymbols.ATT_XPATH, array[n49]);
        container54.put(SchemaSymbols.ATT_XPATH_DEFAULT_NS, array[n54]);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_FIELD, container54);
        final Container container55 = Container.getContainer(7);
        container55.put(SchemaSymbols.ATT_ID, array[n17]);
        container55.put(SchemaSymbols.ATT_MAXOCCURS, array[n19]);
        container55.put(SchemaSymbols.ATT_MINOCCURS, array[n22]);
        container55.put(SchemaSymbols.ATT_NAMESPACE, array[n57]);
        container55.put(SchemaSymbols.ATT_NOTNAMESPACE, array[n58]);
        container55.put(SchemaSymbols.ATT_NOTQNAME, array[n59]);
        container55.put(SchemaSymbols.ATT_PROCESSCONTENTS, array[n30]);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_ANY, container55);
        final Container container56 = Container.getContainer(5);
        container56.put(SchemaSymbols.ATT_ID, array[n17]);
        container56.put(SchemaSymbols.ATT_NAMESPACE, array[n57]);
        container56.put(SchemaSymbols.ATT_NOTNAMESPACE, array[n58]);
        container56.put(SchemaSymbols.ATT_NOTQNAME, array[n59]);
        container56.put(SchemaSymbols.ATT_PROCESSCONTENTS, array[n30]);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_ANYATTRIBUTE, container56);
        final Container container57 = Container.getContainer(4);
        container57.put(SchemaSymbols.ATT_ID, array[n17]);
        container57.put(SchemaSymbols.ATT_TEST, array[n60]);
        container57.put(SchemaSymbols.ATT_TYPE, array[n40]);
        container57.put(SchemaSymbols.ATT_XPATH_DEFAULT_NS, array[n54]);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_ALTERNATIVE, container57);
        final Container container58 = Container.getContainer(3);
        container58.put(SchemaSymbols.ATT_ID, array[n17]);
        container58.put(SchemaSymbols.ATT_TEST, array[n60]);
        container58.put(SchemaSymbols.ATT_XPATH_DEFAULT_NS, array[n54]);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_ASSERT, container58);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_ASSERTION, container58);
        final Container container59 = Container.getContainer(3);
        container59.put(SchemaSymbols.ATT_APPLIESTOEMPTY, array[n51]);
        container59.put(SchemaSymbols.ATT_ID, array[n17]);
        container59.put(SchemaSymbols.ATT_MODE, array[n55]);
        XSAttributeChecker.fEleAttrs11MapG.put(SchemaSymbols.ELT_DEFAULTOPENCONTENT, container59);
        final Container container60 = Container.getContainer(2);
        container60.put(SchemaSymbols.ATT_ID, array[n17]);
        container60.put(SchemaSymbols.ATT_MODE, array[n56]);
        XSAttributeChecker.fEleAttrs11MapL.put(SchemaSymbols.ELT_OPENCONTENT, container60);
        XSAttributeChecker.fSeenTemp = new boolean[XSAttributeChecker.ATTIDX_COUNT];
        XSAttributeChecker.fTempArray = new Object[XSAttributeChecker.ATTIDX_COUNT];
    }
}
