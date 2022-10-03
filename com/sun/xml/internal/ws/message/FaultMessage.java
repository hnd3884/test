package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.message.FilterMessageImpl;

public class FaultMessage extends FilterMessageImpl
{
    @Nullable
    private final QName detailEntryName;
    
    public FaultMessage(final Message delegate, @Nullable final QName detailEntryName) {
        super(delegate);
        this.detailEntryName = detailEntryName;
    }
    
    @Nullable
    @Override
    public QName getFirstDetailEntryName() {
        return this.detailEntryName;
    }
}
