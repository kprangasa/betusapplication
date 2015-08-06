package com.lotus.betuscustomer;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.lotus.betus.BetusRestApi;
import com.lotus.event.Event;
import com.lotus.event.Outcome;
import com.lotus.event.SportsCategory;
import com.lotus.eventdao.EventDao;
import com.lotus.eventdao.EventDetails;
import com.lotus.eventdao.EventOJDBCDAO;
import com.lotus.eventdao.OutcomeDao;
import com.lotus.eventdao.OutcomeDetails;
import com.lotus.eventdao.OutcomeOJDBCDAO;
import com.lotus.userdao.UserDao;
import com.lotus.userdao.UserOJDBCDAO;
import com.lotus.users.Bet;
import com.lotus.users.BetStatus;
import com.lotus.users.User;
import com.lotus.users.UserType;
import com.sun.jersey.api.client.ClientResponse.Status;

@Path("/customer")
public class BetusRestApiCustomer {
	private OutcomeDao outcomeDAO = OutcomeOJDBCDAO.getInstance();
	private EventDao eventDAO = EventOJDBCDAO.getInstance();
	private UserDao userDAO = UserOJDBCDAO.getInstance();
	User loggedInUser = BetusRestApi.getLoggedInUser();

	
	@GET
	@Produces("application/json")
	public Response listOutcomes() throws JsonGenerationException,
			JsonMappingException, IOException {
		class EventInfoResponse{
			EventDetails eventDetails;
			List<OutcomeDetails> outcomeDetails;
			public EventDetails getEventDetails() {
				return eventDetails;
			}
			public void setEventDetails(EventDetails eventDetails) {
				this.eventDetails = eventDetails;
			}
			public List<OutcomeDetails> getOutcomeDetails() {
				return outcomeDetails;
			}
			public void setOutcomeDetails(List<OutcomeDetails> outcomeDetails) {
				this.outcomeDetails = outcomeDetails;
			}
			private EventInfoResponse(EventDetails eventDetails,
					List<OutcomeDetails> outcomeDetails) {
				super();
				this.eventDetails = eventDetails;
				this.outcomeDetails = outcomeDetails;
			}
			
			
		}
	
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObject = new JSONObject();
		
		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.ADMIN)) {

			return returnForbiddenResponse(jsonObject);
		} else {
			
			List<EventInfoResponse> eventInfos = new ArrayList<EventInfoResponse>();
			List<Event> events = eventDAO.listEvents();
			List<OutcomeDetails> outcomeDetails =null;
			for(Event event: events){
				outcomeDetails= new ArrayList<OutcomeDetails>();
				for(Outcome outcome: outcomeDAO.getListOfOutcomeById(event.getId())){
					
					
					outcomeDetails.add(new OutcomeDetails(outcome.getDescription(), outcome.getId()));
					
				}
				EventDetails eventDtails = new EventDetails(event.getId(), event.getSportsCode(), event.getEventStartDate());
				eventInfos.add(new EventInfoResponse(eventDtails, outcomeDetails));
			}
			
			String response = "{}";
			DateFormat simpleDateFormat = new SimpleDateFormat(
					"MM/dd/yyyy HH:mm:ss");
			mapper.setDateFormat(simpleDateFormat);
			response = mapper.writeValueAsString(eventInfos);
			return Response.status(200).entity(response).build();
		}

	}
	
	@Path("/{sportsCode}")
	@GET
	@Produces("application/json")
	public Response listOutcomesBySportsCode(@PathParam("sportsCode") String sportsCode) throws JsonGenerationException,
			JsonMappingException, IOException {
		class EventInfoResponse{
			EventDetails eventDetails;
			List<OutcomeDetails> outcomeDetails;
			public EventDetails getEventDetails() {
				return eventDetails;
			}
			public void setEventDetails(EventDetails eventDetails) {
				this.eventDetails = eventDetails;
			}
			public List<OutcomeDetails> getOutcomeDetails() {
				return outcomeDetails;
			}
			public void setOutcomeDetails(List<OutcomeDetails> outcomeDetails) {
				this.outcomeDetails = outcomeDetails;
			}
			private EventInfoResponse(EventDetails eventDetails,
					List<OutcomeDetails> outcomeDetails) {
				super();
				this.eventDetails = eventDetails;
				this.outcomeDetails = outcomeDetails;
			}
			
			
		}
	
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObject = new JSONObject();
		
		if (this.loggedInUser == null
				|| this.loggedInUser.getType().equals(UserType.ADMIN)) {

			return returnForbiddenResponse(jsonObject);
		} else {
			
			List<EventInfoResponse> eventInfos = new ArrayList<EventInfoResponse>();
			List<Event> events = eventDAO.listEvents();
			List<OutcomeDetails> outcomeDetails =null;
			for(Event event: events){
				if(event.getSportsCode().equals(sportsCode)){
				outcomeDetails= new ArrayList<OutcomeDetails>();
				for(Outcome outcome: outcomeDAO.getListOfOutcomeById(event.getId())){
					
					
					outcomeDetails.add(new OutcomeDetails(outcome.getDescription(), outcome.getId()));
					
				}
				EventDetails eventDtails = new EventDetails(event.getId(), event.getSportsCode(), event.getEventStartDate());
				eventInfos.add(new EventInfoResponse(eventDtails, outcomeDetails));
				}
				continue;
			}
			
			String response = "{}";
			DateFormat simpleDateFormat = new SimpleDateFormat(
					"MM/dd/yyyy HH:mm:ss");
			mapper.setDateFormat(simpleDateFormat);
			response = mapper.writeValueAsString(eventInfos);
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
				userDAO.addBalance(this.loggedInUser, betAmount.negate());
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
