package api.business;

import api.utils.Constants;
import api.utils.DateValidator;
import com.fasterxml.jackson.databind.JsonNode;
import companydata.Employee;
import companydata.Timecard;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TimecardBusiness extends BusinessLayer {
	public TimecardBusiness() {
		super(Constants.USERNAME, Constants.TIMECARD_TEMPLATE, Constants.ERROR_JSON);
	}

	/**
	 * Get timecard by company + id
	 *
	 * @param company    rit username
	 * @param timecardId timecard id
	 * @return Timecard
	 */
	public Timecard getTimecard(String company, int timecardId) {
		// Check inputs
		if (company.equals(Constants.USERNAME) && timecardId > 0) {
			Timecard t = dl.getTimecard(timecardId);

			if (t == null) {
				setErrorJson("{\"error\": \"Could not find timecard!\"}");
			}

			return t;
		}

		return null;
	}

	/**
	 * Gets list of timecards from database based on employee id
	 *
	 * @param company    rit username
	 * @param employeeId employee id
	 * @return list of Timecards for specified employee
	 */
	public List<Timecard> getTimecards(String company, int employeeId) {
		List<Timecard> timecards = new ArrayList<>();

		// Check inputs
		if (company.equals(Constants.USERNAME) && employeeId > 0) {
			timecards = dl.getAllTimecard(employeeId);

			if (timecards.size() == 0) {
				setErrorJson("{\"error\": \"There are no timecards!\"}");
			}

			return timecards;
		}

		return timecards;
	}

	/**
	 * Inserts data entered to endpoint to create a Timecard object
	 *
	 * @param company    rit username
	 * @param employeeId existing employee id
	 * @param startTime  start_time must be a valid date and time equal to the current date or up to 1 week ago from the
	 *                   current date
	 * @param endTime    end_time must be a valid date and time at least 1 hour greater than the start_time and be on
	 *                   the same day as the start_time
	 * @return created Timecard
	 */
	public Timecard createTimecard(String company, int employeeId, String startTime, String endTime) {
		Employee e = dl.getEmployee(employeeId);
		DateValidator dv = new DateValidator(Constants.TIMESTAMP_FORMAT);

		// Check company, employee exists and validate timestamps
		if (company.equals(Constants.USERNAME) &&
				e != null &&
				dv.validateTimestamps(startTime, endTime)) {

			// Create timecard POJO
			Timestamp start = new Timestamp(dv.stringToDate(startTime).getTime());
			Timestamp end = new Timestamp(dv.stringToDate(endTime).getTime());
			Timecard t = new Timecard(start, end, employeeId);
			t = dl.insertTimecard(t);

			if (t == null) {
				setErrorJson("{\"error\": \"Could not create timecard!\"}");
			}

			return t;
		}

		if (e == null) {
			setErrorJson("{\"error\": \"Employee does not exist!\"}");
		}

		if (!dv.validateTimestamps(startTime, endTime)) {
			setErrorJson("{\"error\": \"Error with timestamps!\"}");
		}

		return null;
	}

	/**
	 * Updates a timecard
	 *
	 * @param node JsonNode object that contains values of input json from service layer
	 * @return updated Timecard
	 */
	public Timecard updateTimecard(JsonNode node) {
		// Grab all possible JSON values
		String company = node.get("company").asText();
		int timecardId = node.get("timecard_id").asInt();
		String startTime = node.get("start_time").asText();
		String endTime = node.get("end_time").asText();
		int empId = node.get("emp_id").asInt();

		Employee e = dl.getEmployee(empId);
		Timecard t = dl.getTimecard(timecardId);

		DateValidator dv = new DateValidator(Constants.TIMESTAMP_FORMAT);

		// Check if company, employee and timecard exists and validate timestamps
		if (company.equals(Constants.USERNAME) &&
				e != null &&
				t != null &&
				dv.validateTimestamps(startTime, endTime)) {

			// Update timecard
			Timestamp start = new Timestamp(dv.stringToDate(startTime).getTime());
			Timestamp end = new Timestamp(dv.stringToDate(endTime).getTime());
			t.setStartTime(start);
			t.setEndTime(end);
			t.setEmpId(empId);
			t.setId(timecardId);
			t = dl.updateTimecard(t);

			if (t == null) {
				setErrorJson("{\"error\": \"Could not update timecard!\"}");
			}

			return t;
		}

		if (e == null) {
			setErrorJson("{\"error\": \"Employee does not exist!\"}");
		}

		if (t == null) {
			setErrorJson("{\"error\": \"Timecard does not exist!\"}");
		}

		if (!dv.validateTimestamps(startTime, endTime)) {
			setErrorJson("{\"error\": \"Error with timestamps!\"}");
		}

		return null;
	}

	/**
	 * Deletes a timecard
	 *
	 * @param company    rit username
	 * @param timecardId id of timecard
	 * @return number of rows affected
	 */
	public int deleteTimecard(String company, int timecardId) {
		// Check company + timecard exists
		Timecard t = dl.getTimecard(timecardId);

		if (company.equals(Constants.USERNAME) && t != null) {
			int affected = dl.deleteTimecard(timecardId);

			if (affected == 0) {
				setErrorJson("{\"error\": \"Could not delete timecard!\"}");
			}

			return affected;
		}

		setErrorJson("{\"error\": \"Could not delete timecard!\"}");
		return 0;
	}
}
