package api.business;

import api.utils.Constants;

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
			int affected = dl.deleteCompany(Constants.USERNAME);

			if(affected == 0) return true;
			else if(affected > 0) return true;
		}

		return false;
	}
}
