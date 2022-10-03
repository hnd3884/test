package org.glassfish.jersey.jaxb.internal;

import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.transform.TransformerFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.parsers.SAXParserFactory;
import org.glassfish.jersey.internal.inject.PerThread;
import java.lang.reflect.Type;
import javax.xml.parsers.DocumentBuilderFactory;
import org.glassfish.jersey.internal.inject.SupplierClassBinding;
import javax.inject.Singleton;
import javax.ws.rs.ext.MessageBodyReader;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.AbstractBinder;

public class JaxbMessagingBinder extends AbstractBinder
{
    protected void configure() {
        this.bindSingletonWorker(DocumentProvider.class);
        this.bindSingletonWorker(XmlJaxbElementProvider.App.class);
        this.bindSingletonWorker(XmlJaxbElementProvider.Text.class);
        this.bindSingletonWorker(XmlJaxbElementProvider.General.class);
        this.bindSingletonWorker(XmlCollectionJaxbProvider.App.class);
        this.bindSingletonWorker(XmlCollectionJaxbProvider.Text.class);
        this.bindSingletonWorker(XmlCollectionJaxbProvider.General.class);
        this.bindSingletonWorker(XmlRootElementJaxbProvider.App.class);
        this.bindSingletonWorker(XmlRootElementJaxbProvider.Text.class);
        this.bindSingletonWorker(XmlRootElementJaxbProvider.General.class);
        ((ClassBinding)this.bind((Class)XmlRootObjectJaxbProvider.App.class).to((Class)MessageBodyReader.class)).in((Class)Singleton.class);
        ((ClassBinding)this.bind((Class)XmlRootObjectJaxbProvider.Text.class).to((Class)MessageBodyReader.class)).in((Class)Singleton.class);
        ((ClassBinding)this.bind((Class)XmlRootObjectJaxbProvider.General.class).to((Class)MessageBodyReader.class)).in((Class)Singleton.class);
        ((SupplierClassBinding)this.bindFactory((Class)DocumentBuilderFactoryInjectionProvider.class).to((Type)DocumentBuilderFactory.class)).in((Class)PerThread.class);
        ((SupplierClassBinding)this.bindFactory((Class)SaxParserFactoryInjectionProvider.class).to((Type)SAXParserFactory.class)).in((Class)PerThread.class);
        ((SupplierClassBinding)this.bindFactory((Class)XmlInputFactoryInjectionProvider.class).to((Type)XMLInputFactory.class)).in((Class)PerThread.class);
        ((SupplierClassBinding)this.bindFactory((Class)TransformerFactoryInjectionProvider.class).to((Type)TransformerFactory.class)).in((Class)PerThread.class);
    }
    
    private <T extends MessageBodyReader & MessageBodyWriter> void bindSingletonWorker(final Class<T> worker) {
        ((ClassBinding)((ClassBinding)this.bind((Class)worker).to((Class)MessageBodyReader.class)).to((Class)MessageBodyWriter.class)).in((Class)Singleton.class);
    }
}
