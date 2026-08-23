package com.biblioteca.virtual.controller;

import com.biblioteca.virtual.dao.PrestamoDao;
import com.biblioteca.virtual.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class ReporteController {

    @Autowired
    private PrestamoDao prestamoDao;

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/reportes")
    public String vistaReportes() {
        return "reportes"; // llama a la vista html
    }

    @PostMapping("/reportes/generar")
    public Object generarReporte(
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin,
            @RequestParam("formato") String formato,
            RedirectAttributes redirectAttributes) {

        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);

        // regla de negocio: validar si existen datos (prevenir reportes vacios)
        if (!prestamoDao.existsByFechaPrestamoBetween(inicio, fin)) {
            redirectAttributes.addFlashAttribute("mensaje", "No hay actividad registrada en las fechas seleccionadas (" + fechaInicio + " al " + fechaFin + "). No se puede generar un reporte vacío.");
            redirectAttributes.addFlashAttribute("tipo", "warning");
            return "redirect:/admin/reportes";
        }

        // si hay datos, traemos las estadisticas
        List<Object[]> libros = prestamoDao.findLibrosMasPrestados(inicio, fin);
        List<Object[]> horas = prestamoDao.findHorasMayorFlujo(inicio, fin);

        try {
            // descarga dinamica (excel o pdf)
            if ("PDF".equalsIgnoreCase(formato)) {
                byte[] pdfBytes = reporteService.generarReportePdf(libros, horas, fechaInicio, fechaFin);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Biblioteca.pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdfBytes);
            } else {
                byte[] excelBytes = reporteService.generarReporteExcel(libros, horas);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Reporte_Biblioteca.xlsx")
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(excelBytes);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Ocurrió un error al procesar el archivo: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/admin/reportes";
        }
    }
}