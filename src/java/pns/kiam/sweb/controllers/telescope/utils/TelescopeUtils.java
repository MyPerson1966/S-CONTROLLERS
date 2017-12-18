/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.telescope.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import pns.kiam.entities.telescopes.Telescope;
import pns.kiam.entities.telescopes.TelescopeHorizontMask;

/**
 *
 * @author PSEVO tochka
 */
public class TelescopeUtils {

    /**
     * Generates an double array with a given min and max values and a fixed
     * delta=step between any neighbors
     *
     * @param minVal
     * @param maxVal
     * @param step
     * @return
     */
    public static double[] generateDoubleArray(double minVal, double maxVal, double step) {
        int dim = 1;
        if (step > 0) {
            dim = (int) (Math.abs(minVal - maxVal) / step) + 1;
        }
        double[] res = new double[dim];
        res[0] = minVal;
        for (int k = 1; k < dim; k++) {
            res[k] = res[k - 1] + step;
        }
        return res;
    }

    public static String genRandomBigInt() {
        return ((int) (Integer.MAX_VALUE * Math.random()) / 3 + "" + (int) (Integer.MAX_VALUE * Math.random()) / 3);
    }

    public static void deAttachTelescopeMasks(Telescope t, EntityManager em) {
        for (int k = 0; k < t.getTelescopeMask().size(); k++) {
            if (em.contains(t.getTelescopeMask().get(k))) {
                em.detach(t.getTelescopeMask().get(k));
            }
        }
    }

    public static boolean maskInUse(Telescope t, double angle, double horizont) {
        for (TelescopeHorizontMask mask : t.getTelescopeMask()) {
            boolean a = (angle == mask.getAngle());
            boolean h = (horizont == mask.getHorizont());
            if (a && h) {
                return true;
            }
        }

        return false;
    }

    /**
     * Generates a message on actions
     *
     * @param mainMSG
     * @param detailMSG
     */
    public static void messageGenerator(String mainMSG, String detailMSG) {

        FacesMessage msg = new FacesMessage(mainMSG, detailMSG);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
