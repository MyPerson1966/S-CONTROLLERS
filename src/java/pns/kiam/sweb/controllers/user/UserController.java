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
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pns.kiam.entities.telescopes.Telescope;
import pns.kiam.entities.users.User;
import pns.kiam.entities.users.UserType;
import pns.kiam.sweb.controllers.AbstractController;
import pns.kiam.sweb.utils.MessageUtils;

/**
 *
 * @author PSEVO tochka
 */
//@SessionScoped
@Stateless
public class UserController extends AbstractController implements Serializable {

    private List<Telescope> telescopeList = new ArrayList<>();

    private User user;
    private List<User> userList;

    private CriteriaQuery<User> cq;

    private String login = "", passw = "";

    @Size(max = 1054)
    private String comment = "";

    @NotNull
    private UserType userType;

    private boolean active = true;

    @PostConstruct
    public void init() {
	login = passw = "";
	userType = new UserType();
	if (telescopeList == null) {
	    telescopeList = new ArrayList<>();
	}
	try {
	    cb = em.getCriteriaBuilder();
	    cq = cb.createQuery(User.class);
	    userList = loadAllUsers();
	    generatePW(true);
	} catch (NullPointerException e) {
	}

    }

    private List loadAllUsers() {

	Root<User> res = cq.from(User.class);
	cq.select(res);
	cq.orderBy(cb.asc(res.get("id")));
	TypedQuery<User> Q = em.createQuery(cq);
	System.out.println(" " + Q.getResultList().size());

	(new MessageUtils()).messageGenerator(" User Type Number is: " + Q.getResultList().size(), "");
	return Q.getResultList();
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

    public String getLogin() {
	return login;
    }

    public void setLogin(String login) {
	this.login = login;
    }

    public String getPassw() {
	return passw;
    }

    public void setPassw(String passw) {
	this.passw = passw;
    }

    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    public boolean isActive() {
	return active;
    }

    public void setActive(boolean active) {
	this.active = active;
    }

    public UserType getUserType() {
	return userType;
    }

    public void setUserType(UserType userType) {
	this.userType = userType;
    }

    public List<Telescope> getTelescopeList() {
	return telescopeList;
    }

    public void setTelescopeList(List<Telescope> telescopeList) {
	this.telescopeList = telescopeList;
    }

    /**
     * generate a new password for a user
     *
     * @param random if true , than the new pw is not empty
     */
    public void generatePW(boolean random) {
	if (random) {
	    int k = pns.utils.numbers.RInts.rndInt(10, 14);
	    for (int i = 0; i < 10; i++) {
		passw += pns.utils.strings.RStrings.rndLetterString();
	    }
	    passw = passw.substring(0, k);
	} else {
	    passw = "";
	}
	//System.out.println("  passw " + passw);
    }

    public String validateUser() {
	System.out.println("userList.size() == 0 " + (userList.size() == 0));
	if (userList.size() == 0) {
	    return "/users/usercreate";
	}
	if (userExists()) {
	    return "/users/userdata";
	} else {
	    return "index";
	}
    }

    /**
     * tests is a user exist or not
     *
     * @return
     */
    private boolean userExists() {
	if (userList.size() == 0) {
	    return false;
	}
	int npp = 0;
	for (int k = 0; k < userList.size(); k++) {
	    User tmp = userList.get(k);
	    if (login.equals(tmp.getEmail()) && passw.equals(tmp.getPassword())) {
		npp++;
	    }
	}
	return npp == 1;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
