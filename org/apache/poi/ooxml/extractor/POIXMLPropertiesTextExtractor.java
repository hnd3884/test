package org.apache.poi.ooxml.extractor;

import org.apache.poi.extractor.POITextExtractor;
import java.math.BigDecimal;
import java.util.Iterator;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTProperties;
import org.apache.poi.openxml4j.opc.internal.PackagePropertiesPart;
import java.util.Date;
import java.util.Optional;
import org.apache.poi.util.LocaleUtil;
import java.text.SimpleDateFormat;
import java.text.DateFormatSymbols;
import java.util.Locale;
import org.apache.poi.ooxml.POIXMLDocument;
import java.text.DateFormat;

public class POIXMLPropertiesTextExtractor extends POIXMLTextExtractor
{
    private final DateFormat dateFormat;
    
    public POIXMLPropertiesTextExtractor(final POIXMLDocument doc) {
        super(doc);
        final DateFormatSymbols dfs = DateFormatSymbols.getInstance(Locale.ROOT);
        (this.dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", dfs)).setTimeZone(LocaleUtil.TIMEZONE_UTC);
    }
    
    public POIXMLPropertiesTextExtractor(final POIXMLTextExtractor otherExtractor) {
        this(otherExtractor.getDocument());
    }
    
    private void appendIfPresent(final StringBuilder text, final String thing, final boolean value) {
        this.appendIfPresent(text, thing, Boolean.toString(value));
    }
    
    private void appendIfPresent(final StringBuilder text, final String thing, final int value) {
        this.appendIfPresent(text, thing, Integer.toString(value));
    }
    
    private void appendDateIfPresent(final StringBuilder text, final String thing, final Optional<Date> value) {
        if (!value.isPresent()) {
            return;
        }
        this.appendIfPresent(text, thing, this.dateFormat.format(value.get()));
    }
    
    private void appendIfPresent(final StringBuilder text, final String thing, final Optional<String> value) {
        if (!value.isPresent()) {
            return;
        }
        this.appendIfPresent(text, thing, value.get());
    }
    
    private void appendIfPresent(final StringBuilder text, final String thing, final String value) {
        if (value == null) {
            return;
        }
        text.append(thing);
        text.append(" = ");
        text.append(value);
        text.append('\n');
    }
    
    public String getCorePropertiesText() {
        final POIXMLDocument document = this.getDocument();
        if (document == null) {
            return "";
        }
        final StringBuilder text = new StringBuilder(64);
        final PackagePropertiesPart props = document.getProperties().getCoreProperties().getUnderlyingProperties();
        this.appendIfPresent(text, "Category", props.getCategoryProperty());
        this.appendIfPresent(text, "Category", props.getCategoryProperty());
        this.appendIfPresent(text, "ContentStatus", props.getContentStatusProperty());
        this.appendIfPresent(text, "ContentType", props.getContentTypeProperty());
        this.appendDateIfPresent(text, "Created", props.getCreatedProperty());
        this.appendIfPresent(text, "CreatedString", props.getCreatedPropertyString());
        this.appendIfPresent(text, "Creator", props.getCreatorProperty());
        this.appendIfPresent(text, "Description", props.getDescriptionProperty());
        this.appendIfPresent(text, "Identifier", props.getIdentifierProperty());
        this.appendIfPresent(text, "Keywords", props.getKeywordsProperty());
        this.appendIfPresent(text, "Language", props.getLanguageProperty());
        this.appendIfPresent(text, "LastModifiedBy", props.getLastModifiedByProperty());
        this.appendDateIfPresent(text, "LastPrinted", props.getLastPrintedProperty());
        this.appendIfPresent(text, "LastPrintedString", props.getLastPrintedPropertyString());
        this.appendDateIfPresent(text, "Modified", props.getModifiedProperty());
        this.appendIfPresent(text, "ModifiedString", props.getModifiedPropertyString());
        this.appendIfPresent(text, "Revision", props.getRevisionProperty());
        this.appendIfPresent(text, "Subject", props.getSubjectProperty());
        this.appendIfPresent(text, "Title", props.getTitleProperty());
        this.appendIfPresent(text, "Version", props.getVersionProperty());
        return text.toString();
    }
    
    public String getExtendedPropertiesText() {
        final POIXMLDocument document = this.getDocument();
        if (document == null) {
            return "";
        }
        final StringBuilder text = new StringBuilder(64);
        final CTProperties props = document.getProperties().getExtendedProperties().getUnderlyingProperties();
        this.appendIfPresent(text, "Application", props.getApplication());
        this.appendIfPresent(text, "AppVersion", props.getAppVersion());
        this.appendIfPresent(text, "Characters", props.getCharacters());
        this.appendIfPresent(text, "CharactersWithSpaces", props.getCharactersWithSpaces());
        this.appendIfPresent(text, "Company", props.getCompany());
        this.appendIfPresent(text, "HyperlinkBase", props.getHyperlinkBase());
        this.appendIfPresent(text, "HyperlinksChanged", props.getHyperlinksChanged());
        this.appendIfPresent(text, "Lines", props.getLines());
        this.appendIfPresent(text, "LinksUpToDate", props.getLinksUpToDate());
        this.appendIfPresent(text, "Manager", props.getManager());
        this.appendIfPresent(text, "Pages", props.getPages());
        this.appendIfPresent(text, "Paragraphs", props.getParagraphs());
        this.appendIfPresent(text, "PresentationFormat", props.getPresentationFormat());
        this.appendIfPresent(text, "Template", props.getTemplate());
        this.appendIfPresent(text, "TotalTime", props.getTotalTime());
        return text.toString();
    }
    
    public String getCustomPropertiesText() {
        final POIXMLDocument document = this.getDocument();
        if (document == null) {
            return "";
        }
        final StringBuilder text = new StringBuilder();
        final org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperties props = document.getProperties().getCustomProperties().getUnderlyingProperties();
        for (final CTProperty property : props.getPropertyList()) {
            String val = "(not implemented!)";
            if (property.isSetLpwstr()) {
                val = property.getLpwstr();
            }
            else if (property.isSetLpstr()) {
                val = property.getLpstr();
            }
            else if (property.isSetDate()) {
                val = property.getDate().toString();
            }
            else if (property.isSetFiletime()) {
                val = property.getFiletime().toString();
            }
            else if (property.isSetBool()) {
                val = Boolean.toString(property.getBool());
            }
            else if (property.isSetI1()) {
                val = Integer.toString(property.getI1());
            }
            else if (property.isSetI2()) {
                val = Integer.toString(property.getI2());
            }
            else if (property.isSetI4()) {
                val = Integer.toString(property.getI4());
            }
            else if (property.isSetI8()) {
                val = Long.toString(property.getI8());
            }
            else if (property.isSetInt()) {
                val = Integer.toString(property.getInt());
            }
            else if (property.isSetUi1()) {
                val = Integer.toString(property.getUi1());
            }
            else if (property.isSetUi2()) {
                val = Integer.toString(property.getUi2());
            }
            else if (property.isSetUi4()) {
                val = Long.toString(property.getUi4());
            }
            else if (property.isSetUi8()) {
                val = property.getUi8().toString();
            }
            else if (property.isSetUint()) {
                val = Long.toString(property.getUint());
            }
            else if (property.isSetR4()) {
                val = Float.toString(property.getR4());
            }
            else if (property.isSetR8()) {
                val = Double.toString(property.getR8());
            }
            else if (property.isSetDecimal()) {
                final BigDecimal d = property.getDecimal();
                if (d == null) {
                    val = null;
                }
                else {
                    val = d.toPlainString();
                }
            }
            text.append(property.getName()).append(" = ").append(val).append("\n");
        }
        return text.toString();
    }
    
    public String getText() {
        try {
            return this.getCorePropertiesText() + this.getExtendedPropertiesText() + this.getCustomPropertiesText();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public POIXMLPropertiesTextExtractor getMetadataTextExtractor() {
        throw new IllegalStateException("You already have the Metadata Text Extractor, not recursing!");
    }
}
