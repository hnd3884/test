package org.apache.tika.metadata;

public interface XMP
{
    public static final String NAMESPACE_URI = "http://ns.adobe.com/xap/1.0/";
    public static final String PREFIX = "xmp";
    public static final String PREFIX_ = "xmp:";
    public static final Property ABOUT = Property.externalTextBag("xmp:About");
    public static final Property ADVISORY = Property.externalTextBag("xmp:Advisory");
    public static final Property CREATE_DATE = Property.externalDate("xmp:CreateDate");
    public static final Property CREATOR_TOOL = Property.externalText("xmp:CreatorTool");
    public static final Property IDENTIFIER = Property.externalTextBag("xmp:Identifier");
    public static final Property LABEL = Property.externalText("xmp:Label");
    public static final Property METADATA_DATE = Property.externalDate("xmp:MetadataDate");
    public static final Property MODIFY_DATE = Property.externalDate("xmp:ModifyDate");
    public static final Property NICKNAME = Property.externalText("xmp:NickName");
    public static final Property RATING = Property.externalInteger("xmp:Rating");
}
