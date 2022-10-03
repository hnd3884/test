package com.lowagie.text;

import java.util.EventListener;

public interface ElementListener extends EventListener
{
    boolean add(final Element p0) throws DocumentException;
}
