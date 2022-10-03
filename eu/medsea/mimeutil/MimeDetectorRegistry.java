package eu.medsea.mimeutil;

import java.net.URL;
import java.io.InputStream;
import java.io.File;
import java.util.Iterator;
import eu.medsea.util.EncodingGuesser;
import java.util.ArrayList;
import java.util.Collection;
import eu.medsea.mimeutil.detector.MimeDetector;
import java.util.TreeMap;
import java.util.Map;

class MimeDetectorRegistry
{
    private static final MimeUtil2.MimeLogger log;
    private TextMimeDetector TextMimeDetector;
    private Map mimeDetectors;
    
    MimeDetectorRegistry() {
        this.TextMimeDetector = new TextMimeDetector(1);
        this.mimeDetectors = new TreeMap();
    }
    
    MimeDetector registerMimeDetector(final String mimeDetector) {
        if (this.mimeDetectors.containsKey(mimeDetector)) {
            MimeDetectorRegistry.log.warn("MimeDetector [" + mimeDetector + "] will not be registered as a MimeDetector with this name is already registered.");
            return this.mimeDetectors.get(mimeDetector);
        }
        try {
            final MimeDetector md = (MimeDetector)Class.forName(mimeDetector).newInstance();
            md.init();
            if (MimeDetectorRegistry.log.isDebugEnabled()) {
                MimeDetectorRegistry.log.debug("Registering MimeDetector with name [" + md.getName() + "] and description [" + md.getDescription() + "]");
            }
            this.mimeDetectors.put(mimeDetector, md);
            return md;
        }
        catch (final Exception e) {
            MimeDetectorRegistry.log.error("Exception while registering MimeDetector [" + mimeDetector + "].", e);
            return null;
        }
    }
    
    MimeDetector getMimeDetector(final String name) {
        return this.mimeDetectors.get(name);
    }
    
    Collection getMimeTypes(final byte[] data) throws MimeException {
        Collection mimeTypes = new ArrayList();
        try {
            if (!EncodingGuesser.getSupportedEncodings().isEmpty()) {
                mimeTypes = this.TextMimeDetector.getMimeTypes(data);
            }
        }
        catch (final UnsupportedOperationException ex) {}
        final Iterator it = this.mimeDetectors.values().iterator();
        while (it.hasNext()) {
            try {
                final MimeDetector md = it.next();
                mimeTypes.addAll(md.getMimeTypes(data));
            }
            catch (final UnsupportedOperationException ex2) {}
            catch (final Exception e) {
                MimeDetectorRegistry.log.error(e.getLocalizedMessage(), e);
            }
        }
        return mimeTypes;
    }
    
    Collection getMimeTypes(final String fileName) throws MimeException {
        Collection mimeTypes = new ArrayList();
        try {
            if (!EncodingGuesser.getSupportedEncodings().isEmpty()) {
                mimeTypes = this.TextMimeDetector.getMimeTypes(fileName);
            }
        }
        catch (final UnsupportedOperationException ex) {}
        final Iterator it = this.mimeDetectors.values().iterator();
        while (it.hasNext()) {
            try {
                final MimeDetector md = it.next();
                mimeTypes.addAll(md.getMimeTypes(fileName));
            }
            catch (final UnsupportedOperationException ex2) {}
            catch (final Exception e) {
                MimeDetectorRegistry.log.error(e.getLocalizedMessage(), e);
            }
        }
        return mimeTypes;
    }
    
    Collection getMimeTypes(final File file) throws MimeException {
        Collection mimeTypes = new ArrayList();
        try {
            if (!EncodingGuesser.getSupportedEncodings().isEmpty()) {
                mimeTypes = this.TextMimeDetector.getMimeTypes(file);
            }
        }
        catch (final UnsupportedOperationException ex) {}
        final Iterator it = this.mimeDetectors.values().iterator();
        while (it.hasNext()) {
            try {
                final MimeDetector md = it.next();
                mimeTypes.addAll(md.getMimeTypes(file));
            }
            catch (final UnsupportedOperationException ex2) {}
            catch (final Exception e) {
                MimeDetectorRegistry.log.error(e.getLocalizedMessage(), e);
            }
        }
        return mimeTypes;
    }
    
    Collection getMimeTypes(final InputStream in) throws MimeException {
        Collection mimeTypes = new ArrayList();
        try {
            if (!EncodingGuesser.getSupportedEncodings().isEmpty()) {
                mimeTypes = this.TextMimeDetector.getMimeTypes(in);
            }
        }
        catch (final UnsupportedOperationException ex) {}
        final Iterator it = this.mimeDetectors.values().iterator();
        while (it.hasNext()) {
            try {
                final MimeDetector md = it.next();
                mimeTypes.addAll(md.getMimeTypes(in));
            }
            catch (final UnsupportedOperationException ex2) {}
            catch (final Exception e) {
                MimeDetectorRegistry.log.error(e.getLocalizedMessage(), e);
            }
        }
        return mimeTypes;
    }
    
    Collection getMimeTypes(final URL url) throws MimeException {
        Collection mimeTypes = new ArrayList();
        try {
            if (!EncodingGuesser.getSupportedEncodings().isEmpty()) {
                mimeTypes = this.TextMimeDetector.getMimeTypes(url);
            }
        }
        catch (final UnsupportedOperationException ex) {}
        final Iterator it = this.mimeDetectors.values().iterator();
        while (it.hasNext()) {
            try {
                final MimeDetector md = it.next();
                mimeTypes.addAll(md.getMimeTypes(url));
            }
            catch (final UnsupportedOperationException ex2) {}
            catch (final Exception e) {
                MimeDetectorRegistry.log.error(e.getLocalizedMessage(), e);
            }
        }
        return mimeTypes;
    }
    
    MimeDetector unregisterMimeDetector(final String mimeDetector) {
        if (mimeDetector == null) {
            return null;
        }
        if (MimeDetectorRegistry.log.isDebugEnabled()) {
            MimeDetectorRegistry.log.debug("Unregistering MimeDetector [" + mimeDetector + "] from registry.");
        }
        try {
            final MimeDetector md = this.mimeDetectors.get(mimeDetector);
            if (md != null) {
                md.delete();
                return this.mimeDetectors.remove(mimeDetector);
            }
        }
        catch (final Exception e) {
            MimeDetectorRegistry.log.error("Exception while un-registering MimeDetector [" + mimeDetector + "].", e);
        }
        return null;
    }
    
    MimeDetector unregisterMimeDetector(final MimeDetector mimeDetector) {
        if (mimeDetector == null) {
            return null;
        }
        return this.unregisterMimeDetector(mimeDetector.getName());
    }
    
    static {
        log = new MimeUtil2.MimeLogger(MimeDetectorRegistry.class.getName());
    }
}
