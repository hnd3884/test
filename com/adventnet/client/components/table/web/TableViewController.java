package com.adventnet.client.components.table.web;

import java.util.Properties;
import com.adventnet.client.view.web.ViewContext;

public interface TableViewController
{
    long getCount(final ViewContext p0) throws Exception;
    
    Properties getCustomRedactConfiguration(final ViewContext p0) throws Exception;
}
