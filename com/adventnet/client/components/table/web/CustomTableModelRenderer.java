package com.adventnet.client.components.table.web;

import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;

public interface CustomTableModelRenderer
{
    CustomTableModel renderModel(final HashMap p0, final ViewContext p1) throws Exception;
}
