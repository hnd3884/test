package com.zoho.mickeyclient.action;

import java.io.PrintWriter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class HttpUtil
{
    public static void include(final String path, final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (path == null) {
            throw new NullPointerException("The input path was found to be a null value. Kindly check your input.");
        }
        final RequestDispatcher requestDispatch = request.getRequestDispatcher(path);
        requestDispatch.include((ServletRequest)request, (ServletResponse)response);
    }
    
    public static void forward(final String path, final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (path == null) {
            throw new NullPointerException("The input path was found to be a null value. Kindly check your input.");
        }
        final RequestDispatcher requestDispatch = request.getRequestDispatcher(path);
        requestDispatch.forward((ServletRequest)request, (ServletResponse)response);
    }
    
    public static void redirect(final String path, final HttpServletResponse response) throws IOException {
        if (path == null) {
            throw new NullPointerException("The input path was found to be a null value. Kindly check your input.");
        }
        response.sendRedirect(path);
    }
    
    public static void writeInResponse(final String data, final int responseStatusCode, final HttpServletResponse response) throws IOException {
        response.setStatus(responseStatusCode);
        try (final PrintWriter printWriter = response.getWriter()) {
            printWriter.write(data);
        }
    }
    
    public static void writeInResponse(final String data, final HttpServletResponse response) throws IOException {
        writeInResponse(data, 202, response);
    }
}
