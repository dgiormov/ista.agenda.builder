package admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import persistency.entities.gamification.PointsCategory;
import persistency.entities.gamification.PointsInstance;
import persistency.exposed.PointsExposed;
import persistency.exposed.json.PointsInstanceJson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class Codes
 */
public class Codes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PointsExposed pe = new PointsExposed();
		List<PointsInstance> allCodes = pe.allCodes();
		Gson g = new Gson();
		response.getWriter().print(g.toJson(toJsonObject(allCodes)));
	}

	private List<PointsInstanceJson> toJsonObject(List<PointsInstance> allCodes) {
		List<PointsInstanceJson> result = new ArrayList<PointsInstanceJson>();
		for (PointsInstance pointsInstance : allCodes) {
			result.add(new PointsInstanceJson(pointsInstance));
		}
		return result;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Gson g= new Gson();
		List<PointsInstanceJson> codesToUpdate = g.fromJson(req.getReader(),  new TypeToken<List<PointsInstanceJson>>(){}.getType());
		if(codesToUpdate != null){
			PointsExposed pe = new PointsExposed();
			for (PointsInstanceJson pointsInstanceJson : codesToUpdate) {
				PointsInstance p = pe.fingById(pointsInstanceJson.getId());
				if(p!= null && pointsInstanceJson.getCode() != null && pointsInstanceJson.getCode().trim().length() == p.getCategory().getCodeLength()){
					p.setCode(pointsInstanceJson.getCode());
					pe.updateEntity(p);
				}
			}
		}
	}

}
