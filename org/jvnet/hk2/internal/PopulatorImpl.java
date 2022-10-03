package org.jvnet.hk2.internal;

import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import java.util.Iterator;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.utilities.DescriptorImpl;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import org.glassfish.hk2.api.MultiException;
import java.io.IOException;
import org.glassfish.hk2.api.DescriptorFileFinderInformation;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.util.List;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.DescriptorFileFinder;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Populator;

public class PopulatorImpl implements Populator
{
    private final ServiceLocator serviceLocator;
    private final DynamicConfigurationService dcs;
    
    PopulatorImpl(final ServiceLocator serviceLocator, final DynamicConfigurationService dcs) {
        this.serviceLocator = serviceLocator;
        this.dcs = dcs;
    }
    
    public List<ActiveDescriptor<?>> populate(DescriptorFileFinder fileFinder, PopulatorPostProcessor... postProcessors) throws IOException {
        final List<ActiveDescriptor<?>> descriptors = new LinkedList<ActiveDescriptor<?>>();
        if (fileFinder == null) {
            fileFinder = (DescriptorFileFinder)this.serviceLocator.getService((Class)DescriptorFileFinder.class, new Annotation[0]);
            if (fileFinder == null) {
                return descriptors;
            }
        }
        if (postProcessors == null) {
            postProcessors = new PopulatorPostProcessor[0];
        }
        List<String> descriptorInformation = null;
        List<InputStream> descriptorFileInputStreams;
        try {
            descriptorFileInputStreams = fileFinder.findDescriptorFiles();
            if (fileFinder instanceof DescriptorFileFinderInformation) {
                final DescriptorFileFinderInformation dffi = (DescriptorFileFinderInformation)fileFinder;
                descriptorInformation = dffi.getDescriptorFileInformation();
                if (descriptorInformation != null && descriptorInformation.size() != descriptorFileInputStreams.size()) {
                    throw new IOException("The DescriptorFileFinder implementation " + fileFinder.getClass().getName() + " also implements DescriptorFileFinderInformation, however the cardinality of the list returned from getDescriptorFileInformation (" + descriptorInformation.size() + ") does not equal the cardinality of the list returned from findDescriptorFiles (" + descriptorFileInputStreams.size() + ")");
                }
            }
        }
        catch (final IOException ioe) {
            throw ioe;
        }
        catch (final Throwable th) {
            throw new MultiException(th);
        }
        final Collector collector = new Collector();
        final DynamicConfiguration config = this.dcs.createDynamicConfiguration();
        int lcv = 0;
        for (final InputStream is : descriptorFileInputStreams) {
            final String identifier = (descriptorInformation == null) ? null : descriptorInformation.get(lcv);
            ++lcv;
            final BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                boolean readOne = false;
                do {
                    DescriptorImpl descriptorImpl = new DescriptorImpl();
                    try {
                        readOne = descriptorImpl.readObject(br);
                    }
                    catch (final IOException ioe2) {
                        if (identifier != null) {
                            collector.addThrowable(new IOException("InputStream with identifier \"" + identifier + "\" failed", ioe2));
                        }
                        else {
                            collector.addThrowable(ioe2);
                        }
                    }
                    if (readOne) {
                        for (final PopulatorPostProcessor pp : postProcessors) {
                            try {
                                descriptorImpl = pp.process(this.serviceLocator, descriptorImpl);
                            }
                            catch (final Throwable th2) {
                                if (identifier != null) {
                                    collector.addThrowable(new IOException("InputStream with identifier \"" + identifier + "\" failed", th2));
                                }
                                else {
                                    collector.addThrowable(th2);
                                }
                                descriptorImpl = null;
                            }
                            if (descriptorImpl == null) {
                                break;
                            }
                        }
                        if (descriptorImpl == null) {
                            continue;
                        }
                        descriptors.add((ActiveDescriptor<?>)config.bind((Descriptor)descriptorImpl, false));
                    }
                } while (readOne);
            }
            finally {
                br.close();
            }
        }
        collector.throwIfErrors();
        config.commit();
        return descriptors;
    }
    
    public List<ActiveDescriptor<?>> populate() throws IOException {
        return this.populate((DescriptorFileFinder)new ClasspathDescriptorFileFinder(), new PopulatorPostProcessor[0]);
    }
}
