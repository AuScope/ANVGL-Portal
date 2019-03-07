package org.auscope.portal.server.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.auscope.portal.core.services.cloud.CloudComputeService;
import org.auscope.portal.core.services.cloud.CloudStorageService;
import org.auscope.portal.server.web.service.NCIDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Class for redirecting a user that isn't setup correctly to the user setup page.
 * @author Josh Vote (CSIRO)
 *
 */
public class RedirectUnconfiguredUserHandler implements AuthenticationSuccessHandler {

    protected CloudStorageService[] cloudStorageServices;
    protected CloudComputeService[] cloudComputeServices;
    @Autowired
    protected NCIDetailsService nciDetailsService;
    private String frontEndUrl;

    /*
    public NCIDetailsService getNciDetailsService() {
        return nciDetailsService;
    }

    public void setNciDetailsService(NCIDetailsService nciDetailsServcie) {
        this.nciDetailsService = nciDetailsServcie;
    }
    */

    public CloudStorageService[] getCloudStorageServices() {
        return cloudStorageServices;
    }

    public void setCloudStorageServices(CloudStorageService[] cloudStorageServices) {
        this.cloudStorageServices = cloudStorageServices;
    }

    public CloudComputeService[] getCloudComputeServices() {
        return cloudComputeServices;
    }

    public void setCloudComputeServices(CloudComputeService[] cloudComputeServices) {
        this.cloudComputeServices = cloudComputeServices;
    }

    public String getFrontEndUrl() {
		return frontEndUrl;
	}

	public void setFrontEndUrl(String frontEndUrl) {
		this.frontEndUrl = frontEndUrl;
	}

	@SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(getClass());

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException, ServletException {
    	/*
        Object principal = auth.getPrincipal();
        DefaultSavedRequest savedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");

        if (principal instanceof ANVGLUser) {
            ANVGLUser user = (ANVGLUser) principal;
            try {
                boolean tcs = user.acceptedTermsConditionsStatus();
                boolean configured = user.configuredServicesStatus(nciDetailsDao, cloudComputeServices);

                //Redirect if the user needs to accept T&Cs OR if they don't have any config services setup
                if (!configured || !tcs) {
                    String params = "";
                    String redirect = configured ? "../notcs.html" : "../noconfig.html";
                    if (savedRequest != null) {
                        URL requestUrl = new URL(savedRequest.getRequestURL());
                        if (!requestUrl.getPath().contains("login.html")) {
                            params = "?next=" + requestUrl.getPath();
                        }
                    }
                    response.sendRedirect(redirect + params);
                    return;
                }
            } catch (PortalServiceException e) {
                log.error("Unable to verify if user is fully configured:", e);
            }
        }

        if (savedRequest != null) {
            response.sendRedirect(savedRequest.getRequestURL());
            return;
        }
        */
        response.sendRedirect(frontEndUrl + "/login/loggedIn");
    }
}
