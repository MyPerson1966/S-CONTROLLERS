/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.telescope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.primefaces.event.RowEditEvent;
import pns.kiam.entities.telescopes.Telescope;
import pns.kiam.entities.telescopes.TelescopeHorizontMask;
import pns.kiam.sweb.controllers.AbstractController;
import pns.kiam.sweb.utils.MessageUtils;

/**
 *
 * @author PSEVO tochka
 */
@Stateless
//@Named
public class TelescopeController extends AbstractController implements Serializable {

    @EJB
    private TelescopeMaskController telescopeMaskController;

    private Telescope telescope;
    private List<Telescope> telescopeList = new ArrayList<>();
    private CriteriaQuery<Telescope> cq;

    private String selectedInfo = "";
    private int numbersOfTelescopes = 0;
    private boolean freshUploadComplete;

    @PostConstruct
    public void initial() {
        try {
            abstractInit();
            cq = cb.createQuery(Telescope.class);
            telescopeList = loadAllTelescopes();
            freshUploadComplete = false;
            numbersOfTelescopes = telescopeList.size();
        } catch (NullPointerException e) {
        }
    }

    private List loadAllTelescopes() {

        Root<Telescope> res = cq.from(Telescope.class);
        cq.select(res);

        cq.orderBy(cb.asc(res.get("id")));
        TypedQuery<Telescope> Q = em.createQuery(cq);
        rowDeSelect();
//	(new MessageUtils()).messageGenerator("Total Number of Telescopes  is: " + Q.getResultList().size(), "");
        return Q.getResultList();
    }

    public String getSelectedInfo() {
        return selectedInfo;
    }

    public Telescope getTelescope() {
        return telescope;
    }

    public void setTelescope(Telescope telescope) {
        this.telescope = telescope;
    }

    public List<Telescope> getTelescopeList() {
        return telescopeList;
    }

    public void setTelescopeList(List<Telescope> telescopeList) {
        this.telescopeList = telescopeList;
    }

    public int getNumbersOfTelescopes() {
        return numbersOfTelescopes;
    }

    public void setNumbersOfTelescopes(int numbersOfTelescopes) {
        this.numbersOfTelescopes = numbersOfTelescopes;
    }

    public boolean isFreshUploadComplete() {
        return freshUploadComplete;
    }

    public void setFreshUploadComplete(boolean freshUploadComplete) {
        this.freshUploadComplete = freshUploadComplete;
    }

    /**
     * Row Select action
     */
    public void rowSelect() {
        try {
            System.out.println(" RowSelect ---  TELESCOPE ID " + telescope.getId());
            telescopeMaskController.setTelescope(telescope);
            telescopeMaskController.setSelectedInfo("");
        } catch (NullPointerException e) {
        }
    }

    /**
     * Row Select action by given Telescope
     *
     * @param t
     */
    public void rowSelectAction(Telescope t) {
        telescope = t;

        System.out.println(" -->> rowSelectAction()      " + getClass().getCanonicalName() + " ::: Telescope Selected " + t);

        telescopeMaskController.setTelescope(telescope);
        selectedInfo = " ( Selected " + telescope.getIdentifier() + " telescope) ";
        (new MessageUtils()).messageGenerator("Telescope Selected", telescope.toString());
    }

    /**
     * Deselect the Selected Row
     */
    public void rowDeSelect() {
        telescope = null;
        selectedInfo = "";
        telescopeMaskController.setTelescope(null);
    }

    /**
     * Removes the record(s) from Telescope table. if the "all " parameter is
     * true, removes all records else it is false removes only selected record
     * The represenation data in the lisr remove correspondently
     *
     * @param all
     */
    public void removeRow(boolean all) {
        System.out.println("  Deleting telescope ALL=" + all);
        deleteTelescope(all);
        telescopeList = loadAllTelescopes();
        rowDeSelect();
    }

    public void prepareCreation() {
        telescope = new Telescope();
        telescopeList.add(telescope);

        (new MessageUtils()).messageGenerator("Prepare to Create a new Telescope ", "");
        System.out.println(" Prepare to create a new  telescope " + telescope);
        rowDeSelect();
    }

    /**
     * Editing Table's row
     *
     * @param event
     */
    public void onRowEdit(RowEditEvent event) {
        telescope = (Telescope) event.getObject();
        if (telescope.getId() == null) {
            persist(telescope);
            (new MessageUtils()).messageGenerator("New Telescope Created", ((Telescope) event.getObject()).toString());
        } else {
            merge(telescope);
            (new MessageUtils()).messageGenerator("Telescope Edited Result is:", ((Telescope) event.getObject()).toString());
        }
        telescopeList = loadAllTelescopes();
        rowDeSelect();

    }

    /**
     * Cancelling edit a row. Setting up a selection as null
     *
     * @param event
     */
    public void onRowCancel(RowEditEvent event) {
        telescope = null;
        (new MessageUtils()).messageGenerator("Edit Cancelled ", ((Telescope) event.getObject()).toString());
    }

    /**
     * Fixing a new added telescope list
     */
    public void fixAllAdded() {

        System.out.println(" telescopeList size " + telescopeList.size());
        for (int k = 0; k < telescopeList.size(); k++) {
            Telescope tmp = telescopeList.get(k);

            if (tmp.getId() == null) { // we are ignoring the empty data
                System.out.println(k + " " + tmp + "; " + System.lineSeparator() + tmp.getTelescopeMask());

                if (tmp.getIdentifier() > 0) {// ignoring a non-existing telescope
                    /*
                Persisting a telescope mask of telescope tmp
                     */
                    for (int kChild = 0; kChild < tmp.getTelescopeMask().size(); kChild++) {
                        TelescopeHorizontMask tmpChild = tmp.getTelescopeMask().get(kChild);
                        persist(tmpChild);
                    }

                    try { // persist a telescope
                        persist(tmp);
                        System.out.println(k + ": Done " + tmp);
                    } catch (Exception e) {

                        System.out.println("   e: " + e);
                    }
                }
            }
        }
        initial();
    }

    public void deleteTelescope(long id) {
        Telescope tt = em.find(Telescope.class, id);
        System.out.println("Deleting Telescope: " + tt);
        em.remove(tt);
    }

    public void deleteTelescope() {
        System.out.println("  Removing all telescopes ");
        for (int k = 0; k < telescopeList.size(); k++) {
            Telescope tt = telescopeList.get(k);
            deleteTelescope(tt.getId());
        }
    }

    /**
     * Removes the record(s) from Telescope table. if the "all " parameter is
     * true, removes all records else it is false removes only selected record
     *
     * @param all
     */
    private void deleteTelescope(boolean all) {
        if (all) {
            System.out.println("Remove ALL");
            deleteTelescope();
//            telescopeList.clear();
            telescope = null;

            (new MessageUtils()).messageGenerator("Remove All existing telescopes! ", "");
            return;
        }
        System.out.println("tt " + telescope);
        if (telescope != null) {
            System.out.println("  Remove telescope..." + telescope);
            if (telescope.getId() != null) {
                deleteTelescope(telescope.getId());
                telescopeList.remove(telescope);
                telescope.getTelescopeMask().clear();
                (new MessageUtils()).messageGenerator("Remove telescope ", telescope.toString());
            } else {
                telescopeList.clear();
                initial();
            }
        }

        System.out.println(" TelescopeList Size:  " + telescopeList.size());
    }

}
