package com.me.devicemanagement.onpremise.server.twofactor;

import javax.xml.transform.Transformer;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.util.logging.Level;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Node;
import java.io.File;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.utils.XMLUtils;
import com.adventnet.persistence.xml.Do2XmlConverter;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.alerts.DCEMailAlertData;

public class GoogleAuthenticationEMailAlerts extends DCEMailAlertData
{
    private static Logger logger;
    
    public DataObject createEmailAlertDO(final JSONObject jsonObj, DataObject dataObject) throws Exception {
        final Long userID = (Long)jsonObj.get("userID");
        final Long loginID = DMUserHandler.getLoginIdForUserId(userID);
        final SelectQuery selQuery = (SelectQuery)new SelectQueryImpl(new Table("GoogleAuthentication"));
        selQuery.addSelectColumn(new Column("GoogleAuthentication", "LOGIN_ID"));
        selQuery.addSelectColumn(new Column("GoogleAuthentication", "KEY_LABEL"));
        selQuery.addSelectColumn(new Column("GoogleAuthentication", "SECRET_KEY"));
        final Criteria googleAuthCri = new Criteria(new Column("GoogleAuthentication", "LOGIN_ID"), (Object)loginID, 0);
        selQuery.setCriteria(googleAuthCri);
        dataObject = SyMUtil.getPersistence().get(selQuery);
        return dataObject;
    }
    
    public boolean generateXMLDataFile(final String fileName, final DataObject dataObject) throws Exception {
        boolean generated = false;
        try {
            if (dataObject != null) {
                Do2XmlConverter.transform(dataObject, fileName, true);
                final DocumentBuilder documentBuilder = XMLUtils.getDocumentBuilderInstance();
                final Document document = documentBuilder.parse(fileName);
                final Element root = document.getDocumentElement();
                final Element QRImage = document.createElement("QRImage");
                Long loginId = null;
                if (dataObject != null && !dataObject.isEmpty()) {
                    final Iterator rowIterator = dataObject.getRows("GoogleAuthentication");
                    final Row googleAuthRow = rowIterator.next();
                    loginId = (Long)googleAuthRow.get("LOGIN_ID");
                }
                final Long userID = DMUserHandler.getUserIdForLoginId(loginId);
                final GoogleAuthAction secKey = new GoogleAuthAction(userID);
                final String QRImg = secKey.getQRImage(userID);
                QRImage.setAttribute("image", QRImg);
                QRImage.setAttribute("imageName", "cid:" + QRImg.replaceAll("\\s+", "") + ".png");
                final String destinationFolder = SyMUtil.getInstallationDir() + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "images" + File.separator + "TwoFactor";
                this.pdfFile = destinationFolder + File.separator + QRImg + ".png";
                root.appendChild(QRImage);
                final DOMSource source = new DOMSource(document);
                final Transformer transformer = XMLUtils.getTransformerInstance();
                final StreamResult result = new StreamResult(fileName);
                transformer.transform(source, result);
                generated = true;
            }
        }
        catch (final Exception ex) {
            GoogleAuthenticationEMailAlerts.logger.log(Level.SEVERE, "Caught exception in generating XML File", ex);
        }
        return generated;
    }
    
    static {
        GoogleAuthenticationEMailAlerts.logger = Logger.getLogger("UserManagementLogger");
    }
}
