package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.lang.annotation.Annotation;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class Adapter<TypeT, ClassDeclT>
{
    public final ClassDeclT adapterType;
    public final TypeT defaultType;
    public final TypeT customType;
    
    public Adapter(final XmlJavaTypeAdapter spec, final AnnotationReader<TypeT, ClassDeclT, ?, ?> reader, final Navigator<TypeT, ClassDeclT, ?, ?> nav) {
        this(nav.asDecl(reader.getClassValue(spec, "value")), (Navigator<TypeT, Object, ?, ?>)nav);
    }
    
    public Adapter(final ClassDeclT adapterType, final Navigator<TypeT, ClassDeclT, ?, ?> nav) {
        this.adapterType = adapterType;
        final TypeT baseClass = nav.getBaseClass(nav.use(adapterType), nav.asDecl(XmlAdapter.class));
        assert baseClass != null;
        if (nav.isParameterizedType(baseClass)) {
            this.defaultType = nav.getTypeArgument(baseClass, 0);
        }
        else {
            this.defaultType = nav.ref(Object.class);
        }
        if (nav.isParameterizedType(baseClass)) {
            this.customType = nav.getTypeArgument(baseClass, 1);
        }
        else {
            this.customType = nav.ref(Object.class);
        }
    }
}
