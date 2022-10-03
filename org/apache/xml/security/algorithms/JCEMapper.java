package org.apache.xml.security.algorithms;

import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;
import java.util.Map;
import org.apache.commons.logging.Log;

public class JCEMapper
{
    static Log log;
    private static Map uriToJCEName;
    private static Map algorithmsMap;
    private static String providerName;
    
    public static void init(final Element element) throws Exception {
        loadAlgorithms((Element)element.getElementsByTagName("Algorithms").item(0));
    }
    
    static void loadAlgorithms(final Element element) {
        final Element[] selectNodes = XMLUtils.selectNodes(element.getFirstChild(), "http://www.xmlsecurity.org/NS/#configuration", "Algorithm");
        JCEMapper.uriToJCEName = new HashMap(selectNodes.length * 2);
        JCEMapper.algorithmsMap = new HashMap(selectNodes.length * 2);
        for (int i = 0; i < selectNodes.length; ++i) {
            final Element element2 = selectNodes[i];
            final String attribute = element2.getAttribute("URI");
            JCEMapper.uriToJCEName.put(attribute, element2.getAttribute("JCEName"));
            JCEMapper.algorithmsMap.put(attribute, new Algorithm(element2));
        }
    }
    
    static Algorithm getAlgorithmMapping(final String s) {
        return JCEMapper.algorithmsMap.get(s);
    }
    
    public static String translateURItoJCEID(final String s) {
        if (JCEMapper.log.isDebugEnabled()) {
            JCEMapper.log.debug((Object)("Request for URI " + s));
        }
        return JCEMapper.uriToJCEName.get(s);
    }
    
    public static String getAlgorithmClassFromURI(final String s) {
        if (JCEMapper.log.isDebugEnabled()) {
            JCEMapper.log.debug((Object)("Request for URI " + s));
        }
        return JCEMapper.algorithmsMap.get(s).algorithmClass;
    }
    
    public static int getKeyLengthFromURI(final String s) {
        return Integer.parseInt(JCEMapper.algorithmsMap.get(s).keyLength);
    }
    
    public static String getJCEKeyAlgorithmFromURI(final String s) {
        return JCEMapper.algorithmsMap.get(s).requiredKey;
    }
    
    public static String getProviderId() {
        return JCEMapper.providerName;
    }
    
    public static void setProviderId(final String providerName) {
        JCEMapper.providerName = providerName;
    }
    
    static {
        JCEMapper.log = LogFactory.getLog(JCEMapper.class.getName());
        JCEMapper.providerName = null;
    }
    
    public static class Algorithm
    {
        String algorithmClass;
        String keyLength;
        String requiredKey;
        
        public Algorithm(final Element element) {
            this.algorithmClass = element.getAttribute("AlgorithmClass");
            this.keyLength = element.getAttribute("KeyLength");
            this.requiredKey = element.getAttribute("RequiredKey");
        }
    }
}
