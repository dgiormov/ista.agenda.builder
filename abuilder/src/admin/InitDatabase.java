package admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import persistency.entities.EnabledFunctionality;
import persistency.entities.LoggedUser;
import persistency.entities.PointsCategory;
import persistency.entities.PointsInstance;
import persistency.entities.Session;
import persistency.entities.Speaker;
import persistency.exposed.EnabledFuncExposed;
import persistency.exposed.LoggedUserExposed;
import persistency.exposed.PointsCategoryExposed;
import persistency.exposed.PointsExposed;
import persistency.exposed.SessionExposedBasic;
import persistency.exposed.SpeakerExposed;
import persistency.exposed.json.SessionJson;
import utils.CodeGenerator;
import utils.SecurityException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class InitDatabase
 */
public class InitDatabase extends HttpServlet {

	private Logger logger = LoggerFactory.getLogger(InitDatabase.class);

	private static final long serialVersionUID = 1L;
	private SpeakerExposed lue;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			securityCheck(request, response);

			logger.warn("Init Database in progress...");
			if (request.getParameter("init") != null) {
				logger.warn("Full Init");
				initSessions(request, response);
				initReadOnlyMode(request);
			} else if (request.getParameter("reset") != null) {
				logger.warn("ratings reset");
				response.getWriter().print("Ratings reset...");
				LoggedUserExposed lue = new LoggedUserExposed();
				List<LoggedUser> allPersons = lue.getAllPersons();
				for (LoggedUser person : allPersons) {
					person.getSessionRatings().clear();
					person.getSpeakerRatings().clear();
					lue.updateEntity(person);
				}
				response.getWriter().print("[FAILED]");
			} else if (request.getParameter("reread") != null) {
				logger.warn("Reread session data");
				mergeInfo(request, response);
			} else if (request.getParameter("init_codes") != null) {
				logger.warn("reinit codes");
				response.getWriter().print("Codes initializing...");
//				initPuzzleCodes(request);
//				initHandoutCodes(request);
				response.getWriter().print("[OK]");
			}
		} catch (SecurityException e1) {
			logger.warn("Failed access to InitDatabase by " + e1.getMessage());
		}
	}

	private void securityCheck(HttpServletRequest request,
			HttpServletResponse response) throws SecurityException {
		// TODO Auto-generated method stub
		
	}

	private void initReadOnlyMode(HttpServletRequest request) {
		EnabledFunctionality e = new EnabledFunctionality();
		e.setUserName("admin");
		e.setEnabled(false);
		//FIXME set to true before deploying the real application
		EnabledFuncExposed ee = new EnabledFuncExposed();
		ee.createEntity(e);
	}

	private void mergeInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().print("Rereading Database initializing...");
		lue = SpeakerExposed.getInstance();
		try {
			parseSessions(request, true);
		} catch (IOException e) {
			throw new ServletException(e);
		} catch (ParseException e) {
			throw new ServletException(e);
		}
		response.getWriter().print("[OK]");
	}

	private void initSessions(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		response.getWriter().print("Database initializing...");
		lue = SpeakerExposed.getInstance();
		try {
			parseSessions(request, false);
		} catch (IOException e) {
			throw new ServletException(e);
		} catch (ParseException e) {
			throw new ServletException(e);
		}
		response.getWriter().println("[OK]");
	}

	private void createSpeakers(List<Speaker> ps) {
		for (Speaker speaker : ps) {
			if(speaker != null){
				System.out.println(speaker.toString());
				lue.createEntity(speaker);
			} else {
				System.out.println("Speaker is null");
			}
				
		}
	}

	public List<Session> parseSessions(HttpServletRequest request, boolean merge)
			throws IOException, ParseException {
		SessionExposedBasic ex = new SessionExposedBasic();

		Gson g = new Gson();
		final BufferedReader readerSpeakers = new BufferedReader(new InputStreamReader(request.getServletContext()
				.getResourceAsStream("speakers.json")));
		final BufferedReader readerSessions = new BufferedReader(new InputStreamReader(request.getServletContext()
				.getResourceAsStream("sessions.json")));

		List<Speaker> speakers = g.fromJson(readerSpeakers, new TypeToken<List<Speaker>>(){}.getType());
		createSpeakers(speakers);
		List<SessionJson> sessions = g.fromJson(readerSessions, new TypeToken<List<SessionJson>>(){}.getType());
		List<Session> result = toSessionEntities(sessions, speakers);
		for (Session session : result) {
			if(merge){
				Session newSession = ex.findSessionById(session.getId()+"");
				if(newSession != null){
					ex.updateEntity(session);	
				} else {
					ex.createEntity(session);
				}
			} else {
				ex.createEntity(session);
			}
		}
		return result;
	}

	private List<Session> toSessionEntities(List<SessionJson> sessions, List<Speaker> speakers) throws ParseException {
		List<Session> result = new ArrayList<Session>();
		for (SessionJson session : sessions) {
			result.add(session.toEntity());
		}
		return result;
	}

	public void initCodes(HttpServletRequest request) {
		final BufferedReader pointTypes = new BufferedReader(new InputStreamReader(request.getServletContext()
				.getResourceAsStream("gamification.json")));
		Gson g = new Gson();
		List<PointsCategory> pointCategories = g.fromJson(pointTypes, new TypeToken<List<PointsCategory>>(){}.getType());
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsExposed pe = new PointsExposed();
		List<PointsInstance> allCodes = pe.allCodes();
		for (PointsCategory category : pointCategories) {
			if(!category.isSelfGeneratingInstances()){
				int max = category.getMaxNumberOfInstances();
				List<String> codePossitions = category.getCompositeCodePossitions();
				List<PointsInstance> pi;
				if(codePossitions != null && codePossitions.size()>0){
					 pi = CodeGenerator.generateCompsiteCodes(max, category.getCodeLength(), codePossitions,  allCodes, category);
				} else {
					pi = CodeGenerator.generateCodes(max, category.getCodeLength(), allCodes, category);
				}
				pe.persistEntities(pi);
				allCodes.addAll(pi);
			}
			
			pce.createEntity(category);
		}
	}
}
