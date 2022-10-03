package com.ocpsoft.pretty.time.web.jsf;

import java.util.Date;
import javax.faces.convert.ConverterException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import com.ocpsoft.pretty.time.PrettyTime;
import java.io.Serializable;
import javax.faces.convert.Converter;

public class PrettyTimeConverter implements Converter, Serializable
{
    private static final long serialVersionUID = 7690470362440868259L;
    private static PrettyTime prettyTime;
    
    static {
        PrettyTimeConverter.prettyTime = new PrettyTime();
    }
    
    public Object getAsObject(final FacesContext context, final UIComponent comp, final String value) {
        throw new ConverterException("Does not yet support converting String to Date");
    }
    
    public String getAsString(final FacesContext context, final UIComponent comp, final Object value) {
        if (value instanceof Date) {
            return PrettyTimeConverter.prettyTime.format((Date)value);
        }
        throw new ConverterException("May only be used to convert java.util.Date objects. Got: " + value.getClass());
    }
    
    public static PrettyTime getPrettyTime() {
        return PrettyTimeConverter.prettyTime;
    }
    
    public static void setPrettyTime(final PrettyTime prettyTime) {
        PrettyTimeConverter.prettyTime = prettyTime;
    }
}
