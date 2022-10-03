package org.glassfish.jersey.server.wadl.internal.generators;

import java.lang.reflect.Type;
import java.security.PrivilegedActionException;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Constructor;
import javax.xml.bind.JAXBIntrospector;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import java.util.logging.Level;
import java.io.Writer;
import java.io.CharArrayWriter;
import javax.xml.transform.Result;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.bind.JAXBContext;
import java.lang.reflect.ParameterizedType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;
import java.util.HashMap;
import com.sun.research.ws.wadl.Response;
import com.sun.research.ws.wadl.Resources;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.Iterator;
import com.sun.research.ws.wadl.Representation;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import javax.ws.rs.core.GenericType;
import com.sun.research.ws.wadl.Param;
import org.glassfish.jersey.server.model.Parameter;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Method;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.Resource;
import com.sun.research.ws.wadl.Application;
import java.util.ArrayList;
import java.util.HashSet;
import org.glassfish.jersey.server.wadl.internal.WadlGeneratorImpl;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public class WadlGeneratorJAXBGrammarGenerator implements WadlGenerator
{
    private static final Logger LOGGER;
    private static final Set<Class> SPECIAL_GENERIC_TYPES;
    private WadlGenerator wadlGeneratorDelegate;
    private Set<Class> seeAlsoClasses;
    private List<TypeCallbackPair> nameCallbacks;
    
    public WadlGeneratorJAXBGrammarGenerator() {
        this.wadlGeneratorDelegate = new WadlGeneratorImpl();
    }
    
    @Override
    public void setWadlGeneratorDelegate(final WadlGenerator delegate) {
        this.wadlGeneratorDelegate = delegate;
    }
    
    @Override
    public String getRequiredJaxbContextPath() {
        return this.wadlGeneratorDelegate.getRequiredJaxbContextPath();
    }
    
    @Override
    public void init() throws Exception {
        this.wadlGeneratorDelegate.init();
        this.seeAlsoClasses = new HashSet<Class>();
        this.nameCallbacks = new ArrayList<TypeCallbackPair>();
    }
    
    @Override
    public Application createApplication() {
        return this.wadlGeneratorDelegate.createApplication();
    }
    
    @Override
    public Method createMethod(final Resource ar, final ResourceMethod arm) {
        return this.wadlGeneratorDelegate.createMethod(ar, arm);
    }
    
    @Override
    public Request createRequest(final Resource ar, final ResourceMethod arm) {
        return this.wadlGeneratorDelegate.createRequest(ar, arm);
    }
    
    @Override
    public Param createParam(final Resource ar, final ResourceMethod am, final Parameter p) {
        final Param param = this.wadlGeneratorDelegate.createParam(ar, am, p);
        if (p.getSource() == Parameter.Source.ENTITY) {
            this.nameCallbacks.add(new TypeCallbackPair((GenericType<?>)new GenericType(p.getType()), new NameCallbackSetter() {
                @Override
                public void setName(final QName name) {
                    param.setType(name);
                }
            }));
        }
        return param;
    }
    
    @Override
    public Representation createRequestRepresentation(final Resource ar, final ResourceMethod arm, final MediaType mt) {
        final Representation rt = this.wadlGeneratorDelegate.createRequestRepresentation(ar, arm, mt);
        for (final Parameter p : arm.getInvocable().getParameters()) {
            if (p.getSource() == Parameter.Source.ENTITY) {
                this.nameCallbacks.add(new TypeCallbackPair((GenericType<?>)new GenericType(p.getType()), new NameCallbackSetter() {
                    @Override
                    public void setName(final QName name) {
                        rt.setElement(name);
                    }
                }));
            }
        }
        return rt;
    }
    
    @Override
    public com.sun.research.ws.wadl.Resource createResource(final Resource ar, final String path) {
        for (final Class<?> resClass : ar.getHandlerClasses()) {
            final XmlSeeAlso seeAlso = resClass.getAnnotation(XmlSeeAlso.class);
            if (seeAlso != null) {
                Collections.addAll(this.seeAlsoClasses, (Class[])seeAlso.value());
            }
        }
        return this.wadlGeneratorDelegate.createResource(ar, path);
    }
    
    @Override
    public Resources createResources() {
        return this.wadlGeneratorDelegate.createResources();
    }
    
    @Override
    public List<Response> createResponses(final Resource resource, final ResourceMethod resourceMethod) {
        final List<Response> responses = this.wadlGeneratorDelegate.createResponses(resource, resourceMethod);
        if (responses != null) {
            for (final Response response : responses) {
                for (final Representation representation : response.getRepresentation()) {
                    this.nameCallbacks.add(new TypeCallbackPair((GenericType<?>)new GenericType(resourceMethod.getInvocable().getResponseType()), new NameCallbackSetter() {
                        @Override
                        public void setName(final QName name) {
                            representation.setElement(name);
                        }
                    }));
                }
            }
        }
        return responses;
    }
    
    @Override
    public ExternalGrammarDefinition createExternalGrammar() {
        final Map<String, ApplicationDescription.ExternalGrammar> extraFiles = new HashMap<String, ApplicationDescription.ExternalGrammar>();
        final Resolver resolver = this.buildModelAndSchemas(extraFiles);
        final ExternalGrammarDefinition previous = this.wadlGeneratorDelegate.createExternalGrammar();
        previous.map.putAll(extraFiles);
        if (resolver != null) {
            previous.addResolver(resolver);
        }
        return previous;
    }
    
    private Resolver buildModelAndSchemas(final Map<String, ApplicationDescription.ExternalGrammar> extraFiles) {
        final Set<Class> classSet = new HashSet<Class>(this.seeAlsoClasses);
        for (final TypeCallbackPair pair : this.nameCallbacks) {
            final GenericType genericType = pair.genericType;
            final Class<?> clazz = genericType.getRawType();
            if (clazz.getAnnotation(XmlRootElement.class) != null) {
                classSet.add(clazz);
            }
            else {
                if (!WadlGeneratorJAXBGrammarGenerator.SPECIAL_GENERIC_TYPES.contains(clazz)) {
                    continue;
                }
                final Type type = genericType.getType();
                if (!(type instanceof ParameterizedType)) {
                    continue;
                }
                final Type parameterType = ((ParameterizedType)type).getActualTypeArguments()[0];
                if (!(parameterType instanceof Class)) {
                    continue;
                }
                classSet.add((Class)parameterType);
            }
        }
        JAXBIntrospector introspector = null;
        try {
            final JAXBContext context = JAXBContext.newInstance((Class[])classSet.toArray(new Class[classSet.size()]));
            final List<StreamResult> results = new ArrayList<StreamResult>();
            context.generateSchema(new SchemaOutputResolver() {
                int counter = 0;
                
                @Override
                public Result createOutput(final String namespaceUri, final String suggestedFileName) {
                    final StreamResult result = new StreamResult(new CharArrayWriter());
                    result.setSystemId("xsd" + this.counter++ + ".xsd");
                    results.add(result);
                    return result;
                }
            });
            for (final StreamResult result : results) {
                final CharArrayWriter writer = (CharArrayWriter)result.getWriter();
                final byte[] contents = writer.toString().getBytes("UTF8");
                extraFiles.put(result.getSystemId(), new ApplicationDescription.ExternalGrammar(MediaType.APPLICATION_XML_TYPE, contents));
            }
            introspector = context.createJAXBIntrospector();
        }
        catch (final JAXBException e) {
            WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.SEVERE, "Failed to generate the schema for the JAX-B elements", e);
        }
        catch (final IOException e2) {
            WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.SEVERE, "Failed to generate the schema for the JAX-B elements due to an IO error", e2);
        }
        if (introspector != null) {
            final JAXBIntrospector copy = introspector;
            return new Resolver() {
                @Override
                public QName resolve(final Class type) {
                    Object parameterClassInstance = null;
                    try {
                        final Constructor<?> defaultConstructor = AccessController.doPrivileged((PrivilegedExceptionAction<Constructor<?>>)new PrivilegedExceptionAction<Constructor<?>>() {
                            @Override
                            public Constructor<?> run() throws NoSuchMethodException {
                                final Constructor<?> constructor = type.getDeclaredConstructor((Class<?>[])new Class[0]);
                                constructor.setAccessible(true);
                                return constructor;
                            }
                        });
                        parameterClassInstance = defaultConstructor.newInstance(new Object[0]);
                    }
                    catch (final InstantiationException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.FINE, null, ex);
                    }
                    catch (final PrivilegedActionException ex2) {
                        WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.FINE, null, ex2.getCause());
                    }
                    if (parameterClassInstance == null) {
                        return null;
                    }
                    try {
                        return copy.getElementName(parameterClassInstance);
                    }
                    catch (final NullPointerException e) {
                        return null;
                    }
                }
            };
        }
        return null;
    }
    
    @Override
    public void attachTypes(final ApplicationDescription introspector) {
        if (introspector != null) {
            for (final TypeCallbackPair pair : this.nameCallbacks) {
                Class<?> parameterClass = pair.genericType.getRawType();
                if (WadlGeneratorJAXBGrammarGenerator.SPECIAL_GENERIC_TYPES.contains(parameterClass)) {
                    final Type type = pair.genericType.getType();
                    if (!ParameterizedType.class.isAssignableFrom(type.getClass()) || !Class.class.isAssignableFrom(((ParameterizedType)type).getActualTypeArguments()[0].getClass())) {
                        WadlGeneratorJAXBGrammarGenerator.LOGGER.fine("Couldn't find JAX-B element due to nested parameterized type " + type);
                        return;
                    }
                    parameterClass = (Class)((ParameterizedType)type).getActualTypeArguments()[0];
                }
                final QName name = introspector.resolve(parameterClass);
                if (name != null) {
                    pair.nameCallbackSetter.setName(name);
                }
                else {
                    WadlGeneratorJAXBGrammarGenerator.LOGGER.fine("Couldn't find JAX-B element for class " + parameterClass.getName());
                }
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(WadlGeneratorJAXBGrammarGenerator.class.getName());
        SPECIAL_GENERIC_TYPES = new HashSet<Class>() {
            {
                ((HashSet<Class<List>>)this).add(List.class);
            }
        };
    }
    
    private class TypeCallbackPair
    {
        GenericType<?> genericType;
        NameCallbackSetter nameCallbackSetter;
        
        public TypeCallbackPair(final GenericType<?> genericType, final NameCallbackSetter nameCallbackSetter) {
            this.genericType = genericType;
            this.nameCallbackSetter = nameCallbackSetter;
        }
    }
    
    private interface NameCallbackSetter
    {
        void setName(final QName p0);
    }
}
