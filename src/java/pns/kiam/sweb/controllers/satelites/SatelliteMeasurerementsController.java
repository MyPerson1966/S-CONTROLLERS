/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.satelites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.scene.chart.PieChart;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import pns.kiam.entities.satellites.SatelliteMeasurement;
import pns.kiam.entities.telescopes.Telescope;
import pns.kiam.sweb.controllers.AbstractController;

/**
 *
 * @author User
 */
@Stateless
public class SatelliteMeasurerementsController extends AbstractController implements Serializable {

    protected CriteriaBuilder cb;

    private SatelliteMeasurement satelliteMeasurement;
    List<SatelliteMeasurement> satelliteMeasurementList = new ArrayList<>();

    private CriteriaQuery<SatelliteMeasurement> cq;

    @PostConstruct
    public void initial() {

        try {
            cb = em.getCriteriaBuilder();
            cq = cb.createQuery(SatelliteMeasurement.class);
            satelliteMeasurementList = loadAllSatMeas();
            System.out.println("");
            infMeasured();
        } catch (NullPointerException e) {
        }
    }

    public void infMeasured() {
        satelliteMeasurementList = loadAllSatMeas();
        System.out.println(satelliteMeasurementList.size());
    }

    private List loadAllSatMeas() {
        System.out.println(" 0  ===============loadAllSatMeas===================");
        Root<SatelliteMeasurement> res = cq.from(SatelliteMeasurement.class);
        cq.select(res);
        System.out.println(" 1  ===============loadAllSatMeas===================");

        cq.orderBy(cb.asc(res.get("id")));
        TypedQuery<SatelliteMeasurement> Q = em.createQuery(cq);
        rowDeselect();
        return Q.getResultList();
    }

    private void rowDeselect() {
        satelliteMeasurement = null;
    }

    public SatelliteMeasurement getSatelliteMeasurement() {
        return satelliteMeasurement;
    }

    public List<SatelliteMeasurement> getSatelliteMeasurementList() {
        return satelliteMeasurementList;
    }
}
