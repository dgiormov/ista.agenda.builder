package service.rest;

import gamification.ExecuteAction;
import gamification.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import persistency.entities.LoggedUser;
import persistency.entities.feedback.Answer;
import persistency.entities.feedback.Question;
import persistency.entities.gamification.PointsCategory;
import persistency.entities.gamification.PointsInstance;
import persistency.exposed.LoggedUserExposed;
import persistency.exposed.PointsCategoryExposed;
import persistency.exposed.feedback.AnswerExposed;
import persistency.exposed.feedback.QuestionExposed;
import utils.Status;
import utils.Status.STATE;
import admin.AppControl;

import com.google.gson.Gson;

@Produces({ MediaType.APPLICATION_JSON })
@Path("me")
public class GamificationMiscResources {

	private Gson g;

	public GamificationMiscResources() {
		g = new Gson();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("share")
	public Response shareButtonUsed(@Context HttpServletRequest request) {
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser(request);
		if(currentUser == null){
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		Status execute = ExecuteAction.getInstance().execute("share", currentUser, null);
		if(execute.severity == STATE.OK){
			return Response.status(Response.Status.ACCEPTED).build();	
		}
		return Response.status(Response.Status.CONFLICT).entity(g.toJson(execute)).build();
	}

	@GET
	@Path("achievements")
	public Response achievements(@Context HttpServletRequest request){
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser(request);
		if(currentUser == null){
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		List<PointsCategory> uniqueCategories = currentUser.getUniqueCategories();
		Map<String, List<String>> hast = new HashMap<String, List<String>>();

		for (PointsCategory pointsCategory : uniqueCategories) {
			int rank = pointsCategory.getRank();
			if(rank > 0){
				String rankStr = "rank"+rank;		
				if(hast.get(rankStr) != null){
					hast.get(rankStr).add(pointsCategory.getShortid());
				} else {
					List<String> value = new ArrayList<String>();
					value.add(pointsCategory.getShortid());
					hast.put(rankStr, value);
				}
			}
		}

		Gson g = new Gson();
		return Response.ok().entity(g.toJson(hast)).build();	
	}

	@GET
	@Path("challenge")
	public Response getChallenges(@Context HttpServletRequest request){
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser(request);
		if(currentUser == null){
			return Response.status(javax.ws.rs.core.Response.Status.UNAUTHORIZED).build();
		}
		Player player = currentUser.getPlayer();
		ChallengeJson cj = new ChallengeJson();
		cj.rank = player.getRankInt();
		PointsCategoryExposed pce = new PointsCategoryExposed();
		PointsCategory charityCategory = pce.findCategoryByShortName("charity");
		PointsCategory tshirtCategory = pce.findCategoryByShortName("tshirt");
		List<PointsInstance> charityPoints = currentUser.getPointsInstancesOfCategory(charityCategory);
		List<PointsInstance> tshirtPoints = currentUser.getPointsInstancesOfCategory(tshirtCategory);

		cj.unlocked = charityPoints != null && charityPoints.size()>0;
		cj.code = tshirtPoints != null && tshirtPoints.size()>0;
		if(cj.rank > 1){
			cj.counter = count(tshirtCategory.getInstancesOfThisType());
		}
		return Response.ok().entity((new Gson()).toJson(cj)).build();
	}

	private int count(List<PointsInstance> instancesOfThisType) {
		int i = 0;
		for (PointsInstance pointsInstance : instancesOfThisType) {
			if(pointsInstance.isUsed()){
				i++;
			}
		}
		return i;
	}

	private class ChallengeJson {
		private int rank = 0;
		private int counter = 0;
		private boolean unlocked = false;
		private boolean code = false; 
	}

	@GET
	@Path("feedback")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFeedbackObject(@Context HttpServletRequest request){
		Gson g = new Gson();
//		if(!AppControl.writeMode(request)){
//			return Response.ok().entity(g.toJson(new FeedbackObject(true, false, false))).build();
//		}
		LoggedUserExposed lue = new LoggedUserExposed();
		LoggedUser currentUser = lue.getCurrentUser(request);
		if(currentUser == null){
			return Response.status(javax.ws.rs.core.Response.Status.UNAUTHORIZED).build();
		}
		//		AnswerExposed ae = new AnswerExposed();
		//		List<Answer> answersOfUser = ae.findAnswersOfUser(currentUser.getId());
		//		if(answersOfUser != null && answersOfUser.size() > 0){
		//			return Response.ok().entity(g.toJson(new FeedbackObject(false, true, false))).build();
		//		}
		return Response.ok().entity(g.toJson(new FeedbackObject(false, false, true))).build();
	}

	@POST
	@Path("feedback")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postFeedback(@Context HttpServletRequest request){
		try {
			
			if(!AppControl.writeMode(request)){
				return Response.ok().entity(g.toJson(new Status(STATE.ERROR, "Feedback is disabled for a while, try again a bit later."))).build();
			}
			QueryJson fromJson = g.fromJson(new InputStreamReader(request.getInputStream(), "UTF-8"), QueryJson.class);

			LoggedUserExposed lue = new LoggedUserExposed();
			LoggedUser currentUser = lue.getCurrentUser(request);
			if(currentUser == null){
				return Response.status(javax.ws.rs.core.Response.Status.UNAUTHORIZED).build();
			}
			QuestionExposed qe = new QuestionExposed();
			AnswerExposed ae = new AnswerExposed();
			Question questionById1 = qe.findQuestionById(1);
			Answer a1 = new Answer();
			a1.setCurrentUser(currentUser.getId());
			a1.setFreeText(fromJson.q1);
			ae.createEntity(a1);

			Question questionById2 = qe.findQuestionById(2);
			Answer a2 = new Answer();
			a2.setCurrentUser(currentUser.getId());
			a1.setFreeText(fromJson.q2);
			ae.createEntity(a1);
			questionById1.getAnswers().add(a1);
			questionById2.getAnswers().add(a2);
			qe.updateEntity(questionById1);
			qe.updateEntity(questionById2);
			ExecuteAction.getInstance().execute("fillupsurvey", currentUser, null);
			if(fromJson.q1.length() + fromJson.q2.length() > 10){
				StringTokenizer sq1 = new StringTokenizer(fromJson.q1, " ");
				StringTokenizer sq2 = new StringTokenizer(fromJson.q2, " ");
				int allWords = sq1.countTokens() + sq2.countTokens();
				if(allWords > 10 && allWords <= 100){
					ExecuteAction.getInstance().execute("surveybonus1", currentUser, null);
				} else if(allWords > 100){
					ExecuteAction.getInstance().execute("surveybonus2", currentUser, null);
				}
			}
		} catch (IOException e) {
			return Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
		return Response.ok().build();
	}


	private class QueryJson {
		private String q1;
		private String q2;
	}


	@XmlRootElement
	private class FeedbackObject{
		private boolean waitForIt;
		private boolean hasProvided;
		private boolean available;

		public FeedbackObject(boolean waitForIt, boolean hasProvided,
				boolean available) {
			super();
			this.waitForIt = waitForIt;
			this.hasProvided = hasProvided;
			this.available = available;
		}
	}

}
