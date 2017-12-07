/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.timers;

import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import pns.kiam.sweb.contentparsers.ResParser;

/**
 *
 * @author User
 */
@Stateless
public class AdderMeasured {

    @EJB
    private ResParser resParser;

    @Schedule(dayOfWeek = "*", month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "*", second = "*/10", persistent = true)
    public void myTimer() {

        System.out.println(this.getClass().getCanonicalName() + " -- Timer event: " + new Date());
//        System.out.println( fileMeasuredController.);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
