package admin;

import gamification.ExecuteAction;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import persistency.entities.feedback.Answer;
import persistency.entities.feedback.Question;
import persistency.exposed.feedback.AnswerExposed;
import persistency.exposed.feedback.QuestionExposed;

/**
 * Servlet implementation class GetFeedback
 */
public class GetFeedback extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFeedback() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AnswerExposed ae = new AnswerExposed();
//		QuestionExposed qe = new QuestionExposed();
//		Question questionById1 = qe.findQuestionById(1);
//		List<Answer> answers = questionById1.getAnswers();	
//		Question questionById2 = qe.findQuestionById(2);
//		List<Answer> answers2 = questionById2.getAnswers();
		response.getWriter().println((new Gson()).toJson(new Data(ae.allAnswers(), null)));
	}
	
	private class Data {
		
		public Data(List<Answer> answers, List<Answer> answers2) {
			q1 = answers;
			q2 = answers2;
		}
		List<Answer> q1;
		List<Answer> q2;
	}
}
