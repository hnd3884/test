package org.apache.tika.metadata;

public interface Photoshop
{
    public static final String NAMESPACE_URI_PHOTOSHOP = "http://ns.adobe.com/photoshop/1.0/";
    public static final String PREFIX_PHOTOSHOP = "photoshop";
    public static final Property AUTHORS_POSITION = Property.internalText("photoshop:AuthorsPosition");
    public static final String[] _COLOR_MODE_CHOICES_INDEXED = { "Bitmap", "Greyscale", "Indexed Colour", "RGB Color", "CMYK Colour", "Multi-Channel", "Duotone", "LAB Colour", "reserved", "reserved", "YCbCr Colour", "YCgCo Colour", "YCbCrK Colour" };
    public static final Property COLOR_MODE = Property.internalClosedChoise("photoshop:ColorMode", Photoshop._COLOR_MODE_CHOICES_INDEXED);
    public static final Property CAPTION_WRITER = Property.internalText("photoshop:CaptionWriter");
    public static final Property CATEGORY = Property.internalText("photoshop:Category");
    public static final Property CITY = Property.internalText("photoshop:City");
    public static final Property COUNTRY = Property.internalText("photoshop:Country");
    public static final Property CREDIT = Property.internalText("photoshop:Credit");
    public static final Property DATE_CREATED = Property.internalDate("photoshop:DateCreated");
    public static final Property HEADLINE = Property.internalText("photoshop:Headline");
    public static final Property INSTRUCTIONS = Property.internalText("photoshop:Instructions");
    public static final Property SOURCE = Property.internalText("photoshop:Source");
    public static final Property STATE = Property.internalText("photoshop:State");
    public static final Property SUPPLEMENTAL_CATEGORIES = Property.internalTextBag("photoshop:SupplementalCategories");
    public static final Property TRANSMISSION_REFERENCE = Property.internalText("photoshop:TransmissionReference");
    public static final Property URGENCY = Property.internalText("photoshop:Urgency");
}
