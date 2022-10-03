package javax.xml.soap;

import java.util.Iterator;
import javax.xml.namespace.QName;

public interface SOAPHeader extends SOAPElement
{
    SOAPHeaderElement addHeaderElement(final Name p0) throws SOAPException;
    
    SOAPHeaderElement addHeaderElement(final QName p0) throws SOAPException;
    
    Iterator examineMustUnderstandHeaderElements(final String p0);
    
    Iterator examineHeaderElements(final String p0);
    
    Iterator extractHeaderElements(final String p0);
    
    SOAPHeaderElement addNotUnderstoodHeaderElement(final QName p0) throws SOAPException;
    
    SOAPHeaderElement addUpgradeHeaderElement(final Iterator p0) throws SOAPException;
    
    SOAPHeaderElement addUpgradeHeaderElement(final String[] p0) throws SOAPException;
    
    SOAPHeaderElement addUpgradeHeaderElement(final String p0) throws SOAPException;
    
    Iterator examineAllHeaderElements();
    
    Iterator extractAllHeaderElements();
}
