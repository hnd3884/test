package org.owasp.validator.html.scan;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import com.zoho.security.validator.url.ZSecURL;
import org.owasp.validator.html.model.Attribute;
import java.util.Iterator;
import java.util.List;
import java.net.MalformedURLException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DOMException;
import java.util.Collection;
import org.owasp.validator.css.ZohoCssScanner;
import org.owasp.validator.css.ExternalCssScanner;
import org.owasp.validator.html.model.Tag;
import org.w3c.dom.NodeList;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Comment;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.cyberneko.html.parsers.DOMFragmentParser;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.util.concurrent.Callable;
import java.io.Writer;
import java.io.StringWriter;
import org.w3c.dom.Node;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.PolicyException;
import org.apache.xerces.dom.DocumentImpl;
import org.owasp.validator.html.Policy;
import java.util.Queue;
import java.util.regex.Pattern;
import org.owasp.validator.html.CleanResults;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Document;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = { "REDOS" }, justification = "Tested the Regex against saferegex and safe-regex and not vulnerable")
public class AntiSamyDOMScanner extends AbstractAntiSamyScanner
{
    private Document document;
    private DocumentFragment dom;
    private CleanResults results;
    private static final int maxDepth = 250;
    private static final Pattern invalidXmlCharacters;
    private static final Pattern conditionalDirectives;
    private static final Queue<CachedItem> cachedItems;
    
    public AntiSamyDOMScanner(final Policy policy) {
        super(policy);
        this.document = (Document)new DocumentImpl();
        this.dom = this.document.createDocumentFragment();
        this.results = null;
    }
    
    public AntiSamyDOMScanner() throws PolicyException {
        this.document = (Document)new DocumentImpl();
        this.dom = this.document.createDocumentFragment();
        this.results = null;
    }
    
    @Override
    public CleanResults scan(String html) throws ScanException {
        if (html == null) {
            throw new ScanException(new NullPointerException("Null html input"));
        }
        this.errorMessages.clear();
        final int maxInputSize = this.policy.getMaxInputSize();
        if (maxInputSize < html.length()) {
            this.addError("error.size.toolarge", new Object[] { html.length(), maxInputSize });
            throw new ScanException(this.errorMessages.get(0));
        }
        this.isNofollowAnchors = this.policy.isNofollowAnchors();
        this.isValidateParamAsEmbed = this.policy.isValidateParamAsEmbed();
        final long startOfScan = System.currentTimeMillis();
        try {
            CachedItem cachedItem = AntiSamyDOMScanner.cachedItems.poll();
            if (cachedItem == null) {
                cachedItem = new CachedItem();
            }
            html = this.stripNonValidXMLCharacters(html, cachedItem.invalidXmlCharMatcher);
            final DOMFragmentParser parser = cachedItem.getDomFragmentParser();
            try {
                parser.parse(new InputSource(new StringReader(html)), this.dom);
            }
            catch (final Exception e) {
                throw new ScanException(e);
            }
            this.processChildren(this.dom, 0);
            final String trimmedHtml = html;
            final StringWriter out = new StringWriter();
            final OutputFormat format = this.getOutputFormat();
            final HTMLSerializer serializer = this.getHTMLSerializer(out, format);
            serializer.serialize(this.dom);
            final String trimmed = this.trim(trimmedHtml, out.getBuffer().toString());
            final Callable<String> cleanHtml = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return trimmed;
                }
            };
            this.results = new CleanResults(startOfScan, cleanHtml, this.dom, this.errorMessages);
            AntiSamyDOMScanner.cachedItems.add(cachedItem);
            return this.results;
        }
        catch (final SAXException | IOException e2) {
            throw new ScanException(e2);
        }
    }
    
    static DOMFragmentParser getDomParser() throws SAXNotRecognizedException, SAXNotSupportedException {
        final DOMFragmentParser parser = new DOMFragmentParser();
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", (Object)"lower");
        parser.setFeature("http://cyberneko.org/html/features/scanner/style/strip-cdata-delims", false);
        parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
        try {
            parser.setFeature("http://cyberneko.org/html/features/enforce-strict-attribute-names", true);
        }
        catch (final SAXNotRecognizedException ex) {}
        return parser;
    }
    
    private void recursiveValidateTag(final Node node, int currentStackDepth) throws ScanException {
        if (++currentStackDepth > 250) {
            throw new ScanException("Too many nested tags");
        }
        if (node instanceof Comment) {
            this.processCommentNode(node);
            return;
        }
        final boolean isElement = node instanceof Element;
        final NodeList eleChildNodes = node.getChildNodes();
        if (isElement && eleChildNodes.getLength() == 0 && this.removeDisallowedEmpty(node)) {
            return;
        }
        if (node instanceof Text && 4 == node.getNodeType()) {
            this.stripCData(node);
            return;
        }
        if (node instanceof ProcessingInstruction) {
            this.removePI(node);
        }
        if (!isElement) {
            return;
        }
        final Element ele = (Element)node;
        final Node parentNode = ele.getParentNode();
        final String tagName = ele.getNodeName();
        final String tagNameLowerCase = tagName.toLowerCase();
        Tag tagRule = this.policy.getTagByLowercaseName(tagNameLowerCase);
        final Tag embedTag = this.policy.getEmbedTag();
        final boolean masqueradingParam = this.isMasqueradingParam(tagRule, embedTag, tagNameLowerCase);
        if (masqueradingParam) {
            tagRule = Constants.BASIC_PARAM_TAG_RULE;
        }
        if ((tagRule == null && this.policy.isEncodeUnknownTag()) || (tagRule != null && tagRule.isAction("encode"))) {
            this.encodeTag(currentStackDepth, ele, tagName, eleChildNodes);
        }
        else if (tagRule == null || tagRule.isAction("filter")) {
            this.actionFilter(currentStackDepth, ele, tagName, tagRule, eleChildNodes);
        }
        else if (tagRule.isAction("validate")) {
            this.actionValidate(currentStackDepth, ele, parentNode, tagName, tagNameLowerCase, tagRule, masqueradingParam, embedTag, eleChildNodes);
        }
        else if (tagRule.isAction("truncate")) {
            this.actionTruncate(ele, tagName, eleChildNodes);
        }
        else {
            this.addError("error.tag.removed", new Object[] { HTMLEntityEncoder.htmlEntityEncode(tagName) });
            this.removeNode(ele);
        }
    }
    
    private boolean isMasqueradingParam(final Tag tagRule, final Tag embedTag, final String tagNameLowerCase) {
        return tagRule == null && this.isValidateParamAsEmbed && "param".equals(tagNameLowerCase) && embedTag != null && embedTag.isAction("validate");
    }
    
    private void encodeTag(final int currentStackDepth, final Element ele, final String tagName, final NodeList eleChildNodes) throws ScanException {
        this.addError("error.tag.encoded", new Object[] { HTMLEntityEncoder.htmlEntityEncode(tagName) });
        this.processChildren(eleChildNodes, currentStackDepth);
        this.encodeAndPromoteChildren(ele);
    }
    
    private void actionFilter(final int currentStackDepth, final Element ele, final String tagName, final Tag tag, final NodeList eleChildNodes) throws ScanException {
        if (tag == null) {
            this.addError("error.tag.notfound", new Object[] { HTMLEntityEncoder.htmlEntityEncode(tagName) });
        }
        else {
            this.addError("error.tag.filtered", new Object[] { HTMLEntityEncoder.htmlEntityEncode(tagName) });
        }
        this.processChildren(eleChildNodes, currentStackDepth);
        this.promoteChildren(ele);
    }
    
    private void actionValidate(final int currentStackDepth, final Element ele, final Node parentNode, final String tagName, final String tagNameLowerCase, Tag tag, final boolean masqueradingParam, final Tag embedTag, final NodeList eleChildNodes) throws ScanException {
        String nameValue = null;
        if (masqueradingParam) {
            nameValue = ele.getAttribute("name");
            if (nameValue != null && !"".equals(nameValue)) {
                final String valueValue = ele.getAttribute("value");
                ele.setAttribute(nameValue, valueValue);
                ele.removeAttribute("name");
                ele.removeAttribute("value");
                tag = embedTag;
            }
        }
        if ("style".equals(tagNameLowerCase) && this.policy.getStyleTag() != null && this.processStyleTag(ele, parentNode)) {
            return;
        }
        if (this.processAttributes(ele, tagName, tag, currentStackDepth)) {
            return;
        }
        if (this.isNofollowAnchors && "a".equals(tagNameLowerCase)) {
            ele.setAttribute("rel", "nofollow");
        }
        this.processChildren(eleChildNodes, currentStackDepth);
        if (masqueradingParam && nameValue != null && !"".equals(nameValue)) {
            final String valueValue = ele.getAttribute(nameValue);
            ele.setAttribute("name", nameValue);
            ele.setAttribute("value", valueValue);
            ele.removeAttribute(nameValue);
        }
    }
    
    private boolean processStyleTag(final Element ele, final Node parentNode) {
        ZohoCssScanner styleScanner;
        if (this.policy.isEmbedStyleSheets()) {
            styleScanner = new ExternalCssScanner(this.policy, AntiSamyDOMScanner.messages);
        }
        else {
            styleScanner = new ZohoCssScanner(this.policy, AntiSamyDOMScanner.messages);
        }
        try {
            final Node firstChild = ele.getFirstChild();
            if (firstChild != null) {
                final String toScan = firstChild.getNodeValue();
                final CleanResults cr = styleScanner.scanStyleSheet(toScan);
                this.errorMessages.addAll(cr.getErrorMessages());
                final String cleanHTML = cr.getCleanHTML();
                if (cleanHTML == null || cleanHTML.equals("")) {
                    firstChild.setNodeValue("/* */");
                }
                else {
                    firstChild.setNodeValue(cleanHTML);
                }
            }
        }
        catch (final DOMException | ScanException | NumberFormatException e) {
            this.addError("error.css.tag.malformed", new Object[] { HTMLEntityEncoder.htmlEntityEncode(ele.getFirstChild().getNodeValue()) });
            parentNode.removeChild(ele);
            return true;
        }
        return false;
    }
    
    private void actionTruncate(final Element ele, final String tagName, final NodeList eleChildNodes) {
        final NamedNodeMap nnmap = ele.getAttributes();
        while (nnmap.getLength() > 0) {
            this.addError("error.attribute.notfound", new Object[] { tagName, HTMLEntityEncoder.htmlEntityEncode(nnmap.item(0).getNodeName()) });
            ele.removeAttribute(nnmap.item(0).getNodeName());
        }
        int i = 0;
        int j = 0;
        for (int length = eleChildNodes.getLength(); i < length; ++i) {
            final Node nodeToRemove = eleChildNodes.item(j);
            if (nodeToRemove.getNodeType() != 3) {
                ele.removeChild(nodeToRemove);
            }
            else {
                ++j;
            }
        }
    }
    
    private boolean processAttributes(final Element ele, final String tagName, final Tag tag, final int currentStackDepth) throws ScanException {
        final NamedNodeMap attributes = ele.getAttributes();
        boolean match = false;
        boolean removeTag = false;
        final List<String> mandatoryAttributes = tag.getMandatoryAttributes();
        if (mandatoryAttributes != null) {
            if (attributes.getLength() != 0) {
                for (final String mandatoryAttribute : mandatoryAttributes) {
                    match = false;
                    for (int currentAttributeIndex = 0; currentAttributeIndex < attributes.getLength(); ++currentAttributeIndex) {
                        final Node attribute = attributes.item(currentAttributeIndex);
                        final String nameLower = attribute.getNodeName().toLowerCase();
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
        if (removeTag) {
            this.removeNode(ele);
        }
        else {
            for (int currentAttributeIndex2 = 0; currentAttributeIndex2 < attributes.getLength(); ++currentAttributeIndex2) {
                final Node attribute = attributes.item(currentAttributeIndex2);
                final String name = attribute.getNodeName();
                final String value = attribute.getNodeValue();
                Attribute attr = tag.getAttributeByName(name.toLowerCase());
                if (attr == null) {
                    attr = this.policy.getGlobalAttributeByName(name);
                }
                boolean isAttributeValid = false;
                if ("style".equals(name.toLowerCase()) && attr != null) {
                    final ZohoCssScanner styleScanner = new ZohoCssScanner(this.policy, AntiSamyDOMScanner.messages);
                    try {
                        final CleanResults cr = styleScanner.scanInlineStyle(value);
                        attribute.setNodeValue(cr.getCleanHTML());
                        final List<String> cssScanErrorMessages = cr.getErrorMessages();
                        this.errorMessages.addAll(cssScanErrorMessages);
                    }
                    catch (final DOMException | ScanException e) {
                        this.addError("error.css.attribute.malformed", new Object[] { tagName, HTMLEntityEncoder.htmlEntityEncode(ele.getNodeValue()) });
                        ele.removeAttribute(attribute.getNodeName());
                        --currentAttributeIndex2;
                    }
                }
                else if (attr != null) {
                    if (this.policy.isEnabledURLValidation() && this.policy.getUrlValidator() != null && this.policy.getURLValidation_Attributes().contains(name.toLowerCase())) {
                        ZSecURL urlobj = null;
                        String safeurl = "";
                        try {
                            urlobj = this.policy.getUrlValidator().getValidatedURLObject(value);
                            safeurl = urlobj.getSafeURL();
                        }
                        catch (final MalformedURLException e2) {
                            this.addError("error.attribute.invalid", new Object[] { tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value) });
                        }
                        catch (final StringIndexOutOfBoundsException e3) {
                            this.addError("error.attribute.invalid", new Object[] { tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value) });
                        }
                        ele.removeAttribute(attribute.getNodeName());
                        ele.setAttribute(attribute.getNodeName(), safeurl);
                        isAttributeValid = true;
                    }
                    if (!isAttributeValid && !attr.containsAllowedValue(value.toLowerCase()) && !attr.matchesAllowedExpression(value)) {
                        final String onInvalidAction = attr.getOnInvalid();
                        if ("removeTag".equals(onInvalidAction)) {
                            this.removeNode(ele);
                            this.addError("error.attribute.invalid.removed", new Object[] { tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value) });
                            return true;
                        }
                        if ("filterTag".equals(onInvalidAction)) {
                            this.processChildren(ele, currentStackDepth);
                            this.promoteChildren(ele);
                            this.addError("error.attribute.invalid.filtered", new Object[] { tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value) });
                        }
                        else if ("encodeTag".equals(onInvalidAction)) {
                            this.processChildren(ele, currentStackDepth);
                            this.encodeAndPromoteChildren(ele);
                            this.addError("error.attribute.invalid.encoded", new Object[] { tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value) });
                        }
                        else {
                            ele.removeAttribute(attribute.getNodeName());
                            --currentAttributeIndex2;
                            this.addError("error.attribute.invalid", new Object[] { tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value) });
                            if ("removeTag".equals(onInvalidAction) || "filterTag".equals(onInvalidAction)) {
                                return true;
                            }
                        }
                    }
                }
                else {
                    this.addError("error.attribute.notfound", new Object[] { tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value) });
                    ele.removeAttribute(attribute.getNodeName());
                    --currentAttributeIndex2;
                }
            }
        }
        return false;
    }
    
    private void processChildren(final Node ele, final int currentStackDepth) throws ScanException {
        this.processChildren(ele.getChildNodes(), currentStackDepth);
    }
    
    private void processChildren(final NodeList childNodes, final int currentStackDepth) throws ScanException {
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node tmp = childNodes.item(i);
            this.recursiveValidateTag(tmp, currentStackDepth);
            if (tmp.getParentNode() == null) {
                --i;
            }
        }
    }
    
    private void removePI(final Node node) {
        this.addError("error.pi.found", new Object[] { HTMLEntityEncoder.htmlEntityEncode(node.getTextContent()) });
        this.removeNode(node);
    }
    
    private void stripCData(final Node node) {
        this.addError("error.cdata.found", new Object[] { HTMLEntityEncoder.htmlEntityEncode(node.getTextContent()) });
        final Node text = this.document.createTextNode(node.getTextContent());
        node.getParentNode().insertBefore(text, node);
        node.getParentNode().removeChild(node);
    }
    
    private void processCommentNode(final Node node) {
        if (!this.policy.isPreserveComments()) {
            node.getParentNode().removeChild(node);
        }
        else {
            final String value = ((Comment)node).getData();
            if (value != null) {
                ((Comment)node).setData(AntiSamyDOMScanner.conditionalDirectives.matcher(value).replaceAll(""));
            }
        }
    }
    
    private boolean removeDisallowedEmpty(final Node node) {
        final String tagName = node.getNodeName();
        if (!this.isAllowedEmptyTag(tagName)) {
            this.addError("error.tag.empty", new Object[] { HTMLEntityEncoder.htmlEntityEncode(node.getNodeName()) });
            this.removeNode(node);
            return true;
        }
        return false;
    }
    
    private void removeNode(final Node node) {
        final Node parent = node.getParentNode();
        parent.removeChild(node);
        final String tagName = parent.getNodeName();
        if (parent instanceof Element && parent.getChildNodes().getLength() == 0 && !this.isAllowedEmptyTag(tagName)) {
            this.removeNode(parent);
        }
    }
    
    private boolean isAllowedEmptyTag(final String tagName) {
        return "head".equals(tagName) || this.policy.getAllowedEmptyTags().matches(tagName);
    }
    
    public static void main(final String[] args) throws PolicyException {
    }
    
    private void promoteChildren(final Element ele) {
        this.promoteChildren(ele, ele.getChildNodes());
    }
    
    private void promoteChildren(final Element ele, final NodeList eleChildNodes) {
        final Node parent = ele.getParentNode();
        while (eleChildNodes.getLength() > 0) {
            final Node node = ele.removeChild(eleChildNodes.item(0));
            parent.insertBefore(node, ele);
        }
        if (parent != null) {
            this.removeNode(ele);
        }
    }
    
    private String stripNonValidXMLCharacters(final String in, final Matcher invalidXmlCharsMatcher) {
        if (in == null || "".equals(in)) {
            return "";
        }
        invalidXmlCharsMatcher.reset(in);
        return invalidXmlCharsMatcher.matches() ? invalidXmlCharsMatcher.replaceAll("") : in;
    }
    
    private void encodeAndPromoteChildren(final Element ele) {
        final Node parent = ele.getParentNode();
        final String tagName = ele.getTagName();
        final Node openingTag = parent.getOwnerDocument().createTextNode(this.toString(ele));
        parent.insertBefore(openingTag, ele);
        if (ele.hasChildNodes()) {
            final Node closingTag = parent.getOwnerDocument().createTextNode("</" + tagName + ">");
            parent.insertBefore(closingTag, ele.getNextSibling());
        }
        this.promoteChildren(ele);
    }
    
    private String toString(final Element ele) {
        final StringBuilder eleAsString = new StringBuilder("<" + ele.getNodeName());
        final NamedNodeMap attributes = ele.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            final Node attribute = attributes.item(i);
            final String name = attribute.getNodeName();
            final String value = attribute.getNodeValue();
            eleAsString.append(" ");
            eleAsString.append(HTMLEntityEncoder.htmlEntityEncode(name));
            eleAsString.append("=\"");
            eleAsString.append(HTMLEntityEncoder.htmlEntityEncode(value));
            eleAsString.append("\"");
        }
        if (ele.hasChildNodes()) {
            eleAsString.append(">");
        }
        else {
            eleAsString.append("/>");
        }
        return eleAsString.toString();
    }
    
    @Override
    public CleanResults getResults() {
        return this.results;
    }
    
    static {
        invalidXmlCharacters = Pattern.compile("[\\u0000-\\u001F\\uD800-\\uDFFF\\uFFFE-\\uFFFF&&[^\\u0009\\u000A\\u000D]]");
        conditionalDirectives = Pattern.compile("<?!?\\[\\s*(?:end)?if[^]]*\\]>?");
        cachedItems = new ConcurrentLinkedQueue<CachedItem>();
    }
    
    static class CachedItem
    {
        private final DOMFragmentParser parser;
        private final Matcher invalidXmlCharMatcher;
        
        CachedItem() throws SAXNotSupportedException, SAXNotRecognizedException {
            this.invalidXmlCharMatcher = AntiSamyDOMScanner.invalidXmlCharacters.matcher("");
            this.parser = AntiSamyDOMScanner.getDomParser();
        }
        
        DOMFragmentParser getDomFragmentParser() {
            return this.parser;
        }
    }
}
