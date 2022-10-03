package org.apache.tika.metadata;

public interface HTML
{
    public static final String PREFIX_HTML_META = "html_meta";
    public static final Property SCRIPT_SOURCE = Property.internalText("html_meta:scriptSrc");
}
