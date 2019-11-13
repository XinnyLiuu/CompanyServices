package api.business;

import api.utils.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import companydata.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentBusiness extends BusinessLayer {
	public DepartmentBusiness() {
		super(Constants.USERNAME, Constants.DEPARTMENT_TEMPLATE, Constants.ERROR_JSON);
	}

	/**
	 * Checks the department no against all current department no(s) to check if the new department no is unique
	 *
	 * @param company      rit username
	 * @param departmentNo department no
	 * @return boolean
	 */
	private boolean checkIsUniqueDepartmentNo(String company, String departmentNo, int departmentId) {
		List<Department> departments = dl.getAllDepartment(company);

		// Remove the current department from the list since we want to check if the department no is unique in comparison of other departments
		if (departmentId != 0) {
			int index = 0;
			for (int i = 0; i < departments.size(); i++) {
				Department d = departments.get(i);

				if (d.getDeptNo().equals(departmentNo) && d.getId() == departmentId) {
					index = i;
				}
			}
			departments.remove(index);
		}

		// Now check if the department no is unique
		boolean unique = departments.stream().noneMatch(d -> d.getDeptNo().equals(departmentNo));

		if (!unique) setErrorJson("{\"error\": \"Department No is not unique!\"}");
		return unique;
	}

	/**
	 * Get department by company + id
	 *
	 * @param company      rit username
	 * @param departmentId dept id
	 * @return Department
	 */
	public Department getDepartment(String company, int departmentId) {
		// Check inputs
		if (company.equals(Constants.USERNAME) && departmentId > 0) {
			Department d = dl.getDepartment(company, departmentId);

			if (d == null) {
				setErrorJson("{\"error\": \"Could not find the department!\"}");
			}

			return d;
		}

		return null;
	}

	/**
	 * Gets list of departments from DL
	 *
	 * @param company rit username
	 * @return list of Departments
	 */
	public List<Department> getDepartments(String company) {
		List<Department> d = new ArrayList<>();

		// Check input
		if (company.equals(Constants.USERNAME)) {
			d = dl.getAllDepartment(company);

			if (d.size() == 0) {
				setErrorJson("{\"error\": \"There are no departments!\"}");
			}

			return d;
		}

		return d;
	}

	/**
	 * Updates a department
	 *
	 * @param node JsonNode object that contains values of input json from service layer
	 * @return updated Department
	 */
	public Department updateDepartment(JsonNode node) {
		// Grab all possible JSON values
		String company = node.get("company").asText();
		int departmentId = node.get("dept_id").asInt();
		String departmentName = node.get("dept_name").asText();
		String departmentNo = node.get("dept_no").asText();
		String location = node.get("location").asText();

		// Check company name, if department exists and if the specified dept_no is unique
		if (company.equals(Constants.USERNAME) &&
				dl.getDepartment(company, departmentId) != null &&
				checkIsUniqueDepartmentNo(company, departmentNo, departmentId)) {

			// Update department
			Department d = dl.getDepartment(company, departmentId);
			d.setCompany(company);
			d.setDeptName(departmentName);
			d.setDeptNo(departmentNo);
			d.setLocation(location);
			d = dl.updateDepartment(d);

			if (d == null) {
				setErrorJson("{\"error\": \"Could not update the department!\"}");
			}

			return d;
		}

		return null;
	}

	/**
	 * Inserts a department into the database
	 *
	 * @param company        rit username
	 * @param departmentName name of department
	 * @param departmentNo   no of department
	 * @param location       location
	 * @return created Department
	 */
	public Department createDepartment(String company, String departmentName, String departmentNo, String location) {
		// Check inputs
		if (company.equals(Constants.USERNAME) &&
				checkIsUniqueDepartmentNo(company, departmentNo, 0)) {

			// Create department
			Department d = new Department(company, departmentName, departmentNo, location);
			d = dl.insertDepartment(d);

			if (d == null) {
				setErrorJson("{\"error\": \"Could not create the department!\"}");
			}

			return d;
		}

		return null;
	}

	/**
	 * Deletes a department
	 *
	 * @param company      rit username
	 * @param departmentId id of department
	 * @return number of rows affected
	 */
	public int deleteDepartment(String company, int departmentId) {
		// Check company + department exists
		Department d = dl.getDepartment(company, departmentId);

		if (company.equals(Constants.USERNAME) && d != null) {
			int affected = dl.deleteDepartment(company, departmentId);

			if (affected == 0) {
				setErrorJson("{\"error\": \"Could not delete the department!\"}");
			}

			return affected;
		}

		setErrorJson("{\"error\": \"Could not delete the department!\"}");
		return 0;
	}
}
