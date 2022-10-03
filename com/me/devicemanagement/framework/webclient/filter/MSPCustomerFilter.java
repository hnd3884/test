package com.me.devicemanagement.framework.webclient.filter;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.Cookie;
import java.util.List;
import java.security.Principal;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import javax.servlet.http.HttpServletResponse;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.Filter;

public class MSPCustomerFilter implements Filter
{
    private String excludeURLs;
    private String excludePatterns;
    private RequestDispatcher root;
    private FilterConfig config;
    private String forward;
    
    public MSPCustomerFilter() {
        this.excludeURLs = null;
        this.excludePatterns = null;
        this.root = null;
        this.config = null;
        this.forward = null;
    }
    
    public void destroy() {
        this.root = null;
    }
    
    public void init(final FilterConfig filterconfig) {
        this.config = filterconfig;
        this.forward = filterconfig.getInitParameter("forward");
        this.excludeURLs = filterconfig.getInitParameter("excludeURLs");
        this.excludePatterns = filterconfig.getInitParameter("excludePatterns");
    }
    
    public void doFilter(final ServletRequest servletrequest, final ServletResponse servletresponse, final FilterChain filterchain) throws IOException, ServletException {
        try {
            final HttpServletRequest request = (HttpServletRequest)servletrequest;
            if ((this.excludeURLs != null && this.excludeURLs.contains(SecurityUtil.getNormalizedRequestURI(request))) || (this.excludePatterns != null && this.excludePatterns.contains(SecurityUtil.getNormalizedRequestURI(request)))) {
                filterchain.doFilter(servletrequest, servletresponse);
                return;
            }
            final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            if (isMSP) {
                final HttpServletRequest servletRequest = (HttpServletRequest)servletrequest;
                final HttpServletResponse servletResponse = (HttpServletResponse)servletresponse;
                final Principal userPrincipal = servletRequest.getUserPrincipal();
                final String isClientCall = CustomerInfoThreadLocal.getIsClientCall();
                final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                if (userPrincipal != null || userId != null) {
                    if (isClientCall != null) {
                        final String sumpage = (String)servletRequest.getAttribute("summarypage");
                        final String customersegmentation = (String)servletRequest.getAttribute("customersegmentation");
                        if (customersegmentation != null && customersegmentation.equals("false")) {
                            final String sPrevCustomerId = MSPWebClientUtil.getCustomerIDFromCookie(servletRequest);
                            if (sPrevCustomerId != null && !sPrevCustomerId.equalsIgnoreCase("All")) {
                                MSPWebClientUtil.setPrevCustomerIdInCookie(servletRequest, servletResponse, sPrevCustomerId);
                            }
                            CustomerInfoThreadLocal.setCustomerId("All");
                            CustomerInfoThreadLocal.setSummaryPage("true");
                            MSPWebClientUtil.setCustomerIDSummaryInCookie(servletRequest, servletResponse, "All");
                        }
                        else if (sumpage == null) {
                            final String spage = MSPWebClientUtil.getSummaryPageFromCookie(servletRequest);
                            final boolean isStateFilterFwdCall = servletRequest.getAttribute("TIME_TO_LOAD_START_TIME") != null;
                            if (spage != null && !spage.equals("false") && !isStateFilterFwdCall) {
                                final List customerList = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userId);
                                if (customerList.size() > 0) {
                                    String cid = "";
                                    final String sPrevCustomerId2 = MSPWebClientUtil.getPrevCustomerIdFromCookie(servletRequest);
                                    if (Boolean.parseBoolean(MSPWebClientUtil.getSummaryPageFromCookie(servletRequest))) {
                                        cid = "All";
                                    }
                                    else if (sPrevCustomerId2 != null && MSPWebClientUtil.isCustomerIDAvailableInCustomerList(sPrevCustomerId2, customerList)) {
                                        cid = sPrevCustomerId2;
                                    }
                                    else {
                                        final HashMap firstCustomer = customerList.get(0);
                                        cid = String.valueOf(firstCustomer.get("CUSTOMER_ID"));
                                    }
                                    String summaryPage = "false";
                                    if (cid.equals("All")) {
                                        summaryPage = "true";
                                    }
                                    CustomerInfoThreadLocal.setCustomerId(cid);
                                    CustomerInfoThreadLocal.setSummaryPage(summaryPage);
                                    MSPWebClientUtil.setCustomerInCookie(servletRequest, servletResponse, cid);
                                    MSPWebClientUtil.setSummaryPageInCookie(servletRequest, servletResponse, summaryPage);
                                }
                            }
                        }
                    }
                    else {
                        final List customerList2 = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userId);
                        final Cookie[] cookies = servletRequest.getCookies();
                        final HashMap<String, String> map = MSPWebClientUtil.getClientSettingsFromCookie(cookies);
                        if (!map.isEmpty()) {
                            String custid = map.get("customerid");
                            String summaryPage2 = map.get("summarypage");
                            if (summaryPage2 == null) {
                                summaryPage2 = "false";
                            }
                            if (custid == null) {
                                if (customerList2.size() > 0) {
                                    final HashMap hMap = customerList2.get(0);
                                    custid = hMap.get("CUSTOMER_ID").toString();
                                }
                            }
                            else if (!"All".equalsIgnoreCase(custid) && !MSPWebClientUtil.isCustomerIDAvailableInCustomerList(custid, customerList2) && customerList2.size() > 0) {
                                final HashMap hMap = customerList2.get(0);
                                custid = hMap.get("CUSTOMER_ID").toString();
                            }
                            CustomerInfoThreadLocal.setCustomerId(custid);
                            CustomerInfoThreadLocal.setSummaryPage(summaryPage2);
                        }
                        else if (customerList2.size() > 0) {
                            final HashMap hMap2 = customerList2.get(0);
                            final String custid2 = hMap2.get("CUSTOMER_ID").toString();
                            CustomerInfoThreadLocal.setCustomerId(custid2);
                            CustomerInfoThreadLocal.setSummaryPage("false");
                        }
                        CustomerInfoThreadLocal.setIsClientCall("true");
                    }
                }
                final String requestURI = SecurityUtil.getNormalizedRequestURI(request);
                final boolean isDCMSP = CustomerInfoUtil.getInstance().isMSP() && ProductUrlLoader.getInstance().getValue("productcode").equals("DCMSP");
                if (isDCMSP) {
                    final boolean isView = requestURI.endsWith(".ecu") || requestURI.endsWith(".ec");
                    if (isView) {
                        this.setCustomerIDinThread(servletRequest, userId);
                    }
                }
                try {
                    filterchain.doFilter(servletrequest, servletresponse);
                }
                finally {
                    CustomerInfoThreadLocal.clearClientThreadSettings();
                }
            }
            else {
                filterchain.doFilter(servletrequest, servletresponse);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setCustomerIDinThread(final HttpServletRequest request, final Long userId) throws Exception {
        String headerCustomerID = request.getHeader("X-Customer");
        if (headerCustomerID != null) {
            final List customerList = CustomerInfoUtil.getInstance().getCustomersForLoginUser(userId);
            String summaryPage;
            if (headerCustomerID.equalsIgnoreCase("All")) {
                summaryPage = "true";
            }
            else if (MSPWebClientUtil.isCustomerIDAvailableInCustomerList(headerCustomerID, customerList)) {
                summaryPage = "false";
            }
            else {
                if (customerList.size() <= 0) {
                    return;
                }
                final HashMap hMap = customerList.get(0);
                headerCustomerID = hMap.get("CUSTOMER_ID").toString();
                summaryPage = "false";
            }
            CustomerInfoThreadLocal.setCustomerId(headerCustomerID);
            CustomerInfoThreadLocal.setSummaryPage(summaryPage);
            CustomerInfoThreadLocal.setIsClientCall("true");
        }
    }
}
