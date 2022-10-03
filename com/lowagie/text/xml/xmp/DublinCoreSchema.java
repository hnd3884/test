package com.lowagie.text.xml.xmp;

public class DublinCoreSchema extends XmpSchema
{
    private static final long serialVersionUID = -4551741356374797330L;
    public static final String DEFAULT_XPATH_ID = "dc";
    public static final String DEFAULT_XPATH_URI = "http://purl.org/dc/elements/1.1/";
    public static final String CONTRIBUTOR = "dc:contributor";
    public static final String COVERAGE = "dc:coverage";
    public static final String CREATOR = "dc:creator";
    public static final String DATE = "dc:date";
    public static final String DESCRIPTION = "dc:description";
    public static final String FORMAT = "dc:format";
    public static final String IDENTIFIER = "dc:identifier";
    public static final String LANGUAGE = "dc:language";
    public static final String PUBLISHER = "dc:publisher";
    public static final String RELATION = "dc:relation";
    public static final String RIGHTS = "dc:rights";
    public static final String SOURCE = "dc:source";
    public static final String SUBJECT = "dc:subject";
    public static final String TITLE = "dc:title";
    public static final String TYPE = "dc:type";
    
    public DublinCoreSchema() {
        super("xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
        this.setProperty("dc:format", "application/pdf");
    }
    
    public void addTitle(final String title) {
        final XmpArray array = new XmpArray("rdf:Alt");
        array.add(title);
        this.setProperty("dc:title", array);
    }
    
    public void addDescription(final String desc) {
        final XmpArray array = new XmpArray("rdf:Alt");
        array.add(desc);
        this.setProperty("dc:description", array);
    }
    
    public void addSubject(final String subject) {
        final XmpArray array = new XmpArray("rdf:Bag");
        array.add(subject);
        this.setProperty("dc:subject", array);
    }
    
    public void addSubject(final String[] subject) {
        final XmpArray array = new XmpArray("rdf:Bag");
        for (int i = 0; i < subject.length; ++i) {
            array.add(subject[i]);
        }
        this.setProperty("dc:subject", array);
    }
    
    public void addAuthor(final String author) {
        final XmpArray array = new XmpArray("rdf:Seq");
        array.add(author);
        this.setProperty("dc:creator", array);
    }
    
    public void addAuthor(final String[] author) {
        final XmpArray array = new XmpArray("rdf:Seq");
        for (int i = 0; i < author.length; ++i) {
            array.add(author[i]);
        }
        this.setProperty("dc:creator", array);
    }
    
    public void addPublisher(final String publisher) {
        final XmpArray array = new XmpArray("rdf:Seq");
        array.add(publisher);
        this.setProperty("dc:publisher", array);
    }
    
    public void addPublisher(final String[] publisher) {
        final XmpArray array = new XmpArray("rdf:Seq");
        for (int i = 0; i < publisher.length; ++i) {
            array.add(publisher[i]);
        }
        this.setProperty("dc:publisher", array);
    }
}
