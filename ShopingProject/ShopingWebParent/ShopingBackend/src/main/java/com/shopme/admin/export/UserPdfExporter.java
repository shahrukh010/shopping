package com.shopme.admin.export;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.common.entity.User;

public class UserPdfExporter extends AbstractExporter {

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {

		super.setResponseHeader(response, "application/pdf", ".pdf");

		Document document = new Document(PageSize.A4);
		PdfWriter pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(16);
		font.setColor(Color.BLUE);
		Paragraph paragraph = new Paragraph("List of user", font);
		paragraph.setAlignment(paragraph.ALIGN_CENTER);
		document.add(paragraph);

		PdfPTable pdfTable = new PdfPTable(6);
		pdfTable.setWidthPercentage(110f);
		pdfTable.setSpacingBefore(10);
		pdfTable.setWidths(new float[] {1.5f,3.5f,3.0f,3.0f,3.0f,1.5f});

		writeTableHeader(pdfTable);
		writeTableData(pdfTable, listUsers);

		document.add(pdfTable);

		document.close();
	}

	private void writeTableData(PdfPTable pdfTable, List<User> listUsers) {

		for (User user : listUsers) {

			pdfTable.addCell(String.valueOf(user.getId()));
			pdfTable.addCell(user.getEmail());
			pdfTable.addCell(user.getFirstName());
			pdfTable.addCell(user.getLastName());
			pdfTable.addCell(user.getRoles().toString());
			pdfTable.addCell(String.valueOf(user.isEnabled()));
		}

	}

	private void writeTableHeader(PdfPTable pdfTable) {

		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.blue);
		cell.setPadding(9);

		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(Color.WHITE);
		cell.setPhrase(new Phrase("User Id", font));
		pdfTable.addCell(cell);
		cell.setPhrase(new Phrase("Email", font));
		pdfTable.addCell(cell);
		cell.setPhrase(new Phrase("First Name", font));
		pdfTable.addCell(cell);
		cell.setPhrase(new Phrase("Last Name", font));
		pdfTable.addCell(cell);
		cell.setPhrase(new Phrase("Roles", font));
		pdfTable.addCell(cell);
		cell.setPhrase(new Phrase("Enabled", font));
		pdfTable.addCell(cell);

	}
}
