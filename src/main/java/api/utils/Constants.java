package api.utils;

public class Constants {
	public static final String USERNAME = "xl4998";
	public static final String ERROR_JSON = "{" +
			"\"error\": \"An error has occurred, please try again!!\"" +
			"}";
	public static final String JSON_PARSE_ERROR = "{" +
			"\"error\": \"Could not parse JSON!\"" +
			"}";
	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String HIREDATE_FORMAT = "yyyy-MM-dd";
	public static final String DEPARTMENT_TEMPLATE = "{" +
			"\"dept_id\": %d, " +
			"\"company\": \"%s\", " +
			"\"dept_name\": \"%s\", " +
			"\"dept_no\": \"%s\", " +
			"\"location\": \"%s\"" +
			"}";
	public static final String EMPLOYEE_TEMPLATE = "{" +
			"\"emp_id\": %d, " +
			"\"emp_name\": \"%s\", " +
			"\"emp_no\": \"%s\", " +
			"\"hire_date\": \"%s\", " +
			"\"job\": \"%s\", " +
			"\"salary\": %.2f, " +
			"\"dept_id\": %d, " +
			"\"mng_id\": %d " +
			"}";
	public static final String TIMECARD_TEMPLATE = "{" +
			"\"timecard_id\": %d, " +
			"\"start_time\": \"%s\", " +
			"\"end_time\": \"%s\", " +
			"\"emp_id\": %d" +
			"}";
}
