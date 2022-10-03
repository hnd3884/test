package com.adventnet.iam.xss;

import java.util.ArrayList;
import org.cyberneko.html.HTMLElements;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import java.util.logging.Level;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import java.util.List;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import org.cyberneko.html.filters.ElementRemover;

public class XSSElementRemover extends ElementRemover
{
    public static final Logger LOGGER;
    public XSSFilterConfiguration xssFilterConf;
    public CSSUtil cssUtil;
    private boolean emptyElement;
    private String domain;
    private static final Pattern IE_CONDITIONAL_COMMENT;
    short currentElementCode;
    List<String> removedElementsList;
    boolean altered;
    
    public XSSElementRemover() {
        this.xssFilterConf = null;
        this.cssUtil = null;
        this.domain = null;
        this.currentElementCode = -1;
        this.removedElementsList = null;
        this.altered = false;
    }
    
    public void setDomain(final String domain) {
        this.domain = domain;
    }
    
    public void comment(XMLString text, final Augmentations augs) throws XNIException {
        final String commStr = text.toString();
        XSSElementRemover.LOGGER.log(Level.FINE, "Element Value : {0} ", commStr);
        final boolean isCDATA = commStr.startsWith("[CDATA[");
        final boolean removeContent = isCDATA ? this.xssFilterConf.removeCData : this.xssFilterConf.removeComments;
        this.SET_ALTERED();
        if (removeContent || XSSUtil.detect(commStr, false)) {
            return;
        }
        String pureComment = null;
        if (!isCDATA && !(pureComment = XSSElementRemover.IE_CONDITIONAL_COMMENT.matcher(commStr).replaceAll("")).equals(commStr)) {
            text = new XMLString(pureComment.toCharArray(), 0, pureComment.length());
        }
        super.comment(text, augs);
    }
    
    public void characters(XMLString text, final Augmentations augs) throws XNIException {
        try {
            String elemValue = text.toString();
            XSSElementRemover.LOGGER.log(Level.FINE, "Element Value : {0} ", elemValue);
            if (this.xssFilterConf != null && elemValue != null && !elemValue.equals("")) {
                final boolean isXss = this.xssFilterConf.getRemoveElementValue(elemValue);
                if (isXss) {
                    this.SET_ALTERED();
                    return;
                }
                if (this.xssFilterConf.encodeElementValuesMarkupEntities && this.isTextTag(this.currentElementCode)) {
                    final boolean isStyle = this.currentElementCode == 99;
                    elemValue = this.xssFilterConf.encodeElementValuesMarkupEntities(elemValue, isStyle);
                    final char[] valArray = elemValue.toCharArray();
                    text = new XMLString(valArray, 0, valArray.length);
                    this.SET_ALTERED();
                }
            }
            if (this.xssFilterConf != null && this.cssUtil != null && this.currentElementCode == 99) {
                elemValue = this.cssUtil.cleanStyleSheet(elemValue);
                if (elemValue == null) {
                    XSSElementRemover.LOGGER.log(Level.WARNING, "Unable to parse style element as the element value is null / Invalid syntax . Removing");
                    return;
                }
                final char[] valArray2 = elemValue.toCharArray();
                if (valArray2 != null) {
                    text = new XMLString(valArray2, 0, valArray2.length);
                }
            }
        }
        catch (final Exception e) {
            XSSElementRemover.LOGGER.log(Level.WARNING, "{0}", e.getMessage());
        }
        super.characters(text, augs);
    }
    
    private boolean isTextTag(final short currentElementCode) {
        switch (currentElementCode) {
            case 23:
            case 48:
            case 69:
            case 70:
            case 72:
            case 79:
            case 90:
            case 99:
            case 105:
            case 109:
            case 117: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    protected boolean elementAccepted(final String element) {
        return true;
    }
    
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (HTMLElements.getElement(element.rawname).isEmpty()) {
            this.emptyElement = true;
            super.emptyElement(element, attributes, augs);
            this.emptyElement = false;
        }
        else {
            this.startElement(element, attributes, augs);
        }
    }
    
    protected boolean handleOpenTag(final QName element, final XMLAttributes attributes) {
        if (this.xssFilterConf == null) {
            XSSElementRemover.LOGGER.log(Level.SEVERE, "No XSS Filter Configuration.");
            return true;
        }
        final String elementName = element.rawname.toLowerCase().trim();
        XSSElementRemover.LOGGER.log(Level.FINE, "Element - {0}", new Object[] { elementName });
        if ("script".equalsIgnoreCase(elementName) && this.domain != null) {
            final String srcValue = attributes.getValue("src");
            if (this.xssFilterConf.isAllowedScript(this.domain, srcValue)) {
                return true;
            }
        }
        final XSSFilterConfiguration.AttributeValue insertAttrValue = this.xssFilterConf.getInsertAttribute(elementName);
        if (insertAttrValue != null) {
            XSSElementRemover.LOGGER.log(Level.FINE, "Inserting atribute value {0} - {1}", new Object[] { insertAttrValue.name, insertAttrValue.value });
            final int index = attributes.getIndex(insertAttrValue.name);
            if (index != -1) {
                attributes.setValue(index, insertAttrValue.value);
            }
            else {
                attributes.addAttribute(new QName((String)null, (String)null, insertAttrValue.name, (String)null), (String)null, insertAttrValue.value);
            }
            this.SET_ALTERED();
        }
        if (this.xssFilterConf.isRemoveElement(elementName)) {
            XSSElementRemover.LOGGER.log(Level.FINE, "Removing element - {0}", new Object[] { elementName });
            if (!this.emptyElement) {
                this.fRemovalElementDepth = this.fElementDepth;
            }
            this.ELEMENT_REMOVED(elementName);
            return false;
        }
        for (int attributeCount = attributes.getLength(), i = 0; i < attributeCount; ++i) {
            final String aname = attributes.getQName(i).toLowerCase().trim();
            String aval = attributes.getValue(i).trim();
            final String avalLower = aval.toLowerCase();
            XSSElementRemover.LOGGER.log(Level.FINE, "RemElemAttr: Attr Name : {0} value {1}", new Object[] { aname, aval });
            final int removeType = this.xssFilterConf.removeXSSElementOrAttribute(elementName, aname, avalLower);
            if (removeType == 2) {
                XSSElementRemover.LOGGER.log(Level.FINE, "Removing attribute - {0} - {1} - {2}", new Object[] { elementName, aname, aval });
                attributes.removeAttributeAt(i--);
                --attributeCount;
                this.SET_ALTERED();
            }
            else {
                if (removeType == 1) {
                    XSSElementRemover.LOGGER.log(Level.FINE, "Removing ElementName - {0} - {1} - {2}", new Object[] { elementName, aname, aval });
                    if (!this.emptyElement) {
                        this.fRemovalElementDepth = this.fElementDepth;
                    }
                    this.ELEMENT_REMOVED(elementName);
                    return false;
                }
                if (aname.equals("style") && this.cssUtil != null) {
                    aval = this.cssUtil.cleanStyleDeclaration(aval);
                    if (aval != null) {
                        attributes.setValue(i, aval);
                    }
                    else {
                        XSSElementRemover.LOGGER.log(Level.FINE, "Removing attribute - {0} - {1} - {2}", new Object[] { elementName, aname, aval });
                        attributes.removeAttributeAt(i--);
                        --attributeCount;
                    }
                }
            }
        }
        return true;
    }
    
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        final HTMLElements.Element elem = HTMLElements.getElement(element.rawname);
        this.currentElementCode = elem.code;
        super.startElement(element, attributes, augs);
    }
    
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        this.currentElementCode = -1;
        super.endElement(element, augs);
    }
    
    public void doctypeDecl(final String root, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
        this.SET_ALTERED();
        if (this.xssFilterConf.isAllowDoctype() && XSSUtil.isValidHTMLDoctypeDeclaration(root, publicId, systemId)) {
            super.doctypeDecl(root, publicId, systemId, augs);
        }
    }
    
    void SET_ALTERED() {
        if (!this.altered) {
            this.altered = true;
        }
    }
    
    void ELEMENT_REMOVED(final String elementName) {
        if (this.removedElementsList == null) {
            this.removedElementsList = new ArrayList<String>();
        }
        this.removedElementsList.add(elementName.toLowerCase());
        this.SET_ALTERED();
    }
    
    static {
        LOGGER = Logger.getLogger(XSSElementRemover.class.getName());
        IE_CONDITIONAL_COMMENT = Pattern.compile("<?!?\\[\\s*(?:end)?if[^]]*\\]>?", 2);
    }
}
