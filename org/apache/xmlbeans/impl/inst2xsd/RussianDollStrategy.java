package org.apache.xmlbeans.impl.inst2xsd;

import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.impl.values.JavaQNameHolder;
import org.apache.xmlbeans.impl.common.PrefixResolver;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.impl.values.JavaUriHolder;
import org.apache.xmlbeans.impl.values.JavaGDurationHolderEx;
import org.apache.xmlbeans.XmlDuration;
import org.apache.xmlbeans.XmlTime;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.impl.values.JavaGDateHolderEx;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlLong;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlShort;
import org.apache.xmlbeans.XmlByte;
import org.apache.xmlbeans.impl.util.XsTypeConverter;
import org.apache.xmlbeans.impl.inst2xsd.util.Attribute;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.common.XmlWhitespace;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.inst2xsd.util.Type;
import org.apache.xmlbeans.impl.inst2xsd.util.Element;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.inst2xsd.util.TypeSystemHolder;
import org.apache.xmlbeans.XmlObject;
import javax.xml.namespace.QName;

public class RussianDollStrategy implements XsdGenStrategy
{
    static final String _xsi = "http://www.w3.org/2001/XMLSchema-instance";
    static final QName _xsiNil;
    static final QName _xsiType;
    private SCTValidationContext _validationContext;
    
    public RussianDollStrategy() {
        this._validationContext = new SCTValidationContext();
    }
    
    @Override
    public void processDoc(final XmlObject[] instances, final Inst2XsdOptions options, final TypeSystemHolder typeSystemHolder) {
        for (int i = 0; i < instances.length; ++i) {
            final XmlObject instance = instances[i];
            final XmlCursor xc = instance.newCursor();
            final StringBuffer comment = new StringBuffer();
            while (!xc.isStart()) {
                xc.toNextToken();
                if (xc.isComment()) {
                    comment.append(xc.getTextValue());
                }
                else {
                    if (xc.isEnddoc()) {
                        return;
                    }
                    continue;
                }
            }
            final Element withElem = this.processElement(xc, comment.toString(), options, typeSystemHolder);
            withElem.setGlobal(true);
            this.addGlobalElement(withElem, typeSystemHolder, options);
        }
    }
    
    protected Element addGlobalElement(final Element withElem, final TypeSystemHolder typeSystemHolder, final Inst2XsdOptions options) {
        assert withElem.isGlobal();
        final Element intoElem = typeSystemHolder.getGlobalElement(withElem.getName());
        if (intoElem == null) {
            typeSystemHolder.addGlobalElement(withElem);
            return withElem;
        }
        this.combineTypes(intoElem.getType(), withElem.getType(), options);
        this.combineElementComments(intoElem, withElem);
        return intoElem;
    }
    
    protected Element processElement(final XmlCursor xc, final String comment, final Inst2XsdOptions options, final TypeSystemHolder typeSystemHolder) {
        assert xc.isStart();
        final Element element = new Element();
        element.setName(xc.getName());
        element.setGlobal(false);
        final Type elemType = Type.createUnnamedType(1);
        element.setType(elemType);
        final StringBuffer textBuff = new StringBuffer();
        final StringBuffer commentBuff = new StringBuffer();
        final List children = new ArrayList();
        final List attributes = new ArrayList();
    Label_0338:
        while (true) {
            final XmlCursor.TokenType tt = xc.toNextToken();
            switch (tt.intValue()) {
                case 6: {
                    final QName attName = xc.getName();
                    if (!RussianDollStrategy._xsiNil.getNamespaceURI().equals(attName.getNamespaceURI())) {
                        attributes.add(this.processAttribute(xc, options, element.getName().getNamespaceURI(), typeSystemHolder));
                        continue;
                    }
                    if (RussianDollStrategy._xsiNil.equals(attName)) {
                        element.setNillable(true);
                        continue;
                    }
                    continue;
                }
                case 3: {
                    children.add(this.processElement(xc, commentBuff.toString(), options, typeSystemHolder));
                    commentBuff.delete(0, commentBuff.length());
                    continue;
                }
                case 5: {
                    textBuff.append(xc.getChars());
                    continue;
                }
                case 8: {
                    commentBuff.append(xc.getTextValue());
                    continue;
                }
                case 7: {
                    continue;
                }
                case 4: {
                    break Label_0338;
                }
                case 9: {
                    continue;
                }
                case 2: {
                    break Label_0338;
                }
                case 0: {
                    break Label_0338;
                }
                case 1: {
                    throw new IllegalStateException();
                }
                default: {
                    throw new IllegalStateException("Unknown TokenType.");
                }
            }
        }
        final String collapsedText = XmlWhitespace.collapse(textBuff.toString(), 3);
        final String commnetStr = (comment == null) ? ((commentBuff.length() == 0) ? null : commentBuff.toString()) : ((commentBuff.length() == 0) ? comment : commentBuff.insert(0, comment).toString());
        element.setComment(commnetStr);
        if (children.size() > 0) {
            if (collapsedText.length() > 0) {
                elemType.setContentType(4);
            }
            else {
                elemType.setContentType(3);
            }
            this.processElementsInComplexType(elemType, children, element.getName().getNamespaceURI(), typeSystemHolder, options);
            this.processAttributesInComplexType(elemType, attributes);
        }
        else {
            final XmlCursor xcForNamespaces = xc.newCursor();
            xcForNamespaces.toParent();
            if (attributes.size() > 0) {
                elemType.setContentType(2);
                final Type extendedType = Type.createNamedType(this.processSimpleContentType(textBuff.toString(), options, xcForNamespaces), 1);
                elemType.setExtensionType(extendedType);
                this.processAttributesInComplexType(elemType, attributes);
            }
            else {
                elemType.setContentType(1);
                elemType.setName(this.processSimpleContentType(textBuff.toString(), options, xcForNamespaces));
                final String enumValue = XmlString.type.getName().equals(elemType.getName()) ? textBuff.toString() : collapsedText;
                elemType.addEnumerationValue(enumValue, xcForNamespaces);
            }
            xcForNamespaces.dispose();
        }
        this.checkIfReferenceToGlobalTypeIsNeeded(element, typeSystemHolder, options);
        return element;
    }
    
    protected void processElementsInComplexType(final Type elemType, final List children, final String parentNamespace, final TypeSystemHolder typeSystemHolder, final Inst2XsdOptions options) {
        final Map elemNamesToElements = new HashMap();
        Element currentElem = null;
        for (final Element child : children) {
            if (currentElem == null) {
                this.checkIfElementReferenceIsNeeded(child, parentNamespace, typeSystemHolder, options);
                elemType.addElement(child);
                elemNamesToElements.put(child.getName(), child);
                currentElem = child;
            }
            else if (currentElem.getName() == child.getName()) {
                this.combineTypes(currentElem.getType(), child.getType(), options);
                this.combineElementComments(currentElem, child);
                currentElem.setMinOccurs(0);
                currentElem.setMaxOccurs(-1);
            }
            else {
                final Element sameElem = elemNamesToElements.get(child.getName());
                if (sameElem == null) {
                    this.checkIfElementReferenceIsNeeded(child, parentNamespace, typeSystemHolder, options);
                    elemType.addElement(child);
                    elemNamesToElements.put(child.getName(), child);
                }
                else {
                    this.combineTypes(currentElem.getType(), child.getType(), options);
                    this.combineElementComments(currentElem, child);
                    elemType.setTopParticleForComplexOrMixedContent(2);
                }
                currentElem = child;
            }
        }
    }
    
    protected void checkIfElementReferenceIsNeeded(final Element child, final String parentNamespace, final TypeSystemHolder typeSystemHolder, final Inst2XsdOptions options) {
        if (!child.getName().getNamespaceURI().equals(parentNamespace)) {
            Element referencedElem = new Element();
            referencedElem.setGlobal(true);
            referencedElem.setName(child.getName());
            referencedElem.setType(child.getType());
            if (child.isNillable()) {
                referencedElem.setNillable(true);
                child.setNillable(false);
            }
            referencedElem = this.addGlobalElement(referencedElem, typeSystemHolder, options);
            child.setRef(referencedElem);
        }
    }
    
    protected void checkIfReferenceToGlobalTypeIsNeeded(final Element elem, final TypeSystemHolder typeSystemHolder, final Inst2XsdOptions options) {
    }
    
    protected void processAttributesInComplexType(final Type elemType, final List attributes) {
        assert elemType.isComplexType();
        for (final Attribute att : attributes) {
            elemType.addAttribute(att);
        }
    }
    
    protected Attribute processAttribute(final XmlCursor xc, final Inst2XsdOptions options, final String parentNamespace, final TypeSystemHolder typeSystemHolder) {
        assert xc.isAttr() : "xc not on attribute";
        final Attribute attribute = new Attribute();
        final QName attName = xc.getName();
        attribute.setName(attName);
        final XmlCursor parent = xc.newCursor();
        parent.toParent();
        final Type simpleContentType = Type.createNamedType(this.processSimpleContentType(xc.getTextValue(), options, parent), 1);
        parent.dispose();
        attribute.setType(simpleContentType);
        this.checkIfAttributeReferenceIsNeeded(attribute, parentNamespace, typeSystemHolder);
        return attribute;
    }
    
    protected void checkIfAttributeReferenceIsNeeded(final Attribute attribute, final String parentNamespace, final TypeSystemHolder typeSystemHolder) {
        if (!attribute.getName().getNamespaceURI().equals("") && !attribute.getName().getNamespaceURI().equals(parentNamespace)) {
            final Attribute referencedAtt = new Attribute();
            referencedAtt.setGlobal(true);
            referencedAtt.setName(attribute.getName());
            referencedAtt.setType(attribute.getType());
            typeSystemHolder.addGlobalAttribute(referencedAtt);
            attribute.setRef(referencedAtt);
        }
    }
    
    protected QName processSimpleContentType(final String lexicalValue, final Inst2XsdOptions options, final XmlCursor xc) {
        if (options.getSimpleContentTypes() == 2) {
            return XmlString.type.getName();
        }
        if (options.getSimpleContentTypes() != 1) {
            throw new IllegalArgumentException("Unknown value for Inst2XsdOptions.getSimpleContentTypes() :" + options.getSimpleContentTypes());
        }
        try {
            XsTypeConverter.lexByte(lexicalValue);
            return XmlByte.type.getName();
        }
        catch (final Exception e) {
            try {
                XsTypeConverter.lexShort(lexicalValue);
                return XmlShort.type.getName();
            }
            catch (final Exception e) {
                try {
                    XsTypeConverter.lexInt(lexicalValue);
                    return XmlInt.type.getName();
                }
                catch (final Exception e) {
                    try {
                        XsTypeConverter.lexLong(lexicalValue);
                        return XmlLong.type.getName();
                    }
                    catch (final Exception e) {
                        try {
                            XsTypeConverter.lexInteger(lexicalValue);
                            return XmlInteger.type.getName();
                        }
                        catch (final Exception e) {
                            try {
                                XsTypeConverter.lexFloat(lexicalValue);
                                return XmlFloat.type.getName();
                            }
                            catch (final Exception e) {
                                JavaGDateHolderEx.validateLexical(lexicalValue, XmlDate.type, this._validationContext);
                                if (this._validationContext.isValid()) {
                                    return XmlDate.type.getName();
                                }
                                this._validationContext.resetToValid();
                                JavaGDateHolderEx.validateLexical(lexicalValue, XmlDateTime.type, this._validationContext);
                                if (this._validationContext.isValid()) {
                                    return XmlDateTime.type.getName();
                                }
                                this._validationContext.resetToValid();
                                JavaGDateHolderEx.validateLexical(lexicalValue, XmlTime.type, this._validationContext);
                                if (this._validationContext.isValid()) {
                                    return XmlTime.type.getName();
                                }
                                this._validationContext.resetToValid();
                                JavaGDurationHolderEx.validateLexical(lexicalValue, XmlDuration.type, this._validationContext);
                                if (this._validationContext.isValid()) {
                                    return XmlDuration.type.getName();
                                }
                                this._validationContext.resetToValid();
                                if (lexicalValue.startsWith("http://") || lexicalValue.startsWith("www.")) {
                                    JavaUriHolder.validateLexical(lexicalValue, this._validationContext);
                                    if (this._validationContext.isValid()) {
                                        return XmlAnyURI.type.getName();
                                    }
                                    this._validationContext.resetToValid();
                                }
                                final int idx = lexicalValue.indexOf(58);
                                if (idx >= 0 && idx == lexicalValue.lastIndexOf(58) && idx + 1 < lexicalValue.length()) {
                                    final PrefixResolver prefixResolver = new PrefixResolver() {
                                        @Override
                                        public String getNamespaceForPrefix(final String prefix) {
                                            return xc.namespaceForPrefix(prefix);
                                        }
                                    };
                                    final QName qname = JavaQNameHolder.validateLexical(lexicalValue, this._validationContext, prefixResolver);
                                    if (this._validationContext.isValid()) {
                                        return XmlQName.type.getName();
                                    }
                                    this._validationContext.resetToValid();
                                }
                                return XmlString.type.getName();
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void combineTypes(final Type into, final Type with, final Inst2XsdOptions options) {
        if (into == with) {
            return;
        }
        if (into.isGlobal() && with.isGlobal() && into.getName().equals(with.getName())) {
            return;
        }
        if (into.getContentType() == 1 && with.getContentType() == 1) {
            this.combineSimpleTypes(into, with, options);
            return;
        }
        if ((into.getContentType() == 1 || into.getContentType() == 2) && (with.getContentType() == 1 || with.getContentType() == 2)) {
            final QName intoTypeName = into.isComplexType() ? into.getExtensionType().getName() : into.getName();
            final QName withTypeName = with.isComplexType() ? with.getExtensionType().getName() : with.getName();
            into.setContentType(2);
            final QName moreGeneralTypeName = this.combineToMoreGeneralSimpleType(intoTypeName, withTypeName);
            if (into.isComplexType()) {
                final Type extendedType = Type.createNamedType(moreGeneralTypeName, 1);
                into.setExtensionType(extendedType);
            }
            else {
                into.setName(moreGeneralTypeName);
            }
            this.combineAttributesOfTypes(into, with);
            return;
        }
        if (into.getContentType() == 3 && with.getContentType() == 3) {
            this.combineAttributesOfTypes(into, with);
            this.combineElementsOfTypes(into, with, false, options);
            return;
        }
        if (into.getContentType() == 1 || into.getContentType() == 2 || with.getContentType() == 1 || with.getContentType() == 2) {
            into.setContentType(4);
            this.combineAttributesOfTypes(into, with);
            this.combineElementsOfTypes(into, with, true, options);
            return;
        }
        if ((into.getContentType() == 1 || into.getContentType() == 2 || into.getContentType() == 3 || into.getContentType() == 4) && (with.getContentType() == 1 || with.getContentType() == 2 || with.getContentType() == 3 || with.getContentType() == 4)) {
            into.setContentType(4);
            this.combineAttributesOfTypes(into, with);
            this.combineElementsOfTypes(into, with, false, options);
            return;
        }
        throw new IllegalArgumentException("Unknown content type.");
    }
    
    protected void combineSimpleTypes(final Type into, final Type with, final Inst2XsdOptions options) {
        assert into.getContentType() == 1 && with.getContentType() == 1 : "Invalid arguments";
        into.setName(this.combineToMoreGeneralSimpleType(into.getName(), with.getName()));
        if (options.isUseEnumerations()) {
            into.addAllEnumerationsFrom(with);
            if (into.getEnumerationValues().size() > options.getUseEnumerations()) {
                into.closeEnumeration();
            }
        }
    }
    
    protected QName combineToMoreGeneralSimpleType(final QName t1, final QName t2) {
        if (t1.equals(t2)) {
            return t1;
        }
        if (t2.equals(XmlShort.type.getName()) && t1.equals(XmlByte.type.getName())) {
            return t2;
        }
        if (t1.equals(XmlShort.type.getName()) && t2.equals(XmlByte.type.getName())) {
            return t1;
        }
        if (t2.equals(XmlInt.type.getName()) && (t1.equals(XmlShort.type.getName()) || t1.equals(XmlByte.type.getName()))) {
            return t2;
        }
        if (t1.equals(XmlInt.type.getName()) && (t2.equals(XmlShort.type.getName()) || t2.equals(XmlByte.type.getName()))) {
            return t1;
        }
        if (t2.equals(XmlLong.type.getName()) && (t1.equals(XmlInt.type.getName()) || t1.equals(XmlShort.type.getName()) || t1.equals(XmlByte.type.getName()))) {
            return t2;
        }
        if (t1.equals(XmlLong.type.getName()) && (t2.equals(XmlInt.type.getName()) || t2.equals(XmlShort.type.getName()) || t2.equals(XmlByte.type.getName()))) {
            return t1;
        }
        if (t2.equals(XmlInteger.type.getName()) && (t1.equals(XmlLong.type.getName()) || t1.equals(XmlInt.type.getName()) || t1.equals(XmlShort.type.getName()) || t1.equals(XmlByte.type.getName()))) {
            return t2;
        }
        if (t1.equals(XmlInteger.type.getName()) && (t2.equals(XmlLong.type.getName()) || t2.equals(XmlInt.type.getName()) || t2.equals(XmlShort.type.getName()) || t2.equals(XmlByte.type.getName()))) {
            return t1;
        }
        if (t2.equals(XmlFloat.type.getName()) && (t1.equals(XmlInteger.type.getName()) || t1.equals(XmlLong.type.getName()) || t1.equals(XmlInt.type.getName()) || t1.equals(XmlShort.type.getName()) || t1.equals(XmlByte.type.getName()))) {
            return t2;
        }
        if (t1.equals(XmlFloat.type.getName()) && (t2.equals(XmlInteger.type.getName()) || t2.equals(XmlLong.type.getName()) || t2.equals(XmlInt.type.getName()) || t2.equals(XmlShort.type.getName()) || t2.equals(XmlByte.type.getName()))) {
            return t1;
        }
        return XmlString.type.getName();
    }
    
    protected void combineAttributesOfTypes(final Type into, final Type from) {
        int i = 0;
    Label_0002:
        while (i < from.getAttributes().size()) {
            final Attribute fromAtt = from.getAttributes().get(i);
            while (true) {
                for (int j = 0; j < into.getAttributes().size(); ++j) {
                    final Attribute intoAtt = into.getAttributes().get(j);
                    if (intoAtt.getName().equals(fromAtt.getName())) {
                        intoAtt.getType().setName(this.combineToMoreGeneralSimpleType(intoAtt.getType().getName(), fromAtt.getType().getName()));
                        ++i;
                        continue Label_0002;
                    }
                }
                into.addAttribute(fromAtt);
                continue;
            }
        }
        for (i = 0; i < into.getAttributes().size(); ++i) {
            final Attribute intoAtt2 = into.getAttributes().get(i);
            for (int j = 0; j < from.getAttributes().size(); ++j) {
                final Attribute fromAtt2 = from.getAttributes().get(j);
                if (fromAtt2.getName().equals(intoAtt2.getName())) {}
            }
            intoAtt2.setOptional(true);
        }
    }
    
    protected void combineElementsOfTypes(final Type into, final Type from, final boolean makeElementsOptional, final Inst2XsdOptions options) {
        boolean needsUnboundedChoice = false;
        if (into.getTopParticleForComplexOrMixedContent() != 1 || from.getTopParticleForComplexOrMixedContent() != 1) {
            needsUnboundedChoice = true;
        }
        final List res = new ArrayList();
        int fromStartingIndex = 0;
        int fromMatchedIndex = -1;
        int intoMatchedIndex = -1;
        for (int i = 0; !needsUnboundedChoice && i < into.getElements().size(); ++i) {
            final Element intoElement = into.getElements().get(i);
            for (int j = fromStartingIndex; j < from.getElements().size(); ++j) {
                final Element fromElement = from.getElements().get(j);
                if (intoElement.getName().equals(fromElement.getName())) {
                    fromMatchedIndex = j;
                    break;
                }
            }
            if (fromMatchedIndex < fromStartingIndex) {
                res.add(intoElement);
                intoElement.setMinOccurs(0);
            }
            else {
            Label_0265:
                for (int j2 = fromStartingIndex; j2 < fromMatchedIndex; ++j2) {
                    final Element fromCandidate = from.getElements().get(j2);
                    for (int i2 = i + 1; i2 < into.getElements().size(); ++i2) {
                        final Element intoCandidate = into.getElements().get(i2);
                        if (fromCandidate.getName().equals(intoCandidate.getName())) {
                            intoMatchedIndex = i2;
                            break Label_0265;
                        }
                    }
                }
                if (intoMatchedIndex < i) {
                    for (int j3 = fromStartingIndex; j3 < fromMatchedIndex; ++j3) {
                        final Element fromCandidate = from.getElements().get(j3);
                        res.add(fromCandidate);
                        fromCandidate.setMinOccurs(0);
                    }
                    res.add(intoElement);
                    final Element fromMatchedElement = from.getElements().get(fromMatchedIndex);
                    if (fromMatchedElement.getMinOccurs() <= 0) {
                        intoElement.setMinOccurs(0);
                    }
                    if (fromMatchedElement.getMaxOccurs() == -1) {
                        intoElement.setMaxOccurs(-1);
                    }
                    this.combineTypes(intoElement.getType(), fromMatchedElement.getType(), options);
                    this.combineElementComments(intoElement, fromMatchedElement);
                    fromStartingIndex = fromMatchedIndex + 1;
                }
                else {
                    needsUnboundedChoice = true;
                }
            }
        }
        for (int k = fromStartingIndex; k < from.getElements().size(); ++k) {
            final Element remainingFromElement = from.getElements().get(k);
            res.add(remainingFromElement);
            remainingFromElement.setMinOccurs(0);
        }
        if (needsUnboundedChoice) {
            into.setTopParticleForComplexOrMixedContent(2);
            int k = 0;
        Label_0639_Outer:
            while (k < from.getElements().size()) {
                final Element fromElem = from.getElements().get(k);
                int l = 0;
                while (true) {
                    while (l < into.getElements().size()) {
                        final Element intoElem = into.getElements().get(l);
                        intoElem.setMinOccurs(1);
                        intoElem.setMaxOccurs(1);
                        if (intoElem != fromElem) {
                            if (!intoElem.getName().equals(fromElem.getName())) {
                                ++l;
                                continue Label_0639_Outer;
                            }
                            this.combineTypes(intoElem.getType(), fromElem.getType(), options);
                            this.combineElementComments(intoElem, fromElem);
                        }
                        ++k;
                        continue Label_0639_Outer;
                    }
                    into.addElement(fromElem);
                    fromElem.setMinOccurs(1);
                    fromElem.setMaxOccurs(1);
                    continue;
                }
            }
            return;
        }
        into.setElements(res);
    }
    
    protected void combineElementComments(final Element into, final Element with) {
        if (with.getComment() != null && with.getComment().length() > 0) {
            if (into.getComment() == null) {
                into.setComment(with.getComment());
            }
            else {
                into.setComment(into.getComment() + with.getComment());
            }
        }
    }
    
    static {
        _xsiNil = new QName("http://www.w3.org/2001/XMLSchema-instance", "nil", "xsi");
        _xsiType = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
    }
    
    protected class SCTValidationContext implements ValidationContext
    {
        protected boolean valid;
        
        protected SCTValidationContext() {
            this.valid = true;
        }
        
        public boolean isValid() {
            return this.valid;
        }
        
        public void resetToValid() {
            this.valid = true;
        }
        
        @Override
        public void invalid(final String message) {
            this.valid = false;
        }
        
        @Override
        public void invalid(final String code, final Object[] args) {
            this.valid = false;
        }
    }
}
