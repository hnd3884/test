package com.sun.xml.internal.ws.streaming;

import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import com.sun.xml.internal.ws.util.FastInfosetUtil;
import java.io.InputStream;
import java.net.URL;
import java.io.Reader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import java.lang.reflect.Method;

public class SourceReaderFactory
{
    static Class fastInfosetSourceClass;
    static Method fastInfosetSource_getInputStream;
    
    public static XMLStreamReader createSourceReader(final Source source, final boolean rejectDTDs) {
        return createSourceReader(source, rejectDTDs, null);
    }
    
    public static XMLStreamReader createSourceReader(final Source source, final boolean rejectDTDs, final String charsetName) {
        try {
            if (source instanceof StreamSource) {
                final StreamSource streamSource = (StreamSource)source;
                final InputStream is = streamSource.getInputStream();
                if (is != null) {
                    if (charsetName != null) {
                        return XMLStreamReaderFactory.create(source.getSystemId(), new InputStreamReader(is, charsetName), rejectDTDs);
                    }
                    return XMLStreamReaderFactory.create(source.getSystemId(), is, rejectDTDs);
                }
                else {
                    final Reader reader = streamSource.getReader();
                    if (reader != null) {
                        return XMLStreamReaderFactory.create(source.getSystemId(), reader, rejectDTDs);
                    }
                    return XMLStreamReaderFactory.create(source.getSystemId(), new URL(source.getSystemId()).openStream(), rejectDTDs);
                }
            }
            else {
                if (source.getClass() == SourceReaderFactory.fastInfosetSourceClass) {
                    return FastInfosetUtil.createFIStreamReader((InputStream)SourceReaderFactory.fastInfosetSource_getInputStream.invoke(source, new Object[0]));
                }
                if (source instanceof DOMSource) {
                    final DOMStreamReader dsr = new DOMStreamReader();
                    dsr.setCurrentNode(((DOMSource)source).getNode());
                    return dsr;
                }
                if (source instanceof SAXSource) {
                    final Transformer tx = XmlUtil.newTransformer();
                    final DOMResult domResult = new DOMResult();
                    tx.transform(source, domResult);
                    return createSourceReader(new DOMSource(domResult.getNode()), rejectDTDs);
                }
                throw new XMLReaderException("sourceReader.invalidSource", new Object[] { source.getClass().getName() });
            }
        }
        catch (final Exception e) {
            throw new XMLReaderException(e);
        }
    }
    
    static {
        try {
            SourceReaderFactory.fastInfosetSourceClass = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource");
            SourceReaderFactory.fastInfosetSource_getInputStream = SourceReaderFactory.fastInfosetSourceClass.getMethod("getInputStream", (Class[])new Class[0]);
        }
        catch (final Exception e) {
            SourceReaderFactory.fastInfosetSourceClass = null;
        }
    }
}
