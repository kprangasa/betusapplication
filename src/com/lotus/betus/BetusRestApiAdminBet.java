package com.lotus.betus;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.eventdao.OutcomeDao;
import com.lotus.eventdao.OutcomeOJDBCDAO;
import com.lotus.userdao.BetDao;
import com.lotus.userdao.BetOJDBCDAO;
import com.lotus.users.Bet;
import com.lotus.users.User;
import com.lotus.users.UserType;
import com.sun.jersey.api.client.ClientResponse.Status;

@Path("/admin/bet")
public class BetusRestApiAdminBet {
	User loggedInUser = BetusRestApi.getLoggedInUser();
	private BetDao betDao = BetOJDBCDAO.getInstance();
	private EventDao eventDAO = EventOJDBCDAO.getInstance();
	private OutcomeDao outcomeDAO = OutcomeOJDBCDAO.getInstance();
	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createBet(@FormParam("eventId") String eventId, @FormParam("amount") String amount, @FormParam("outcomeId") String outcomeId) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		BigDecimal betAmount = new BigDecimal(amount);
		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
			return returnForbiddenResponse(jsonObject);
		} else if (eventId == null ||  amount == null || outcomeId == null) {
			return returnSuccessFalse(jsonObject);
		} else if (!eventId.matches("[0-9]+")||!outcomeId.matches("[0-9]+") || !amount.matches("[0-9]+")) {
			return returnSuccessFalse(jsonObject);
		}else if(betAmount.compareTo(new BigDecimal(1000) )== 1 || betAmount.compareTo(new BigDecimal(100) )== -1){
			return returnSuccessFalse(jsonObject);
		}

		try {
			Long longEventId = new Long(eventId);
			Long longOutcomeId = new Long(outcomeId);
			Bet bet = new Bet(longEventId, this.loggedInUser.getId(), betAmount, new Long(outcomeId), false);
			if(!outcomeDAO.getOutcomeById(longOutcomeId).getEventId().equals(longEventId)){
				jsonObject.put("No such outcome for the event", false);
				return returnSuccessFalse(jsonObject);
			}
			if (bet.persist()) {
				jsonObject.put("success", true);
				return Response.status(200).entity(jsonObject.toString())
						.build();
			}
			jsonObject.put("Error", "Cannot bet on the same event twice.");
			return Response.status(200).entity(jsonObject.toString()).build();

		} catch (Exception e) {
			e.printStackTrace();
			return returnErrorOccured(jsonObject);
		}

	}
	@GET
	@Produces("application/json")
	public Response listBets() throws JsonGenerationException,
			JsonMappingException, IOException {
		

		List<Bet> bets = betDao.listAllBets();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObject = new JSONObject();
		String response = "{}";

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {

			return returnForbiddenResponse(jsonObject);
		} else {
			response = mapper.writeValueAsString(bets);
			return Response.status(200).entity(response).build();
		}

	}
	private Response returnSuccessFalse(JSONObject jsonObject) {
		return returnSuccessTrue(jsonObject);
	}

	private Response returnForbiddenResponse(JSONObject jsonObject) {
		jsonObject.put("Forbidden", "Log in as admin.");
		return Response.status(Status.FORBIDDEN).entity(jsonObject.toString())
				.build();
	}

	private Response returnErrorOccured(JSONObject jsonObject) {
		jsonObject.put("success", false);
		jsonObject.put("error", "This error occured.");
		return Response.status(400).entity(jsonObject.toString()).build();
	}

	private Response returnSuccessTrue(JSONObject jsonObject) {
		jsonObject.put("success", false);
		return Response.status(200).entity(jsonObject.toString()).build();
	}

}
