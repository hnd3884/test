package org.apache.poi.sl.draw.geom;

import org.apache.poi.util.POILogFactory;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import org.apache.poi.sl.draw.binding.CTCustomGeometry2D;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.poi.util.XMLHelper;
import java.io.InputStream;
import org.apache.poi.util.POILogger;
import java.util.LinkedHashMap;

public class PresetGeometries extends LinkedHashMap<String, CustomGeometry>
{
    private static final POILogger LOG;
    private static final String BINDING_PACKAGE = "org.apache.poi.sl.draw.binding";
    protected static PresetGeometries _inst;
    
    protected PresetGeometries() {
    }
    
    public void init(final InputStream is) throws XMLStreamException, JAXBException {
        final XMLInputFactory staxFactory = XMLHelper.newXMLInputFactory();
        final XMLStreamReader streamReader = staxFactory.createXMLStreamReader(new StreamSource(is));
        try {
            streamReader.nextTag();
            final JAXBContext jaxbContext = SingletonHelper.JAXB_CONTEXT;
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            long cntElem = 0L;
            while (streamReader.hasNext() && streamReader.nextTag() == 1) {
                final String name = streamReader.getLocalName();
                final JAXBElement<CTCustomGeometry2D> el = unmarshaller.unmarshal(streamReader, CTCustomGeometry2D.class);
                final CTCustomGeometry2D cus = el.getValue();
                ++cntElem;
                if (this.containsKey(name)) {
                    PresetGeometries.LOG.log(5, "Duplicate definition of " + name);
                }
                this.put(name, new CustomGeometry(cus));
            }
        }
        finally {
            streamReader.close();
        }
    }
    
    public static CustomGeometry convertCustomGeometry(final XMLStreamReader staxReader) {
        try {
            final JAXBContext jaxbContext = SingletonHelper.JAXB_CONTEXT;
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final JAXBElement<CTCustomGeometry2D> el = unmarshaller.unmarshal(staxReader, CTCustomGeometry2D.class);
            return new CustomGeometry(el.getValue());
        }
        catch (final JAXBException e) {
            PresetGeometries.LOG.log(7, "Unable to parse single custom geometry", e);
            return null;
        }
    }
    
    public static synchronized PresetGeometries getInstance() {
        if (PresetGeometries._inst == null) {
            final PresetGeometries lInst = new PresetGeometries();
            try (final InputStream is = PresetGeometries.class.getResourceAsStream("presetShapeDefinitions.xml")) {
                lInst.init(is);
            }
            catch (final Exception e) {
                throw new RuntimeException(e);
            }
            PresetGeometries._inst = lInst;
        }
        return PresetGeometries._inst;
    }
    
    static {
        LOG = POILogFactory.getLogger(PresetGeometries.class);
    }
    
    private static class SingletonHelper
    {
        private static JAXBContext JAXB_CONTEXT;
        
        static {
            try {
                SingletonHelper.JAXB_CONTEXT = JAXBContext.newInstance("org.apache.poi.sl.draw.binding");
            }
            catch (final JAXBException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
