package com.biblioteca.virtual.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReporteService {

    public byte[] generarReportePdf(List<Object[]> libros, List<Object[]> horas, String inicio, String fin) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        document.add(new Paragraph("Reporte Estadístico de Biblioteca", fontTitulo));
        document.add(new Paragraph("Período: " + inicio + " al " + fin + "\n\n"));

        document.add(new Paragraph("Top Libros Más Prestados:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        for (Object[] fila : libros) {
            document.add(new Paragraph("- " + fila[0] + " (" + fila[1] + " préstamos)"));
        }

        document.add(new Paragraph("\nHoras de Mayor Flujo (Picos de Tráfico):", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        for (Object[] fila : horas) {
            document.add(new Paragraph("- " + fila[0] + ":00 hrs (" + fila[1] + " transacciones)"));
        }

        document.close();
        return out.toByteArray();
    }

    public byte[] generarReporteExcel(List<Object[]> libros, List<Object[]> horas) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // hoja 1: libros
        Sheet sheetLibros = workbook.createSheet("Top Libros");
        Row headerLibros = sheetLibros.createRow(0);
        headerLibros.createCell(0).setCellValue("Título del Libro");
        headerLibros.createCell(1).setCellValue("Total Préstamos");
        int rowIdx = 1;
        for (Object[] fila : libros) {
            Row row = sheetLibros.createRow(rowIdx++);
            row.createCell(0).setCellValue(fila[0].toString());
            row.createCell(1).setCellValue(Integer.parseInt(fila[1].toString()));
        }

        // hoja 2: horas
        Sheet sheetHoras = workbook.createSheet("Flujo por Horas");
        Row headerHoras = sheetHoras.createRow(0);
        headerHoras.createCell(0).setCellValue("Hora del Día");
        headerHoras.createCell(1).setCellValue("Cantidad de Préstamos");
        rowIdx = 1;
        for (Object[] fila : horas) {
            Row row = sheetHoras.createRow(rowIdx++);
            row.createCell(0).setCellValue(fila[0].toString() + ":00");
            row.createCell(1).setCellValue(Integer.parseInt(fila[1].toString()));
        }

        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}