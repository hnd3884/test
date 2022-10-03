package org.apache.poi.poifs.eventfilesystem;

import java.util.Iterator;
import java.util.Collection;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.poi.poifs.filesystem.DocumentDescriptor;
import java.util.Map;
import java.util.Set;

class POIFSReaderRegistry
{
    private Set<POIFSReaderListener> omnivorousListeners;
    private Map<POIFSReaderListener, Set<DocumentDescriptor>> selectiveListeners;
    private Map<DocumentDescriptor, Set<POIFSReaderListener>> chosenDocumentDescriptors;
    
    POIFSReaderRegistry() {
        this.omnivorousListeners = new HashSet<POIFSReaderListener>();
        this.selectiveListeners = new HashMap<POIFSReaderListener, Set<DocumentDescriptor>>();
        this.chosenDocumentDescriptors = new HashMap<DocumentDescriptor, Set<POIFSReaderListener>>();
    }
    
    void registerListener(final POIFSReaderListener listener, final POIFSDocumentPath path, final String documentName) {
        if (!this.omnivorousListeners.contains(listener)) {
            final Set<DocumentDescriptor> descriptors = this.selectiveListeners.computeIfAbsent(listener, k -> new HashSet());
            final DocumentDescriptor descriptor = new DocumentDescriptor(path, documentName);
            if (descriptors.add(descriptor)) {
                final Set<POIFSReaderListener> listeners = this.chosenDocumentDescriptors.computeIfAbsent(descriptor, k -> new HashSet());
                listeners.add(listener);
            }
        }
    }
    
    void registerListener(final POIFSReaderListener listener) {
        if (!this.omnivorousListeners.contains(listener)) {
            this.removeSelectiveListener(listener);
            this.omnivorousListeners.add(listener);
        }
    }
    
    Iterable<POIFSReaderListener> getListeners(final POIFSDocumentPath path, final String name) {
        final Set<POIFSReaderListener> rval = new HashSet<POIFSReaderListener>(this.omnivorousListeners);
        final Set<POIFSReaderListener> selectiveListenersInner = this.chosenDocumentDescriptors.get(new DocumentDescriptor(path, name));
        if (selectiveListenersInner != null) {
            rval.addAll(selectiveListenersInner);
        }
        return rval;
    }
    
    private void removeSelectiveListener(final POIFSReaderListener listener) {
        final Set<DocumentDescriptor> selectedDescriptors = this.selectiveListeners.remove(listener);
        if (selectedDescriptors != null) {
            for (final DocumentDescriptor selectedDescriptor : selectedDescriptors) {
                this.dropDocument(listener, selectedDescriptor);
            }
        }
    }
    
    private void dropDocument(final POIFSReaderListener listener, final DocumentDescriptor descriptor) {
        final Set<POIFSReaderListener> listeners = this.chosenDocumentDescriptors.get(descriptor);
        listeners.remove(listener);
        if (listeners.size() == 0) {
            this.chosenDocumentDescriptors.remove(descriptor);
        }
    }
}
