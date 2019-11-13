package api.service;

import api.business.CompanyBusiness;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("CompanyServices")
public class CompanyServices {
	private CompanyBusiness comBL;

	public CompanyServices() {
		this.comBL = new CompanyBusiness();
	}

	/**
	 * Deletes all Department, Employee and Timecard records in the database for the given company
	 *
	 * @param company rit username
	 * @return json confirmation
	 */
	@Path("company")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCompany(@QueryParam("company") String company) {
		boolean deleted = comBL.deleteAll(company);

		if (deleted) {
			String json = "{\"success\": " + String.format("\"%s's information deleted\"", company) + "}";
			return Response.ok(json).build();
		}

		return Response.ok(comBL.getErrorJson()).status(400).build();
	}
}
