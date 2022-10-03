package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.impl.ior.iiop.IIOPProfileTemplateImpl;
import org.omg.IOP.TaggedProfile;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.impl.ior.iiop.IIOPProfileImpl;
import com.sun.corba.se.impl.ior.iiop.JavaSerializationComponent;
import com.sun.corba.se.impl.ior.iiop.MaxStreamFormatVersionComponentImpl;
import com.sun.corba.se.impl.ior.iiop.ORBTypeComponentImpl;
import com.sun.corba.se.impl.ior.iiop.JavaCodebaseComponentImpl;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.ior.iiop.CodeSetsComponentImpl;
import com.sun.corba.se.impl.ior.iiop.AlternateIIOPAddressComponentImpl;
import com.sun.corba.se.impl.ior.iiop.IIOPAddressImpl;
import com.sun.corba.se.impl.ior.iiop.RequestPartitioningComponentImpl;
import com.sun.corba.se.spi.ior.Identifiable;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.ior.EncapsulationFactoryBase;
import com.sun.corba.se.spi.ior.IdentifiableFactory;

public abstract class IIOPFactories
{
    private IIOPFactories() {
    }
    
    public static IdentifiableFactory makeRequestPartitioningComponentFactory() {
        return new EncapsulationFactoryBase(1398099457) {
            public Identifiable readContents(final InputStream inputStream) {
                return new RequestPartitioningComponentImpl(inputStream.read_ulong());
            }
        };
    }
    
    public static RequestPartitioningComponent makeRequestPartitioningComponent(final int n) {
        return new RequestPartitioningComponentImpl(n);
    }
    
    public static IdentifiableFactory makeAlternateIIOPAddressComponentFactory() {
        return new EncapsulationFactoryBase(3) {
            public Identifiable readContents(final InputStream inputStream) {
                return new AlternateIIOPAddressComponentImpl(new IIOPAddressImpl(inputStream));
            }
        };
    }
    
    public static AlternateIIOPAddressComponent makeAlternateIIOPAddressComponent(final IIOPAddress iiopAddress) {
        return new AlternateIIOPAddressComponentImpl(iiopAddress);
    }
    
    public static IdentifiableFactory makeCodeSetsComponentFactory() {
        return new EncapsulationFactoryBase(1) {
            public Identifiable readContents(final InputStream inputStream) {
                return new CodeSetsComponentImpl(inputStream);
            }
        };
    }
    
    public static CodeSetsComponent makeCodeSetsComponent(final ORB orb) {
        return new CodeSetsComponentImpl(orb);
    }
    
    public static IdentifiableFactory makeJavaCodebaseComponentFactory() {
        return new EncapsulationFactoryBase(25) {
            public Identifiable readContents(final InputStream inputStream) {
                return new JavaCodebaseComponentImpl(inputStream.read_string());
            }
        };
    }
    
    public static JavaCodebaseComponent makeJavaCodebaseComponent(final String s) {
        return new JavaCodebaseComponentImpl(s);
    }
    
    public static IdentifiableFactory makeORBTypeComponentFactory() {
        return new EncapsulationFactoryBase(0) {
            public Identifiable readContents(final InputStream inputStream) {
                return new ORBTypeComponentImpl(inputStream.read_ulong());
            }
        };
    }
    
    public static ORBTypeComponent makeORBTypeComponent(final int n) {
        return new ORBTypeComponentImpl(n);
    }
    
    public static IdentifiableFactory makeMaxStreamFormatVersionComponentFactory() {
        return new EncapsulationFactoryBase(38) {
            public Identifiable readContents(final InputStream inputStream) {
                return new MaxStreamFormatVersionComponentImpl(inputStream.read_octet());
            }
        };
    }
    
    public static MaxStreamFormatVersionComponent makeMaxStreamFormatVersionComponent() {
        return new MaxStreamFormatVersionComponentImpl();
    }
    
    public static IdentifiableFactory makeJavaSerializationComponentFactory() {
        return new EncapsulationFactoryBase(1398099458) {
            public Identifiable readContents(final InputStream inputStream) {
                return new JavaSerializationComponent(inputStream.read_octet());
            }
        };
    }
    
    public static JavaSerializationComponent makeJavaSerializationComponent() {
        return JavaSerializationComponent.singleton();
    }
    
    public static IdentifiableFactory makeIIOPProfileFactory() {
        return new EncapsulationFactoryBase(0) {
            public Identifiable readContents(final InputStream inputStream) {
                return new IIOPProfileImpl(inputStream);
            }
        };
    }
    
    public static IIOPProfile makeIIOPProfile(final ORB orb, final ObjectKeyTemplate objectKeyTemplate, final ObjectId objectId, final IIOPProfileTemplate iiopProfileTemplate) {
        return new IIOPProfileImpl(orb, objectKeyTemplate, objectId, iiopProfileTemplate);
    }
    
    public static IIOPProfile makeIIOPProfile(final ORB orb, final TaggedProfile taggedProfile) {
        return new IIOPProfileImpl(orb, taggedProfile);
    }
    
    public static IdentifiableFactory makeIIOPProfileTemplateFactory() {
        return new EncapsulationFactoryBase(0) {
            public Identifiable readContents(final InputStream inputStream) {
                return new IIOPProfileTemplateImpl(inputStream);
            }
        };
    }
    
    public static IIOPProfileTemplate makeIIOPProfileTemplate(final ORB orb, final GIOPVersion giopVersion, final IIOPAddress iiopAddress) {
        return new IIOPProfileTemplateImpl(orb, giopVersion, iiopAddress);
    }
    
    public static IIOPAddress makeIIOPAddress(final ORB orb, final String s, final int n) {
        return new IIOPAddressImpl(orb, s, n);
    }
    
    public static IIOPAddress makeIIOPAddress(final InputStream inputStream) {
        return new IIOPAddressImpl(inputStream);
    }
}
