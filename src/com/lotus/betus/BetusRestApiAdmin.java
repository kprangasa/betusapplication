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
import com.lotus.event.SportsCategory;
import com.lotus.userdao.UserDao;
import com.lotus.userdao.UserOJDBCDAO;
import com.lotus.users.User;
import com.lotus.users.UserType;
import com.sun.jersey.api.client.ClientResponse.Status;

@Path("/admin")
public class BetusRestApiAdmin {
	
	private UserDao userDAO = UserOJDBCDAO.getInstance();
	User loggedInUser = BetusRestApi.getLoggedInUser();
	@Path("/users")
	@GET
	@Produces("application/json")
	public Response list() throws JsonGenerationException, JsonMappingException, IOException {
		System.out.println(this.loggedInUser);
		List<User> user = userDAO.listUsers();
		ObjectMapper mapper = new ObjectMapper();
		JSONObject jsonObject = new JSONObject();
		String response = "{}"; 

		if(this.loggedInUser == null|| this.loggedInUser.getType().equals(UserType.CUSTOMER)){
			
			return returnForbiddenResponse(jsonObject);
		}
		else {
			response = mapper.writeValueAsString(user);
			return Response.status(200).entity(response).build();
		}
		
	}

	

	@Path("/users/{name}")
	@GET
	@Produces("application/json")
	public Response show(@PathParam("name") String name) throws JSONException, JsonGenerationException, JsonMappingException, IOException {
		User user = userDAO.getUserByName(name);
		ObjectMapper mapper = new ObjectMapper();
		String response = "{}";
		JSONObject jsonObject = new JSONObject();
		if(user == null){
			jsonObject.put("success", false);
			return Response.status(Status.OK).entity(jsonObject.toString()).build();
		}
		else if(this.loggedInUser == null|| this.loggedInUser.getType().equals(UserType.CUSTOMER)){
			return returnForbiddenResponse(jsonObject);
		}
		else{
			response = mapper.writeValueAsString(user);
			return Response.status(200).entity(response).build();
		}
	}
	@Path("/addBalance")
	@POST
	@Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addBalance(@FormParam("username") String username, @FormParam("balance") String balance)  throws JSONException {
		JSONObject jsonObject = new JSONObject();
		
		if(this.loggedInUser == null|| this.loggedInUser.getType().equals(UserType.CUSTOMER)){
			return returnForbiddenResponse(jsonObject);
		}
		else if(username == null || balance == null || username.isEmpty()|| balance.isEmpty() ) {
			return returnSuccessFalse(jsonObject);
		}
		else if(!balance.matches("[0-9]+")){
			return returnSuccessFalse(jsonObject);
		}
		
		
		try {
				BigDecimal balanceToAdd = new BigDecimal(balance);
				User user = userDAO.getUserByName(username);
				System.out.println(user);
				userDAO.addBalance(user, balanceToAdd);
				System.out.println(user.getBalance().add(balanceToAdd));
				jsonObject.put("success", true);
				
			
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("errorMessage", "This error occured.");
			return Response.status(400).entity(jsonObject.toString()).build();
		}
		return Response.status(200).entity(jsonObject.toString()).build();
		
	}
	@POST
	@Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUser(@FormParam("username") String username, @FormParam("balance") String balance, @FormParam("password") String password, @FormParam("type") String type)  throws JSONException {
		JSONObject jsonObject = new JSONObject();
		
		if(this.loggedInUser == null|| this.loggedInUser.getType().equals(UserType.CUSTOMER)){
			return returnForbiddenResponse(jsonObject);
		}
		else if(username == null || balance == null || password == null || type == null) {
			return returnSuccessFalse(jsonObject);
		}
		else if(username.length()>10 || username.isEmpty()){
			return returnSuccessFalse(jsonObject);
		}
		else if(password.length() >10 || password.length() < 7){
			return returnSuccessFalse(jsonObject);
		}
		else if(balance.isEmpty() || !balance.matches("[0-9]+")){
			return returnSuccessFalse(jsonObject);
		}
		else if(!type.equals(UserType.ADMIN.toString()) || !type.equals(UserType.CUSTOMER.toString())){
			return returnSuccessFalse(jsonObject);
		}
		
		
		try {
				BigDecimal newBalance = new BigDecimal(balance);
				User newUser = new User(username, password, newBalance, UserType.valueOf(type.toUpperCase()));
				if(newUser.persist()){
					jsonObject.put("success", true);
				}
				else{
					jsonObject.put("error", "User already exists.");
					return Response.status(200).entity(jsonObject.toString()).build();
				}
				
			
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("error", "This error occured.");
			return Response.status(400).entity(jsonObject.toString()).build();
		}
		return Response.status(200).entity(jsonObject.toString()).build();
		
	}
	@Path("/event")
	@POST
	@Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createEvent(@FormParam("eventCode") String eventCode, @FormParam("sportsCode") String sportsCode, @FormParam("startDate") String startDate)  throws JSONException {
		JSONObject jsonObject = new JSONObject();
		
		if(this.loggedInUser == null|| this.loggedInUser.getType().equals(UserType.CUSTOMER)){
			return returnForbiddenResponse(jsonObject);
		}
		else if(eventCode == null || sportsCode == null || startDate == null) {
			return returnSuccessFalse(jsonObject);
		}
		else if(eventCode.length()!=5 || eventCode.contains(" ")){
			return returnSuccessFalse(jsonObject);
		}
		else if(getSportsCode(sportsCode).equals(null)){
			return returnSuccessFalse(jsonObject);
		}
		
		
		try {
				DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:SS");
				Date eventStartDate = simpleDateFormat.parse(startDate);
				Event newEvent = new Event(eventCode, SportsCategory.valueOf(sportsCode), eventStartDate);
				System.out.println(eventCode);
				newEvent.persist();
				jsonObject.put("success", true);
			
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("error", "This error occured.");
			return Response.status(400).entity(jsonObject.toString()).build();
		}
		return Response.status(200).entity(jsonObject.toString()).build();
		
	}

	private Response returnSuccessFalse(JSONObject jsonObject) {
		jsonObject.put("success", false);
		return Response.status(200).entity(jsonObject.toString()).build();
	}
	private Response returnForbiddenResponse(JSONObject jsonObject) {
		jsonObject.put("Forbidden", "Log in as admin.");
		return Response.status(Status.FORBIDDEN).entity(jsonObject.toString()).build();
	}
	private SportsCategory getSportsCode(String sportsCode){
		for(SportsCategory sportsCategory: SportsCategory.values()){
			if(sportsCategory.equals(SportsCategory.valueOf(sportsCode))){
				return sportsCategory;
			}
		}
		return null;
	}
}
