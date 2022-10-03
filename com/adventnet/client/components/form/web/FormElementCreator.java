package com.adventnet.client.components.form.web;

import com.adventnet.persistence.DataObject;
import com.adventnet.client.components.web.TransformerContext;

public interface FormElementCreator
{
    String createElement(final TransformerContext p0, final DataObject p1);
    
    String getHtmlForReadMode();
}
