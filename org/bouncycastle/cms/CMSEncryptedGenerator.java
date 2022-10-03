package org.bouncycastle.cms;

public class CMSEncryptedGenerator
{
    protected CMSAttributeTableGenerator unprotectedAttributeGenerator;
    
    protected CMSEncryptedGenerator() {
        this.unprotectedAttributeGenerator = null;
    }
    
    public void setUnprotectedAttributeGenerator(final CMSAttributeTableGenerator unprotectedAttributeGenerator) {
        this.unprotectedAttributeGenerator = unprotectedAttributeGenerator;
    }
}
