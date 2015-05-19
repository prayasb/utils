package com.prayasb.http.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

public class CodeChecker {
	private static final String LINE_BREAK = System
			.getProperty("line.separator");

	private final File inputFile;
	private final File outputFile;
	private final RequestConfig noRedirectConfig;

	public CodeChecker(File inputFile, File outputFile) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.noRedirectConfig = RequestConfig.custom()
				.setRedirectsEnabled(false).build();
	}

	public void readAndCheckFromFile() throws FileNotFoundException,
			IOException {
		if (outputFile.exists()) {
			outputFile.delete();
		} else {
			outputFile.createNewFile();
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(
				inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						outputFile));) {
			String line = null;
			String lineBreak = "";
			writeHeader(writer);
			while ((line = reader.readLine()) != null) {
				String[] urls = line.split(",");
				if (urls.length == 2) {
					URLCheck check = new URLCheck(urls[0].trim(), urls[1].trim());
					try {
						check = processURL(check);
						writer.append(lineBreak);
						writer.append(check.commaSeparatedOutput());
						lineBreak = LINE_BREAK;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void writeHeader(BufferedWriter writer) throws IOException {
		writer.append("URL,expectedURL,returnedURL,statusCode,result"
				+ LINE_BREAK);
	}

	public URLCheck processURL(URLCheck urlCheck) throws Exception {
		try (CloseableHttpClient httpclient = HttpClients.createDefault();) {
			HttpGet httpget = new HttpGet(urlCheck.getURL());
			httpget.setConfig(noRedirectConfig);

			HttpContext context = new BasicHttpContext();
			CloseableHttpResponse response = httpclient.execute(httpget,
					context);
			// get redirected status code
			urlCheck.setStatusCode(response.getStatusLine().getStatusCode());

			httpget.setConfig(RequestConfig.DEFAULT);
			httpclient.execute(httpget, context);

			HttpUriRequest currentReq = (HttpUriRequest) context
					.getAttribute(HttpCoreContext.HTTP_REQUEST);

			HttpHost currentHost = (HttpHost) context
					.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
			String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq
					.getURI().toString() : (currentHost.toURI() + currentReq
					.getURI());
			urlCheck.setReturnedURL(currentUrl);

			HttpURLConnection conn = (HttpURLConnection) new URL(
					urlCheck.getURL()).openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.connect();
		}

		return urlCheck;
	}

	public static void main(String[] args) {
		try {
			System.out.println("Input file should be in the following comma separated format:");
			System.out.println("actualURL,expectedURL");
			if(args.length == 2) {
				File inputFile = new File(args[0]);
				File outputFile = new File(args[1]);
				
				CodeChecker codeChecker = new CodeChecker(inputFile, outputFile);
				codeChecker.readAndCheckFromFile();
				System.out.println("Done checking URLs");
			} else {
				throw new Exception("Please specify input and output file");
			}
		}catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
	}
}
