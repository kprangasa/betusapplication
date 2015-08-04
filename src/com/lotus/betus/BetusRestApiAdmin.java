package com.lotus.betus;

import java.io.IOException;
import java.math.BigDecimal;
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

import com.lotus.dao.UserDao;
import com.lotus.dao.UserOJDBCDAO;
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

		if(this.loggedInUser == null|| this.loggedInUser.getType() == UserType.CUSTOMER|| user.isEmpty()){
			return returnForbiddenResponse(jsonObject);
		}
		else {
			response = mapper.writeValueAsString(user);
			return Response.status(200).entity(response).build();
		}
		
	}

	private Response returnForbiddenResponse(JSONObject jsonObject) {
		jsonObject.put("success", false);
		return Response.status(Status.FORBIDDEN).entity(jsonObject.toString()).build();
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
		else if(this.loggedInUser == null|| this.loggedInUser.getType() == UserType.CUSTOMER){
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
		
		if(this.loggedInUser == null|| this.loggedInUser.getType() == UserType.CUSTOMER){
			return returnForbiddenResponse(jsonObject);
		}
		else if(username == null || username.isEmpty()|| balance.isEmpty()|| balance == null ) {
			jsonObject.put("success", false);
			return Response.status(200).entity(jsonObject.toString()).build();
		}
		else if(!balance.matches("[0-9]+")){
			jsonObject.put("success", false);
			return Response.status(200).entity(jsonObject.toString()).build();
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
			
		}
		return Response.status(200).entity(jsonObject.toString()).build();
		
	}
	@POST
	@Produces("application/json")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUser(@FormParam("username") String username, @FormParam("balance") String balance, @FormParam("password") String password, @FormParam("type") String type)  throws JSONException {
		JSONObject jsonObject = new JSONObject();
		
		if(this.loggedInUser == null|| this.loggedInUser.getType() == UserType.CUSTOMER){
			return returnForbiddenResponse(jsonObject);
		}
		else if(username == null || username.length() > 10 || username.isEmpty()) {
			jsonObject.put("success", false);
			return Response.status(200).entity(jsonObject.toString()).build();
		}
		else if(password.length() >10 || password.length() < 7 || password.isEmpty() ||password == null){
			jsonObject.put("success", false);
			return Response.status(200).entity(jsonObject.toString()).build();
		}
		else if(balance.isEmpty()|| balance == null || !balance.matches("[0-9]+")){
			jsonObject.put("success", false);
			return Response.status(200).entity(jsonObject.toString()).build();
		}
		else if(!type.equals(UserType.ADMIN.toString()) || !type.equals(UserType.CUSTOMER.toString())){
			jsonObject.put("success", false);
			return Response.status(200).entity(jsonObject.toString()).build();
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
			jsonObject.put("errorMessage", "This error occured.");
			
		}
		return Response.status(200).entity(jsonObject.toString()).build();
		
	}
}
