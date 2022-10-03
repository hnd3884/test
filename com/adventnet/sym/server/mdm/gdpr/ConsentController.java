package com.adventnet.sym.server.mdm.gdpr;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.me.idps.core.util.DirectoryUtil;
import java.util.Properties;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DMViewRetrieverAction;

public class ConsentController extends DMViewRetrieverAction
{
    private static String className;
    private static Logger logger;
    
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerId = MSPWebClientUtil.getCustomerID(request);
            if (viewCtx.getUniqueId().equals("ConsentsHistoryView")) {
                final String consentId = viewCtx.getRequest().getParameter("consentId");
                final int id = Integer.parseInt(consentId);
                final Criteria consentIdEqualRequestId = new Criteria(Column.getColumn("EventLogAndConsentsRel", "CONSENT_ID"), (Object)id, 0);
                selectQuery.setCriteria(consentIdEqualRequestId);
            }
            else if (viewCtx.getUniqueId().equals("ConsentsView")) {
                boolean azureADfound = false;
                boolean opADusersSyncedFromZD = false;
                final List<Properties> dmDomainProps = DMDomainDataHandler.getInstance().getAllDMManagedProps(customerId);
                if (dmDomainProps != null && !dmDomainProps.isEmpty()) {
                    for (final Properties dmDomainProp : dmDomainProps) {
                        final Integer clientID = ((Hashtable<K, Integer>)dmDomainProp).get("CLIENT_ID");
                        if (clientID == 3) {
                            azureADfound = true;
                        }
                        else {
                            if (clientID != 201 || !DirectoryUtil.getInstance().isZDexplicit((long)customerId)) {
                                continue;
                            }
                            opADusersSyncedFromZD = true;
                        }
                    }
                }
                final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
                dirProdImplRequest.eventType = IdpEventConstants.AD_INTEG_CONSENT;
                dirProdImplRequest.args = new Object[] { azureADfound, opADusersSyncedFromZD };
                DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
            }
        }
        catch (final Exception e) {
            ConsentController.logger.log(Level.SEVERE, e, () -> ConsentController.className + ":setCritetia:: Exception while setting Criteria for consent.");
        }
        super.setCriteria(selectQuery, viewCtx);
    }
    
    static {
        ConsentController.className = ConsentController.class.getName();
        ConsentController.logger = Logger.getLogger(ConsentController.className);
    }
}
