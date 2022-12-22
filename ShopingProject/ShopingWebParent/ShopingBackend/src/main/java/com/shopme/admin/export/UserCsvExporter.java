package com.shopme.admin.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.entity.User;

public class UserCsvExporter extends AbstractExporter{

	public void export(List<User> listUser, HttpServletResponse httpServletResponse) throws IOException {

		setResponseHeader(httpServletResponse, "text/csv", ".csv");

		ICsvBeanWriter icsvBeanWrite = new CsvBeanWriter(httpServletResponse.getWriter(),
				CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = { "User ID", "Email-Id", "First Name", "Last Name", "Roles", "Enabled" };
		String[] fieldMapping = { "id", "email", "firstName", "lastName", "roles", "enabled" };
		icsvBeanWrite.writeHeader(csvHeader);

		for (User user : listUser) {

			icsvBeanWrite.write(user, fieldMapping);
		}

		icsvBeanWrite.close();

	}
}
