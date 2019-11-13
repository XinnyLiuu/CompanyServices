package api.service;

import api.business.TimecardBusiness;
import api.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import companydata.Timecard;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Path("CompanyServices")
public class TimecardServices {
	private TimecardBusiness timecardBL;

	public TimecardServices() {
		this.timecardBL = new TimecardBusiness();
	}

	/**
	 * Returns the requested Timecard as a JSON String.
	 *
	 * @param company    rit username
	 * @param timecardId timecard id
	 * @return json of fetched timecard
	 */
	@Path("timecard")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimecard(@QueryParam("company") String company,
	                            @QueryParam("timecard_id") String timecardId) {
		Timecard t = timecardBL.getTimecard(company, Integer.parseInt(timecardId));

		if (t != null) {
			StringBuilder json = new StringBuilder();

			// Convert timestamps to String
			DateFormat df = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
			String start = df.format(t.getStartTime());
			String end = df.format(t.getEndTime());

			json.append(String.format(timecardBL.getJSONTemplate(), t.getId(), start, end, t.getEmpId()));

			return Response.ok(json.toString()).build();
		}

		return Response.ok(timecardBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the requested list of Timecards.
	 *
	 * @param company    rit username
	 * @param employeeId id of employee to query timecards for
	 * @return json list of timecards
	 */
	@Path("timecards")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimecards(@QueryParam("company") String company,
	                             @QueryParam("emp_id") String employeeId) {
		List<Timecard> timecards = timecardBL.getTimecards(company, Integer.parseInt(employeeId));

		// Check length of list
		if (timecards.size() > 0) {
			// Create json
			StringBuilder json = new StringBuilder();
			json.append("[");

			timecards.forEach(t -> {
				// Convert timestamps to String
				DateFormat df = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
				String start = df.format(t.getStartTime());
				String end = df.format(t.getEndTime());

				json.append(String.format(timecardBL.getJSONTemplate(), t.getId(), start, end, t.getEmpId()));
				json.append(",");
			});

			json.setLength(json.length() - 1); // Remove trailing comma
			json.append("]");

			return Response.ok(json.toString()).build();
		}

		return Response.ok(timecardBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the new Timecard as a JSON String.
	 *
	 * @param company    rit username
	 * @param employeeId existing employee id
	 * @param startTime  start_time must be a valid date and time equal to the current date or up to 1 week ago from the
	 *                   current date
	 * @param endTime    end_time must be a valid date and time at least 1 hour greater than the start_time and be on
	 *                   the same day as the start_time
	 * @return json created timecard
	 */
	@Path("timecard")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createTimecard(@FormParam("company") String company,
	                               @FormParam("emp_id") String employeeId,
	                               @FormParam("start_time") String startTime,
	                               @FormParam("end_time") String endTime) {
		Timecard t = timecardBL.createTimecard(company, Integer.parseInt(employeeId), startTime, endTime);

		if (t != null) {
			// Create json
			StringBuilder json = new StringBuilder();

			// Convert timestamps to String
			DateFormat df = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
			String start = df.format(t.getStartTime());
			String end = df.format(t.getEndTime());

			json.append("{ " + "\"success\": ");
			json.append(String.format(timecardBL.getJSONTemplate(), t.getId(), start, end, t.getEmpId()));
			json.append("}");

			return Response.ok(json.toString()).build();
		}

		return Response.ok(timecardBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the updated Timecard as a JSON String.
	 *
	 * @param inputJson json containing timecard fields to be updated
	 * @return json timecard
	 */
	@Path("timecard")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateTimecard(String inputJson) {
		// Parse the JSON using jackson
		ObjectMapper om = new ObjectMapper();

		try {
			// Pass the JsonNode to BL to validate and process
			JsonNode node = om.readTree(inputJson);
			Timecard t = timecardBL.updateTimecard(node);

			if (t != null) {
				// Create json
				StringBuilder json = new StringBuilder();

				// Convert timestamps to String
				DateFormat df = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
				String start = df.format(t.getStartTime());
				String end = df.format(t.getEndTime());

				json.append("{ " + "\"success\": ");
				json.append(String.format(timecardBL.getJSONTemplate(), t.getId(), start, end, t.getEmpId()));
				json.append("}");

				return Response.ok(json.toString()).build();
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.ok(Constants.JSON_PARSE_ERROR).status(400).build();
		}

		return Response.ok(timecardBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the number of rows deleted.
	 *
	 * @param company    rit username
	 * @param timecardId id of timecard
	 * @return number of rows deleted
	 */
	@Path("timecard")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTimecard(@QueryParam("company") String company,
	                               @QueryParam("timecard_id") String timecardId) {
		int affected = timecardBL.deleteTimecard(company, Integer.parseInt(timecardId));

		if (affected == 1) {
			String json = String.format("{\"success\": \"Timecard %s deleted.\"}", timecardId);

			return Response.ok(json).build();
		}

		return Response.ok(timecardBL.getErrorJson()).status(400).build();
	}
}
