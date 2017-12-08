/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.satelites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import pns.kiam.filecontrol.FileMeasuredController;
import pns.kiam.sweb.controllers.AbstractController;

/**
 *
 * @author User
 */
@Stateless
@LocalBean
public class FileArchivManager extends AbstractController implements Serializable {

    protected CriteriaBuilder cb;
    private FileMeasuredController fileMeasured;
    List<FileMeasuredController> fileMeasuredList = new ArrayList<>();

    private CriteriaQuery<FileMeasuredController> cq;

    @PostConstruct
    public void initial() {

        try {
            cbTM = em.getCriteriaBuilder();
            cq = cbTM.createQuery(FileMeasuredController.class);
            fileMeasuredList = loadAllSatMeas();

        } catch (NullPointerException e) {
        }
    }

    private List loadAllSatMeas() {
        System.out.println(" 0  ===============loadAllSatMeas===================");
        Root<FileMeasuredController> res = cq.from(FileMeasuredController.class);
        cq.select(res);
        System.out.println(" 1  ===============loadAllSatMeas===================");

        cq.orderBy(cb.asc(res.get("id")));
        TypedQuery<FileMeasuredController> Q = em.createQuery(cq);
        rowDeselect();
        return Q.getResultList();
    }

    private void rowDeselect() {

    }

}
