/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import org.primefaces.context.RequestContext;

/**
 *
 * @author User
 */
@Stateless
@LocalBean
public class SsessionControl implements HttpSessionListener, Serializable {

    private int timeout = -1;

    private HttpSession session;

    private boolean needToDelogin = false;

    public boolean isNeedToDelogin() {
        return needToDelogin;
    }

    public void setNeedToDelogin(boolean needToDelogin) {
        this.needToDelogin = needToDelogin;
    }

    private boolean active = false;

    /**
     * Get the value of active
     *
     * @return the value of active
     */
    public boolean isActive() {
        return active;
    }

    public void init() {
        FacesContext fContext = FacesContext.getCurrentInstance();
        session = (HttpSession) fContext.getExternalContext().getSession(true);
        active = true;
    }

    public int getTimeout() {
        return timeout;
    }

    public HttpSession getSession() {
        return session;
    }

    public void setTimeout(int time) {
        if (time < 0) {
            time = Integer.MAX_VALUE;
        }
        timeout = time * 1000;
        session.setMaxInactiveInterval(time);
        System.out.println("    ->> session Timeout result: " + sessionLiveTo() + "  Timeout is: " + session.getMaxInactiveInterval());
        active = true;
    }

    public void setTimeout(int time, boolean inMillisec) {

        if (!inMillisec) {
            time = time / 100;
        }
        setTimeout(time);
    }

    public void sessionDestroy() {
        Date endDate = new Date();
        if (session != null) {

            session.invalidate();
            System.out.println("Session Killed at " + endDate);
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        if (timeout < 0) {
            timeout = Integer.MAX_VALUE;
        }
        session = se.getSession();
        session.setMaxInactiveInterval(timeout);
        System.out.println("Session Start result: " + sessionLiveTo() + " sessID " + session.getId() + "   " + active);

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        active = false;
        session = null;

//
        System.out.println("   Session  Stopped!    " + new Date() + ""
                + "  se: " + se.getSession().getId()
                + "  session " + (session == null) + "       active:  " + active);

    }

    public String sessionLiveTo() {
        Date startDate = new Date();
        Date endDate = new Date(System.currentTimeMillis() + timeout);
        String res = " HTTP Session starts at " + startDate + "  finises at  " + endDate;
        return res;
    }

}
