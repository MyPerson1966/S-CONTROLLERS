/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.app;

import java.io.Serializable;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import pns.kiam.sweb.controllers.telescope.TelescopeController;
import pns.kiam.sweb.controllers.user.UserController;

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

    @Inject
    private TelescopeController telescopeController;

    @Inject
    private UserController userController;

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

	    try {
		userController = null;
		telescopeController = null;
		session.invalidate();
	    } catch (IllegalStateException e) {
	    }
	    System.out.println("Session Killed at " + endDate);
	}
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public void sessionCreated(HttpSessionEvent se) {
	System.out.println("       TIMEOUT:  " + timeout);

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
