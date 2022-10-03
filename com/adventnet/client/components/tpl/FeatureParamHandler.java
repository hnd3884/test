package com.adventnet.client.components.tpl;

import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.tpl.TemplateAPI;

public class FeatureParamHandler implements TemplateAPI.VariableHandler
{
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) throws Exception {
        final ViewContext viewCtx = (ViewContext)handlerContext;
        return viewCtx.getModel().getFeatureValue(variableName);
    }
}
