package com.lowagie.text.xml.xmp;

public class XmpBasicSchema extends XmpSchema
{
    private static final long serialVersionUID = -2416613941622479298L;
    public static final String DEFAULT_XPATH_ID = "xmp";
    public static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/xap/1.0/";
    public static final String ADVISORY = "xmp:Advisory";
    public static final String BASEURL = "xmp:BaseURL";
    public static final String CREATEDATE = "xmp:CreateDate";
    public static final String CREATORTOOL = "xmp:CreatorTool";
    public static final String IDENTIFIER = "xmp:Identifier";
    public static final String METADATADATE = "xmp:MetadataDate";
    public static final String MODIFYDATE = "xmp:ModifyDate";
    public static final String NICKNAME = "xmp:Nickname";
    public static final String THUMBNAILS = "xmp:Thumbnails";
    
    public XmpBasicSchema() {
        super("xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\"");
    }
    
    public void addCreatorTool(final String creator) {
        this.setProperty("xmp:CreatorTool", creator);
    }
    
    public void addCreateDate(final String date) {
        this.setProperty("xmp:CreateDate", date);
    }
    
    public void addModDate(final String date) {
        this.setProperty("xmp:ModifyDate", date);
    }
    
    public void addMetaDataDate(final String date) {
        this.setProperty("xmp:MetadataDate", date);
    }
    
    public void addIdentifiers(final String[] id) {
        final XmpArray array = new XmpArray("rdf:Bag");
        for (int i = 0; i < id.length; ++i) {
            array.add(id[i]);
        }
        this.setProperty("xmp:Identifier", array);
    }
    
    public void addNickname(final String name) {
        this.setProperty("xmp:Nickname", name);
    }
}
