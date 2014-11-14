package admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import persistency.entities.gamification.PointsCategory;
import persistency.exposed.PointsCategoryExposed;
import persistency.exposed.json.PointsCategoryJson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Servlet implementation class PointsCategs
 */
public class PointsCategs extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson g = new Gson();
		PointsCategoryExposed pce = new PointsCategoryExposed();
		List<PointsCategory> allCodes = pce.allCodes();
		response.getWriter().print(g.toJson(toJsonFriendly(allCodes)));
	}

	private List<PointsCategoryJson> toJsonFriendly(List<PointsCategory> allCodes) {
		List<PointsCategoryJson> result = new ArrayList<PointsCategoryJson>();
		if(allCodes != null){
			for (PointsCategory pointsCategory : allCodes) {
				result.add(new PointsCategoryJson(pointsCategory));
			}
		}
		return result;
	}
}
