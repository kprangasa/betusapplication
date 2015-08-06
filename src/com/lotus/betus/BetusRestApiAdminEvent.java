package com.lotus.betus;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.lotus.event.SportsCategory;
import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.eventdao.OutcomeDao;
import com.lotus.eventdao.OutcomeOJDBCDAO;
import com.lotus.userdao.UserDao;
import com.lotus.userdao.UserOJDBCDAO;
import com.lotus.users.User;
import com.lotus.users.UserType;
import com.sun.jersey.api.client.ClientResponse.Status;

@Path("/admin/event")
public class BetusRestApiAdminEvent {
	private EventDao eventDAO = EventOJDBCDAO.getInstance();
	
	User loggedInUser = BetusRestApi.getLoggedInUser();
	
	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createEvent(@FormParam("eventCode") String eventCode,
			@FormParam("sportsCode") String sportsCode,
			@FormParam("startDate") String startDate) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
			return returnForbiddenResponse(jsonObject);
		} else if (eventCode == null || sportsCode == null || startDate == null) {
			return returnSuccessFalse(jsonObject);
		} else if (eventCode.length() != 5 || eventCode.contains(" ")) {
			return returnSuccessFalse(jsonObject);
		} else if (SportsCategory.getSportsCode(sportsCode) == null) {
			return returnSuccessFalse(jsonObject);
		}

		try {
			DateFormat simpleDateFormat = new SimpleDateFormat(
					"MM/dd/yyyy HH:mm:ss");
			Date eventStartDate = simpleDateFormat.parse(startDate);
			simpleDateFormat.format(eventStartDate);
			Event newEvent = new Event(eventCode,
					SportsCategory.valueOf(sportsCode), eventStartDate);
			newEvent.persist();
			return returnSuccessTrue(jsonObject);

		} catch (Exception e) {
			e.printStackTrace();
			return returnErrorOccured(jsonObject);
		}

	}
	@Path("/{eventCode}")
	@GET
	@Produces("application/json")
	public Response showEvent(@PathParam("eventCode") String eventCode)
			throws JsonGenerationException, JsonMappingException, IOException {
		System.out.println(this.loggedInUser);

		Event event = eventDAO.getEventByCode(eventCode);
		DateFormat simpleDateFormat = new SimpleDateFormat(
				"MM/dd/yyyy HH:mm:ss");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(simpleDateFormat);
		JSONObject jsonObject = new JSONObject();
		String response = "{}";

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {

			return returnForbiddenResponse(jsonObject);
		} else if (event == null) {
			return Response.status(200).entity(response).build();
		} else {
			response = mapper.writeValueAsString(event);
			return Response.status(200).entity(response).build();
		}

	}

	
	@GET
	@Produces("application/json")
	public Response listEvents() throws JsonGenerationException,
			JsonMappingException, IOException {
		System.out.println(this.loggedInUser);

		List<Event> events = eventDAO.listEvents();
		DateFormat simpleDateFormat = new SimpleDateFormat(
				"MM/dd/yyyy HH:mm:ss");
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(simpleDateFormat);
		JSONObject jsonObject = new JSONObject();
		String response = "{}";

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {

			return returnForbiddenResponse(jsonObject);
		} else {
			response = mapper.writeValueAsString(events);
			return Response.status(200).entity(response).build();
		}

	}
	private Response returnSuccessFalse(JSONObject jsonObject) {
		jsonObject.put("success", false);
		return Response.status(200).entity(jsonObject.toString()).build();
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
		jsonObject.put("success", true);
		return Response.status(200).entity(jsonObject.toString()).build();
	}
}
