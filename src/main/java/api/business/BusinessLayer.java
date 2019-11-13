package api.business;

import companydata.DataLayer;

public abstract class BusinessLayer {
	public DataLayer dl;
	private String jsonTemplate;
	private String errorJson;

	public BusinessLayer(String company, String template, String error) {
		this.dl = new DataLayer(company);
		this.jsonTemplate = template;
		this.errorJson = error;
	}

	/**
	 * @return JSON template for department
	 */
	public String getJSONTemplate() {
		return jsonTemplate;
	}

	/**
	 * @return Error json message
	 */
	public String getErrorJson() {
		return errorJson;
	}

	/**
	 * Sets error json message
	 *
	 * @param json error message
	 */
	public void setErrorJson(String json) {
		this.errorJson = json;
	}
}
