package com.lotus.betus;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.lotus.event.Event;
import com.lotus.event.Outcome;
import com.lotus.event.Result;
import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.eventdao.OutcomeDao;
import com.lotus.eventdao.OutcomeOJDBCDAO;
import com.lotus.users.BetStatus;
import com.lotus.users.User;
import com.lotus.users.UserType;
import com.sun.jersey.api.client.ClientResponse.Status;

@Path("/admin/outcome")
public class BetusRestApiAdminOutcome {
	private EventDao eventDAO = EventOJDBCDAO.getInstance();
	private OutcomeDao outcomeDAO = OutcomeOJDBCDAO.getInstance();
	User loggedInUser = BetusRestApi.getLoggedInUser();

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createOutcome(@FormParam("description") String description,
			@FormParam("eventId") String eventId) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
			return returnForbiddenResponse(jsonObject);
		} else if (description == null || eventId == null) {
			return returnSuccessFalse(jsonObject);
		} else if (description.length() <= 0 || !eventId.matches("[0-9]+")) {
			return returnSuccessFalse(jsonObject);
		}

		try {
			Outcome outcome = new Outcome(description, new Long(eventId),
					Result.NONE);
			if (outcome.persist()) {
				jsonObject.put("success", true);
				return Response.status(200).entity(jsonObject.toString())
						.build();
			}
			jsonObject.put("Error", "No updating of outcome");
			return Response.status(200).entity(jsonObject.toString()).build();

		} catch (Exception e) {
			e.printStackTrace();
			return returnErrorOccured(jsonObject);
		}

	}

	@Path("/result")
	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response resultOutcome(@FormParam("id") String id,
			@FormParam("result") String result) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
			return returnForbiddenResponse(jsonObject);
		} else if (id == null || result == null) {
			return returnSuccessFalse(jsonObject);
		} else if (id.length() <= 0 || !id.matches("[0-9]+")) {
			return returnSuccessFalse(jsonObject);
		} else if (Result.getResult(result) == null
				|| Result.getResult(result).equals(Result.NONE)) {
			return returnSuccessFalse(jsonObject);
		}

		try {
			Outcome outcome = outcomeDAO.getOutcomeById(new Long(id));
			Event event = eventDAO.getEventById(outcome.getEventId());
			if (event.getBetStatus().equals(BetStatus.SETTLED)) {
				return returnSuccessFalse(jsonObject);
			}
			outcomeDAO.setOutcomeResult(outcome, result);
			for (Outcome outcomes : outcomeDAO.getListOfOutcomeById(outcome
					.getEventId())) {
				if (outcomes.getResult().equals(Result.NONE)) {
					jsonObject.put("success", true);
					return Response.status(200).entity(jsonObject.toString())
							.build();
				}
			}
			event.setBetStatus(BetStatus.RESULTED);
			System.out.println(event.getBetStatus().toString());
			eventDAO.updateEvent(event);
			jsonObject.put("All outcomes have result", "Event Resulted");
			return Response.status(200).entity(jsonObject.toString()).build();

		} catch (Exception e) {
			e.printStackTrace();
			return returnErrorOccured(jsonObject);
		}

	}

	@GET
	@Produces("application/json")
	public Response listOutcomes() throws JsonGenerationException,
			JsonMappingException, IOException {
		System.out.println(this.loggedInUser);

		List<Outcome> outcomes = outcomeDAO.listOutcomes();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObject = new JSONObject();
		String response = "{}";

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {

			return returnForbiddenResponse(jsonObject);
		} else {
			response = mapper.writeValueAsString(outcomes);
			return Response.status(200).entity(response).build();
		}

	}

	@Path("/{eventCode}")
	@GET
	@Produces("application/json")
	public Response showOutcome(@PathParam("eventCode") String eventCode)
			throws JsonGenerationException, JsonMappingException, IOException {
		System.out.println(this.loggedInUser);
		List<Outcome> outcomes = outcomeDAO.getListOfOutcome(eventCode);
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObject = new JSONObject();
		String response = "{}";

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {

			return returnForbiddenResponse(jsonObject);
		} else if (outcomes.isEmpty()) {
			return Response.status(200).entity(response).build();
		} else {
			response = mapper.writeValueAsString(outcomes);
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
