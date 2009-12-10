package org.auscope.portal.server.web.controllers;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.lib.legacy.ClassImposteriser;
import org.auscope.portal.server.web.service.HttpServiceCaller;
import org.auscope.portal.server.util.GmlToKml;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.Header;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletOutputStream;
import java.io.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;

/**
 * User: Mathew Wyatt
 * Date: 10/09/2009
 * Time: 10:46:21 AM
 */


public class TestDownloadController {

    /**
     * JMock context
     */
    private Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    /**
     * Mock httpService caller
     */
    private HttpServiceCaller httpServiceCaller = context.mock(HttpServiceCaller.class);

    /**
     * The controller to test
     */
    private DownloadController downloadController;

    /**
     * Mock response
     */
    private HttpServletResponse mockHttpResponse = context.mock(HttpServletResponse.class);

    /**
     * Needed so we can check the contents of our zip file after it is written
     */
    final class MyServletOutputStream extends ServletOutputStream {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        public void write(int i) throws IOException {
            byteArrayOutputStream.write(i);
        }

        public ZipInputStream getZipInputStream() {
            return new ZipInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        }
    }

    ;

    @Before
    public void setup() {
        downloadController = new DownloadController(httpServiceCaller);
    }

    /**
     * Test that this function makes all of the approriate calls, and see if it returns gml given some dummy data
     */
    @Test
    public void testDownloadGMLAsZip() throws Exception {
        final MyServletOutputStream servletOutputStream = new MyServletOutputStream();
        final String[] serviceUrls = {"http://someUrl"};
        final String dummyGml = "<someGmlHere/>";
        final String dummyJSONResponse = "{\"data\":{\"kml\":\"<someKmlHere/>\", \"gml\":\"" + dummyGml + "\"},\"success\":true}";

        context.checking(new Expectations() {{
            //setting of the headers for the return content
            oneOf(mockHttpResponse).setContentType(with(any(String.class)));
            oneOf(mockHttpResponse).setHeader(with(any(String.class)), with(any(String.class)));
            oneOf(mockHttpResponse).getOutputStream();
            will(returnValue(servletOutputStream));

            //calling the service
            oneOf(httpServiceCaller).getHttpClient();
            oneOf(httpServiceCaller).getMethodResponseAsString(with(any(HttpMethodBase.class)), with(any(HttpClient.class)));
            will(returnValue(dummyJSONResponse));
        }});

        downloadController.downloadGMLAsZip(serviceUrls, mockHttpResponse);

        //check that the zip file contains the correct data
        ZipInputStream zipInputStream = servletOutputStream.getZipInputStream();
        ZipEntry ze = null;
        while ((ze = zipInputStream.getNextEntry()) != null) {
            ByteArrayOutputStream fout = new ByteArrayOutputStream();
            for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                fout.write(c);
            }
            zipInputStream.closeEntry();
            fout.close();

            //should only have one entery with the gml data in it
            Assert.assertEquals(new String(dummyGml.getBytes()), new String(fout.toByteArray()));
        }
        zipInputStream.close();
    }

    /**
     * Test that this function makes all of the approriate calls, and see if it returns gml given some dummy data
     */
    @Test
    public void testDownloadGMLAsZipWithError() throws Exception {
        final MyServletOutputStream servletOutputStream = new MyServletOutputStream();
        final String[] serviceUrls = {"http://someUrl"};
        final String dummyGml = "<someGmlHere/>";
        final String dummyJSONResponse = "{\"data\":{\"kml\":\"<someKmlHere/>\", \"gml\":\"" + dummyGml + "\"},\"success\":false}";

        context.checking(new Expectations() {{
            //setting of the headers for the return content
            oneOf(mockHttpResponse).setContentType(with(any(String.class)));
            oneOf(mockHttpResponse).setHeader(with(any(String.class)), with(any(String.class)));
            oneOf(mockHttpResponse).getOutputStream();
            will(returnValue(servletOutputStream));

            //calling the service
            oneOf(httpServiceCaller).getHttpClient();
            oneOf(httpServiceCaller).getMethodResponseAsString(with(any(HttpMethodBase.class)), with(any(HttpClient.class)));
            will(returnValue(dummyJSONResponse));
        }});

        downloadController.downloadGMLAsZip(serviceUrls, mockHttpResponse);

        //check that the zip file contains the correct data
        ZipInputStream zipInputStream = servletOutputStream.getZipInputStream();
        ZipEntry ze = null;
        while ((ze = zipInputStream.getNextEntry()) != null) {
            System.out.println(ze.getName());
            Assert.assertTrue(ze.getName().endsWith("operation-failed.xml"));
        }
        zipInputStream.close();
    }

    /**
     * Test that this function makes all of the approriate calls, and see if it returns xml file zipped up
     */
    @Test
    public void testDownloadWMSAsZipWithError() throws Exception {
        final MyServletOutputStream servletOutputStream = new MyServletOutputStream();
        final String[] serviceUrls = {"http://someUrl"};
        final String dummyData = "dummyData";
        final Header header = new Header("Content-Type", "text/xml");

        context.checking(new Expectations() {{
            //setting of the headers for the return content
            oneOf(mockHttpResponse).setContentType(with(any(String.class)));
            oneOf(mockHttpResponse).setHeader(with(any(String.class)), with(any(String.class)));
            oneOf(mockHttpResponse).getOutputStream();
            will(returnValue(servletOutputStream));

            //calling the service
            oneOf(httpServiceCaller).getHttpClient();
            oneOf(httpServiceCaller).getMethodResponseInBytes(with(any(HttpMethodBase.class)), with(any(HttpClient.class)));will(returnValue(dummyData.getBytes()));

            //return a string containing xml, which will denote some form of error from a WMS call
            oneOf(httpServiceCaller).getResponseHeader(with(any(HttpMethodBase.class)), with(any(String.class)));will(returnValue(header));
        }});

        downloadController.downloadWMSAsZip(serviceUrls, mockHttpResponse);

        //check that the zip file contains the correct data
        ZipInputStream zipInputStream = servletOutputStream.getZipInputStream();
        ZipEntry ze = null;
        while ((ze = zipInputStream.getNextEntry()) != null) {
            //name of the file should end in .xml
            Assert.assertTrue(ze.getName().endsWith(".xml"));

            ByteArrayOutputStream fout = new ByteArrayOutputStream();
            for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                fout.write(c);
            }
            zipInputStream.closeEntry();
            fout.close();

            //should only have one entery with the gml data in it
            Assert.assertEquals(new String(dummyData.getBytes()), new String(fout.toByteArray()));
        }
        zipInputStream.close();
    }

    /**
     * Test that this function makes all of the approriate calls, and see if it returns png file zipped up
     *
     * @throws Exception
     */
    @Test
    public void testDownloadWMSAsZipWithPNG() throws Exception {
        final MyServletOutputStream servletOutputStream = new MyServletOutputStream();
        final String[] serviceUrls = {"http://someUrl"};
        final String dummyData = "dummyData";
        final Header header = new Header("Content-Type", "image/png");

        context.checking(new Expectations() {{
            //setting of the headers for the return content
            oneOf(mockHttpResponse).setContentType(with(any(String.class)));
            oneOf(mockHttpResponse).setHeader(with(any(String.class)), with(any(String.class)));
            oneOf(mockHttpResponse).getOutputStream();
            will(returnValue(servletOutputStream));

            //calling the service
            oneOf(httpServiceCaller).getHttpClient();
            oneOf(httpServiceCaller).getMethodResponseInBytes(with(any(HttpMethodBase.class)), with(any(HttpClient.class)));will(returnValue(dummyData.getBytes()));

            //return a string containing xml, which will denote some form of error from a WMS call
            oneOf(httpServiceCaller).getResponseHeader(with(any(HttpMethodBase.class)), with(any(String.class)));will(returnValue(header));
        }});

        downloadController.downloadWMSAsZip(serviceUrls, mockHttpResponse);

        //check that the zip file contains the correct data
        ZipInputStream zipInputStream = servletOutputStream.getZipInputStream();
        ZipEntry ze = null;
        while ((ze = zipInputStream.getNextEntry()) != null) {
            //name of the file should end in .xml
            Assert.assertTrue(ze.getName().endsWith(".png"));

            ByteArrayOutputStream fout = new ByteArrayOutputStream();
            for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                fout.write(c);
            }
            zipInputStream.closeEntry();
            fout.close();

            //should only have one entery with the gml data in it
            Assert.assertEquals(new String(dummyData.getBytes()), new String(fout.toByteArray()));
        }
        zipInputStream.close();
    }

}
