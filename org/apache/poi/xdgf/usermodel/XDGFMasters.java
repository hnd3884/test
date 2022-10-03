package org.apache.poi.xdgf.usermodel;

import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import org.apache.poi.xdgf.exceptions.XDGFException;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import com.microsoft.schemas.office.visio.x2012.main.MasterType;
import java.io.IOException;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import com.microsoft.schemas.office.visio.x2012.main.MastersDocument;
import org.apache.poi.util.Internal;
import java.util.HashMap;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.Map;
import com.microsoft.schemas.office.visio.x2012.main.MastersType;
import org.apache.poi.xdgf.xml.XDGFXMLDocumentPart;

public class XDGFMasters extends XDGFXMLDocumentPart
{
    MastersType _mastersObject;
    protected Map<Long, XDGFMaster> _masters;
    
    public XDGFMasters(final PackagePart part) {
        super(part);
        this._masters = new HashMap<Long, XDGFMaster>();
    }
    
    @Internal
    protected MastersType getXmlObject() {
        return this._mastersObject;
    }
    
    @Override
    protected void onDocumentRead() {
        try {
            try {
                this._mastersObject = MastersDocument.Factory.parse(this.getPackagePart().getInputStream()).getMasters();
            }
            catch (final XmlException | IOException e) {
                throw new POIXMLException(e);
            }
            final Map<String, MasterType> masterSettings = new HashMap<String, MasterType>();
            for (final MasterType master : this._mastersObject.getMasterArray()) {
                masterSettings.put(master.getRel().getId(), master);
            }
            for (final RelationPart rp : this.getRelationParts()) {
                final POIXMLDocumentPart part = rp.getDocumentPart();
                final String relId = rp.getRelationship().getId();
                final MasterType settings = masterSettings.get(relId);
                if (settings == null) {
                    throw new POIXMLException("Master relationship for " + relId + " not found");
                }
                if (!(part instanceof XDGFMasterContents)) {
                    throw new POIXMLException("Unexpected masters relationship for " + relId + ": " + part);
                }
                final XDGFMasterContents contents = (XDGFMasterContents)part;
                contents.onDocumentRead();
                final XDGFMaster master2 = new XDGFMaster(settings, contents, this._document);
                this._masters.put(master2.getID(), master2);
            }
        }
        catch (final POIXMLException e2) {
            throw XDGFException.wrap(this, e2);
        }
    }
    
    public Collection<XDGFMaster> getMastersList() {
        return Collections.unmodifiableCollection((Collection<? extends XDGFMaster>)this._masters.values());
    }
    
    public XDGFMaster getMasterById(final long masterId) {
        return this._masters.get(masterId);
    }
}
