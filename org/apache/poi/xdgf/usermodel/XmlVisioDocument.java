package org.apache.poi.xdgf.usermodel;

import java.util.Collection;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.List;
import java.util.Iterator;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.util.PackageHelper;
import java.io.InputStream;
import com.microsoft.schemas.office.visio.x2012.main.VisioDocumentType;
import org.apache.poi.ooxml.POIXMLFactory;
import java.io.IOException;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import com.microsoft.schemas.office.visio.x2012.main.VisioDocumentDocument1;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ooxml.POIXMLDocument;

public class XmlVisioDocument extends POIXMLDocument
{
    protected XDGFPages _pages;
    protected XDGFMasters _masters;
    protected XDGFDocument _document;
    
    public XmlVisioDocument(final OPCPackage pkg) throws IOException {
        super(pkg, "http://schemas.microsoft.com/visio/2010/relationships/document");
        VisioDocumentType document;
        try {
            document = VisioDocumentDocument1.Factory.parse(this.getPackagePart().getInputStream()).getVisioDocument();
        }
        catch (final XmlException | IOException e) {
            throw new POIXMLException(e);
        }
        this._document = new XDGFDocument(document);
        this.load(new XDGFFactory(this._document));
    }
    
    public XmlVisioDocument(final InputStream is) throws IOException {
        this(PackageHelper.open(is));
    }
    
    @Override
    protected void onDocumentRead() {
        for (final POIXMLDocumentPart part : this.getRelations()) {
            if (part instanceof XDGFPages) {
                this._pages = (XDGFPages)part;
            }
            else {
                if (!(part instanceof XDGFMasters)) {
                    continue;
                }
                this._masters = (XDGFMasters)part;
            }
        }
        if (this._masters != null) {
            this._masters.onDocumentRead();
        }
        this._pages.onDocumentRead();
    }
    
    @Override
    public List<PackagePart> getAllEmbeddedParts() {
        return new ArrayList<PackagePart>();
    }
    
    public Collection<XDGFPage> getPages() {
        return this._pages.getPageList();
    }
    
    public XDGFStyleSheet getStyleById(final long id) {
        return this._document.getStyleById(id);
    }
}
