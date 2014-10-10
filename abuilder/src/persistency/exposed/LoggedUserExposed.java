package persistency.exposed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import persistency.entities.LoggedUser;
import utils.Constants;
import utils.DBUtils;

public class LoggedUserExposed {
	public EntityManager entityManager = null;

	public LoggedUserExposed() {
		entityManager = DBUtils.getEMF().createEntityManager();
	}

	public void createEntity(LoggedUser e) {
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
		entityManager.getTransaction().begin();
		entityManager.merge(e);
		entityManager.getTransaction().commit();
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
//			user = getUser(request.getUserPrincipal());
			Object attribute = request.getSession().getAttribute("uid");
			if(attribute == null){
				return null;
			}
			return findPersonById(attribute.toString());
	}
	
	public LoggedUser findPersonByOpenId(String openId) {
		Query namedQuery = entityManager.createNamedQuery("getPersonByOpenId");
		namedQuery.setParameter("openId", openId);
		LoggedUser result = null;
		try {
			result = (LoggedUser) namedQuery.getSingleResult();
		} catch (NoResultException e) {
			result = null;
		}
		return result;
	}
	
	public String assebleOpenId(String id, String provider){
		return id+"@"+provider;
	}

	public LoggedUser createNewUser(String application, UserInfoJson userInfo) {
		LoggedUser lu = new LoggedUser();
		String openId = assebleOpenId(userInfo.getEmail(), application);
		//Some IDP require additional request to get the email, so we have to absolutely sure that the person is not registered 
		LoggedUser findPersonByOpenId = findPersonByOpenId(openId);
		if(findPersonByOpenId != null){
			return findPersonByOpenId;
		}
		lu.setOpenId(openId);
		if(userInfo != null){
			lu.setName(userInfo.getName());
		}
		createEntity(lu);
		return lu;
	}
	
	public class LinkedInUserInfoJson {
		private String firstName;
		private String lastName;
		private String headline;
		public String getFirstName() {
			return firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public String getHeadline() {
			return headline;
		}
	}
	
	public class FacebookUserInfoJson {
		private String name;
		private String email;

		private String firstName;
		private String lastName;
		
		public String getFirstName() {
			return firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public String getName() {
			return name;
		}
		public String getEmail() {
			return email;
		}
		
	}
}
