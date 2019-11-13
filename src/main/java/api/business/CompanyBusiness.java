package api.business;

import api.utils.Constants;
import companydata.Department;
import companydata.Employee;
import companydata.Timecard;

import java.util.List;

public class CompanyBusiness extends BusinessLayer {
	public CompanyBusiness() {
		super(Constants.USERNAME, null, Constants.ERROR_JSON);
	}

	/**
	 * Deletes all Department, Employee and Timecard records in the database for the given company
	 *
	 * @param company rit username
	 * @return boolean
	 */
	public boolean deleteAll(String company) {
		if (company.equals(Constants.USERNAME)) {
			// Grab all the dta from every table
			List<Department> departments = dl.getAllDepartment(company);
			List<Employee> employees = dl.getAllEmployee(company);

			employees.forEach(e -> {
				dl.deleteEmployee(e.getId());

				List<Timecard> timecards = dl.getAllTimecard(e.getId());
				timecards.forEach(t -> {
					dl.deleteTimecard(t.getId());
				});
			});

			departments.forEach(d -> {
				dl.deleteDepartment(company, d.getId());
			});

			// Check final size of lists
			departments = dl.getAllDepartment(company);
			employees = dl.getAllEmployee(company);

			if (departments.size() == 0 && employees.size() == 0) return true;
		}

		return false;
	}
}
