package com.sun.xml.internal.ws.message.source;

import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.istack.internal.NotNull;
import javax.xml.transform.Source;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.stream.PayloadStreamReaderMessage;

public class PayloadSourceMessage extends PayloadStreamReaderMessage
{
    public PayloadSourceMessage(@Nullable final MessageHeaders headers, @NotNull final Source payload, @NotNull final AttachmentSet attSet, @NotNull final SOAPVersion soapVersion) {
        super(headers, SourceReaderFactory.createSourceReader(payload, true), attSet, soapVersion);
    }
    
    public PayloadSourceMessage(final Source s, final SOAPVersion soapVer) {
        this(null, s, new AttachmentSetImpl(), soapVer);
    }
}
