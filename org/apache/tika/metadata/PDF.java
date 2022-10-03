package org.apache.tika.metadata;

public interface PDF
{
    public static final String PDF_PREFIX = "pdf:";
    public static final String PDFA_PREFIX = "pdfa:";
    public static final String PDFAID_PREFIX = "pdfaid:";
    public static final String PDF_DOC_INFO_PREFIX = "pdf:docinfo:";
    public static final String PDF_DOC_INFO_CUSTOM_PREFIX = "pdf:docinfo:custom:";
    public static final Property DOC_INFO_CREATED = Property.internalDate("pdf:docinfo:created");
    public static final Property DOC_INFO_CREATOR = Property.internalText("pdf:docinfo:creator");
    public static final Property DOC_INFO_CREATOR_TOOL = Property.internalText("pdf:docinfo:creator_tool");
    public static final Property DOC_INFO_MODIFICATION_DATE = Property.internalDate("pdf:docinfo:modified");
    public static final Property DOC_INFO_KEY_WORDS = Property.internalText("pdf:docinfo:keywords");
    public static final Property DOC_INFO_PRODUCER = Property.internalText("pdf:docinfo:producer");
    public static final Property DOC_INFO_SUBJECT = Property.internalText("pdf:docinfo:subject");
    public static final Property DOC_INFO_TITLE = Property.internalText("pdf:docinfo:title");
    public static final Property DOC_INFO_TRAPPED = Property.internalText("pdf:docinfo:trapped");
    public static final Property PDF_VERSION = Property.internalRational("pdf:PDFVersion");
    public static final Property PDFA_VERSION = Property.internalRational("pdfa:PDFVersion");
    public static final Property PDF_EXTENSION_VERSION = Property.internalRational("pdf:PDFExtensionVersion");
    public static final Property PDFAID_CONFORMANCE = Property.internalText("pdfaid:conformance");
    public static final Property PDFAID_PART = Property.internalText("pdfaid:part");
    public static final Property IS_ENCRYPTED = Property.internalBoolean("pdf:encrypted");
    public static final Property PRODUCER = Property.internalText("pdf:producer");
    public static final Property ACTION_TRIGGER = Property.internalText("pdf:actionTrigger");
    public static final Property CHARACTERS_PER_PAGE = Property.internalIntegerSequence("pdf:charsPerPage");
    public static final Property UNMAPPED_UNICODE_CHARS_PER_PAGE = Property.internalIntegerSequence("pdf:unmappedUnicodeCharsPerPage");
    public static final Property HAS_XFA = Property.internalBoolean("pdf:hasXFA");
    public static final Property HAS_XMP = Property.internalBoolean("pdf:hasXMP");
    public static final Property XMP_LOCATION = Property.internalText("pdf:xmpLocation");
    public static final Property HAS_ACROFORM_FIELDS = Property.internalBoolean("pdf:hasAcroFormFields");
    public static final Property HAS_MARKED_CONTENT = Property.internalBoolean("pdf:hasMarkedContent");
}
