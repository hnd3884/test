package com.me.ems.onpremise.security.certificate.summeryserver.summary.api.v1.service;

import java.util.Iterator;
import java.util.List;
import com.me.ems.summaryserver.common.util.ProbePropertyUtil;
import java.util.HashMap;
import com.adventnet.ds.query.Criteria;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service.ProbeDetailsService;
import java.util.Map;
import com.me.ems.onpremise.security.certificate.factory.CertificateService;
import com.me.ems.onpremise.security.certificate.api.v1.service.CertificateServiceImpl;

public class SSCertificateServiceImpl extends CertificateServiceImpl implements CertificateService
{
    @Override
    public Map getProbeCertificateDetailsList() {
        final ProbeDetailsService probeDetailsService = new ProbeDetailsService();
        final List<HashMap> details = ProbeDetailsService.getProbeDetails(null);
        final Map result = new HashMap();
        if (details != null && details.size() > 0) {
            for (final HashMap detail : details) {
                final Long probeID = detail.get("probeID");
                final String probeName = detail.get("probeName");
                final String val = ProbePropertyUtil.getProbeProperty("ps.ems.ssl_certificate_configured", probeID);
                final HashMap probeConfig = new HashMap();
                probeConfig.put("probeName", probeName);
                probeConfig.put("isConfigured", Boolean.valueOf(val));
                result.put(probeID + "", probeConfig);
            }
        }
        return result;
    }
}
