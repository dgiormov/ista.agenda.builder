package admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.SecurityException;
import entities.Code;
import entities.EnabledFunctionality;
import entities.Session;
import entities.Speaker;
import entities.LoggedUser;
import exposed.CodesExposed;
import exposed.EnabledFuncExposed;
import exposed.SessionExposedBasic;
import exposed.SpeakerExposed;
import exposed.LoggedUserExposed;
import game.Score;

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
				initEvents(request, response);
//				initPuzzleCodes(request);
//				initHandoutCodes(request);
				initReadOnlyMode(request);
			} else if (request.getParameter("reset") != null) {
				logger.warn("ratings reset");
				response.getWriter().print("Ratings reset...");
				//			PersonExposed pe = new PersonExposed();
				//			List<Person> allPersons = pe.getAllPersons();
				//			for (Person person : allPersons) {
				//				person.getEventRatings().clear();
				//				pe.updateEntity(person);
				//			}
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
		e.setEnabled(true);
		EnabledFuncExposed ee = new EnabledFuncExposed();
		ee.createEntity(e);
	}

	private void mergeInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().print("Rereading Database initializing...");
		lue = SpeakerExposed.getInstance();
		try {
			parseEvents(request, true);
		} catch (IOException e) {
			throw new ServletException(e);
		}
		response.getWriter().print("[OK]");
	}

	private void initEvents(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		response.getWriter().print("Database initializing...");
		lue = SpeakerExposed.getInstance();
		try {
			parseEvents(request, false);
		} catch (IOException e) {
			throw new ServletException(e);
		}
		response.getWriter().println("[OK]");
	}

	private Speaker createParticipant(String id, String name, String email,
			String twitterAccount) {
		Speaker p = new Speaker();
		p.setId(id);
		p.setName(name);
		if (email == null) {
			p.setEmail(name.toLowerCase().replaceAll(" ", ".")
					.replaceAll("..", ".")
					+ "@sap.com");
		} else {
			p.setEmail(email);
		}
		if (twitterAccount == null) {
			p.setTwitterAccount(twitterAccount);
		}

		lue.createEntity(p);
		return p;
	}

	public List<Session> parseEvents(HttpServletRequest request, boolean merge)
			throws IOException {
		SessionExposedBasic ex = new SessionExposedBasic();


		String text0 = getFileContents(request.getServletContext()
				.getResourceAsStream("events.csv"));
		StringTokenizer st0 = new StringTokenizer(text0, "~");
		List<Session> result = new ArrayList<Session>();
		while (st0.hasMoreTokens()) {
			String text = st0.nextToken().trim();
			StringTokenizer st = new StringTokenizer(text, "=");
			Session e = new Session();
			while (st.hasMoreTokens()) {
				String id0 = st.nextToken().trim();
				e.setId(Integer.parseInt(id0));
				String name0 = st.nextToken().trim();
				e.setName(name0);
				String description0 = st.nextToken().trim();
				e.setDescription(description0);
				String speakers0 = st.nextToken().trim();
				String speakers_id0 = st.nextToken().trim();
				e.setSpeakers(getSpeakers(speakers_id0, speakers0));
				String startTime0 = st.nextToken().trim();
				e.setStartTime(Integer.parseInt(startTime0));
				String duration0 = st.nextToken().trim();
				e.setDuration(Integer.parseInt(duration0));
				String room0 = st.nextToken().trim();
				e.setRoom(room0);
				String tags0 = st.nextToken().trim();
				e.setTags(toCollection(tags0));
				System.out.println("id: " + id0);
				System.out.println("name: " + name0);
				System.out.println("description: " + description0);
				System.out.println("speakers0: " + speakers0);
				System.out.println("speakers_ids: " + speakers_id0);
				System.out.println("start time: " + startTime0);
				System.out.println("duration: " + duration0);
				System.out.println("room: " + room0);
				System.out.println("tags: " + tags0);
			}
			result.add(e);
		}
		for (Session event : result) {
			if(merge){
				Session newEvent = ex.findEventById(event.getId()+"");
				if(newEvent != null){
					ex.updateEntity(event);	
				} else {
					ex.createEntity(event);
				}
			} else {
				ex.createEntity(event);
			}
		}
		return result;
	}

	private List<String> toCollection(String tags0) {
		List<String> result = new ArrayList<String>();
		if (tags0 != null) {
			StringTokenizer st = new StringTokenizer(tags0, ",");
			while (st.hasMoreTokens()) {
				result.add(st.nextToken().trim());
			}
		}
		return result;
	}

	private List<Speaker> getSpeakers(String sIds, String sNames) {
		List<Speaker> result = new ArrayList<Speaker>();
		StringTokenizer stIds = new StringTokenizer(sIds, ",");
		StringTokenizer stNames = new StringTokenizer(sNames, ",");
		while (stIds.hasMoreTokens()) {
			String uid = stIds.nextToken().trim().replaceAll("\n", "");
			String name = stNames.nextToken().trim().replaceAll("\n", "");
			if(uid.toLowerCase().equals("all") || uid.toLowerCase().equals("none")){
				break;
			}
			Speaker p = lue.findUserById(uid.trim());
			if (p != null) {
				result.add(p);
			} else {
				result.add(createParticipant(uid, name, null, null));
			}
		}
		return result;
	}

	/**
	 * Gets the string content of a file.
	 * 
	 * @param file
	 *            File handle.
	 * @param charsetName
	 *            charset
	 * @param request
	 * @return File content represented as string
	 * @throws IOException
	 */
	public String getFileContents(InputStream is) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1000];
		try {
			int read = is.read(buffer);
			while (read > 0) {
				bos.write(buffer, 0, read);
				read = is.read(buffer);
			}
			return bos.toString("UTF-8");
		} finally {
			is.close();
			bos.close();
		}
	}

	private void initPuzzleCodes(HttpServletRequest request) throws IOException {
		String text0 = getFileContents(request.getServletContext()
				.getResourceAsStream("codes_puzzle.txt"));
		persistCodes(processPuzzleCodesFromText(text0));
	}

	public List<Code> processPuzzleCodesFromText(String text0) {
		StringTokenizer st = new StringTokenizer(text0, "\r\n");
		List<Code> result = new ArrayList<Code>();
		int puzzleTypeIndex = 1;
		int puzzlePieceIndex = 0;
		while (st.hasMoreTokens()) {
			String token = (String) st.nextToken();
			if(token.length() > 0 && token.indexOf(",") != -1){
				String code = token.substring(0, token.indexOf(","));
				Code c = new Code();
				c.setCode(code.trim());
				c.setName("Puzzle");
				c.setType(Code.PUZZLE_CODE);
				c.setGid(Code.PUZZLE_CODE);
				c.setPoints(Score.PUZZLE);
				c.setMaxCodesOfThisType(1);
				String puzzleCode = puzzleType[puzzleTypeIndex]+puzzlePieces[puzzlePieceIndex++];
				c.setPuzzleCodeId(puzzleCode);
				if(puzzlePieceIndex > 5){
					puzzleTypeIndex++;
					puzzlePieceIndex=0;
				}
				if(puzzleTypeIndex > 4){
					puzzleTypeIndex=0;
				}
				result.add(c);
				System.out.println(code + " - "+puzzleCode);
			}
		}
		return result;
	}

	private void initHandoutCodes(HttpServletRequest request) throws IOException {
		String text0 = getFileContents(request.getServletContext()
				.getResourceAsStream("codes.txt"));
		persistCodes(processHandoutCodesFromText(text0));
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
