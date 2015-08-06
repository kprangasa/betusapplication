package com.lotus.betuscustomer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
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

import com.lotus.betus.BetusRestApi;
import com.lotus.event.Event;
import com.lotus.event.Outcome;
import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.eventdao.OutcomeDao;
import com.lotus.eventdao.OutcomeOJDBCDAO;
import com.lotus.users.Bet;
import com.lotus.users.BetStatus;
import com.lotus.users.User;
import com.lotus.users.UserType;
import com.sun.jersey.api.client.ClientResponse.Status;

@Path("/customer")
public class BetusRestApiCustomer {
	private OutcomeDao outcomeDAO = OutcomeOJDBCDAO.getInstance();
	private EventDao eventDAO = EventOJDBCDAO.getInstance();
	User loggedInUser = BetusRestApi.getLoggedInUser();
	@GET
	@Produces("application/json")
	public Response listOutcomes() throws JsonGenerationException,
			JsonMappingException, IOException {
		class EventInfo{
			List<Event> event = null;
			List<Outcome> outcome = null;
			
			
			public EventInfo(List<Event> event, List<Outcome> outcome) {
				super();
				this.event = event;
				this.outcome = outcome;
			}
			public List<Event> getEvent() {
				return event;
			}
			public List<Outcome> getOutcome() {
				return outcome;
			}
			
		}

		List<Event> events = eventDAO.listEvents();
		List<Outcome> outcomes = outcomeDAO.listOutcomes();
		EventInfo eventInfo = new EventInfo(events, outcomes);
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObject = new JSONObject();
		String response = "{}";

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.ADMIN)) {

			return returnForbiddenResponse(jsonObject);
		} else {
			response = mapper.writeValueAsString(eventInfo);
			return Response.status(200).entity(response).build();
		}

	}
	@Path("/balance")
	@GET
	@Produces("application/json")
	public Response getBalance() throws JsonGenerationException,
			JsonMappingException, IOException {
		
		
		
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObject = new JSONObject();
		String response = "{}";

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.ADMIN)) {

			return returnForbiddenResponse(jsonObject);
		} else {
			response = mapper.writeValueAsString(this.loggedInUser.getBalance());
			return Response.status(200).entity(response).build();
		}

	}
	private Response returnSuccessFalse(JSONObject jsonObject) {
		return returnSuccessTrue(jsonObject);
	}

	private Response returnForbiddenResponse(JSONObject jsonObject) {
		jsonObject.put("Forbidden", "Log in as customer.");
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
	@Path("/bet")
	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createBet(@FormParam("eventId") String eventId, @FormParam("amount") String amount, @FormParam("outcomeId") String outcomeId) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		BigDecimal betAmount = new BigDecimal(amount);
		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.ADMIN)) {
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
			Date now = new Date();
			Event event = eventDAO.getEventById(longEventId);
			if (!event.getBetStatus().equals(BetStatus.OPEN)||event.getEventStartDate().before(now)||betAmount.compareTo(this.loggedInUser.getBalance())==1) {
				return returnSuccessFalse(jsonObject);
			}
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

}
