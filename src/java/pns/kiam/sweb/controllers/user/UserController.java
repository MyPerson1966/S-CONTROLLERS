/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.user;

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
import org.primefaces.event.SelectEvent;
import pns.kiam.entities.telescopes.Telescope;
import pns.kiam.entities.users.User;
import pns.kiam.entities.users.UserType;
import pns.kiam.sweb.controllers.AbstractController;
import pns.kiam.sweb.utils.MessageUtils;

/**
 *
 * @author PSEVO tochka
 */
@Stateless
public class UserController extends AbstractController implements Serializable {

    @EJB
    private UserTypeController userTypeController;

    private User user;
    private List<User> userList = new ArrayList<>();
    private List<Telescope> telescopeUserList = new ArrayList<>();
    private CriteriaQuery<User> cq;

    private String selectedInfo = "";
    private int numbersOfTelescopes = 0;
    private boolean freshUploadComplete;

    @PostConstruct
    public void initial() {
	try {
	    abstractInit();
	    cq = cb.createQuery(User.class);
	    cq = cb.createQuery(User.class);
	    userList = loadAllUsers();
//	    freshUploadComplete = false;
//	    numbersOfTelescopes = telescopeList.size();
	} catch (NullPointerException e) {
	}
    }

    private List loadAllUsers() {

	Root<User> res = cq.from(User.class);
	cq.select(res);

	cq.orderBy(cb.asc(res.get("id")));
	TypedQuery<User> Q = em.createQuery(cq);
	rowDeSelect();
//	(new MessageUtils()).messageGenerator("Total Number of Telescopes  is: " + Q.getResultList().size(), "");
	return Q.getResultList();
    }

    public String getSelectedInfo() {
	return selectedInfo;
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public List<User> getUserList() {
	return userList;
    }

    public void setUserList(List<User> userList) {
	this.userList = userList;
    }

    public List<Telescope> getTelescopeUserList() {
	return telescopeUserList;
    }

    public void setTelescopeUserList(List<Telescope> telescopeUserList) {
	this.telescopeUserList = telescopeUserList;
    }

    /**
     * Row Select action
     */
    public void rowSelect(SelectEvent event) {
	user = (User) event.getObject();
	System.out.println("   " + user);
//	try {
//	    System.out.println(" RowSelect ---  TELESCOPE ID " + telescope.getId());
//	    telescopeMaskController.setTelescope(telescope);
//	    telescopeMaskController.setSelectedInfo("");
//	} catch (NullPointerException e) {
//	}
    }

    public void addTelescopeToList(Telescope t) {
	System.out.println("       " + t);
//	if (!telescopeUserList.contains(t)) {
	telescopeUserList.add(t);
//	}
    }

    public void removeTelescopeFromList(Telescope t) {
	if (telescopeUserList.contains(t)) {
	    telescopeUserList.remove(t);
	}

    }

    /**
     * Row Select action
     */
    /**
     * Row Select action by given Telescope
     *
     * @param t
     */
    public void rowSelectAction(User u) {
	user = u;

    }

    /**
     * Deselect the Selected Row
     */
    public void rowDeSelect() {
	user = null;
	selectedInfo = "";

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
	deleteUser(all);
	userList = loadAllUsers();
	rowDeSelect();
    }

    public void prepareCreation() {
	User u = new User();
	userList.add(u);

	(new MessageUtils()).messageGenerator("Prepare to Create a new User", "");
	System.out.println(" Prepare to create a new  user " + u);
//	rowDeSelect();

    }

    /**
     * Editing Table's row
     *
     * @param event
     */
    public void onRowEdit(RowEditEvent event) {

	user = (User) event.getObject();

//        System.out.println("   New User  " + user + "  saved  " + System.lineSeparator()
//                + "  user.getUserType().getName() " + user.getUserType().getName() + ""
//                + "  user.getUserType().getBinRights() " + user.getUserType().getBinRights() + ""
//                + " user.getUserType().getId() " + user.getUserType().getId() + ""
//                + "     user.getUserTelescopeList().size()  " + user.getUserTelescopeList().size()
//        );
	if (user.getId() == null) {
	    UserType ut = userTypeController.seachForType(user.getUserType().getId());
	    System.out.println("     ut: " + ut);
	    user.setUserType(ut);
	    persistOrMergeUsersTelescopes();
	    persist(user);
	    (new MessageUtils()).messageGenerator("New User Created", ((User) event.getObject()).toString());
	} else {

	    persistOrMergeUsersTelescopes();
	    merge(user);
//	    (new MessageUtils()).messageGenerator("Telescope Edited Result is:", ((Telescope) event.getObject()).toString());
	}
	rowDeSelect();
	userList.clear();
	userList = loadAllUsers();

    }

    /**
     * Cancelling edit a row. Setting up a selection as null
     *
     * @param event
     */
    public void onRowCancel(RowEditEvent event) {
	user = null;
	(new MessageUtils()).messageGenerator("Edit Cancelled ", ((User) event.getObject()).toString());
    }

    /**
     * Fixing a new added user list if we've added the set of users
     */
    public void fixAllAdded() {

	System.out.println(" userList size " + userList.size());
	for (int k = 0; k < userList.size(); k++) {
	    User tmp = userList.get(k);

	    if (tmp.getId() != null) { // we are ignoring the empty data
		System.out.println(k + " " + tmp + "; " + System.lineSeparator());

		try { // persist a telescope
		    persist(tmp);
		    System.out.println(k + ": Done " + tmp);
		} catch (Exception e) {

		    System.out.println("   e: " + e);
		}
	    }
	}
	initial();
    }

    public void deleteUser(long id) {
	User uu = em.find(User.class, id);
	System.out.println("Deleting User: " + uu);
	em.remove(uu);
    }

    public void deleteUser() {
	System.out.println("  Removing all telescopes ");
	for (int k = 0; k < userList.size(); k++) {
	    User uu = userList.get(k);
	    deleteUser(uu.getId());
	}
    }

    private void persistOrMergeUsersTelescopes() {
	if (user != null) {
	    for (int k = 0; k < user.getUserTelescopeList().size(); k++) {
		Telescope tmp = user.getUserTelescopeList().get(k);
		System.out.println(k + "   " + user.getUserTelescopeList().get(k).getClass().getName() + "       " + user.getUserTelescopeList().get(k));
		merge(tmp);
	    }
	}
    }

    /**
     * Removes the record(s) from Telescope table. if the "all " parameter is
     * true, removes all records else it is false removes only selected record
     *
     * @param all
     */
    private void deleteUser(boolean all) {
	if (all) {
	    System.out.println("Remove ALL");
	    deleteUser();
//           telescopeList.clear();
	    user = null;

	    (new MessageUtils()).messageGenerator("Remove All existing users! ", "");
	    return;
	}
	//System.out.println("tt " + user);
	if (user != null) {
	    System.out.println("  Remove user..." + user);
	    if (user.getId() != null) {
		deleteUser(user.getId());
		userList.remove(user);
		(new MessageUtils()).messageGenerator("Remove telescope ", user.toString());
	    } else {
		userList.clear();
		initial();
	    }
	}

	System.out.println(" TelescopeList Size:  " + userList.size());
    }

}
