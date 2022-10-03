package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;

public class SerializerMessages_sv extends ListResourceBundle
{
    public Object[][] getContents() {
        final Object[][] contents = { { "BAD_MSGKEY", "Meddelandenyckeln ''{0}'' \u00e4r inte i meddelandeklassen ''{1}''" }, { "BAD_MSGFORMAT", "Formatet p\u00e5 meddelandet ''{0}'' i meddelandeklassen ''{1}'' underk\u00e4ndes." }, { "ER_SERIALIZER_NOT_CONTENTHANDLER", "Serializerklassen ''{0}'' implementerar inte org.xml.sax.ContentHandler." }, { "ER_RESOURCE_COULD_NOT_FIND", "Resursen [ {0} ] kunde inte h\u00e4mtas.\n {1}" }, { "ER_RESOURCE_COULD_NOT_LOAD", "Resursen [ {0} ] kunde inte laddas: {1} \n {2} \t {3}" }, { "ER_BUFFER_SIZE_LESSTHAN_ZERO", "Buffertstorlek <=0" }, { "ER_INVALID_UTF16_SURROGATE", "Ogiltigt UTF-16-surrogat uppt\u00e4ckt: {0} ?" }, { "ER_OIERROR", "IO-fel" }, { "ER_ILLEGAL_ATTRIBUTE_POSITION", "Kan inte l\u00e4gga till attributet {0} efter underordnade noder eller innan ett element har skapats. Attributet ignoreras." }, { "ER_NAMESPACE_PREFIX", "Namnrymd f\u00f6r prefix ''{0}'' har inte deklarerats." }, { "ER_STRAY_ATTRIBUTE", "Attributet ''{0}'' finns utanf\u00f6r elementet." }, { "ER_STRAY_NAMESPACE", "Namnrymdsdeklarationen ''{0}''=''{1}'' finns utanf\u00f6r element." }, { "ER_COULD_NOT_LOAD_RESOURCE", "Kunde inte ladda ''{0}'' (kontrollera CLASSPATH), anv\u00e4nder nu enbart standardv\u00e4rden" }, { "ER_ILLEGAL_CHARACTER", "F\u00f6rs\u00f6k att skriva utdatatecken med integralv\u00e4rdet {0} som inte \u00e4r representerat i angiven utdatakodning av {1}." }, { "ER_COULD_NOT_LOAD_METHOD_PROPERTY", "Kunde inte ladda egenskapsfilen ''{0}'' f\u00f6r utdatametoden ''{1}'' (kontrollera CLASSPATH)" }, { "ER_INVALID_PORT", "Ogiltigt portnummer" }, { "ER_PORT_WHEN_HOST_NULL", "Port kan inte st\u00e4llas in n\u00e4r v\u00e4rd \u00e4r null" }, { "ER_HOST_ADDRESS_NOT_WELLFORMED", "V\u00e4rd \u00e4r inte en v\u00e4lformulerad adress" }, { "ER_SCHEME_NOT_CONFORMANT", "Schemat \u00e4r inte likformigt." }, { "ER_SCHEME_FROM_NULL_STRING", "Kan inte st\u00e4lla in schema fr\u00e5n null-str\u00e4ng" }, { "ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE", "S\u00f6kv\u00e4gen inneh\u00e5ller en ogiltig escape-sekvens" }, { "ER_PATH_INVALID_CHAR", "S\u00f6kv\u00e4gen inneh\u00e5ller ett ogiltigt tecken: {0}" }, { "ER_FRAG_INVALID_CHAR", "Fragment inneh\u00e5ller ett ogiltigt tecken" }, { "ER_FRAG_WHEN_PATH_NULL", "Fragment kan inte st\u00e4llas in n\u00e4r s\u00f6kv\u00e4g \u00e4r null" }, { "ER_FRAG_FOR_GENERIC_URI", "Fragment kan bara st\u00e4llas in f\u00f6r en allm\u00e4n URI" }, { "ER_NO_SCHEME_IN_URI", "Schema saknas i URI" }, { "ER_CANNOT_INIT_URI_EMPTY_PARMS", "Kan inte initiera URI med tomma parametrar" }, { "ER_NO_FRAGMENT_STRING_IN_PATH", "Fragment kan inte anges i b\u00e5de s\u00f6kv\u00e4gen och fragmentet" }, { "ER_NO_QUERY_STRING_IN_PATH", "Fr\u00e5gestr\u00e4ng kan inte anges i b\u00e5de s\u00f6kv\u00e4gen och fr\u00e5gestr\u00e4ngen" }, { "ER_NO_PORT_IF_NO_HOST", "Port f\u00e5r inte anges om v\u00e4rden inte \u00e4r angiven" }, { "ER_NO_USERINFO_IF_NO_HOST", "Anv\u00e4ndarinfo f\u00e5r inte anges om v\u00e4rden inte \u00e4r angiven" }, { "ER_XML_VERSION_NOT_SUPPORTED", "Varning:  Versionen av utdatadokumentet som beg\u00e4rts \u00e4r ''{0}''.  Den h\u00e4r versionen av XML st\u00f6ds inte.  Versionen av utdatadokumentet kommer att vara ''1.0''." }, { "ER_SCHEME_REQUIRED", "Schema kr\u00e4vs!" }, { "ER_FACTORY_PROPERTY_MISSING", "Egenskapsobjektet som \u00f6verf\u00f6rts till SerializerFactory har ingen ''{0}''-egenskap." }, { "ER_ENCODING_NOT_SUPPORTED", "Varning: Kodningen ''{0}'' st\u00f6ds inte av Java runtime." } };
        return contents;
    }
}
