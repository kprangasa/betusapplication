package com.lotus.betus;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.lotus.userdao.UserDao;
import com.lotus.userdao.UserOJDBCDAO;
import com.lotus.users.User;

@Path("/logins")
public class BetusRestApi {

		protected static User loggedInUser = null;
		private UserDao userDAO = UserOJDBCDAO.getInstance();
//		@Path("/admin/users")
//		@GET
//		@Produces("application/json")
//		public Response list() throws JsonGenerationException, JsonMappingException, IOException {
//			System.out.println(this.loggedInUser);
//			List<User> user = userDAO.listUsers();
//			ObjectMapper mapper = new ObjectMapper();
//			String response = "{}"; 
//			if(!user.isEmpty()) {
//				response = mapper.writeValueAsString(user);
//			}
//			return Response.status(200).entity(response).build();
//		}
//
//		@Path("{name}")
//		@GET
//		@Produces("application/json")
//		public Response show(@PathParam("name") String name) throws JSONException, JsonGenerationException, JsonMappingException, IOException {
//			User user = userDAO.getUserByName(name);
//			ObjectMapper mapper = new ObjectMapper();
//			String response = "{}";
//			
//			if(user != null) {
//				response = mapper.writeValueAsString(user);
//			}
//			return Response.status(200).entity(response).build();
//		}
		
		
		@POST
		@Produces("application/json")
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		public Response login(@FormParam("username") String username, @FormParam("password") String password)  throws JSONException {
			JSONObject jsonObject = new JSONObject();
			if(username == null || password == null) {
				jsonObject.put("error", "bad request, username and password parameter is required");
				return Response.status(400).entity(jsonObject.toString()).build();
			}
			else if(userDAO.getUserByName(username) == null ){
				jsonObject.put("success", false);
				return Response.status(Status.BAD_REQUEST).entity(jsonObject.toString()).build();
			}
			
			try {
				if(userDAO.getUserByName(username).getPassword().equals(password)){
					BetusRestApi.loggedInUser = userDAO.getUserByName(username);
					jsonObject.put("success", true);
				}
				else{
					jsonObject.put("error", "invalid password");
					return Response.status(200).entity(jsonObject.toString()).build();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				jsonObject.put("success", false);
				jsonObject.put("errorMessage", "Cannot login user.");
				return Response.status(400).entity(jsonObject.toString()).build();
			}

			return Response.status(200).entity(jsonObject.toString()).build();
		}


		protected static User getLoggedInUser() {
			if(BetusRestApi.loggedInUser == null){
				return null;
			}
			return BetusRestApi.loggedInUser;
		}

		
		
		
}
