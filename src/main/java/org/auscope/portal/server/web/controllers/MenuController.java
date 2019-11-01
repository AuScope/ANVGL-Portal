package org.auscope.portal.server.web.controllers;

import java.awt.Menu;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.Charsets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.auscope.portal.core.services.PortalServiceException;
import org.auscope.portal.core.services.cloud.CloudComputeService;
import org.auscope.portal.server.web.security.ANVGLUser;
import org.auscope.portal.server.web.service.NCIDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles all {@link Menu}-related requests,
 * 
 * TODO: Check how necessary thi sis post Spring Boot migration
 *
 * @author Jarek Sanders
 * @author Josh Vote
 */
@Controller
public class MenuController {

   protected final Log logger = LogFactory.getLog(getClass());

   private String buildStamp;

    /**
     * !!! For Unit Testing Only !!!
     *
     * @param buildStamp
     *            the buildStamp to set
     */
    public void setBuildStamp(String buildStamp) {
        this.buildStamp = buildStamp;
    }

    private String googleMapKey;
    private String googleAnalyticsKey;
    private String aafLoginUrl;
    private String adminEmail;
    private CloudComputeService[] cloudComputeServices;
    
    @Autowired
    private NCIDetailsService nciDetailsService;

    @Autowired
    public MenuController(@Value("${googlemap.key}") String googleMapKey,
                         @Value("${google.analytics.key:}") String googleAnalyticsKey,
                         @Value("${portalAdminEmail}") String adminEmail,
                         @Value("${aaf.loginUrl}") String aafLoginUrl,
                         CloudComputeService[] cloudComputeServices,
                         NCIDetailsService nciDetailsService) {
        this.buildStamp = null;
        this.googleMapKey = googleMapKey;
        this.googleAnalyticsKey = googleAnalyticsKey;
        this.aafLoginUrl = aafLoginUrl;
        this.adminEmail = adminEmail;
        this.cloudComputeServices = cloudComputeServices;
        this.nciDetailsService = nciDetailsService;
    }

    /**
     * Return configured admin email address.
     */
    public String getAdminEmail() {
        return this.adminEmail;
    }

   /**
    * Adds the google maps/analytics keys to the specified model
    * @param mav
    */
   private void addGoogleKeys(ModelAndView mav) {
       mav.addObject("googleKey", googleMapKey);
       if (googleAnalyticsKey != null && !googleAnalyticsKey.isEmpty()) {
           mav.addObject("analyticKey", googleAnalyticsKey);
       }
   }

   private String getOrGenerateBuildStamp(HttpServletRequest request) {
       if (buildStamp != null) {
           return buildStamp;
       }
       String appServerHome = request.getSession().getServletContext().getRealPath("/");
       File manifestFile = new File(appServerHome,"META-INF/MANIFEST.MF");

       try (FileInputStream fis = new FileInputStream(manifestFile)){
           Manifest mf = new Manifest();
           mf.read(fis);
           String buildDate = mf.getMainAttributes().getValue("buildDate");

           buildStamp = new String(Hex.encodeHex(buildDate.getBytes(Charsets.UTF_8)));
           return buildStamp;
       } catch (Exception e) {
           logger.info("Error accessing manifest: " + e.getMessage());
           logger.debug("Exception:", e);
           return "";
       }
   }

   /**
    * Adds a number of manifest specific variables to the model
    * @param mav
    * @param request
    */
    private void addManifest(ModelAndView mav, HttpServletRequest request) {
        String appServerHome = request.getSession().getServletContext().getRealPath("/");
        File manifestFile = new File(appServerHome, "META-INF/MANIFEST.MF");
        Manifest mf = new Manifest();
        try (FileInputStream fis = new FileInputStream(manifestFile)) {
            mf.read(fis);
            Attributes atts = mf.getMainAttributes();
            mav.addObject("specificationTitle", atts.getValue("Specification-Title"));
            mav.addObject("implementationVersion", atts.getValue("Implementation-Version"));
            mav.addObject("implementationBuild", atts.getValue("Implementation-Build"));
            mav.addObject("buildDate", atts.getValue("buildDate"));
            mav.addObject("buildJdk", atts.getValue("Build-Jdk"));
            mav.addObject("javaVendor", atts.getValue("javaVendor"));
            mav.addObject("builtBy", atts.getValue("Built-By"));
            mav.addObject("osName", atts.getValue("osName"));
            mav.addObject("osVersion", atts.getValue("osVersion"));

            mav.addObject("serverName", request.getServerName());
            mav.addObject("serverInfo", request.getSession().getServletContext().getServerInfo());
            mav.addObject("serverJavaVersion", System.getProperty("java.version"));
            mav.addObject("serverJavaVendor", System.getProperty("java.vendor"));
            mav.addObject("javaHome", System.getProperty("java.home"));
            mav.addObject("serverOsArch", System.getProperty("os.arch"));
            mav.addObject("serverOsName", System.getProperty("os.name"));
            mav.addObject("serverOsVersion", System.getProperty("os.version"));
        } catch (IOException e) {
            /* ignore, since we'll just leave an empty form */
            logger.info("Error accessing manifest: " + e.getMessage());
            logger.debug("Exception:", e);
        }
    }

   /**
    * Handles all HTML page requests by mapping them to an appropriate view (and adding other details).
    * @param request
    * @param response
    * @return
    * @throws IOException
    * @throws URISyntaxException
    * @throws PortalServiceException
    */
   /*
   @RequestMapping("/XX/*.html")
   public ModelAndView handleHtmlToView(@AuthenticationPrincipal ANVGLUser user,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws IOException, URISyntaxException, PortalServiceException {
       //Detect whether this is a new session or not...
       HttpSession session = request.getSession();
       boolean isNewSession = session.getAttribute("existingSession") == null;
       session.setAttribute("existingSession", true);

       //Decode our request to get the view name we are actually requesting
       String requestUri = request.getRequestURI();
       String[] requestComponents = requestUri.split("/");
       if (requestComponents.length == 0) {
           logger.debug(String.format("request '%1$s' doesnt contain any extractable components", requestUri));
           response.sendError(HttpStatus.SC_NOT_FOUND, "Resource not found : " + requestUri);
           return null;
       }
       String requestedResource = requestComponents[requestComponents.length - 1];
       // OAuth login requires a lower level url, check for this
       if(requestComponents.length > 1 && requestUri.contains("oauth"))
           requestedResource = "oauth/" + requestedResource;
       String resourceName = requestedResource.replace(".html", "");

       logger.trace(String.format("view name '%1$s' extracted from request '%2$s'", resourceName, requestUri));

       //If we have a request come in and the user isn't fully configured, shove them back to the user setup page
       if (user != null && user.getUsername() != null) {
           boolean tcs = user.acceptedTermsConditionsStatus();
           boolean configured = user.configuredServicesStatus(nciDetailsService, cloudComputeServices);

           if (!configured || !tcs) {
               String uri = request.getRequestURI();
               if (!uri.contains("login.html") &&
                   !uri.contains("gmap.html") &&
                   !uri.contains("user.html") &&
                   !uri.contains("noconfig.html") &&
                   !uri.contains("notcs.html") &&
                   !uri.contains("admin.html")) {
                   String params = "";
                   if (!uri.contains("login.html")) {
                       params = "?next=" + new URI(uri).getPath();
                   }

                   return new ModelAndView(configured ?
                                           "redirect:/notcs.html" + params :
                                           "redirect:/noconfig.html" + params);
               }
           }
       }

       //Give the user the view they are actually requesting
       ModelAndView mav = new ModelAndView(resourceName);

       mav.addObject("isNewSession", isNewSession);
       mav.addObject("aafLoginUrl", aafLoginUrl);
       mav.addObject("adminEmail", this.adminEmail);

       //Customise the model as required
       addGoogleKeys(mav); //always add the google keys
       if (resourceName.equals("about") || resourceName.equals("admin")) {
           addManifest(mav, request); //The manifest details aren't really required by much
       }
       mav.addObject("buildTimestamp", getOrGenerateBuildStamp(request));

       return mav;
   }
   */

}
