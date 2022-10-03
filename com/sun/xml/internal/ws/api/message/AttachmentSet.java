package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.Nullable;

public interface AttachmentSet extends Iterable<Attachment>
{
    @Nullable
    Attachment get(final String p0);
    
    boolean isEmpty();
    
    void add(final Attachment p0);
}
