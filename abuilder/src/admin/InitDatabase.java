package admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import persistency.entities.Comment;
import persistency.entities.LoggedUser;
import persistency.entities.Session;
import persistency.entities.Speaker;
import persistency.entities.admin.EnabledFunctionality;
import persistency.entities.feedback.Question;
import persistency.entities.gamification.PointsCategory;
import persistency.entities.gamification.PointsInstance;
import persistency.entities.gamification.Rank;
import persistency.exposed.CommentsExposedBasic;
import persistency.exposed.EnabledFuncExposed;
import persistency.exposed.LoggedUserExposed;
import persistency.exposed.PointsCategoryExposed;
import persistency.exposed.PointsExposed;
import persistency.exposed.RanksExposed;
import persistency.exposed.SessionExposedBasic;
import persistency.exposed.SpeakerExposed;
import persistency.exposed.feedback.QuestionExposed;
import persistency.exposed.json.RankJson;
import persistency.exposed.json.SessionJson;
import service.rest.wrappers.SessionBasic;
import utils.CodeGenerator;
import utils.SecurityException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class InitDatabase
 */
public class InitDatabase extends HttpServlet {

	private static final String DATA_LOCATION = "configuration/";
	
	private static final String SESSIONS_JSON_LOCATION = DATA_LOCATION+"sessions.json";
	private static final String SPEAKERS_JSON_LOCATION = DATA_LOCATION+"speakers.json";
	private static final String QUESTIONS_JSON_LOCATION = DATA_LOCATION+"questions.json";
	private static final String RANKS_JSON_LOCATION = DATA_LOCATION+"ranks.json";
	private static final String POINTS_CATEGORIES_LOCATION = DATA_LOCATION+"points_categories.json";

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
				initQuestions(request, false);
				initCodes(request);
			} else if (request.getParameter("reset_ratings") != null) {
				logger.warn("ratings reset");
				response.getWriter().print("Ratings reset...");
				LoggedUserExposed lue = new LoggedUserExposed();
				List<LoggedUser> allPersons = lue.getAllPersons();
				for (LoggedUser person : allPersons) {
					person.getSessionRatings().clear();
					person.getSpeakerRatings().clear();
					person.getPointsInstances().clear();
					lue.updateEntity(person);
				}
				response.getWriter().print("[FAILED]");
			} else if (request.getParameter("full_reset") != null) {
				logger.warn("ratings reset");
				response.getWriter().print("Master Reset...");
				
				LoggedUserExposed lue = new LoggedUserExposed();
				List<LoggedUser> allPersons = lue.getAllPersons();
				for (LoggedUser person : allPersons) {
					person.getSessionRatings().clear();
					person.getSpeakerRatings().clear();
					person.getLikedComments().clear();
					person.getPointsInstances().clear();
					lue.updateEntity(person);
				}
				clearPointsInstances();
				response.getWriter().print("[OK]");
			} else if (request.getParameter("reread") != null) {
				logger.warn("Reread session data");
				mergeInfo(request, response);
			} else if (request.getParameter("reread_point_categs") != null) {
				logger.warn("reinit codes");
				response.getWriter().print("Codes initializing...");
				initGamificationCategories(request, true);
//				initPuzzleCodes(request);
//				initHandoutCodes(request);
				response.getWriter().print("[OK]");
			} else if(request.getParameter("ranks") != null){
				response.getWriter().print("Ranks initializing...");
				initGamificationRanks(request, true);
				response.getWriter().print("[OK]");
			} else if(request.getParameter("questions") != null){
				initQuestions(request, true);
			}
		} catch (SecurityException e1) {
			logger.warn("Failed access to InitDatabase by " + e1.getMessage());
		}
	}
	
	private void clearPointsInstances() {
		PointsCategoryExposed pce = new PointsCategoryExposed();
		List<PointsCategory> list = pce.allCodes();
		for (PointsCategory pointsCategory : list) {
			if(pointsCategory.isSelfGeneratingInstances()){
				pointsCategory.getInstancesOfThisType().clear();
				pce.updateEntity(pointsCategory);
			}
		}
		PointsExposed pe = new PointsExposed();
		List<PointsInstance> allCodes = pe.allCodes();
		for (PointsInstance pointsInstance : allCodes) {
			if(pointsInstance.getCategory().isSelfGeneratingInstances()){
				pe.deleteEntity(pointsInstance);
			}
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
				.getResourceAsStream(SPEAKERS_JSON_LOCATION)));
		final BufferedReader readerSessions = new BufferedReader(new InputStreamReader(request.getServletContext()
				.getResourceAsStream(SESSIONS_JSON_LOCATION)));

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
		initGamificationCategories(request, false);
		initSecretWords();
		initGamificationRanks(request, false);
	}

	private void initGamificationRanks(HttpServletRequest request, boolean merge) {
		final BufferedReader rankTypes = new BufferedReader(new InputStreamReader(request.getServletContext()
				.getResourceAsStream(RANKS_JSON_LOCATION)));
		Gson g = new Gson();
		List<RankJson> ranks = g.fromJson(rankTypes, new TypeToken<List<RankJson>>(){}.getType());
		RanksExposed re = new RanksExposed();
		for (RankJson rankJson : ranks) {
			Rank entity = rankJson.toEntity();
			if(re.findRankById(entity.getRankPos()) != null){
				re.updateEntity(entity);	
			} else {
				re.createEntity(entity);
			}
		}
	}

	private void initGamificationCategories(HttpServletRequest request, boolean merge) {
		final BufferedReader pointTypes = new BufferedReader(new InputStreamReader(request.getServletContext()
				.getResourceAsStream(POINTS_CATEGORIES_LOCATION)));
		Gson g = new Gson();
		List<PointsCategory> pointCategories = g.fromJson(pointTypes, new TypeToken<List<PointsCategory>>(){}.getType());
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsExposed pe = new PointsExposed();
		List<PointsInstance> allCodes = pe.allCodes();
		for (PointsCategory category : pointCategories) {
			PointsCategory categoryDb = pce.findCategoryByShortName(category.getShortid());
			if(categoryDb == null) {
				pce.createEntity(category);
			} else {
				if(merge){
					updateCategory(category, categoryDb, pce);
					category = categoryDb;
				}
			}
			int size = category.getInstancesOfThisType() == null ? 0 : category.getInstancesOfThisType().size();
			if(category.isCodeCategory() && size < category.getMaxNumberOfInstances()){
				int max = category.getMaxNumberOfInstances();
				List<String> codePossitions = category.getCompositeCodePossitions();
				List<PointsInstance> pi;
				if(codePossitions != null && codePossitions.size()>0){
					pi = CodeGenerator.generateCompsiteCodes(max, codePossitions,  allCodes, category);
				} else {
					pi = CodeGenerator.generateCodes(max, allCodes, category);
				}
				System.out.println(pi);
				pe.persistEntities(pi);
				category.addInstances(pi);
				pce.updateEntity(category);
				allCodes.addAll(pi);
			}
		}
	}

	private void updateCategory(PointsCategory categoryJson,
			PointsCategory categoryDb, PointsCategoryExposed pce) {
		categoryDb.setAreCompositeCodes(categoryJson.areCompositeCodes());
		categoryDb.setCodeLength(categoryJson.getCodeLength());
		categoryDb.setCompositeCodePossitions(categoryJson.getCompositeCodePossitions());
		categoryDb.setDescription(categoryJson.getDescription());
		categoryDb.setMaxInstacesPerPerson(categoryJson.getMaxInstacesPerPerson());
		categoryDb.setMaxNumberOfInstances(categoryJson.getMaxNumberOfInstances());
		categoryDb.setName(categoryJson.getName());
		categoryDb.setPlayerPositionAbove(categoryJson.getPlayerPositionAbove());
		categoryDb.setPoints(categoryJson.getPoints());
		categoryDb.setRank(categoryJson.getRank());
		categoryDb.setRequiresPlayerLevel(categoryJson.getRequiresPlayerLevel());
		categoryDb.setReusable(categoryJson.isReusable());
		categoryDb.setSelfGeneratingInstances(categoryJson.isSelfGeneratingInstances());
		categoryDb.setShortid(categoryJson.getShortid());
		pce.updateEntity(categoryDb);
	}

	private void initSecretWords() {
		SessionExposedBasic seb = new SessionExposedBasic();
		PointsCategoryExposed pce = new PointsCategoryExposed();
		List<Session> allSessions = seb.allEntitiesRaw();
		
		for (Session session : allSessions) {
			if(session.getSecretWord() != null && session.getSecretWord().length() > 0){
				PointsCategory pc = new PointsCategory();
				pc.setName("Secret word for Session: "+session.getName());
				pc.setDescription("Secret word that has been guesed");
				pc.setMaxInstacesPerPerson(1);
				pc.setAreCompositeCodes(false);
				pc.setCodeLength(-1);
				pc.setSelfGeneratingInstances(true);
				pc.setPoints(10);
				pce.createEntity(pc);
			}
		}
	}
	
	private void initQuestions(HttpServletRequest request, boolean merge){
		Gson g = new Gson();
		final BufferedReader readerQuestions = new BufferedReader(new InputStreamReader(request.getServletContext()
				.getResourceAsStream(QUESTIONS_JSON_LOCATION)));

		List<Question> questions = g.fromJson(readerQuestions, new TypeToken<List<Question>>(){}.getType());
		QuestionExposed qe = new QuestionExposed();
		for (Question question : questions) {
			if(merge){
				Question q = qe.findQuestionById(question.getId());
				if(q != null){
					qe.updateEntity(q);
					continue;
				} 
			}
			qe.createEntity(question);
		}
	}
}
