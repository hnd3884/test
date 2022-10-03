package com.adventnet.client.components.tpl;

import com.adventnet.client.view.web.StateAPI;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.tpl.TemplateAPI;

public class StateVariableHandler implements TemplateAPI.VariableHandler
{
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) throws Exception {
        final ViewContext vctx = (ViewContext)handlerContext;
        Object value = null;
        if (variableName.indexOf(35) != -1) {
            final String viewName = variableName.substring(0, variableName.indexOf(35));
            final String key = variableName.substring(variableName.indexOf(35) + 1);
            value = StateAPI.getState(viewName, key);
        }
        else {
            value = vctx.getStateParameter(variableName);
        }
        if (value != null && !(value instanceof String)) {
            return String.valueOf(value);
        }
        return (String)value;
    }
}
