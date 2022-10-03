package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Result;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;

public interface TypeInfoSet<T, C, F, M>
{
    Navigator<T, C, F, M> getNavigator();
    
    NonElement<T, C> getTypeInfo(final T p0);
    
    NonElement<T, C> getAnyTypeInfo();
    
    NonElement<T, C> getClassInfo(final C p0);
    
    Map<? extends T, ? extends ArrayInfo<T, C>> arrays();
    
    Map<C, ? extends ClassInfo<T, C>> beans();
    
    Map<T, ? extends BuiltinLeafInfo<T, C>> builtins();
    
    Map<C, ? extends EnumLeafInfo<T, C>> enums();
    
    ElementInfo<T, C> getElementInfo(final C p0, final QName p1);
    
    NonElement<T, C> getTypeInfo(final Ref<T, C> p0);
    
    Map<QName, ? extends ElementInfo<T, C>> getElementMappings(final C p0);
    
    Iterable<? extends ElementInfo<T, C>> getAllElements();
    
    Map<String, String> getXmlNs(final String p0);
    
    Map<String, String> getSchemaLocations();
    
    XmlNsForm getElementFormDefault(final String p0);
    
    XmlNsForm getAttributeFormDefault(final String p0);
    
    void dump(final Result p0) throws JAXBException;
}
