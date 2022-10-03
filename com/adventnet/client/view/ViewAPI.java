package com.adventnet.client.view;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Arrays;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.persistence.DataObject;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ViewAPI
{
    private static final Logger OUT;
    
    public static JSONObject getViewModel(final String viewName, final State state) throws Exception {
        final ViewContext viewContext = ViewContext.getViewContext(viewName, state);
        viewContext.setRenderType(8);
        return getViewModel(viewContext);
    }
    
    public static JSONObject getViewModel(final String viewName, final HttpServletRequest request) throws Exception {
        final ViewContext viewContext = ViewContext.getViewContext(viewName);
        viewContext.setContextPath(request.getContextPath());
        viewContext.setRenderType(8);
        updateViewStates(viewContext, request);
        return getViewModel(viewContext);
    }
    
    public static JSONObject getViewModel(final ViewContext viewContext) throws Exception {
        return viewContext.getModel().getController().getModelAsJSON(viewContext);
    }
    
    private static String getStateClassName(final DataObject viewConfig) throws Exception {
        String stateClassName = (String)viewConfig.getFirstValue("ViewConfiguration", "STATE");
        if (stateClassName == null) {
            final Long compName = (Long)viewConfig.getFirstValue("ViewConfiguration", 3);
            if (compName != null) {
                final DataObject compConfig = WebViewAPI.getUIComponentConfig(compName);
                stateClassName = (String)compConfig.getValue("UIComponent", "STATE", (Criteria)null);
            }
        }
        return stateClassName;
    }
    
    public static void updateViewStates(final ViewContext viewContext, final HttpServletRequest request) {
        final Enumeration<String> paramNames = request.getParameterNames();
        while (true) {
            if (viewContext.getViewState() == null) {
                try {
                    final String stateClassName = getStateClassName(viewContext.getModel().getViewConfiguration());
                    if (stateClassName == null) {
                        return;
                    }
                    final State state = (State)WebClientUtil.createInstance(stateClassName);
                    final Class c = state.getClass();
                    break Label_0206;
                }
                catch (final IllegalAccessException | ClassNotFoundException | InstantiationException exception) {
                    ViewAPI.OUT.warning("Problem occurred while instantiating state class while rendering the view :" + viewContext.getUniqueId() + " for first time");
                    throw new RuntimeException(exception);
                }
                catch (final Exception e) {
                    ViewAPI.OUT.warning("Problem occurred while fetching state class name while rendering the view :" + viewContext.getUniqueId() + " for first time");
                    throw new RuntimeException(e);
                }
            }
            Label_0141: {
                break Label_0141;
                final State state;
                while (paramNames.hasMoreElements()) {
                    final String key = paramNames.nextElement();
                    final String[] val = request.getParameterValues(key);
                    if (val.length < 1) {
                        continue;
                    }
                    final Class c;
                    Arrays.stream(c.getDeclaredMethods()).filter(method -> method.getName().equals("set" + key) || Optional.ofNullable((Object)method.getAnnotation((Class<T>)Setter.class)).map(annotation -> annotation.paramName().equals(key)).orElse(Boolean.FALSE)).findFirst().map(method -> {
                        method.setAccessible(true);
                        try {
                            return method.invoke(state, (val.length > 1 || "java.lang.String[]".equals(method.getGenericParameterTypes()[0].getTypeName())) ? val : val[0]);
                        }
                        catch (final IllegalAccessException | InvocationTargetException e2) {
                            throw new RuntimeException(e2);
                        }
                    });
                }
                viewContext.setViewState(state);
                return;
            }
            final Class c = viewContext.getViewState().getClass();
            try {
                final State state = c.newInstance();
            }
            catch (final IllegalAccessException | InstantiationException exception) {
                ViewAPI.OUT.warning("Problem occurred while instantiating state class while rendering the view :" + viewContext.getUniqueId() + " for nth time");
                throw new RuntimeException(exception);
            }
            continue;
        }
    }
    
    static {
        OUT = Logger.getLogger(ViewAPI.class.getName());
    }
}
