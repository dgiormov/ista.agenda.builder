package exposed;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import util.Constants;
import util.DBUtils;

import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.UnsupportedUserAttributeException;
import com.sap.security.um.user.User;
import com.sap.security.um.user.UserProvider;

import entities.LoggedUser;

public class LoggedUserExposed {
	public EntityManager entityManager = null;

	public LoggedUserExposed() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(LoggedUser e) {
		if (e.gettAccess() != null && e.gettAccess().length() > 1) {
			LoggedUser person = findPersonByTwitterId(e.gettAccess(),
					e.gettSAccess());
			if (person != null && e.getId() != person.getId()) {
				throw new IllegalArgumentException(
						"Person with such twitter account already exists.");
			}
		}
		entityManager.getTransaction().begin();
		entityManager.persist(e);
		entityManager.getTransaction().commit();
	}

	public void deleteEntity(LoggedUser e) {
		entityManager.getTransaction().begin();
		entityManager.remove(e);
		entityManager.getTransaction().commit();
	}

	public void updateEntity(LoggedUser e) {
		createEntity(e);
	}

	public LoggedUser findPersonById(String id) {
		Query namedQuery = entityManager.createNamedQuery("getPersonById");
		namedQuery.setParameter("id", Long.parseLong(id));
		LoggedUser result = null;
		try {
			result = (LoggedUser) namedQuery.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}
	
	public LoggedUser findPersonByMail(String email) {
		Query namedQuery = entityManager.createNamedQuery("getPersonByEmail");
		namedQuery.setParameter("email", email);
		LoggedUser result = null;
		try {
			result = (LoggedUser) namedQuery.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}
	
	public LoggedUser findPersonByUserName(String username) {
		Query namedQuery = entityManager.createNamedQuery("getPersonByUserName");
		namedQuery.setParameter("userName", username);
		LoggedUser result = null;
		try {
			result = (LoggedUser) namedQuery.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}
	
	public LoggedUser findPersonByTwitterId(String id, String sid) {
		if (id == null || sid == null || id.trim().length() < 5
				|| sid.length() < 5) {
			return null;
		}
		Query namedQuery = entityManager
				.createNamedQuery("getPersonByTwiiterId");
		namedQuery.setParameter("tAccess", id);
		namedQuery.setParameter("tSAccess", sid);
		LoggedUser result = null;
		try {
			result = (LoggedUser) namedQuery.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}

	public List<LoggedUser> getAllPersons() {
		List<LoggedUser> result = new ArrayList<LoggedUser>();
		try {
			Query namedQuery = entityManager.createNamedQuery("getAllPersons");
			result = (List<LoggedUser>) namedQuery.getResultList();
		} catch (NoResultException e) {
			result = new ArrayList<LoggedUser>();
		} finally {
		}
		return result;
	}

	public LoggedUser getCurrentUser(HttpServletRequest request) {
		LoggedUser user = getLoggedUser(request);
		if(user != null){
			return user;
		}	
		return getCookieUser(request);
	}

	private LoggedUser getCookieUser(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String id = null;
		if (cookies == null || cookies.length == 0) {
			return null;
		} else {
			for (Cookie cookie : cookies) {
				if (Constants.COOKIE_NAME.equals(cookie.getName())) {
					id = cookie.getValue();
					break;
				}
			}
			if (id == null) {
				return null;
			}
		}
		return findPersonById(id);
	}
	
	private LoggedUser getLoggedUser(HttpServletRequest request) {
		User user;
		try {
			user = getUser(request.getUserPrincipal());
		} catch (PersistenceException e) {
			return null;
		}
		if(user == null){
			return null;
		}
		try {
			return findPersonByMail(user.getAttribute("email"));
		} catch (UnsupportedUserAttributeException e) {
			return null;
		}
	}
	
	private static User getUser(Principal principal)
			throws PersistenceException {
		if(principal == null){
			return null;
		}
		// UserProvider provides access to the user storage
		UserProvider users = UserManagementAccessor.getUserProvider();

		// Read the currently logged in user from the user storage
		User user = users.getUser(principal.getName());
		return user;
	}
}
