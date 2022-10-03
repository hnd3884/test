package org.apache.xerces.util;

import java.util.Hashtable;
import org.w3c.dom.DOMLocator;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.dom.DOMLocatorImpl;
import org.w3c.dom.DOMError;
import org.apache.xerces.xni.parser.XMLParseException;
import java.io.OutputStream;
import org.apache.xerces.dom.DOMErrorImpl;
import org.w3c.dom.Node;
import java.io.PrintWriter;
import org.w3c.dom.DOMErrorHandler;
import org.apache.xerces.xni.parser.XMLErrorHandler;

public class DOMErrorHandlerWrapper implements XMLErrorHandler, DOMErrorHandler
{
    protected DOMErrorHandler fDomErrorHandler;
    boolean eStatus;
    protected PrintWriter fOut;
    public Node fCurrentNode;
    protected final XMLErrorCode fErrorCode;
    protected final DOMErrorImpl fDOMError;
    
    public DOMErrorHandlerWrapper() {
        this.eStatus = true;
        this.fErrorCode = new XMLErrorCode(null, null);
        this.fDOMError = new DOMErrorImpl();
        this.fOut = new PrintWriter(System.err);
    }
    
    public DOMErrorHandlerWrapper(final DOMErrorHandler fDomErrorHandler) {
        this.eStatus = true;
        this.fErrorCode = new XMLErrorCode(null, null);
        this.fDOMError = new DOMErrorImpl();
        this.fDomErrorHandler = fDomErrorHandler;
    }
    
    public void setErrorHandler(final DOMErrorHandler fDomErrorHandler) {
        this.fDomErrorHandler = fDomErrorHandler;
    }
    
    public DOMErrorHandler getErrorHandler() {
        return this.fDomErrorHandler;
    }
    
    public void warning(final String s, final String fType, final XMLParseException fException) throws XNIException {
        this.fDOMError.fSeverity = 1;
        this.fDOMError.fException = fException;
        this.fDOMError.fType = fType;
        final DOMErrorImpl fdomError = this.fDOMError;
        final DOMErrorImpl fdomError2 = this.fDOMError;
        final String message = fException.getMessage();
        fdomError2.fMessage = message;
        fdomError.fRelatedData = message;
        final DOMLocatorImpl fLocator = this.fDOMError.fLocator;
        if (fLocator != null) {
            fLocator.fColumnNumber = fException.getColumnNumber();
            fLocator.fLineNumber = fException.getLineNumber();
            fLocator.fUtf16Offset = fException.getCharacterOffset();
            fLocator.fUri = fException.getExpandedSystemId();
            fLocator.fRelatedNode = this.fCurrentNode;
        }
        if (this.fDomErrorHandler != null) {
            this.fDomErrorHandler.handleError(this.fDOMError);
        }
    }
    
    public void error(final String s, final String fType, final XMLParseException fException) throws XNIException {
        this.fDOMError.fSeverity = 2;
        this.fDOMError.fException = fException;
        this.fDOMError.fType = fType;
        final DOMErrorImpl fdomError = this.fDOMError;
        final DOMErrorImpl fdomError2 = this.fDOMError;
        final String message = fException.getMessage();
        fdomError2.fMessage = message;
        fdomError.fRelatedData = message;
        final DOMLocatorImpl fLocator = this.fDOMError.fLocator;
        if (fLocator != null) {
            fLocator.fColumnNumber = fException.getColumnNumber();
            fLocator.fLineNumber = fException.getLineNumber();
            fLocator.fUtf16Offset = fException.getCharacterOffset();
            fLocator.fUri = fException.getExpandedSystemId();
            fLocator.fRelatedNode = this.fCurrentNode;
        }
        if (this.fDomErrorHandler != null) {
            this.fDomErrorHandler.handleError(this.fDOMError);
        }
    }
    
    public void fatalError(final String s, final String s2, final XMLParseException fException) throws XNIException {
        this.fDOMError.fSeverity = 3;
        this.fDOMError.fException = fException;
        this.fErrorCode.setValues(s, s2);
        final String domErrorType = DOMErrorTypeMap.getDOMErrorType(this.fErrorCode);
        this.fDOMError.fType = ((domErrorType != null) ? domErrorType : s2);
        final DOMErrorImpl fdomError = this.fDOMError;
        final DOMErrorImpl fdomError2 = this.fDOMError;
        final String message = fException.getMessage();
        fdomError2.fMessage = message;
        fdomError.fRelatedData = message;
        final DOMLocatorImpl fLocator = this.fDOMError.fLocator;
        if (fLocator != null) {
            fLocator.fColumnNumber = fException.getColumnNumber();
            fLocator.fLineNumber = fException.getLineNumber();
            fLocator.fUtf16Offset = fException.getCharacterOffset();
            fLocator.fUri = fException.getExpandedSystemId();
            fLocator.fRelatedNode = this.fCurrentNode;
        }
        if (this.fDomErrorHandler != null) {
            this.fDomErrorHandler.handleError(this.fDOMError);
        }
    }
    
    public boolean handleError(final DOMError domError) {
        this.printError(domError);
        return this.eStatus;
    }
    
    private void printError(final DOMError domError) {
        final short severity = domError.getSeverity();
        this.fOut.print("[");
        if (severity == 1) {
            this.fOut.print("Warning");
        }
        else if (severity == 2) {
            this.fOut.print("Error");
        }
        else {
            this.fOut.print("FatalError");
            this.eStatus = false;
        }
        this.fOut.print("] ");
        final DOMLocator location = domError.getLocation();
        if (location != null) {
            this.fOut.print(location.getLineNumber());
            this.fOut.print(":");
            this.fOut.print(location.getColumnNumber());
            this.fOut.print(":");
            this.fOut.print(location.getByteOffset());
            this.fOut.print(",");
            this.fOut.print(location.getUtf16Offset());
            final Node relatedNode = location.getRelatedNode();
            if (relatedNode != null) {
                this.fOut.print("[");
                this.fOut.print(relatedNode.getNodeName());
                this.fOut.print("]");
            }
            String s = location.getUri();
            if (s != null) {
                final int lastIndex = s.lastIndexOf(47);
                if (lastIndex != -1) {
                    s = s.substring(lastIndex + 1);
                }
                this.fOut.print(": ");
                this.fOut.print(s);
            }
        }
        this.fOut.print(":");
        this.fOut.print(domError.getMessage());
        this.fOut.println();
        this.fOut.flush();
    }
    
    private static class DOMErrorTypeMap
    {
        private static Hashtable fgDOMErrorTypeTable;
        
        public static String getDOMErrorType(final XMLErrorCode xmlErrorCode) {
            return DOMErrorTypeMap.fgDOMErrorTypeTable.get(xmlErrorCode);
        }
        
        static {
            (DOMErrorTypeMap.fgDOMErrorTypeTable = new Hashtable()).put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "TwoColonsInQName"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ColonNotLegalWithNS"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInProlog"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "CDSectUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "DoctypeNotAllowed"), "doctype-not-allowed");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ETagRequired"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementUnterminated"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EqRequiredInAttribute"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "OpenQuoteExpected"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "CloseQuoteExpected"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ETagUnterminated"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MarkupNotRecognizedInContent"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "DoctypeIllegalInContent"), "doctype-not-allowed");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInAttValue"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInPI"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInInternalSubset"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "QuoteRequiredInAttValue"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "LessthanInAttValue"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttributeValueUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PITargetRequired"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredInPI"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PIUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ReservedPITarget"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PI_NOT_IN_ONE_ENTITY"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PINotInOneEntity"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid"), "unsupported-encoding");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported"), "unsupported-encoding");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInEntityValue"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInExternalSubset"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInIgnoreSect"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInPublicID"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInSystemID"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredAfterSYSTEM"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "QuoteRequiredInSystemID"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SystemIDUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredAfterPUBLIC"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "QuoteRequiredInPublicID"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PublicIDUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PubidCharIllegal"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredBetweenPublicAndSystem"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ROOT_ELEMENT_TYPE_REQUIRED"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "DoctypedeclUnterminated"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PEReferenceWithinMarkup"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MARKUP_NOT_RECOGNIZED_IN_DTD"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ELEMENTDECL"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_TYPE_REQUIRED_IN_ELEMENTDECL"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_CONTENTSPEC_IN_ELEMENTDECL"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENTSPEC_REQUIRED_IN_ELEMENTDECL"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementDeclUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_TYPE_REQUIRED_IN_MIXED_CONTENT"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CLOSE_PAREN_REQUIRED_IN_MIXED"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MixedContentUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ATTLISTDECL"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_TYPE_REQUIRED_IN_ATTLISTDECL"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ATTRIBUTE_NAME_IN_ATTDEF"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttNameRequiredInAttDef"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ATTTYPE_IN_ATTDEF"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttTypeRequiredInAttDef"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_DEFAULTDECL_IN_ATTDEF"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ATTRIBUTE_DEFINITION"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_NOTATION_IN_NOTATIONTYPE"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_OPEN_PAREN_REQUIRED_IN_NOTATIONTYPE"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NAME_REQUIRED_IN_NOTATIONTYPE"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "NotationTypeUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NMTOKEN_REQUIRED_IN_ENUMERATION"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EnumerationUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DISTINCT_TOKENS_IN_ENUMERATION"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DISTINCT_NOTATION_IN_ENUMERATION"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_FIXED_IN_DEFAULTDECL"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "IncludeSectUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "IgnoreSectUnterminated"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "NameRequiredInPEReference"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SemicolonRequiredInPEReference"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_PERCENT_IN_PEDECL"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_PEDECL"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ENTITY_NAME_REQUIRED_IN_ENTITYDECL"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_ENTITY_NAME_IN_ENTITYDECL"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_UNPARSED_ENTITYDECL"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_NDATA_IN_UNPARSED_ENTITYDECL"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NAME_REQUIRED_FOR_UNPARSED_ENTITYDECL"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityDeclUnterminated"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ExternalIDRequired"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_PUBIDLITERAL_IN_EXTERNALID"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_PUBIDLITERAL_IN_EXTERNALID"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_SYSTEMLITERAL_IN_EXTERNALID"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_URI_FRAGMENT_IN_SYSTEMID"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_NOTATIONDECL"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NAME_REQUIRED_IN_NOTATIONDECL"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_NOTATION_NAME_IN_NOTATIONDECL"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ExternalIDorPublicIDRequired"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "NotationDeclUnterminated"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ReferenceToExternalEntity"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ReferenceToUnparsedEntity"), "wf-invalid-character");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingNotSupported"), "unsupported-encoding");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingRequired"), "unsupported-encoding");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementXMLNSPrefix"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementPrefixUnbound"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttributePrefixUnbound"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EmptyPrefixedAttName"), "wf-invalid-character-in-node-name");
            DOMErrorTypeMap.fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PrefixDeclared"), "wf-invalid-character-in-node-name");
        }
    }
}
