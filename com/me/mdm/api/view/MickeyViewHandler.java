package com.me.mdm.api.view;

import com.me.mdm.api.APIEndpointStratergy;
import com.me.mdm.api.MickeyViewStratergy;
import com.me.mdm.api.ApiRequestHandler;

public class MickeyViewHandler extends ApiRequestHandler
{
    public MickeyViewHandler() {
        super(new MickeyViewStratergy());
    }
}
