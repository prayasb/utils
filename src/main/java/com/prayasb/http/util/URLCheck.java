package com.prayasb.http.util;

import com.google.common.base.MoreObjects;

public class URLCheck {
	private final String URL;
	private final String expectedURL;
	private int statusCode;
	private String returnedURL;

	public URLCheck(String URL, String expectedURL) {
		this.URL = URL;
		this.expectedURL = expectedURL;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getReturnedURL() {
		return returnedURL;
	}

	public void setReturnedURL(String returnedURL) {
		this.returnedURL = returnedURL;
	}

	public String getURL() {
		return URL;
	}

	public String getExpectedURL() {
		return expectedURL;
	}

	public String commaSeparatedOutput() {
		String result = expectedURL.equalsIgnoreCase(returnedURL) ? "PASSED"
				: "FAILED";
		String separator = ",";
		StringBuilder entry = new StringBuilder();
		entry.append(URL).append(separator).append(expectedURL)
				.append(separator).append(returnedURL).append(separator)
				.append(statusCode).append(separator).append(result);
		return entry.toString();
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(URLCheck.class).add("URL", URL)
				.add("expectedURL", expectedURL)
				.add("returnedURL", returnedURL)
				.add("statusCode", statusCode)
				.toString();
	}
}
