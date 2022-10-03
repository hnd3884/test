package org.apache.xmlbeans;

import javax.xml.namespace.QName;
import java.math.BigInteger;

public interface SchemaParticle
{
    public static final int ALL = 1;
    public static final int CHOICE = 2;
    public static final int SEQUENCE = 3;
    public static final int ELEMENT = 4;
    public static final int WILDCARD = 5;
    public static final int STRICT = 1;
    public static final int LAX = 2;
    public static final int SKIP = 3;
    
    int getParticleType();
    
    BigInteger getMinOccurs();
    
    BigInteger getMaxOccurs();
    
    int getIntMinOccurs();
    
    int getIntMaxOccurs();
    
    boolean isSingleton();
    
    SchemaParticle[] getParticleChildren();
    
    SchemaParticle getParticleChild(final int p0);
    
    int countOfParticleChild();
    
    boolean canStartWithElement(final QName p0);
    
    QNameSet acceptedStartNames();
    
    boolean isSkippable();
    
    QNameSet getWildcardSet();
    
    int getWildcardProcess();
    
    QName getName();
    
    SchemaType getType();
    
    boolean isNillable();
    
    String getDefaultText();
    
    XmlAnySimpleType getDefaultValue();
    
    boolean isDefault();
    
    boolean isFixed();
}
