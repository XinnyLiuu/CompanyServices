package api.service;

import api.business.DepartmentBusiness;
import api.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import companydata.Department;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("CompanyServices")
public class DepartmentServices {
	private DepartmentBusiness deptBL;

	public DepartmentServices() {
		this.deptBL = new DepartmentBusiness();
	}

	/**
	 * Returns the requested Department as a JSON String.
	 *
	 * @param company      rit username
	 * @param departmentId department id
	 * @return json of fetched Department
	 */
	@Path("department")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDepartment(@QueryParam("company") String company,
	                              @QueryParam("dept_id") String departmentId) {
		Department d = deptBL.getDepartment(company, Integer.parseInt(departmentId));

		// Check value returned from BL
		if (d != null) {
			String json = String.format(deptBL.getJSONTemplate(), d.getId(), d.getCompany(), d.getDeptName(), d.getDeptNo(), d.getLocation());

			return Response.ok(json).build();
		}

		return Response.ok(deptBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the requested list of Departments.
	 *
	 * @param company rit username
	 * @return json list of Departments
	 */
	@Path("departments")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDepartments(@QueryParam("company") String company) {
		List<Department> departments = deptBL.getDepartments(company);

		// Check length of list
		if (departments.size() > 0) {
			StringBuilder json = new StringBuilder();
			json.append("[");

			departments.forEach(d -> {
				json.append(String.format(deptBL.getJSONTemplate(), d.getId(), d.getCompany(), d.getDeptName(),
						d.getDeptNo(), d.getLocation()));
				json.append(",");
			});

			json.setLength(json.length() - 1); // Remove trailing comma
			json.append("]");

			return Response.ok(json.toString()).build();
		}

		return Response.ok(deptBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the updated Department as a JSON String.
	 *
	 * @param inputJson json containing department fields to be updated
	 * @return json of updated Department
	 */
	@Path("department")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateDepartment(String inputJson) {
		// Parse the JSON using jackson
		ObjectMapper om = new ObjectMapper();

		try {
			// Pass the JsonNode to BL to validate and process
			JsonNode node = om.readTree(inputJson);
			Department d = deptBL.updateDepartment(node);

			if (d != null) {
				String json = "{\"success\": " + String.format(deptBL.getJSONTemplate(), d.getId(), d.getCompany(), d.getDeptName(), d.getDeptNo(), d.getLocation()) + "}";

				return Response.ok(json).build();
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.ok(Constants.JSON_PARSE_ERROR).status(400).build();
		}

		return Response.ok(deptBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the new Department as a JSON String.
	 *
	 * @param company        rit username
	 * @param departmentName name of department
	 * @param departmentNo   no of department
	 * @param location       location
	 * @return json of created Department
	 */
	@Path("department")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createDepartment(@FormParam("company") String company,
	                                 @FormParam("dept_name") String departmentName,
	                                 @FormParam("dept_no") String departmentNo,
	                                 @FormParam("location") String location) {
		Department d = deptBL.createDepartment(company, departmentName, departmentNo, location);

		if (d != null) {
			String json = "{\"success\": " + String.format(deptBL.getJSONTemplate(), d.getId(), d.getCompany(), d.getDeptName(), d.getDeptNo(), d.getLocation()) + "}";

			return Response.ok(json).build();
		}

		return Response.ok(deptBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the number of rows deleted.
	 *
	 * @param company      rit username
	 * @param departmentId id of department
	 * @return number of rows affected
	 */
	@Path("department")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDepartment(@QueryParam("company") String company,
	                                 @QueryParam("dept_id") String departmentId) {
		int affected = deptBL.deleteDepartment(company, Integer.parseInt(departmentId));

		if (affected == 1) {
			String json = String.format("{\"success\": \"Department %s from %s deleted.\"}", departmentId, company);

			return Response.ok(json).build();
		}

		return Response.ok(deptBL.getErrorJson()).status(400).build();
	}
}
