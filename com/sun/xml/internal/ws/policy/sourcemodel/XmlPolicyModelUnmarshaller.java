package com.sun.xml.internal.ws.policy.sourcemodel;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.XMLInputFactory;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import javax.xml.namespace.QName;
import java.net.URISyntaxException;
import java.net.URI;
import javax.xml.stream.events.Attribute;
import com.sun.xml.internal.ws.policy.PolicyConstants;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class XmlPolicyModelUnmarshaller extends PolicyModelUnmarshaller
{
    private static final PolicyLogger LOGGER;
    
    protected XmlPolicyModelUnmarshaller() {
    }
    
    @Override
    public PolicySourceModel unmarshalModel(final Object storage) throws PolicyException {
        final XMLEventReader reader = this.createXMLEventReader(storage);
        PolicySourceModel model = null;
    Label_0244:
        while (reader.hasNext()) {
            try {
                final XMLEvent event = reader.peek();
                switch (event.getEventType()) {
                    case 5:
                    case 7: {
                        reader.nextEvent();
                        continue;
                    }
                    case 4: {
                        this.processCharacters(ModelNode.Type.POLICY, event.asCharacters(), null);
                        reader.nextEvent();
                        continue;
                    }
                    case 1: {
                        if (NamespaceVersion.resolveAsToken(event.asStartElement().getName()) == XmlToken.Policy) {
                            final StartElement rootElement = reader.nextEvent().asStartElement();
                            model = this.initializeNewModel(rootElement);
                            this.unmarshalNodeContent(model.getNamespaceVersion(), model.getRootNode(), rootElement.getName(), reader);
                            break Label_0244;
                        }
                        throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST()));
                    }
                    default: {
                        throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0048_POLICY_ELEMENT_EXPECTED_FIRST()));
                    }
                }
                continue;
            }
            catch (final XMLStreamException e) {
                throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION(), e));
            }
            break;
        }
        return model;
    }
    
    protected PolicySourceModel createSourceModel(final NamespaceVersion nsVersion, final String id, final String name) {
        return PolicySourceModel.createPolicySourceModel(nsVersion, id, name);
    }
    
    private PolicySourceModel initializeNewModel(final StartElement element) throws PolicyException, XMLStreamException {
        final NamespaceVersion nsVersion = NamespaceVersion.resolveVersion(element.getName().getNamespaceURI());
        final Attribute policyName = this.getAttributeByName(element, nsVersion.asQName(XmlToken.Name));
        final Attribute xmlId = this.getAttributeByName(element, PolicyConstants.XML_ID);
        Attribute policyId = this.getAttributeByName(element, PolicyConstants.WSU_ID);
        if (policyId == null) {
            policyId = xmlId;
        }
        else if (xmlId != null) {
            throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0058_MULTIPLE_POLICY_IDS_NOT_ALLOWED()));
        }
        final PolicySourceModel model = this.createSourceModel(nsVersion, (policyId == null) ? null : policyId.getValue(), (policyName == null) ? null : policyName.getValue());
        return model;
    }
    
    private ModelNode addNewChildNode(final NamespaceVersion nsVersion, final ModelNode parentNode, final StartElement childElement) throws PolicyException {
        final QName childElementName = childElement.getName();
        ModelNode childNode;
        if (parentNode.getType() == ModelNode.Type.ASSERTION_PARAMETER_NODE) {
            childNode = parentNode.createChildAssertionParameterNode();
        }
        else {
            final XmlToken token = NamespaceVersion.resolveAsToken(childElementName);
            switch (token) {
                case Policy: {
                    childNode = parentNode.createChildPolicyNode();
                    return childNode;
                }
                case All: {
                    childNode = parentNode.createChildAllNode();
                    return childNode;
                }
                case ExactlyOne: {
                    childNode = parentNode.createChildExactlyOneNode();
                    return childNode;
                }
                case PolicyReference: {
                    final Attribute uri = this.getAttributeByName(childElement, nsVersion.asQName(XmlToken.Uri));
                    if (uri == null) {
                        throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0040_POLICY_REFERENCE_URI_ATTR_NOT_FOUND()));
                    }
                    try {
                        final URI reference = new URI(uri.getValue());
                        final Attribute digest = this.getAttributeByName(childElement, nsVersion.asQName(XmlToken.Digest));
                        PolicyReferenceData refData;
                        if (digest == null) {
                            refData = new PolicyReferenceData(reference);
                        }
                        else {
                            final Attribute digestAlgorithm = this.getAttributeByName(childElement, nsVersion.asQName(XmlToken.DigestAlgorithm));
                            URI algorithmRef = null;
                            if (digestAlgorithm != null) {
                                algorithmRef = new URI(digestAlgorithm.getValue());
                            }
                            refData = new PolicyReferenceData(reference, digest.getValue(), algorithmRef);
                        }
                        childNode = parentNode.createChildPolicyReferenceNode(refData);
                        return childNode;
                    }
                    catch (final URISyntaxException e) {
                        throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0012_UNABLE_TO_UNMARSHALL_POLICY_MALFORMED_URI(), e));
                    }
                    break;
                }
            }
            if (parentNode.isDomainSpecific()) {
                childNode = parentNode.createChildAssertionParameterNode();
            }
            else {
                childNode = parentNode.createChildAssertionNode();
            }
        }
        return childNode;
    }
    
    private void parseAssertionData(final NamespaceVersion nsVersion, final String value, final ModelNode childNode, final StartElement childElement) throws IllegalArgumentException, PolicyException {
        final Map<QName, String> attributeMap = new HashMap<QName, String>();
        boolean optional = false;
        boolean ignorable = false;
        final Iterator iterator = childElement.getAttributes();
        while (iterator.hasNext()) {
            final Attribute nextAttribute = iterator.next();
            final QName name = nextAttribute.getName();
            if (attributeMap.containsKey(name)) {
                throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0059_MULTIPLE_ATTRS_WITH_SAME_NAME_DETECTED_FOR_ASSERTION(nextAttribute.getName(), childElement.getName())));
            }
            if (nsVersion.asQName(XmlToken.Optional).equals(name)) {
                optional = this.parseBooleanValue(nextAttribute.getValue());
            }
            else if (nsVersion.asQName(XmlToken.Ignorable).equals(name)) {
                ignorable = this.parseBooleanValue(nextAttribute.getValue());
            }
            else {
                attributeMap.put(name, nextAttribute.getValue());
            }
        }
        final AssertionData nodeData = new AssertionData(childElement.getName(), value, attributeMap, childNode.getType(), optional, ignorable);
        if (nodeData.containsAttribute(PolicyConstants.VISIBILITY_ATTRIBUTE)) {
            final String visibilityValue = nodeData.getAttributeValue(PolicyConstants.VISIBILITY_ATTRIBUTE);
            if (!"private".equals(visibilityValue)) {
                throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0004_UNEXPECTED_VISIBILITY_ATTR_VALUE(visibilityValue)));
            }
        }
        childNode.setOrReplaceNodeData(nodeData);
    }
    
    private Attribute getAttributeByName(final StartElement element, final QName attributeName) {
        Attribute attribute = element.getAttributeByName(attributeName);
        if (attribute == null) {
            final String localAttributeName = attributeName.getLocalPart();
            final Iterator iterator = element.getAttributes();
            while (iterator.hasNext()) {
                final Attribute nextAttribute = iterator.next();
                final QName aName = nextAttribute.getName();
                final boolean attributeFoundByWorkaround = aName.equals(attributeName) || (aName.getLocalPart().equals(localAttributeName) && (aName.getPrefix() == null || "".equals(aName.getPrefix())));
                if (attributeFoundByWorkaround) {
                    attribute = nextAttribute;
                    break;
                }
            }
        }
        return attribute;
    }
    
    private String unmarshalNodeContent(final NamespaceVersion nsVersion, final ModelNode node, final QName nodeElementName, final XMLEventReader reader) throws PolicyException {
        StringBuilder valueBuffer = null;
    Label_0210:
        while (reader.hasNext()) {
            try {
                final XMLEvent xmlParserEvent = reader.nextEvent();
                switch (xmlParserEvent.getEventType()) {
                    case 5: {
                        continue;
                    }
                    case 4: {
                        valueBuffer = this.processCharacters(node.getType(), xmlParserEvent.asCharacters(), valueBuffer);
                        continue;
                    }
                    case 2: {
                        this.checkEndTagName(nodeElementName, xmlParserEvent.asEndElement());
                        break Label_0210;
                    }
                    case 1: {
                        final StartElement childElement = xmlParserEvent.asStartElement();
                        final ModelNode childNode = this.addNewChildNode(nsVersion, node, childElement);
                        final String value = this.unmarshalNodeContent(nsVersion, childNode, childElement.getName(), reader);
                        if (childNode.isDomainSpecific()) {
                            this.parseAssertionData(nsVersion, value, childNode, childElement);
                            continue;
                        }
                        continue;
                    }
                    default: {
                        throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0011_UNABLE_TO_UNMARSHALL_POLICY_XML_ELEM_EXPECTED()));
                    }
                }
                continue;
            }
            catch (final XMLStreamException e) {
                throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0068_FAILED_TO_UNMARSHALL_POLICY_EXPRESSION(), e));
            }
            break;
        }
        return (valueBuffer == null) ? null : valueBuffer.toString().trim();
    }
    
    private XMLEventReader createXMLEventReader(final Object storage) throws PolicyException {
        if (storage instanceof XMLEventReader) {
            return (XMLEventReader)storage;
        }
        if (!(storage instanceof Reader)) {
            throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0022_STORAGE_TYPE_NOT_SUPPORTED(storage.getClass().getName())));
        }
        try {
            return XMLInputFactory.newInstance().createXMLEventReader((Reader)storage);
        }
        catch (final XMLStreamException e) {
            throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0014_UNABLE_TO_INSTANTIATE_READER_FOR_STORAGE(), e));
        }
    }
    
    private void checkEndTagName(final QName expected, final EndElement element) throws PolicyException {
        final QName actual = element.getName();
        if (!expected.equals(actual)) {
            throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0003_UNMARSHALLING_FAILED_END_TAG_DOES_NOT_MATCH(expected, actual)));
        }
    }
    
    private StringBuilder processCharacters(final ModelNode.Type currentNodeType, final Characters characters, final StringBuilder currentValueBuffer) throws PolicyException {
        if (characters.isWhiteSpace()) {
            return currentValueBuffer;
        }
        final StringBuilder buffer = (currentValueBuffer == null) ? new StringBuilder() : currentValueBuffer;
        final String data = characters.getData();
        if (currentNodeType == ModelNode.Type.ASSERTION || currentNodeType == ModelNode.Type.ASSERTION_PARAMETER_NODE) {
            return buffer.append(data);
        }
        throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0009_UNEXPECTED_CDATA_ON_SOURCE_MODEL_NODE(currentNodeType, data)));
    }
    
    private boolean parseBooleanValue(final String value) throws PolicyException {
        if ("true".equals(value) || "1".equals(value)) {
            return true;
        }
        if ("false".equals(value) || "0".equals(value)) {
            return false;
        }
        throw XmlPolicyModelUnmarshaller.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0095_INVALID_BOOLEAN_VALUE(value)));
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(XmlPolicyModelUnmarshaller.class);
    }
}
