package org.apache.axiom.om.impl.builder;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMAttachmentAccessor;

public interface XOPBuilder extends OMAttachmentAccessor
{
    Attachments getAttachments();
}
