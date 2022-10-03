package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;

public class XML11DTDValidator extends XMLDTDValidator
{
    protected static final String DTD_VALIDATOR_PROPERTY = "http://apache.org/xml/properties/internal/validator/dtd";
    
    @Override
    public void reset(final XMLComponentManager manager) {
        XMLDTDValidator curr = null;
        if ((curr = (XMLDTDValidator)manager.getProperty("http://apache.org/xml/properties/internal/validator/dtd")) != null && curr != this) {
            this.fGrammarBucket = curr.getGrammarBucket();
        }
        super.reset(manager);
    }
    
    @Override
    protected void init() {
        if (this.fValidation || this.fDynamicValidation) {
            super.init();
            try {
                this.fValID = this.fDatatypeValidatorFactory.getBuiltInDV("XML11ID");
                this.fValIDRef = this.fDatatypeValidatorFactory.getBuiltInDV("XML11IDREF");
                this.fValIDRefs = this.fDatatypeValidatorFactory.getBuiltInDV("XML11IDREFS");
                this.fValNMTOKEN = this.fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKEN");
                this.fValNMTOKENS = this.fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKENS");
            }
            catch (final Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
