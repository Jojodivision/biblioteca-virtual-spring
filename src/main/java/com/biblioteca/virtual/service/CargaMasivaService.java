package com.biblioteca.virtual.service;

import com.biblioteca.virtual.domain.Libro;
import com.biblioteca.virtual.dao.LibroDao;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CargaMasivaService {

    @Autowired
    private LibroDao libroDao;

    public int procesarArchivoCsv(MultipartFile archivo) throws Exception {
        List<Libro> librosNuevos = new ArrayList<>();

        // leemos el archivo en memoria con UTF-8 para aceptar tildes y ñ sin corromper el texto
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            List<CSVRecord> registros = csvParser.getRecords();

            // limitamos a 100 registros por archivo para no saturar la bd ni la memoria ram
            if (registros.size() > 100) {
                throw new Exception("El archivo excede el límite permitido de 100 registros por lote.");
            }

            for (CSVRecord iterador : registros) {
                Libro libro = new Libro();

                // extraemos los datos basandonos en los encabezados exactos del csv
                libro.setTitulo(iterador.get("titulo"));
                libro.setAutor(iterador.get("autor"));
                libro.setEditorial(iterador.get("editorial"));

                // convertimos los campos numericos a integer
                libro.setAnioPublicacion(Integer.parseInt(iterador.get("anio_publicacion")));
                libro.setCantidad(Integer.parseInt(iterador.get("cantidad")));

                librosNuevos.add(libro);
            }

            // guardamos toda la lista de un solo golpe (batch insert) para optimizar
            libroDao.saveAll(librosNuevos);
            return librosNuevos.size();

        } catch (Exception e) {
            throw new Exception("Error al leer la estructura del CSV. Verifique que la plantilla tenga los encabezados correctos. Detalle: " + e.getMessage());
        }
    }
}