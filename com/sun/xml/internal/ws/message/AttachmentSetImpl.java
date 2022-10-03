package com.sun.xml.internal.ws.message;

import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.Attachment;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.message.AttachmentSet;

public final class AttachmentSetImpl implements AttachmentSet
{
    private final ArrayList<Attachment> attList;
    
    public AttachmentSetImpl() {
        this.attList = new ArrayList<Attachment>();
    }
    
    public AttachmentSetImpl(final Iterable<Attachment> base) {
        this.attList = new ArrayList<Attachment>();
        for (final Attachment a : base) {
            this.add(a);
        }
    }
    
    @Override
    public Attachment get(final String contentId) {
        for (int i = this.attList.size() - 1; i >= 0; --i) {
            final Attachment a = this.attList.get(i);
            if (a.getContentId().equals(contentId)) {
                return a;
            }
        }
        return null;
    }
    
    @Override
    public boolean isEmpty() {
        return this.attList.isEmpty();
    }
    
    @Override
    public void add(final Attachment att) {
        this.attList.add(att);
    }
    
    @Override
    public Iterator<Attachment> iterator() {
        return this.attList.iterator();
    }
}
