package org.apache.xmlbeans.impl.jam.xml;

import java.io.Writer;
import org.apache.xmlbeans.impl.jam.JClass;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;
import org.apache.xmlbeans.impl.jam.JamServiceParams;
import org.apache.xmlbeans.impl.jam.internal.JamServiceImpl;
import java.util.Collection;
import java.util.Arrays;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.provider.JamClassBuilder;
import org.apache.xmlbeans.impl.jam.internal.CachedClassBuilder;
import org.apache.xmlbeans.impl.jam.JamServiceFactory;
import org.apache.xmlbeans.impl.jam.JamService;
import java.io.InputStream;

public class JamXmlUtils
{
    private static final JamXmlUtils INSTANCE;
    
    public static final JamXmlUtils getInstance() {
        return JamXmlUtils.INSTANCE;
    }
    
    private JamXmlUtils() {
    }
    
    public JamService createService(final InputStream in) throws IOException, XMLStreamException {
        if (in == null) {
            throw new IllegalArgumentException("null stream");
        }
        final JamServiceFactory jsf = JamServiceFactory.getInstance();
        final JamServiceParams params = jsf.createServiceParams();
        final CachedClassBuilder cache = new CachedClassBuilder();
        params.addClassBuilder(cache);
        final JamService out = jsf.createService(params);
        final JamXmlReader reader = new JamXmlReader(cache, in, (ElementContext)params);
        reader.read();
        final List classNames = Arrays.asList(cache.getClassNames());
        classNames.addAll(Arrays.asList(out.getClassNames()));
        final String[] nameArray = new String[classNames.size()];
        classNames.toArray(nameArray);
        ((JamServiceImpl)out).setClassNames(nameArray);
        return out;
    }
    
    public void toXml(final JClass[] clazzes, final Writer writer) throws IOException, XMLStreamException {
        if (clazzes == null) {
            throw new IllegalArgumentException("null classes");
        }
        if (writer == null) {
            throw new IllegalArgumentException("null writer");
        }
        final JamXmlWriter out = new JamXmlWriter(writer);
        out.begin();
        for (int i = 0; i < clazzes.length; ++i) {
            out.write(clazzes[i]);
        }
        out.end();
    }
    
    static {
        INSTANCE = new JamXmlUtils();
    }
}
