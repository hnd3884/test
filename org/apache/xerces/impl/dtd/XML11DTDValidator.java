package org.apache.xerces.impl.dtd;

import org.apache.xerces.xni.parser.XMLComponentManager;

public class XML11DTDValidator extends XMLDTDValidator
{
    protected static final String DTD_VALIDATOR_PROPERTY = "http://apache.org/xml/properties/internal/validator/dtd";
    
    public void reset(final XMLComponentManager xmlComponentManager) {
        final XMLDTDValidator xmldtdValidator;
        if ((xmldtdValidator = (XMLDTDValidator)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/dtd")) != null && xmldtdValidator != this) {
            this.fGrammarBucket = xmldtdValidator.getGrammarBucket();
        }
        super.reset(xmlComponentManager);
    }
    
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
            catch (final Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}
