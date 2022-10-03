package org.apache.poi.openxml4j.opc.internal;

import java.io.OutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.text.ParsePosition;
import org.apache.poi.util.LocaleUtil;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.opc.PackageProperties;
import org.apache.poi.openxml4j.opc.PackagePart;

public final class PackagePropertiesPart extends PackagePart implements PackageProperties
{
    public static final String NAMESPACE_DC_URI = "http://purl.org/dc/elements/1.1/";
    public static final String NAMESPACE_CP_URI = "http://schemas.openxmlformats.org/package/2006/metadata/core-properties";
    public static final String NAMESPACE_DCTERMS_URI = "http://purl.org/dc/terms/";
    private static final String DEFAULT_DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String[] DATE_FORMATS;
    private final String[] TZ_DATE_FORMATS;
    private final Pattern TIME_ZONE_PAT;
    protected Optional<String> category;
    protected Optional<String> contentStatus;
    protected Optional<String> contentType;
    protected Optional<Date> created;
    protected Optional<String> creator;
    protected Optional<String> description;
    protected Optional<String> identifier;
    protected Optional<String> keywords;
    protected Optional<String> language;
    protected Optional<String> lastModifiedBy;
    protected Optional<Date> lastPrinted;
    protected Optional<Date> modified;
    protected Optional<String> revision;
    protected Optional<String> subject;
    protected Optional<String> title;
    protected Optional<String> version;
    
    public PackagePropertiesPart(final OPCPackage pack, final PackagePartName partName) throws InvalidFormatException {
        super(pack, partName, "application/vnd.openxmlformats-package.core-properties+xml");
        this.TZ_DATE_FORMATS = new String[] { "yyyy-MM-dd'T'HH:mm:ssz", "yyyy-MM-dd'T'HH:mm:ss.Sz", "yyyy-MM-dd'T'HH:mm:ss.SSz", "yyyy-MM-dd'T'HH:mm:ss.SSSz" };
        this.TIME_ZONE_PAT = Pattern.compile("([-+]\\d\\d):?(\\d\\d)");
        this.category = Optional.empty();
        this.contentStatus = Optional.empty();
        this.contentType = Optional.empty();
        this.created = Optional.empty();
        this.creator = Optional.empty();
        this.description = Optional.empty();
        this.identifier = Optional.empty();
        this.keywords = Optional.empty();
        this.language = Optional.empty();
        this.lastModifiedBy = Optional.empty();
        this.lastPrinted = Optional.empty();
        this.modified = Optional.empty();
        this.revision = Optional.empty();
        this.subject = Optional.empty();
        this.title = Optional.empty();
        this.version = Optional.empty();
    }
    
    @Override
    public Optional<String> getCategoryProperty() {
        return this.category;
    }
    
    @Override
    public Optional<String> getContentStatusProperty() {
        return this.contentStatus;
    }
    
    @Override
    public Optional<String> getContentTypeProperty() {
        return this.contentType;
    }
    
    @Override
    public Optional<Date> getCreatedProperty() {
        return this.created;
    }
    
    public String getCreatedPropertyString() {
        return this.getDateValue(this.created);
    }
    
    @Override
    public Optional<String> getCreatorProperty() {
        return this.creator;
    }
    
    @Override
    public Optional<String> getDescriptionProperty() {
        return this.description;
    }
    
    @Override
    public Optional<String> getIdentifierProperty() {
        return this.identifier;
    }
    
    @Override
    public Optional<String> getKeywordsProperty() {
        return this.keywords;
    }
    
    @Override
    public Optional<String> getLanguageProperty() {
        return this.language;
    }
    
    @Override
    public Optional<String> getLastModifiedByProperty() {
        return this.lastModifiedBy;
    }
    
    @Override
    public Optional<Date> getLastPrintedProperty() {
        return this.lastPrinted;
    }
    
    public String getLastPrintedPropertyString() {
        return this.getDateValue(this.lastPrinted);
    }
    
    @Override
    public Optional<Date> getModifiedProperty() {
        return this.modified;
    }
    
    public String getModifiedPropertyString() {
        if (this.modified.isPresent()) {
            return this.getDateValue(this.modified);
        }
        return this.getDateValue(Optional.of(new Date()));
    }
    
    @Override
    public Optional<String> getRevisionProperty() {
        return this.revision;
    }
    
    @Override
    public Optional<String> getSubjectProperty() {
        return this.subject;
    }
    
    @Override
    public Optional<String> getTitleProperty() {
        return this.title;
    }
    
    @Override
    public Optional<String> getVersionProperty() {
        return this.version;
    }
    
    @Override
    public void setCategoryProperty(final String category) {
        this.category = this.setStringValue(category);
    }
    
    @Override
    public void setCategoryProperty(final Optional<String> category) {
        this.category = category;
    }
    
    @Override
    public void setContentStatusProperty(final String contentStatus) {
        this.contentStatus = this.setStringValue(contentStatus);
    }
    
    @Override
    public void setContentStatusProperty(final Optional<String> contentStatus) {
        this.contentStatus = contentStatus;
    }
    
    @Override
    public void setContentTypeProperty(final String contentType) {
        this.contentType = this.setStringValue(contentType);
    }
    
    @Override
    public void setContentTypeProperty(final Optional<String> contentType) {
        this.contentType = contentType;
    }
    
    @Override
    public void setCreatedProperty(final String created) {
        try {
            this.created = this.setDateValue(created);
        }
        catch (final InvalidFormatException e) {
            throw new IllegalArgumentException("Date for created could not be parsed: " + created, e);
        }
    }
    
    @Override
    public void setCreatedProperty(final Optional<Date> created) {
        if (created.isPresent()) {
            this.created = created;
        }
    }
    
    @Override
    public void setCreatorProperty(final String creator) {
        this.creator = this.setStringValue(creator);
    }
    
    @Override
    public void setCreatorProperty(final Optional<String> creator) {
        this.creator = creator;
    }
    
    @Override
    public void setDescriptionProperty(final String description) {
        this.description = this.setStringValue(description);
    }
    
    @Override
    public void setDescriptionProperty(final Optional<String> description) {
        this.description = description;
    }
    
    @Override
    public void setIdentifierProperty(final String identifier) {
        this.identifier = this.setStringValue(identifier);
    }
    
    @Override
    public void setIdentifierProperty(final Optional<String> identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public void setKeywordsProperty(final String keywords) {
        this.keywords = this.setStringValue(keywords);
    }
    
    @Override
    public void setKeywordsProperty(final Optional<String> keywords) {
        this.keywords = keywords;
    }
    
    @Override
    public void setLanguageProperty(final String language) {
        this.language = this.setStringValue(language);
    }
    
    @Override
    public void setLanguageProperty(final Optional<String> language) {
        this.language = language;
    }
    
    @Override
    public void setLastModifiedByProperty(final String lastModifiedBy) {
        this.lastModifiedBy = this.setStringValue(lastModifiedBy);
    }
    
    @Override
    public void setLastModifiedByProperty(final Optional<String> lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    
    @Override
    public void setLastPrintedProperty(final String lastPrinted) {
        try {
            this.lastPrinted = this.setDateValue(lastPrinted);
        }
        catch (final InvalidFormatException e) {
            throw new IllegalArgumentException("lastPrinted  : " + e.getLocalizedMessage(), e);
        }
    }
    
    @Override
    public void setLastPrintedProperty(final Optional<Date> lastPrinted) {
        if (lastPrinted.isPresent()) {
            this.lastPrinted = lastPrinted;
        }
    }
    
    @Override
    public void setModifiedProperty(final String modified) {
        try {
            this.modified = this.setDateValue(modified);
        }
        catch (final InvalidFormatException e) {
            throw new IllegalArgumentException("modified  : " + e.getLocalizedMessage(), e);
        }
    }
    
    @Override
    public void setModifiedProperty(final Optional<Date> modified) {
        if (modified.isPresent()) {
            this.modified = modified;
        }
    }
    
    @Override
    public void setRevisionProperty(final Optional<String> revision) {
        this.revision = revision;
    }
    
    @Override
    public void setRevisionProperty(final String revision) {
        this.revision = this.setStringValue(revision);
    }
    
    @Override
    public void setSubjectProperty(final String subject) {
        this.subject = this.setStringValue(subject);
    }
    
    @Override
    public void setSubjectProperty(final Optional<String> subject) {
        this.subject = subject;
    }
    
    @Override
    public void setTitleProperty(final String title) {
        this.title = this.setStringValue(title);
    }
    
    @Override
    public void setTitleProperty(final Optional<String> title) {
        this.title = title;
    }
    
    @Override
    public void setVersionProperty(final String version) {
        this.version = this.setStringValue(version);
    }
    
    @Override
    public void setVersionProperty(final Optional<String> version) {
        this.version = version;
    }
    
    private Optional<String> setStringValue(final String s) {
        if (s == null || s.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(s);
    }
    
    private Optional<Date> setDateValue(final String dateStr) throws InvalidFormatException {
        if (dateStr == null || dateStr.isEmpty()) {
            return Optional.empty();
        }
        final Matcher m = this.TIME_ZONE_PAT.matcher(dateStr);
        if (m.find()) {
            final String dateTzStr = dateStr.substring(0, m.start()) + m.group(1) + m.group(2);
            for (final String fStr : this.TZ_DATE_FORMATS) {
                final SimpleDateFormat df = new SimpleDateFormat(fStr, Locale.ROOT);
                df.setTimeZone(LocaleUtil.TIMEZONE_UTC);
                final Date d = df.parse(dateTzStr, new ParsePosition(0));
                if (d != null) {
                    return Optional.of(d);
                }
            }
        }
        final String dateTzStr = dateStr.endsWith("Z") ? dateStr : (dateStr + "Z");
        for (final String fStr : PackagePropertiesPart.DATE_FORMATS) {
            final SimpleDateFormat df = new SimpleDateFormat(fStr, Locale.ROOT);
            df.setTimeZone(LocaleUtil.TIMEZONE_UTC);
            final Date d = df.parse(dateTzStr, new ParsePosition(0));
            if (d != null) {
                return Optional.of(d);
            }
        }
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        for (final String fStr2 : this.TZ_DATE_FORMATS) {
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(fStr2);
        }
        for (final String fStr2 : PackagePropertiesPart.DATE_FORMATS) {
            sb.append(", ").append(fStr2);
        }
        throw new InvalidFormatException("Date " + dateStr + " not well formatted, expected format in: " + (Object)sb);
    }
    
    private String getDateValue(final Optional<Date> d) {
        if (d == null || !d.isPresent()) {
            return "";
        }
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT);
        df.setTimeZone(LocaleUtil.TIMEZONE_UTC);
        return df.format(d.get());
    }
    
    @Override
    protected InputStream getInputStreamImpl() {
        throw new InvalidOperationException("Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }
    
    @Override
    protected OutputStream getOutputStreamImpl() {
        throw new InvalidOperationException("Can't use output stream to set properties !");
    }
    
    @Override
    public boolean save(final OutputStream zos) {
        throw new InvalidOperationException("Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }
    
    @Override
    public boolean load(final InputStream ios) {
        throw new InvalidOperationException("Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void flush() {
    }
    
    static {
        DATE_FORMATS = new String[] { "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss.SS'Z'", "yyyy-MM-dd" };
    }
}
