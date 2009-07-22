package org.auscope.portal.server.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.net.HttpURLConnection;

import java.net.Authenticator;
import java.net.URL;
import java.net.URLEncoder;

import java.text.SimpleDateFormat;

/**
 * RestProxyServlet
 *
 * Use this servlet for javascript cross-domain access to rest services
 *
 * When using this, please add servlet mapping to /WEB-INF/web.xml as follows
 *
    <servlet>
        <servlet-name>RestProxy</servlet-name>
        <servlet-class>org.netbeans.rest.proxy.RestProxyServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>RestProxy</servlet-name>
        <url-pattern>/restproxy</url-pattern>
    </servlet-mapping>
 *
 * Sourcecode from - https://bitbucket.org/kkubasik/roomiereal/src/tip/web/rest/RestProxyServlet.txt
 *
 */
public class DownloadProxy extends HttpServlet {
    protected final Log logger = LogFactory.getLog(getClass().getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[][] queryParams = new String[][]{};
        String[][] headers = new String[][]{{"Accept", "application/json"}};

        String rest = request.getParameter("rest");
        logger.debug("Query string: " + request.getQueryString());
        
        //if(rest.equals("true")) {
        if  ( rest != null && !rest.trim().equals("") && !rest.equals("null") ) {
            RestConnection conn = new RestConnection(request.getParameter("url").replace("%26", "&"), queryParams);

            try {
                String result = conn.get(headers).getDataAsString();
                // Send response back to client
                response.getWriter().println(result);
            } catch (IOException ex) {
                logger.error(ex);
            }
        } else {
            String urlsString = request.getParameter("urls");

            if(urlsString != null) {
                String[] urls = urlsString.split("&urls=");

                //ByteArrayOutputStream bout=new ByteArrayOutputStream();

                response.setContentType("application/zip");
                response.setHeader("Content-Disposition","inline; filename=output.zip;");


                ZipOutputStream zout=new ZipOutputStream(response.getOutputStream());
                //ServletOutputStream out = response.getOutputStream();

                for(int i=0; i<urls.length; i++) {
                    RestConnection conn = new RestConnection(urls[i].replace("/restproxy?", ""), queryParams);

                    zout.putNextEntry(new ZipEntry(i+".xml"));
                    zout.write(conn.get(headers).getDataAsByteArray());
                    zout.closeEntry();
                }

                zout.finish();
                zout.flush();
                zout.close();
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        StringBuffer bodyContent = null;
        try {
            String method = request.getParameter("method");
            if(method == null)
                throw new IOException("Method parameter missing in the request.");
            BufferedReader in = request.getReader();
            String line = null;
            while ((line = in.readLine()) != null) {
                if (bodyContent == null) {
                    bodyContent = new StringBuffer();
                }
                bodyContent.append(line);
            }
            RestConnection conn = new RestConnection(request.getParameter("url"));
            if(method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) {
                String[][] headers = new String[][]{{"Content-Type", "application/json"}};
                RestResponse result = null;
                if(method.equalsIgnoreCase("POST"))
                    result = conn.post(headers, bodyContent.toString());
                else
                    result = conn.put(headers, bodyContent.toString());
                response.getWriter().println(result);
            } else if(method.equalsIgnoreCase("DELETE")) {
                RestResponse result = conn.delete();
                response.getWriter().println(result);
            } else {
                throw new IOException("Undefined method parameter in the request: "+method);
            }
        } catch (Exception e) {
            logger.error("doPost",e);
        }
    }

    public class RestConnection {

        private HttpURLConnection conn;
        private String date;

        public RestConnection(String baseUrl) {
            this(baseUrl, null);
        }

        /** Creates a new instance of RestConnection */
        public RestConnection(String baseUrl, String[][] params) {
            this(baseUrl, null, params);
        }

        /** Creates a new instance of RestConnection */
        public RestConnection(String baseUrl, String[][] pathParams, String[][] params) {
            try {
                String urlStr = baseUrl;
                if (pathParams != null && pathParams.length > 0) {
                    urlStr = replaceTemplateParameters(baseUrl, pathParams);
                }
                URL url = new URL(encodeUrl(urlStr, params));
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setDefaultUseCaches(false);
                conn.setAllowUserInteraction(true);

                SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                date = format.format(new Date());
                conn.setRequestProperty("Date", date);
            } catch (Exception ex) {
                logger.error("RestConnection",ex);
            }
        }

        public void setAuthenticator(Authenticator authenticator) {
            Authenticator.setDefault(authenticator);
        }

        public String getDate() {
            return date;
        }

        public RestResponse get() throws IOException {
            return get(null);
        }

        public RestResponse get(String[][] headers) throws IOException {
            conn.setRequestMethod("GET");

            return connect(headers, null);
        }

        public RestResponse put(String[][] headers) throws IOException {
            return put(headers, (InputStream) null);
        }

        public RestResponse put(String data) throws IOException {
            return put(null, data);
        }

        public RestResponse put(InputStream data) throws IOException {
            return put(null, data);
        }

        public RestResponse put(String[][] headers, String data) throws IOException {
            conn.setRequestMethod("PUT");

            return connect(headers, new ByteArrayInputStream(data.getBytes("UTF-8")));
        }

        public RestResponse put(String[][] headers, InputStream data) throws IOException {
            conn.setRequestMethod("PUT");

            return connect(headers, data);
        }

        public RestResponse post(String data) throws IOException {
            return post(null, data);
        }

        public RestResponse post(InputStream data) throws IOException {
            return post(null, data);
        }

        public RestResponse post(String[][] headers, String data) throws IOException {
            conn.setRequestMethod("POST");

            return connect(headers, new ByteArrayInputStream(data.getBytes("UTF-8")));
        }

        public RestResponse post(String[][] headers, InputStream data) throws IOException {
            conn.setRequestMethod("POST");

            return connect(headers, data);
        }

        /**
         * Used by post method whose contents are like form input
         */
        public RestResponse post(String[][] params) throws IOException {
            return post(null, params);
        }

        /**
         * Used by post method whose contents are like form input
         */
        public RestResponse post(String[][] headers, String[][] params) throws IOException {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ContentType", "application/x-www-form-urlencoded");
            String data = encodeParams(params);
            return connect(headers, new ByteArrayInputStream(data.getBytes("UTF-8")));
        }

        public RestResponse delete() throws IOException {
            return delete(null);
        }

        public RestResponse delete(String[][] headers) throws IOException {
            conn.setRequestMethod("DELETE");

            return connect(headers, null);
        }

        /**
         * @return response
         */
        private RestResponse connect(String[][] headers,
                InputStream data) throws IOException {
            try {
                // Send data
                setHeaders(headers);

                String method = conn.getRequestMethod();

                byte[] buffer = new byte[1024];
                int count = 0;

                if (method.equals("PUT") || method.equals("POST")) {
                    if (data != null) {
                        conn.setDoOutput(true);
                        OutputStream os = conn.getOutputStream();

                        while ((count = data.read(buffer)) != -1) {
                            os.write(buffer, 0, count);
                        }
                        os.flush();
                    }
                }

                RestResponse response = new RestResponse();
                InputStream is = conn.getInputStream();

                while ((count = is.read(buffer)) != -1) {
                    response.write(buffer, 0, count);
                }

                response.setResponseCode(conn.getResponseCode());
                response.setResponseMessage(conn.getResponseMessage());
                response.setContentType(conn.getContentType());
                response.setContentEncoding(conn.getContentEncoding());
                response.setLastModified(conn.getLastModified());

                return response;
            } catch (Exception e) {
                String errMsg = "Cannot connect to :" + conn.getURL();
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String line;
                    StringBuffer buf = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        buf.append(line);
                    }
                    errMsg = buf.toString();
                } finally {
                    throw new IOException(errMsg);
                }
            }
        }

        private String replaceTemplateParameters(String baseUrl, String[][] pathParams) {
            String url = baseUrl;
            if (pathParams != null) {
                for (int i = 0; i < pathParams.length; i++) {
                    String key = pathParams[i][0];
                    String value = pathParams[i][1];
                    if (value == null) {
                        value = "";
                    }
                    url = url.replace(key, value);
                }
            }
            return url;
        }

        private String encodeUrl(String baseUrl, String[][] params) {
            return baseUrl + encodeParams(params);
        }

        private String encodeParams(String[][] params) {
            String p = "";

            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    String key = params[i][0];
                    String value = params[i][1];

                    if (value != null) {
                        try {
                            p += key + "=" + URLEncoder.encode(value, "UTF-8") + "&";
                        } catch (UnsupportedEncodingException ex) {
                            logger.error("encodeParams",ex);
                        }
                    }
                }
                if (p.length() > 0) {
                    p = "?" + p.substring(0, p.length() - 1);
                }
            }

            return p;
        }

        private void setHeaders(String[][] headers) {
            if (headers != null) {
                for (int i = 0; i < headers.length; i++) {
                    conn.setRequestProperty(headers[i][0], headers[i][1]);
                }
            }
        }
    }

    public class RestResponse {
        private ByteArrayOutputStream os;
        private String contentType = "text/plain";
        private String contentEncoding;
        private int responseCode;
        private String responseMsg;
        private long lastModified;


        public RestResponse() {
            os = new ByteArrayOutputStream();
        }

        public RestResponse(byte[] bytes) throws IOException {
            this();

            byte[] buffer = new byte[1024];
            int count = 0;
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            while ((count = bis.read(buffer)) != -1) {
                write(buffer, 0, count);
            }
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentEncoding(String contentEncoding) {
            this.contentEncoding = contentEncoding;
        }

        public void setResponseMessage(String msg) {
            this.responseMsg = msg;
        }

        public String getResponseMessage() {
            return responseMsg;
        }

        public void setResponseCode(int code) {
            this.responseCode = code;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void write(byte[] bytes, int start, int length) {
            os.write(bytes, start, length);
        }

        public byte[] getDataAsByteArray() {
            return os.toByteArray();
        }

        public String getDataAsString() {
            try {
                return os.toString("UTF-8");
            } catch (Exception ex) {
                logger.error("getDataAsString",ex);
            }

            return null;
        }

        public OutputStream getOutputStream() {
            return os;
        }
    }
}
