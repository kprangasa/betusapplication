package com.lotus.betus;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import com.lotus.dao.UserDao;
import com.lotus.dao.UserOJDBCDAO;
import com.lotus.users.User;

@Path("/user")
public class betusRestApi {

	
		private UserDao userDAO = UserOJDBCDAO.getInstance();
		
		@GET
		@Produces("application/json")
		public Response list() throws JSONException, JsonGenerationException, JsonMappingException, IOException {
			List<User> animals = userDAO.listUsers();
			ObjectMapper mapper = new ObjectMapper();
			String response = "{}"; 
			if(!animals.isEmpty()) {
				response = mapper.writeValueAsString(animals);
			}
			return Response.status(200).entity(response).build();
		}

		@Path("{name}")
		@GET
		@Produces("application/json")
		public Response show(@PathParam("name") String name) throws JSONException, JsonGenerationException, JsonMappingException, IOException {
			User user = userDAO.getUserByName(name);
			ObjectMapper mapper = new ObjectMapper();
			String response = "{}";
			
			if(user != null) {
				response = mapper.writeValueAsString(user);
			}
			return Response.status(200).entity(response).build();
		}
}
