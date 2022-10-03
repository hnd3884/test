package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class ExtensionType
{
    final int id;
    final String name;
    static List<ExtensionType> knownExtensions;
    static final ExtensionType EXT_SERVER_NAME;
    static final ExtensionType EXT_MAX_FRAGMENT_LENGTH;
    static final ExtensionType EXT_CLIENT_CERTIFICATE_URL;
    static final ExtensionType EXT_TRUSTED_CA_KEYS;
    static final ExtensionType EXT_TRUNCATED_HMAC;
    static final ExtensionType EXT_STATUS_REQUEST;
    static final ExtensionType EXT_USER_MAPPING;
    static final ExtensionType EXT_CERT_TYPE;
    static final ExtensionType EXT_ELLIPTIC_CURVES;
    static final ExtensionType EXT_EC_POINT_FORMATS;
    static final ExtensionType EXT_SRP;
    static final ExtensionType EXT_SIGNATURE_ALGORITHMS;
    static final ExtensionType EXT_ALPN;
    static final ExtensionType EXT_EXTENDED_MASTER_SECRET;
    static final ExtensionType EXT_RENEGOTIATION_INFO;
    
    private ExtensionType(final int id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static ExtensionType get(final int id) {
        for (final ExtensionType ext : ExtensionType.knownExtensions) {
            if (ext.id == id) {
                return ext;
            }
        }
        return new ExtensionType(id, "type_" + id);
    }
    
    private static ExtensionType e(final int id, final String name) {
        final ExtensionType ext = new ExtensionType(id, name);
        ExtensionType.knownExtensions.add(ext);
        return ext;
    }
    
    static {
        ExtensionType.knownExtensions = new ArrayList<ExtensionType>(15);
        EXT_SERVER_NAME = e(0, "server_name");
        EXT_MAX_FRAGMENT_LENGTH = e(1, "max_fragment_length");
        EXT_CLIENT_CERTIFICATE_URL = e(2, "client_certificate_url");
        EXT_TRUSTED_CA_KEYS = e(3, "trusted_ca_keys");
        EXT_TRUNCATED_HMAC = e(4, "truncated_hmac");
        EXT_STATUS_REQUEST = e(5, "status_request");
        EXT_USER_MAPPING = e(6, "user_mapping");
        EXT_CERT_TYPE = e(9, "cert_type");
        EXT_ELLIPTIC_CURVES = e(10, "elliptic_curves");
        EXT_EC_POINT_FORMATS = e(11, "ec_point_formats");
        EXT_SRP = e(12, "srp");
        EXT_SIGNATURE_ALGORITHMS = e(13, "signature_algorithms");
        EXT_ALPN = e(16, "application_layer_protocol_negotiation");
        EXT_EXTENDED_MASTER_SECRET = e(23, "extended_master_secret");
        EXT_RENEGOTIATION_INFO = e(65281, "renegotiation_info");
    }
}
