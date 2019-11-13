package api.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Validates a String to see if the date is a valid date
 */
public class DateValidator {
	private DateFormat df;

	public DateValidator(String dateFormat) {
		this.df = new SimpleDateFormat(dateFormat);
	}

	/**
	 * Validates a date
	 *
	 * @param date date in string
	 * @return boolean
	 */
	private boolean isValid(String date) {
		df.setLenient(false);

		try {
			df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Checks if the hire date is equal to current date or less than, if the date is on a Monday, Tuesday, Wednesday,
	 * Thursday or Friday
	 *
	 * @param hireDate hiredate string
	 * @return boolean
	 */
	public boolean checkHireDate(String hireDate) {
		// Get current
		Date date = new Date();

		try {
			Date current = df.parse(df.format(date));
			Date hire = df.parse(hireDate);

			// Convert to time in milliseconds
			long currTime = current.getTime();
			long hireTime = hire.getTime();

			// Calendar.DAY_OF_WEEK returns 2 - 6 for M - F
			int hireDay = dateToCal(hire).get(Calendar.DAY_OF_WEEK);

			if ((hireDay >= 2 && hireDay <= 6) &&
					hireTime <= currTime) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * Checks if start_time must be a valid date and time equal to the current date or up to 1 week ago from the current
	 * date and end_time must be a valid date and time at least 1 hour greater than the start_time and be on the same
	 * day as the start_time
	 *
	 * @param start start time
	 * @param end   end time
	 * @return boolean
	 */
	public boolean validateTimestamps(String start, String end) {
		// Check if timestamps for valid
		if (isValid(start) && isValid(end)) {
			Date date = new Date();

			// Check if start time is equal to current date or up to 1 week ago
			try {
				// Get current date
				Date current = df.parse(df.format(date));

				// Get start and end date
				Date startDate = df.parse(start);
				Date endDate = df.parse(end);

				// Convert to time (milliseconds)
				long currTime = current.getTime();
				long currMinusOneWeek = currTime - 604800000;
				long startTime = startDate.getTime();
				long endTime = endDate.getTime();
				long startTimePlusOneHour = startTime + 3600000;

				if (startTime >= currMinusOneWeek && startTime <= currTime) {

					// Convert the dates to calendar
					int startDay = dateToCal(startDate).get(Calendar.DATE);
					int endDay = dateToCal(endDate).get(Calendar.DATE);

					int startMonth = dateToCal(startDate).get(Calendar.MONTH);
					int endMonth = dateToCal(endDate).get(Calendar.MONTH);

					// Check if end time is equal to at least 1 hour greater than start time and be
					// on the same month + day as the start time
					if (endTime >= startTimePlusOneHour && startDay == endDay && startMonth == endMonth) {
						return true;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}

	/**
	 * Converts date in string to Date object
	 *
	 * @param date string
	 * @return string in Date
	 */
	public Date stringToDate(String date) {
		try {
			return df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts a Date object to a Calendar object
	 *
	 * @param date date
	 * @return calendar of date
	 */
	private Calendar dateToCal(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
}
