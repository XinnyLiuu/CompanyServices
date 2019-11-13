package api.business;

import api.utils.Constants;
import api.utils.DateValidator;
import com.fasterxml.jackson.databind.JsonNode;
import companydata.Employee;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class EmployeeBusiness extends BusinessLayer {
	public EmployeeBusiness() {
		super(Constants.USERNAME, Constants.EMPLOYEE_TEMPLATE, Constants.ERROR_JSON);
	}

	/**
	 * Checks if there are no existing employees of the company
	 *
	 * @param company rit username
	 * @return boolean
	 */
	private boolean checkIsFirstEmployee(String company) {
		List<Employee> employees = dl.getAllEmployee(company);

		return employees.size() == 0;
	}

	/**
	 * Checks if the input employee id is an existing employee
	 *
	 * @param company    rit username
	 * @param employeeId id of employee
	 * @return boolean
	 */
	private boolean checkIsExistingEmployee(String company, int employeeId) {
		List<Employee> employees = dl.getAllEmployee(company);

		return employees.stream().anyMatch(e -> e.getId() == employeeId);
	}

	/**
	 * Checks if the input employee no is an unique employee no
	 *
	 * @param company    rit username
	 * @param employeeNo no of employee
	 * @return boolean
	 */
	private boolean checkIsUniqueEmployeeNo(String company, String employeeNo, int employeeId) {
		List<Employee> employees = dl.getAllEmployee(company);

		// Remove the current employee from the list since we want to check if the employee no is unique in comparison of other employees
		if (employeeId != 0) {
			int index = 0;
			for (int i = 0; i < employees.size(); i++) {
				Employee e = employees.get(i);

				if (e.getEmpNo().equals(employeeNo) && e.getId() == employeeId) {
					index = i;
				}
			}
			employees.remove(index);
		}

		return employees.stream().noneMatch(e -> e.getEmpNo().equals(employeeNo));
	}

	/**
	 * Get employee by company + id
	 *
	 * @param company    rit username
	 * @param employeeId empl id
	 * @return Employee
	 */
	public Employee getEmployee(String company, int employeeId) {
		// Check inputs
		if (company.equals(Constants.USERNAME) && employeeId > 0) {
			Employee e = dl.getEmployee(employeeId);

			if (e == null) {
				setErrorJson("{\"error\": \"Could not find the employee!\"}");
			}

			return e;
		}

		return null;
	}

	/**
	 * Gets list of employees from DL
	 *
	 * @param company rit username
	 * @return list of Employees
	 */
	public List<Employee> getEmployees(String company) {
		List<Employee> e = new ArrayList<>();

		// Check input
		if (company.equals(Constants.USERNAME)) {
			e = dl.getAllEmployee(company);

			if (e.size() == 0) {
				setErrorJson("{\"error\": \"There are no employees!\"}");
			}

			return e;
		}

		return e;
	}

	/**
	 * Inserts an employee into the database
	 *
	 * @param company      rit username
	 * @param employeeName name of employee
	 * @param employeeNo   no of employee
	 * @param hireDate     string of date hired
	 * @param job          job name
	 * @param salary       salary
	 * @param departmentId id of department
	 * @param managementId id of management
	 * @return created Employee
	 */
	public Employee createEmployee(String company, String employeeName, String employeeNo, String hireDate, String job, double salary, int departmentId, int managementId) {

		// Check company, existing department, management id, valid hire date and that the employee no must be unique among all companies
		DateValidator dv = new DateValidator(Constants.HIREDATE_FORMAT);

		if (company.equals(Constants.USERNAME) &&
				dl.getDepartment(company, departmentId) != null &&
				dv.checkHireDate(hireDate)) {

			// Check if first employee
			if (checkIsFirstEmployee(company)) {
				managementId = 0;
			}

			// Check if manager exists
			if (managementId != 0 && !checkIsExistingEmployee(company, managementId)) {
				setErrorJson("{\"error\": \"Manager does not exist!\"}");
				return null;
			}

			// Check the employee no
			if (!checkIsUniqueEmployeeNo(company, employeeNo, 0)) {
				setErrorJson("{\"error\": \"Employee No already exists!\"}");
				return null;
			}

			// Create employee to be inserted
			Date date = new Date(dv.stringToDate(hireDate).getTime());
			Employee e = new Employee(employeeName, employeeNo, date, job, salary, departmentId, managementId);
			e = dl.insertEmployee(e);

			if (e == null) {
				setErrorJson("{\"error\": \"Could not create employee!\"}");
			}

			return e;
		}

		if (!dv.checkHireDate(hireDate)) {
			setErrorJson("{\"error\": \"Error with the hire date!\"}");
		}

		return null;
	}

	/**
	 * Updates an existing employee
	 *
	 * @param node JsonNode object
	 * @return updated Employee
	 */
	public Employee updateEmployee(JsonNode node) {
		// Grab all possible JSON values
		String company = node.get("company").asText();
		int employeeId = node.get("emp_id").asInt();
		String employeeName = node.get("emp_name").asText();
		String employeeNo = node.get("emp_no").asText();
		String hireDate = node.get("hire_date").asText();
		String job = node.get("job").asText();
		double salary = node.get("salary").asDouble();
		int departmentId = node.get("dept_id").asInt();
		int managementId = node.get("mng_id").asInt();

		// Check is the same as createEmployee(), but also check if the employee exists in the db
		DateValidator dv = new DateValidator(Constants.HIREDATE_FORMAT);

		if (company.equals(Constants.USERNAME) &&
				dl.getEmployee(employeeId) != null &&
				dl.getDepartment(company, departmentId) != null &&
				dv.checkHireDate(hireDate)) {

			// Check if first employee
			if (checkIsFirstEmployee(company)) {
				managementId = 0;
			}

			// Check if manager exists
			if (managementId != 0 && !checkIsExistingEmployee(company, managementId)) {
				setErrorJson("{\"error\": \"Manager does not exist!\"}");
				return null;
			}

			// Check the employee no
			if (!checkIsUniqueEmployeeNo(company, employeeNo, employeeId)) {
				setErrorJson("{\"error\": \"Employee No already exists!\"}");
				return null;
			}

			// Update the existing employee
			Date date = new Date(dv.stringToDate(hireDate).getTime());

			Employee e = dl.getEmployee(employeeId);
			e.setEmpName(employeeName);
			e.setEmpNo(employeeNo);
			e.setHireDate(date);
			e.setJob(job);
			e.setSalary(salary);
			e.setDeptId(departmentId);
			e.setMngId(managementId);

			e = dl.updateEmployee(e);
			if (e == null) {
				setErrorJson("{\"error\": \"Could not update the employee!\"}");
			}

			return e;
		}

		if (!dv.checkHireDate(hireDate)) {
			setErrorJson("{\"error\": \"Error with the hire date!\"}");
		}

		return null;
	}

	/**
	 * Deletes an employee from the database
	 *
	 * @param company    rit username
	 * @param employeeId id of employee
	 * @return number of rows affected
	 */
	public int deleteEmployee(String company, int employeeId) {
		// Check company + employee exists
		Employee e = dl.getEmployee(employeeId);

		if (company.equals(Constants.USERNAME) && e != null) {
			int affected = dl.deleteEmployee(employeeId);

			if (affected == 0) {
				setErrorJson("{\"error\": \"Could not delete the employee!\"}");
			}

			return affected;
		}

		setErrorJson("{\"error\": \"Could not delete the employee!\"}");
		return 0;
	}
}
