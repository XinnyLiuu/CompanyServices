package api.service;

import api.business.EmployeeBusiness;
import api.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import companydata.Employee;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Path("CompanyServices")
public class EmployeeServices {
	private EmployeeBusiness emplBL;

	public EmployeeServices() {
		this.emplBL = new EmployeeBusiness();
	}

	/**
	 * Returns the requested Employee as a JSON String.
	 *
	 * @param company    rit username
	 * @param employeeId employee id
	 * @return json of fetched Employee
	 */
	@Path("employee")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmployee(@QueryParam("company") String company,
	                            @QueryParam("emp_id") String employeeId) {
		Employee e = emplBL.getEmployee(company, Integer.parseInt(employeeId));

		if (e != null) {
			StringBuilder json = new StringBuilder();

			// Convert date to string
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String hireDate = df.format(e.getHireDate());

			json.append(String.format(emplBL.getJSONTemplate(), e.getId(), e.getEmpName(), e.getEmpNo(), hireDate,
					e.getJob(), e.getSalary(), e.getDeptId(), e.getMngId()));

			return Response.ok(json.toString()).build();
		}

		return Response.ok(emplBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the requested list of Employees.
	 *
	 * @param company rit username
	 * @return json list of employees
	 */
	@Path("employees")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmployees(@QueryParam("company") String company) {
		List<Employee> employees = emplBL.getEmployees(company);

		// Check length of list
		if (employees.size() > 0) {
			// Create json
			StringBuilder json = new StringBuilder();
			json.append("[");

			employees.forEach(e -> {
				// Convert date to string
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String hireDate = df.format(e.getHireDate());

				json.append(String.format(emplBL.getJSONTemplate(), e.getId(), e.getEmpName(), e.getEmpNo(), hireDate,
						e.getJob(), e.getSalary(), e.getDeptId(), e.getMngId()));
				json.append(",");
			});

			json.setLength(json.length() - 1); // Remove trailing comma
			json.append("]");

			return Response.ok(json.toString()).build();
		}

		return Response.ok(emplBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the new Employee as a JSON String.
	 *
	 * @param company      rit username
	 * @param employeeName name of employee
	 * @param employeeNo   no of employee
	 * @param hireDate     string of date hired
	 * @param job          job name
	 * @param salary       salary
	 * @param departmentId id of department
	 * @param managementId id of management
	 * @return json of created Employee
	 */
	@Path("employee")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEmployee(@FormParam("company") String company,
	                               @FormParam("emp_name") String employeeName,
	                               @FormParam("emp_no") String employeeNo,
	                               @FormParam("hire_date") String hireDate,
	                               @FormParam("job") String job,
	                               @FormParam("salary") String salary,
	                               @FormParam("dept_id") String departmentId,
	                               @FormParam("mng_id") String managementId) {
		Employee e = emplBL.createEmployee(company, employeeName, employeeNo, hireDate, job, Double.parseDouble(salary), Integer.parseInt(departmentId), Integer.parseInt(managementId));

		if (e != null) {
			// Create json
			StringBuilder json = new StringBuilder();

			// Convert date to string
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String hire = df.format(e.getHireDate());

			json.append("{\"success\": ");
			json.append(String.format(emplBL.getJSONTemplate(), e.getId(), e.getEmpName(), e.getEmpNo(), hire,
					e.getJob(), e.getSalary(), e.getDeptId(), e.getMngId()));
			json.append("}");

			return Response.ok(json.toString()).build();
		}

		return Response.ok(emplBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the updated Employee as a JSON String.
	 *
	 * @param inputJson json string containg updated employee fields
	 * @return json of updated Employee
	 */
	@Path("employee")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEmployee(String inputJson) {
		// Parse the JSON using jackson
		ObjectMapper om = new ObjectMapper();

		try {
			// Pass the JsonNode to BL to validate and process
			JsonNode node = om.readTree(inputJson);
			Employee e = emplBL.updateEmployee(node);

			if (e != null) {
				// Convert date to string
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String hire = df.format(e.getHireDate());

				String json = "{\"success\": " + String.format(emplBL.getJSONTemplate(), e.getId(), e.getEmpName(), e.getEmpNo(), hire, e.getJob(), e.getSalary(), e.getDeptId(), e.getMngId()) + "}";

				return Response.ok(json).build();
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.ok(Constants.JSON_PARSE_ERROR).status(400).build();
		}

		return Response.ok(emplBL.getErrorJson()).status(400).build();
	}

	/**
	 * Returns the that the employee deleted.
	 *
	 * @param company    rit username
	 * @param employeeId id of employee
	 * @return json of deleted Employee
	 */
	@Path("employee")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEmployee(@QueryParam("company") String company,
	                               @QueryParam("emp_id") String employeeId) {
		int affected = emplBL.deleteEmployee(company, Integer.parseInt(employeeId));

		if (affected == 1) {
			String json = String.format("{\"success\": \"Employee %s deleted.\"}", employeeId);

			return Response.ok(json).build();
		}

		return Response.ok(emplBL.getErrorJson()).status(400).build();
	}
}
