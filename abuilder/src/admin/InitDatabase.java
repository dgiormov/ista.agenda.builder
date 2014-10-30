package admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import persistency.entities.Code;
import persistency.entities.EnabledFunctionality;
import persistency.entities.LoggedUser;
import persistency.entities.Session;
import persistency.entities.Speaker;
import persistency.exposed.CodesExposed;
import persistency.exposed.EnabledFuncExposed;
import persistency.exposed.LoggedUserExposed;
import persistency.exposed.SessionExposedBasic;
import persistency.exposed.SpeakerExposed;
import persistency.exposed.json.SessionJson;
import utils.SecurityException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import gamification.Score;

/**
 * Servlet implementation class InitDatabase
 */
public class InitDatabase extends HttpServlet {

	private Logger logger = LoggerFactory.getLogger(InitDatabase.class);

	private static final long serialVersionUID = 1L;
	private SpeakerExposed lue;

	private String[] puzzlePieces = new String[]{"a","b","c","d","e","f"};
	private String[] puzzleType = new String[]{"1","2","3","4","5"};

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
				logger.warn("Reread event data");
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
				Session newEvent = ex.findEventById(session.getId()+"");
				if(newEvent != null){
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

	public List<Code> processHandoutCodesFromText(String text0) {
		StringTokenizer st = new StringTokenizer(text0, "\r\n");
		Map<String, CodeHelper> puzzleTypes = initPuzzleTypes();
		List<Code> result = new ArrayList<Code>();
		while (st.hasMoreTokens()) {
			String token = (String) st.nextToken();
			if(token.length() > 0 && token.indexOf(",") != -1){
				String code = token.substring(0, token.indexOf(","));
				String type = token.substring(token.indexOf(",")+1);
				CodeHelper codeHelper = puzzleTypes.get(type);
				Code c = new Code();
				c.setCode(code);
				c.setName(type);
				c.setType(codeHelper.getType());
				c.setGid(codeHelper.getGid());
				c.setPoints(codeHelper.getPoints());
				c.setMaxCodesOfThisType(codeHelper.getMax());
				result.add(c);
				System.out.println(c);
			}
		}
		return result;
	}

	private Map<String, CodeHelper> initPuzzleTypes(){
		Map<String, CodeHelper> puzzleTypesMap = new HashMap<String, CodeHelper>();
		puzzleTypesMap.put(Score.EARLY_BIRD_NAME, new CodeHelper(Code.EARLY_BIRD, Code.EARLY_BIRD, Score.EARLY_BIRD, 1));
		puzzleTypesMap.put(Score.SESSION_QA_NAME, new CodeHelper(Code.SESSION_QA, Code.SESSION_QA, Score.SESSION_QA, 7));
		return puzzleTypesMap;
	}

	private class CodeHelper {

		private int type;
		private int gid;
		private float points;
		private int max;

		public CodeHelper(int type, int gid, float points, int max) {
			this.setMax(max);
			this.setType(type);
			this.setGid(gid);
			this.setPoints(points);
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getGid() {
			return gid;
		}

		public void setGid(int gid) {
			this.gid = gid;
		}

		public float getPoints() {
			return points;
		}

		public void setPoints(float points) {
			this.points = points;
		}

		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}
	}

	public void persistCodes(List<Code> processHandoutCodesFromText) {
		CodesExposed ce = new CodesExposed();
		for (Code code : processHandoutCodesFromText) {
			ce.createEntity(code);
		}
	}
}
