package org.apache.poi.poifs.crypt.dsig.facets;

import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import org.apache.poi.util.POILogFactory;
import com.microsoft.schemas.office.x2006.digsig.CTSignatureInfoV1;
import com.microsoft.schemas.office.x2006.digsig.SignatureInfoV1Document;
import javax.xml.crypto.dsig.SignatureProperties;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTSignatureTime;
import javax.xml.crypto.dsig.SignatureProperty;
import org.w3c.dom.Node;
import javax.xml.crypto.dom.DOMStructure;
import org.w3c.dom.Element;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.SignatureTimeDocument;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.poi.openxml4j.opc.PackagePartName;
import java.util.Iterator;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.util.Comparator;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.poifs.crypt.dsig.services.RelationshipTransformService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.HashSet;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.XMLStructure;
import java.util.ArrayList;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.Reference;
import java.util.List;
import org.w3c.dom.Document;
import java.util.Set;
import org.apache.poi.util.POILogger;

public class OOXMLSignatureFacet extends SignatureFacet
{
    private static final POILogger LOG;
    private static final String ID_PACKAGE_OBJECT = "idPackageObject";
    private static final Set<String> signed;
    
    @Override
    public void preSign(final Document document, final List<Reference> references, final List<XMLObject> objects) throws XMLSignatureException {
        OOXMLSignatureFacet.LOG.log(1, new Object[] { "pre sign" });
        this.addManifestObject(document, references, objects);
        this.addSignatureInfo(document, references, objects);
    }
    
    protected void addManifestObject(final Document document, final List<Reference> references, final List<XMLObject> objects) throws XMLSignatureException {
        final List<Reference> manifestReferences = new ArrayList<Reference>();
        this.addManifestReferences(manifestReferences);
        final Manifest manifest = this.getSignatureFactory().newManifest(manifestReferences);
        final List<XMLStructure> objectContent = new ArrayList<XMLStructure>();
        objectContent.add(manifest);
        this.addSignatureTime(document, objectContent);
        final XMLObject xo = this.getSignatureFactory().newXMLObject(objectContent, "idPackageObject", null, null);
        objects.add(xo);
        final Reference reference = this.newReference("#idPackageObject", null, "http://www.w3.org/2000/09/xmldsig#Object", null, null);
        references.add(reference);
    }
    
    protected void addManifestReferences(final List<Reference> manifestReferences) throws XMLSignatureException {
        final OPCPackage ooxml = this.signatureConfig.getOpcPackage();
        final List<PackagePart> relsEntryNames = ooxml.getPartsByContentType("application/vnd.openxmlformats-package.relationships+xml");
        final Set<String> digestedPartNames = new HashSet<String>();
        for (final PackagePart pp : relsEntryNames) {
            final String baseUri = pp.getPartName().getName().replaceFirst("(.*)/_rels/.*", "$1");
            PackageRelationshipCollection prc;
            try {
                prc = new PackageRelationshipCollection(ooxml);
                prc.parseRelationshipsPart(pp);
            }
            catch (final InvalidFormatException e) {
                throw new XMLSignatureException("Invalid relationship descriptor: " + pp.getPartName().getName(), e);
            }
            final RelationshipTransformService.RelationshipTransformParameterSpec parameterSpec = new RelationshipTransformService.RelationshipTransformParameterSpec();
            for (final PackageRelationship relationship : prc) {
                final String relationshipType = relationship.getRelationshipType();
                if (TargetMode.EXTERNAL == relationship.getTargetMode()) {
                    continue;
                }
                if (!isSignedRelationship(relationshipType)) {
                    continue;
                }
                parameterSpec.addRelationshipReference(relationship.getId());
                final String partName = normalizePartName(relationship.getTargetURI(), baseUri);
                if (digestedPartNames.contains(partName)) {
                    continue;
                }
                digestedPartNames.add(partName);
                String contentType;
                try {
                    final PackagePartName relName = PackagingURIHelper.createPartName(partName);
                    final PackagePart pp2 = ooxml.getPart(relName);
                    contentType = pp2.getContentType();
                }
                catch (final InvalidFormatException e2) {
                    throw new XMLSignatureException(e2);
                }
                if (relationshipType.endsWith("customXml") && !contentType.equals("inkml+xml") && !contentType.equals("text/xml")) {
                    OOXMLSignatureFacet.LOG.log(1, new Object[] { "skipping customXml with content type: " + contentType });
                }
                else {
                    final String uri = partName + "?ContentType=" + contentType;
                    final Reference reference = this.newReference(uri, null, null, null, null);
                    manifestReferences.add(reference);
                }
            }
            if (parameterSpec.hasSourceIds()) {
                final List<Transform> transforms = new ArrayList<Transform>();
                transforms.add(this.newTransform("http://schemas.openxmlformats.org/package/2006/RelationshipTransform", parameterSpec));
                transforms.add(this.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315"));
                final String uri2 = normalizePartName(pp.getPartName().getURI(), baseUri) + "?ContentType=application/vnd.openxmlformats-package.relationships+xml";
                final Reference reference2 = this.newReference(uri2, transforms, null, null, null);
                manifestReferences.add(reference2);
            }
        }
        manifestReferences.sort(new Comparator<Reference>() {
            @Override
            public int compare(final Reference o1, final Reference o2) {
                return o1.getURI().compareTo(o2.getURI());
            }
        });
    }
    
    private static String normalizePartName(final URI partName, final String baseUri) throws XMLSignatureException {
        String pn = partName.toASCIIString();
        if (!pn.startsWith(baseUri)) {
            pn = baseUri + pn;
        }
        try {
            pn = new URI(pn).normalize().getPath().replace('\\', '/');
            OOXMLSignatureFacet.LOG.log(1, new Object[] { "part name: " + pn });
        }
        catch (final URISyntaxException e) {
            throw new XMLSignatureException(e);
        }
        return pn;
    }
    
    protected void addSignatureTime(final Document document, final List<XMLStructure> objectContent) {
        final SignatureTimeDocument sigTime = SignatureTimeDocument.Factory.newInstance();
        final CTSignatureTime ctTime = sigTime.addNewSignatureTime();
        ctTime.setFormat("YYYY-MM-DDThh:mm:ssTZD");
        ctTime.setValue(this.signatureConfig.formatExecutionTime());
        OOXMLSignatureFacet.LOG.log(1, new Object[] { "execution time: " + ctTime.getValue() });
        final Element n = (Element)document.importNode(ctTime.getDomNode(), true);
        final List<XMLStructure> signatureTimeContent = new ArrayList<XMLStructure>();
        signatureTimeContent.add(new DOMStructure(n));
        final SignatureProperty signatureTimeSignatureProperty = this.getSignatureFactory().newSignatureProperty(signatureTimeContent, "#" + this.signatureConfig.getPackageSignatureId(), "idSignatureTime");
        final List<SignatureProperty> signaturePropertyContent = new ArrayList<SignatureProperty>();
        signaturePropertyContent.add(signatureTimeSignatureProperty);
        final SignatureProperties signatureProperties = this.getSignatureFactory().newSignatureProperties(signaturePropertyContent, null);
        objectContent.add(signatureProperties);
    }
    
    protected void addSignatureInfo(final Document document, final List<Reference> references, final List<XMLObject> objects) throws XMLSignatureException {
        final List<XMLStructure> objectContent = new ArrayList<XMLStructure>();
        final SignatureInfoV1Document sigV1 = SignatureInfoV1Document.Factory.newInstance();
        final CTSignatureInfoV1 ctSigV1 = sigV1.addNewSignatureInfoV1();
        ctSigV1.setManifestHashAlgorithm(this.signatureConfig.getDigestMethodUri());
        if (this.signatureConfig.getSignatureDescription() != null) {
            ctSigV1.setSignatureComments(this.signatureConfig.getSignatureDescription());
        }
        final Element n = (Element)document.importNode(ctSigV1.getDomNode(), true);
        n.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://schemas.microsoft.com/office/2006/digsig");
        final List<XMLStructure> signatureInfoContent = new ArrayList<XMLStructure>();
        signatureInfoContent.add(new DOMStructure(n));
        final SignatureProperty signatureInfoSignatureProperty = this.getSignatureFactory().newSignatureProperty(signatureInfoContent, "#" + this.signatureConfig.getPackageSignatureId(), "idOfficeV1Details");
        final List<SignatureProperty> signaturePropertyContent = new ArrayList<SignatureProperty>();
        signaturePropertyContent.add(signatureInfoSignatureProperty);
        final SignatureProperties signatureProperties = this.getSignatureFactory().newSignatureProperties(signaturePropertyContent, null);
        objectContent.add(signatureProperties);
        final String objectId = "idOfficeObject";
        objects.add(this.getSignatureFactory().newXMLObject(objectContent, objectId, null, null));
        final Reference reference = this.newReference("#" + objectId, null, "http://www.w3.org/2000/09/xmldsig#Object", null, null);
        references.add(reference);
    }
    
    protected static String getRelationshipReferenceURI(final String zipEntryName) {
        return "/" + zipEntryName + "?ContentType=application/vnd.openxmlformats-package.relationships+xml";
    }
    
    protected static String getResourceReferenceURI(final String resourceName, final String contentType) {
        return "/" + resourceName + "?ContentType=" + contentType;
    }
    
    protected static boolean isSignedRelationship(final String relationshipType) {
        OOXMLSignatureFacet.LOG.log(1, new Object[] { "relationship type: " + relationshipType });
        final String rt = relationshipType.replaceFirst(".*/relationships/", "");
        return OOXMLSignatureFacet.signed.contains(rt) || rt.endsWith("customXml");
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)OOXMLSignatureFacet.class);
        signed = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("activeXControlBinary", "aFChunk", "attachedTemplate", "attachedToolbars", "audio", "calcChain", "chart", "chartColorStyle", "chartLayout", "chartsheet", "chartStyle", "chartUserShapes", "commentAuthors", "comments", "connections", "connectorXml", "control", "ctrlProp", "customData", "customData", "customProperty", "customXml", "diagram", "diagramColors", "diagramColorsHeader", "diagramData", "diagramDrawing", "diagramLayout", "diagramLayoutHeader", "diagramQuickStyle", "diagramQuickStyleHeader", "dialogsheet", "dictionary", "documentParts", "downRev", "drawing", "endnotes", "externalLink", "externalLinkPath", "font", "fontTable", "footer", "footnotes", "functionPrototypes", "glossaryDocument", "graphicFrameDoc", "groupShapeXml", "handoutMaster", "hdphoto", "header", "hyperlink", "image", "ink", "inkXml", "keyMapCustomizations", "legacyDiagramText", "legacyDocTextInfo", "mailMergeHeaderSource", "mailMergeRecipientData", "mailMergeSource", "media", "notesMaster", "notesSlide", "numbering", "officeDocument", "officeDocument", "oleObject", "package", "pictureXml", "pivotCacheDefinition", "pivotCacheRecords", "pivotTable", "powerPivotData", "presProps", "printerSettings", "queryTable", "recipientData", "settings", "shapeXml", "sharedStrings", "sheetMetadata", "slicer", "slicer", "slicerCache", "slicerCache", "slide", "slideLayout", "slideMaster", "slideUpdateInfo", "slideUpdateUrl", "smartTags", "styles", "stylesWithEffects", "table", "tableSingleCells", "tableStyles", "tags", "theme", "themeOverride", "timeline", "timelineCache", "transform", "ui/altText", "ui/buttonSize", "ui/controlID", "ui/description", "ui/enabled", "ui/extensibility", "ui/extensibility", "ui/helperText", "ui/imageID", "ui/imageMso", "ui/keyTip", "ui/label", "ui/lcid", "ui/loud", "ui/pressed", "ui/progID", "ui/ribbonID", "ui/showImage", "ui/showLabel", "ui/supertip", "ui/target", "ui/text", "ui/title", "ui/tooltip", "ui/userCustomization", "ui/visible", "userXmlData", "vbaProject", "video", "viewProps", "vmlDrawing", "volatileDependencies", "webSettings", "wordVbaData", "worksheet", "wsSortMap", "xlBinaryIndex", "xlExternalLinkPath/xlAlternateStartup", "xlExternalLinkPath/xlLibrary", "xlExternalLinkPath/xlPathMissing", "xlExternalLinkPath/xlStartup", "xlIntlMacrosheet", "xlMacrosheet", "xmlMaps")));
    }
}
