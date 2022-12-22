package com.shopme.admin.export;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shopme.common.entity.User;

public class UserExcelExporter extends AbstractExporter {

	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	public UserExcelExporter() {

		workbook = new XSSFWorkbook();
	}

	public void writeHeaderLine() {

		sheet = workbook.createSheet();
		XSSFRow row = sheet.createRow(0);
		XSSFFont font = workbook.createFont();
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		font.setBold(true);
		font.setFontHeight(16);
		cellStyle.setFont(font);
		createCell(row, 0, "User Id", cellStyle);
		createCell(row, 1, "Email", cellStyle);
		createCell(row, 2, "First Name", cellStyle);
		createCell(row, 3, "Last Name", cellStyle);
		createCell(row, 4, "Roles", cellStyle);
		createCell(row, 5, "Enabled", cellStyle);

	}

	public void writeDataLines(List<User> listUsers) {

		XSSFFont font = workbook.createFont();
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		font.setFontHeight(16);
		cellStyle.setFont(font);
		int rowIndex = 1;// because skip header line of excel file 😉

		for (User user : listUsers) {

			XSSFRow row = sheet.createRow(rowIndex++);
			int columnIndex = 0;
			createCell(row, columnIndex++, user.getId(), cellStyle);
			createCell(row, columnIndex++, user.getEmail(), cellStyle);
			createCell(row, columnIndex++, user.getFirstName(), cellStyle);
			createCell(row, columnIndex++, user.getLastName(), cellStyle);
			createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
			createCell(row, columnIndex++, user.isEnabled(), cellStyle);
		}
	}

	public void createCell(XSSFRow row, int columnIndex, Object value, XSSFCellStyle cellStyle) {
		XSSFCell cell = row.createCell(columnIndex);
		sheet.autoSizeColumn(columnIndex);

		if (value instanceof Long)
			cell.setCellValue((Long) value);
		else if (value instanceof Boolean)
			cell.setCellValue((Boolean) value);
		else
			cell.setCellValue((String) value);

		cell.setCellStyle(cellStyle);

	}

	public void export(List<User> listUsers, HttpServletResponse httpServletResponse) throws IOException {

		setResponseHeader(httpServletResponse, "application/octet-stream", ".xlsx");

		writeHeaderLine();
		writeDataLines(listUsers);

//		XSSFWorkbook xssWorkbook = new XSSFWorkbook();
//		XSSFFont font = xssWorkbook.createFont();
//		font.setBold(true);
//		font.setFontHeight(16);
//		XSSFSheet XSSSheet = xssWorkbook.createSheet("user");
//		XSSFRow row = XSSSheet.createRow(0);
//		XSSFCell cell = row.createCell(0);
//		XSSFCellStyle cellStyle = xssWorkbook.createCellStyle();
//		cellStyle.setFont(font);
//		cell.setCellValue("User Id");
//		cell.setCellStyle(cellStyle);

		ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();

		workbook.write(servletOutputStream);
		servletOutputStream.close();
		workbook.close();

	}
}
