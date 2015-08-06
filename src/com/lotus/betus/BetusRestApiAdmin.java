package com.lotus.betus;

import java.io.IOException;
import java.math.BigDecimal;
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
import com.lotus.event.Outcome;
import com.lotus.event.Result;
import com.lotus.event.SportsCategory;
import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.eventdao.OutcomeDao;
import com.lotus.eventdao.OutcomeOJDBCDAO;
import com.lotus.userdao.UserDao;
import com.lotus.userdao.UserOJDBCDAO;
import com.lotus.users.BetStatus;
import com.lotus.users.User;
import com.lotus.users.UserType;
import com.sun.jersey.api.client.ClientResponse.Status;

@Path("/admin/users")
public class BetusRestApiAdmin {

	private UserDao userDAO = UserOJDBCDAO.getInstance();
	User loggedInUser = BetusRestApi.getLoggedInUser();

	@Path("")
	@GET
	@Produces("application/json")
	public Response listCustomers() throws JsonGenerationException,
			JsonMappingException, IOException {
		System.out.println(this.loggedInUser);
		List<User> user = userDAO.listUsers();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObject = new JSONObject();
		String response = "{}";

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {

			return returnForbiddenResponse(jsonObject);
		} else {
			response = mapper.writeValueAsString(user);
			return Response.status(200).entity(response).build();
		}

	}

	@Path("/users/{name}")
	@GET
	@Produces("application/json")
	public Response show(@PathParam("name") String name) throws JSONException,
			JsonGenerationException, JsonMappingException, IOException {
		User user = userDAO.getUserByName(name);
		ObjectMapper mapper = new ObjectMapper();
		String response = "{}";
		JSONObject jsonObject = new JSONObject();
		if (user == null) {
			jsonObject.put("success", false);
			return Response.status(200).entity(jsonObject.toString()).build();
		} else if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
			return returnForbiddenResponse(jsonObject);
		} else {
			response = mapper.writeValueAsString(user);
			return Response.status(200).entity(response).build();
		}
	}

	@Path("/addBalance")
	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addBalance(@FormParam("username") String username,
			@FormParam("balance") String balance) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
			return returnForbiddenResponse(jsonObject);
		} else if (username == null || balance == null || username.isEmpty()
				|| balance.isEmpty()) {
			return returnSuccessFalse(jsonObject);
		} else if (!balance.matches("[0-9]+")) {
			return returnSuccessFalse(jsonObject);
		}

		try {
			BigDecimal balanceToAdd = new BigDecimal(balance);
			User user = userDAO.getUserByName(username);
			userDAO.addBalance(user, balanceToAdd);
			System.out.println(user.getBalance().add(balanceToAdd));
			jsonObject.put("success", true);
			return returnSuccessTrue(jsonObject);

		} catch (Exception e) {
			return returnErrorOccured(jsonObject);
		}

	}

	@POST
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUser(@FormParam("username") String username,
			@FormParam("balance") String balance,
			@FormParam("password") String password,
			@FormParam("type") String type) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
			return returnForbiddenResponse(jsonObject);
		} else if (username == null || balance == null || password == null
				|| type == null) {
			return returnSuccessFalse(jsonObject);
		} else if (username.length() > 10 || username.isEmpty()) {
			return returnSuccessFalse(jsonObject);
		} else if (password.length() > 10 || password.length() < 7) {
			return returnSuccessFalse(jsonObject);
		} else if (balance.isEmpty() || !balance.matches("[0-9]+")) {
			return returnSuccessFalse(jsonObject);
		} else if (!type.equals(UserType.ADMIN.toString())
				&& !type.equals(UserType.CUSTOMER.toString())) {
			return returnSuccessFalse(jsonObject);
		}

		try {
			BigDecimal newBalance = new BigDecimal(balance);
			User newUser = new User(username, password, newBalance,
					UserType.valueOf(type.toUpperCase()));
			if (newUser.persist()) {
				jsonObject.put("success", true);
			} else {
				jsonObject.put("error", "User already exists.");
				return Response.status(200).entity(jsonObject.toString())
						.build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return returnErrorOccured(jsonObject);
		}
		return Response.status(200).entity(jsonObject.toString()).build();

	}
//
//	@Path("/event/{eventCode}")
//	@GET
//	@Produces("application/json")
//	public Response showEvent(@PathParam("eventCode") String eventCode)
//			throws JsonGenerationException, JsonMappingException, IOException {
//		System.out.println(this.loggedInUser);
//
//		Event event = eventDAO.getEventByCode(eventCode);
//		DateFormat simpleDateFormat = new SimpleDateFormat(
//				"MM/dd/yyyy HH:mm:ss");
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.setDateFormat(simpleDateFormat);
//		JSONObject jsonObject = new JSONObject();
//		String response = "{}";
//
//		if (this.loggedInUser == null
//				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
//
//			return returnForbiddenResponse(jsonObject);
//		} else if (event == null) {
//			return Response.status(200).entity(response).build();
//		} else {
//			response = mapper.writeValueAsString(event);
//			return Response.status(200).entity(response).build();
//		}
//
//	}
//
//	@Path("/event")
//	@GET
//	@Produces("application/json")
//	public Response listEvents() throws JsonGenerationException,
//			JsonMappingException, IOException {
//		System.out.println(this.loggedInUser);
//
//		List<Event> events = eventDAO.listEvents();
//		DateFormat simpleDateFormat = new SimpleDateFormat(
//				"MM/dd/yyyy HH:mm:ss");
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.setDateFormat(simpleDateFormat);
//		JSONObject jsonObject = new JSONObject();
//		String response = "{}";
//
//		if (this.loggedInUser == null
//				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
//
//			return returnForbiddenResponse(jsonObject);
//		} else {
//			response = mapper.writeValueAsString(events);
//			return Response.status(200).entity(response).build();
//		}
//
//	}

//	@Path("/outcome")
//	@POST
//	@Produces("application/json")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	public Response createOutcome(@FormParam("description") String description,
//			@FormParam("eventId") String eventId) throws JSONException {
//		JSONObject jsonObject = new JSONObject();
//
//		if (this.loggedInUser == null
//				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
//			return returnForbiddenResponse(jsonObject);
//		} else if (description == null || eventId == null) {
//			return returnSuccessFalse(jsonObject);
//		} else if (description.length() <= 0 || !eventId.matches("[0-9]+")) {
//			return returnSuccessFalse(jsonObject);
//		}
//
//		try {
//			Outcome outcome = new Outcome(description, new Long(eventId),
//					Result.NONE);
//			if (outcome.persist()) {
//				jsonObject.put("success", true);
//				return Response.status(200).entity(jsonObject.toString())
//						.build();
//			}
//			jsonObject.put("Error", "No updating of outcome");
//			return Response.status(200).entity(jsonObject.toString()).build();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return returnErrorOccured(jsonObject);
//		}
//
//	}
//
//	@Path("/outcome/result")
//	@POST
//	@Produces("application/json")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	public Response resultOutcome(@FormParam("id") String id,
//			@FormParam("result") String result) throws JSONException {
//		JSONObject jsonObject = new JSONObject();
//
//		if (this.loggedInUser == null
//				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
//			return returnForbiddenResponse(jsonObject);
//		} else if (id == null || result == null) {
//			return returnSuccessFalse(jsonObject);
//		} else if (id.length() <= 0 || !id.matches("[0-9]+")) {
//			return returnSuccessFalse(jsonObject);
//		} else if (Result.getResult(result) == null
//				|| Result.getResult(result).equals(Result.NONE)) {
//			return returnSuccessFalse(jsonObject);
//		}
//
//		try {
//			Outcome outcome = outcomeDAO.getOutcomeById(new Long(id));
//			Event event = eventDAO.getEventById(outcome.getEventId());
//			if (event.getBetStatus().equals(BetStatus.SETTLED)) {
//				return returnSuccessFalse(jsonObject);
//			}
//			outcomeDAO.setOutcomeResult(outcome, result);
//			for (Outcome outcomes : outcomeDAO.getListOfOutcomeById(outcome
//					.getEventId())) {
//				if (outcomes.getResult().equals(Result.NONE)) {
//					jsonObject.put("success", true);
//					return Response.status(200).entity(jsonObject.toString())
//							.build();
//				}
//			}
//			event.setBetStatus(BetStatus.RESULTED);
//			System.out.println(event.getBetStatus().toString());
//			eventDAO.updateEvent(event);
//			jsonObject.put("All outcomes have result", "Event Resulted");
//			return Response.status(200).entity(jsonObject.toString()).build();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return returnErrorOccured(jsonObject);
//		}
//
//	}
//
//	@Path("/outcome")
//	@GET
//	@Produces("application/json")
//	public Response listOutcomes() throws JsonGenerationException,
//			JsonMappingException, IOException {
//		System.out.println(this.loggedInUser);
//
//		List<Outcome> outcomes = outcomeDAO.listOutcomes();
//		ObjectMapper mapper = new ObjectMapper();
//		JSONObject jsonObject = new JSONObject();
//		String response = "{}";
//
//		if (this.loggedInUser == null
//				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
//
//			return returnForbiddenResponse(jsonObject);
//		} else {
//			response = mapper.writeValueAsString(outcomes);
//			return Response.status(200).entity(response).build();
//		}
//
//	}
//
//	@Path("/outcome/{eventCode}")
//	@GET
//	@Produces("application/json")
//	public Response showOutcome(@PathParam("eventCode") String eventCode)
//			throws JsonGenerationException, JsonMappingException, IOException {
//		System.out.println(this.loggedInUser);
//		List<Outcome> outcomes = outcomeDAO.getListOfOutcome(eventCode);
//		ObjectMapper mapper = new ObjectMapper();
//		JSONObject jsonObject = new JSONObject();
//		String response = "{}";
//
//		if (this.loggedInUser == null
//				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
//
//			return returnForbiddenResponse(jsonObject);
//		} else if (outcomes.isEmpty()) {
//			return Response.status(200).entity(response).build();
//		} else {
//			response = mapper.writeValueAsString(outcomes);
//			return Response.status(200).entity(response).build();
//		}
//
//	}

//	@Path("/event")
//	@POST
//	@Produces("application/json")
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	public Response createEvent(@FormParam("eventCode") String eventCode,
//			@FormParam("sportsCode") String sportsCode,
//			@FormParam("startDate") String startDate) throws JSONException {
//		JSONObject jsonObject = new JSONObject();
//
//		if (this.loggedInUser == null
//				|| this.loggedInUser.getType().equals(UserType.CUSTOMER)) {
//			return returnForbiddenResponse(jsonObject);
//		} else if (eventCode == null || sportsCode == null || startDate == null) {
//			return returnSuccessFalse(jsonObject);
//		} else if (eventCode.length() != 5 || eventCode.contains(" ")) {
//			return returnSuccessFalse(jsonObject);
//		} else if (SportsCategory.getSportsCode(sportsCode) == null) {
//			return returnSuccessFalse(jsonObject);
//		}
//
//		try {
//			DateFormat simpleDateFormat = new SimpleDateFormat(
//					"MM/dd/yyyy HH:mm:ss");
//			Date eventStartDate = simpleDateFormat.parse(startDate);
//			simpleDateFormat.format(eventStartDate);
//			Event newEvent = new Event(eventCode,
//					SportsCategory.valueOf(sportsCode), eventStartDate);
//			newEvent.persist();
//			return returnSuccessTrue(jsonObject);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return returnErrorOccured(jsonObject);
//		}
//
//	}

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
