package org.owasp.validator.html.scan;

import org.owasp.validator.html.util.ErrorMessageUtil;
import com.zoho.security.validator.url.ZSecURL;
import com.zoho.security.validator.url.URLValidatorAPI;
import java.util.Iterator;
import org.owasp.validator.html.model.Tag;
import java.net.MalformedURLException;
import org.owasp.validator.html.model.Attribute;
import org.apache.xerces.util.XMLAttributesImpl;
import org.owasp.validator.css.ExternalCssScanner;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.ScanException;
import org.apache.xerces.util.AugmentationsImpl;
import java.util.Collection;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.owasp.validator.css.ZohoCssScanner;
import java.util.ResourceBundle;
import org.owasp.validator.html.InternalPolicy;
import org.apache.xerces.xni.XMLAttributes;
import java.util.List;
import java.util.Stack;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.DefaultFilter;

@SuppressFBWarnings(value = { "REDOS" }, justification = "Tested the Regex against saferegex and safe-regex and not vulnerable")
public class MagicSAXFilter extends DefaultFilter implements XMLDocumentFilter
{
    private final Stack<Ops> operations;
    private final List<String> errorMessages;
    private StringBuffer cssContent;
    private XMLAttributes cssAttributes;
    private InternalPolicy policy;
    private ResourceBundle messages;
    private boolean isNofollowAnchors;
    private boolean isValidateParamAsEmbed;
    private boolean inCdata;
    private boolean preserveComments;
    private int maxInputSize;
    private boolean externalCssScanner;
    private ZohoCssScanner zohoCssScanner;
    private int unClosedNoscriptAndNoembedTagsCount;
    private static final Pattern conditionalDirectives;
    
    public MagicSAXFilter(final ResourceBundle messages) {
        this.operations = new Stack<Ops>();
        this.errorMessages = new ArrayList<String>();
        this.cssContent = null;
        this.cssAttributes = null;
        this.inCdata = false;
        this.zohoCssScanner = null;
        this.unClosedNoscriptAndNoembedTagsCount = 0;
        this.messages = messages;
    }
    
    public void reset(final InternalPolicy instance) {
        this.policy = instance;
        this.isNofollowAnchors = this.policy.isNofollowAnchors();
        this.isValidateParamAsEmbed = this.policy.isValidateParamAsEmbed();
        this.preserveComments = this.policy.isPreserveComments();
        this.maxInputSize = this.policy.getMaxInputSize();
        this.externalCssScanner = this.policy.isEmbedStyleSheets();
        this.operations.clear();
        this.errorMessages.clear();
        this.cssContent = null;
        this.cssAttributes = null;
        this.inCdata = false;
        this.zohoCssScanner = null;
        this.unClosedNoscriptAndNoembedTagsCount = 0;
    }
    
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        final Ops topOp = this.peekTop();
        if (topOp != Ops.REMOVE) {
            if (topOp == Ops.CSS) {
                this.cssContent.append(text.ch, text.offset, text.length);
            }
            else {
                if (this.inCdata) {
                    final String encoded = HTMLEntityEncoder.htmlEntityEncode(text.toString());
                    this.addError("error.cdata.found", new Object[] { encoded });
                }
                super.characters(text, augs);
            }
        }
    }
    
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.preserveComments && this.unClosedNoscriptAndNoembedTagsCount == 0) {
            String value = text.toString();
            if (value != null) {
                value = MagicSAXFilter.conditionalDirectives.matcher(value).replaceAll("");
                super.comment(new XMLString(value.toCharArray(), 0, value.length()), augs);
            }
        }
    }
    
    public void doctypeDecl(final String root, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
    }
    
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.startElement(element, attributes, augs);
        this.endElement(element, augs);
    }
    
    private Ops peekTop() {
        return this.operations.empty() ? null : this.operations.peek();
    }
    
    private XMLStringBuffer makeEndTag(final String tagName) {
        return new XMLStringBuffer("</" + tagName + ">");
    }
    
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if ("noscript".equals(element.localpart) || "noembed".equals(element.localpart)) {
            --this.unClosedNoscriptAndNoembedTagsCount;
        }
        final Ops topOp = this.peekTop();
        if (Ops.REMOVE == topOp) {
            this.operations.pop();
        }
        else if (Ops.FILTER == topOp) {
            this.operations.pop();
        }
        else if (Ops.ENCODE == topOp) {
            this.operations.pop();
            super.characters((XMLString)this.makeEndTag(element.rawname), augs);
        }
        else if (Ops.CSS == topOp) {
            this.operations.pop();
            final ZohoCssScanner cssScanner = this.makeCSSScanner();
            try {
                final CleanResults results = cssScanner.scanStyleSheet(this.cssContent.toString());
                this.errorMessages.addAll(results.getErrorMessages());
                if (results.getCleanHTML() != null) {
                    if (!results.getCleanHTML().equals("")) {
                        super.startElement(element, this.cssAttributes, (Augmentations)new AugmentationsImpl());
                        super.characters((XMLString)new XMLStringBuffer(results.getCleanHTML()), (Augmentations)new AugmentationsImpl());
                        super.endElement(element, augs);
                    }
                }
            }
            catch (final ScanException e) {
                this.addError("error.css.tag.malformed", new Object[] { HTMLEntityEncoder.htmlEntityEncode(this.cssContent.toString()) });
            }
            finally {
                this.cssContent = null;
                this.cssAttributes = null;
            }
        }
        else {
            this.operations.pop();
            super.endElement(element, augs);
        }
    }
    
    private ZohoCssScanner makeCSSScanner() {
        if (this.zohoCssScanner == null) {
            this.zohoCssScanner = (this.externalCssScanner ? new ExternalCssScanner(this.policy, this.messages) : new ZohoCssScanner(this.policy, this.messages));
        }
        return this.zohoCssScanner;
    }
    
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
    }
    
    public void startCDATA(final Augmentations augs) throws XNIException {
        this.inCdata = true;
        super.startCDATA(augs);
    }
    
    public void endCDATA(final Augmentations augs) throws XNIException {
        this.inCdata = false;
        super.endCDATA(augs);
    }
    
    public void startElement(final QName element, XMLAttributes attributes, final Augmentations augs) throws XNIException {
        final String tagNameLowerCase = element.localpart.toLowerCase();
        Tag tag = this.policy.getTagByLowercaseName(tagNameLowerCase);
        if ("noscript".equals(tagNameLowerCase) || "noembed".equals(tagNameLowerCase)) {
            ++this.unClosedNoscriptAndNoembedTagsCount;
        }
        boolean masqueradingParam = false;
        String embedName = null;
        String embedValue = null;
        if (tag == null && this.isValidateParamAsEmbed && "param".equals(tagNameLowerCase)) {
            final Tag embedPolicy = this.policy.getEmbedTag();
            if (embedPolicy != null && embedPolicy.isAction("validate")) {
                tag = embedPolicy;
                masqueradingParam = true;
                embedName = attributes.getValue("name");
                embedValue = attributes.getValue("value");
                final XMLAttributes masqueradingAttrs = (XMLAttributes)new XMLAttributesImpl();
                masqueradingAttrs.addAttribute(this.makeSimpleQname(embedName), "CDATA", embedValue);
                attributes = masqueradingAttrs;
            }
        }
        XMLAttributes validattributes = (XMLAttributes)new XMLAttributesImpl();
        this.addInsertAttributes(tag, null, validattributes, null, null);
        final Ops topOp = this.peekTop();
        if (Ops.REMOVE == topOp || Ops.CSS == topOp) {
            this.operations.push(Ops.REMOVE);
        }
        else if ((tag == null && this.policy.isEncodeUnknownTag()) || (tag != null && tag.isAction("encode"))) {
            final String name = "<" + element.localpart + ">";
            super.characters(new XMLString(name.toCharArray(), 0, name.length()), augs);
            this.operations.push(Ops.ENCODE);
        }
        else if (tag == null) {
            this.addError("error.tag.notfound", new Object[] { HTMLEntityEncoder.htmlEntityEncode(element.localpart) });
            this.operations.push(Ops.FILTER);
        }
        else if (tag.isAction("filter")) {
            this.addError("error.tag.filtered", new Object[] { HTMLEntityEncoder.htmlEntityEncode(element.localpart) });
            this.operations.push(Ops.FILTER);
        }
        else if (tag.isAction("validate")) {
            final boolean isStyle = "style".endsWith(element.localpart.toLowerCase());
            boolean removeTag = false;
            boolean filterTag = false;
            boolean match = false;
            final List<String> mandatoryAttributes = tag.getMandatoryAttributes();
            if (mandatoryAttributes != null) {
                if (attributes.getLength() != 0) {
                    for (final String mandatoryAttribute : mandatoryAttributes) {
                        match = false;
                        for (int currentAttributeIndex = 0; currentAttributeIndex < attributes.getLength(); ++currentAttributeIndex) {
                            final String nameLower = attributes.getQName(currentAttributeIndex).toLowerCase();
                            if (mandatoryAttribute.equals(nameLower)) {
                                match = true;
                                break;
                            }
                        }
                        if (!match) {
                            removeTag = true;
                            break;
                        }
                    }
                }
                else {
                    removeTag = true;
                }
            }
            if (!removeTag) {
                for (int i = 0; i < attributes.getLength(); ++i) {
                    final String name2 = attributes.getQName(i);
                    final String value = attributes.getValue(i);
                    final String nameLower = name2.toLowerCase();
                    if (!this.isAttributeExists(validattributes, nameLower)) {
                        Attribute attribute = tag.getAttributeByName(nameLower);
                        if (attribute == null) {
                            attribute = this.policy.getGlobalAttributeByName(nameLower);
                            if (attribute == null && this.policy.isAllowDynamicAttributes()) {
                                attribute = this.policy.getDynamicAttributeByName(nameLower);
                            }
                        }
                        if ("style".equalsIgnoreCase(name2)) {
                            final ZohoCssScanner styleScanner = this.makeCSSScanner();
                            try {
                                final CleanResults cr = styleScanner.scanInlineStyle(value);
                                attributes.setValue(i, cr.getCleanHTML());
                                validattributes.addAttribute(this.makeSimpleQname(name2), "CDATA", cr.getCleanHTML());
                                this.errorMessages.addAll(cr.getErrorMessages());
                            }
                            catch (final ScanException e) {
                                this.addError("error.css.attribute.malformed", new Object[] { element.localpart, HTMLEntityEncoder.htmlEntityEncode(value) });
                            }
                        }
                        else if (attribute != null) {
                            boolean isValid = false;
                            final URLValidatorAPI urlValidator = this.policy.getUrlValidator();
                            if (this.policy.isEnabledURLValidation() && urlValidator != null && this.policy.getURLValidation_Attributes().contains(nameLower)) {
                                ZSecURL urlobj = null;
                                String safeurl = "";
                                try {
                                    urlobj = urlValidator.getValidatedURLObject(value);
                                    safeurl = urlobj.getSafeURL();
                                }
                                catch (final MalformedURLException e2) {
                                    this.addError("error.attribute.invalid", new Object[] { element.localpart, HTMLEntityEncoder.htmlEntityEncode(value) });
                                }
                                catch (final StringIndexOutOfBoundsException e3) {
                                    this.addError("error.attribute.invalid", new Object[] { element.localpart, HTMLEntityEncoder.htmlEntityEncode(value) });
                                }
                                validattributes.addAttribute(this.makeSimpleQname(name2), "CDATA", safeurl);
                                isValid = true;
                            }
                            if (!isValid && attribute.containsAllowedValue(value.toLowerCase())) {
                                validattributes.addAttribute(this.makeSimpleQname(name2), "CDATA", value);
                                isValid = true;
                            }
                            if (!isValid) {
                                isValid = attribute.matchesAllowedExpression(value);
                                if (isValid) {
                                    validattributes.addAttribute(this.makeSimpleQname(name2), "CDATA", value);
                                }
                            }
                            if (!isValid && "removeTag".equals(attribute.getOnInvalid())) {
                                this.addError("error.attribute.invalid.removed", new Object[] { tag.getName(), HTMLEntityEncoder.htmlEntityEncode(name2), HTMLEntityEncoder.htmlEntityEncode(value) });
                                removeTag = true;
                            }
                            else if (!isValid && ("filterTag".equals(attribute.getOnInvalid()) || masqueradingParam)) {
                                this.addError("error.attribute.invalid.filtered", new Object[] { tag.getName(), HTMLEntityEncoder.htmlEntityEncode(name2), HTMLEntityEncoder.htmlEntityEncode(value) });
                                filterTag = true;
                            }
                            else if (!isValid) {
                                this.addError("error.attribute.invalid", new Object[] { tag.getName(), HTMLEntityEncoder.htmlEntityEncode(name2), HTMLEntityEncoder.htmlEntityEncode(value) });
                            }
                            else if (isValid) {
                                this.addInsertAttributes(tag, attribute, validattributes, nameLower, value);
                            }
                        }
                        else {
                            this.addError("error.attribute.notfound", new Object[] { element.localpart, HTMLEntityEncoder.htmlEntityEncode(name2), HTMLEntityEncoder.htmlEntityEncode(value) });
                            if (masqueradingParam) {
                                filterTag = true;
                            }
                        }
                    }
                }
            }
            if (removeTag) {
                this.operations.push(Ops.REMOVE);
            }
            else if (isStyle) {
                this.operations.push(Ops.CSS);
                this.cssContent = new StringBuffer();
                this.cssAttributes = validattributes;
            }
            else if (filterTag) {
                this.operations.push(Ops.FILTER);
            }
            else {
                if (this.isNofollowAnchors && "a".equals(element.localpart)) {
                    validattributes.addAttribute(this.makeSimpleQname("rel"), "CDATA", "nofollow");
                }
                if (masqueradingParam) {
                    validattributes = (XMLAttributes)new XMLAttributesImpl();
                    validattributes.addAttribute(this.makeSimpleQname("name"), "CDATA", embedName);
                    validattributes.addAttribute(this.makeSimpleQname("value"), "CDATA", embedValue);
                }
                this.operations.push(Ops.KEEP);
            }
        }
        else if (tag.isAction("truncate")) {
            this.operations.push(Ops.TRUNCATE);
        }
        else {
            this.addError("error.tag.removed", new Object[] { HTMLEntityEncoder.htmlEntityEncode(element.localpart) });
            this.operations.push(Ops.REMOVE);
        }
        if (Ops.TRUNCATE.equals(this.operations.peek())) {
            super.startElement(element, (XMLAttributes)new XMLAttributesImpl(), augs);
        }
        else if (Ops.KEEP.equals(this.operations.peek())) {
            super.startElement(element, validattributes, augs);
        }
    }
    
    private boolean isAttributeExists(final XMLAttributes validattributes, final String attrName) {
        for (int i = 0; i < validattributes.getLength(); ++i) {
            if (attrName.equals(validattributes.getQName(i))) {
                return true;
            }
        }
        return false;
    }
    
    private void addInsertAttributes(final Tag tag, final Attribute attribute, final XMLAttributes validattributes, final String name, final String value) {
        final List<Attribute> insertAttributes = (tag != null) ? tag.getInsertAttributes() : null;
        if (insertAttributes != null) {
            for (final Attribute insertAttribute : insertAttributes) {
                final String insertAttrName = insertAttribute.getName();
                List<String> insertAttrValues = insertAttribute.getInsertValues();
                if (insertAttrValues.size() == 0) {
                    final Attribute tagAttribute = tag.getAttributeByName(insertAttrName);
                    if (tagAttribute != null) {
                        insertAttrValues = tagAttribute.getAllowedValues();
                    }
                }
                if (!insertAttribute.isCriteriaMatched(name, value, attribute)) {
                    continue;
                }
                String insertAttrValue = "";
                for (final String insertValue : insertAttrValues) {
                    insertAttrValue = insertAttrValue + insertValue + " ";
                }
                insertAttrValue = insertAttrValue.trim();
                validattributes.addAttribute(this.makeSimpleQname(insertAttrName), "CDATA", insertAttrValue);
            }
        }
    }
    
    private QName makeSimpleQname(final String name) {
        return new QName("", name, name, "");
    }
    
    private void addError(final String errorKey, final Object[] objs) {
        this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, errorKey, objs));
    }
    
    public List<String> getErrorMessages() {
        return this.errorMessages;
    }
    
    static {
        conditionalDirectives = Pattern.compile("<?!?\\[\\s*(?:end)?if[^]]*\\]>?", 2);
    }
    
    private enum Ops
    {
        CSS, 
        FILTER, 
        REMOVE, 
        TRUNCATE, 
        KEEP, 
        ENCODE;
    }
}
