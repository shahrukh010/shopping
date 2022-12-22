package com.shopme.admin.export;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

public class AbstractExporter {

	public void setResponseHeader(HttpServletResponse response, String fileType, String extension) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		String timeStamp = dateFormat.format(new Date());
		System.out.println(timeStamp);
		String fileName = timeStamp + extension;
		System.out.println(timeStamp);

		response.setContentType(fileType);

		String headerKey = "Content-Disposition";
		String headerValue = "attachment;fileName=" + fileName;
		response.setHeader(headerKey, headerValue);
	}
}
