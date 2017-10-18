/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.app;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author User
 */
@Stateful
@LocalBean
public class SsessionControl implements HttpSessionListener, Serializable {

    private int timeout = -1;

    private FacesContext fContext;
    private HttpSession session;

    public void init() {

        fContext = FacesContext.getCurrentInstance();
        session = (HttpSession) fContext.getExternalContext().getSession(true);
        System.out.println("Session Created");
    }

    public int getTimeout() {
        return timeout;
    }

    public HttpSession getSession() {
        return session;
    }

    public void setTimeout(int time) {
        if (time == -1) {
            time = Integer.MAX_VALUE;
        }
        timeout = time;
        session.setMaxInactiveInterval(time);
        System.out.println(" session is null " + (session == null));
        System.out.println("Session: " + session.getId() + "   " + session.getCreationTime());
    }

    public void sessionDestroy() {
        Date endDate = new Date();
        if (session != null) {

            session.invalidate();
            System.out.println("Session Killed at " + endDate);
        }
    }

    public void sessionDown() {
//        FacesContext fContext = FacesContext.getCurrentInstance();
//        HttpSession session = (HttpSession) fContext.getExternalContext().getSession(true);

        long currTM = System.currentTimeMillis() / 1000;
        long sessTM = currTM - session.getCreationTime() / 1000;
        long accsTM = currTM - session.getLastAccessedTime() / 1000;
        long freeTM = session.getMaxInactiveInterval() - accsTM;
//        double frac = (session.getLastAccessedTime() - session.getCreationTime()) / session.getMaxInactiveInterval();
        System.out.println("   =========>>>session creationTime: " + (session.getCreationTime() / 1000) + "  "
                + " ; sessTM  " + sessTM + ""
                + " accsTM " + accsTM + ""
                + "  freeTM  " + freeTM
        );

        if (freeTM < 10) {
            System.out.println(freeTM + " ---------------  " + (session.getMaxInactiveInterval() - 10));
        }
        if (freeTM <= 5) {
            session.invalidate();
        }

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public void sessionCreated(HttpSessionEvent se) {

        Date startDate = new Date();
        Date endDate = new Date(System.currentTimeMillis() + timeout);
        System.out.println("Create a HTTP Session at " + startDate + "  to " + endDate);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("   SessionStopped!" + new Date());
//        try {
//            FacesContext.getCurrentInstance().getExternalContext().redirect("/index.xhtml");
//        } catch (IOException ex) {
//            Logger.getLogger(SsessionControl.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
