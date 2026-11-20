package com.mycompany.mavenproject1.service;

import com.mycompany.mavenproject1.model.Barang;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.poi.export.JRXlsExporter;

/**
 * Laporan daftar barang dan garansi dengan JasperReports.
 * Desain laporan ada di src/main/resources/DaftarBarang.jrxml (dapat dibuka di Jaspersoft Studio);
 * saat dipakai berkas .jrxml dikompilasi menjadi JasperReport, diisi data barang (JavaBean),
 * lalu diekspor ke PDF atau XLS.
 */
public class LaporanService {

    private static final String BERKAS_DESAIN = "/DaftarBarang.jrxml";

    /** Mengisi laporan dengan daftar barang. Parameter menjadi teks pada judul laporan. */
    public JasperPrint isi(List<Barang> data, String pemilik, String keterangan) throws JRException {
        try (InputStream in = getClass().getResourceAsStream(BERKAS_DESAIN)) {
            if (in == null) {
                throw new JRException("Berkas desain DaftarBarang.jrxml tidak ditemukan.");
            }
            JasperReport laporan = JasperCompileManager.compileReport(in);
            Map<String, Object> parameter = new HashMap<>();
            parameter.put("PEMILIK", pemilik);
            parameter.put("KETERANGAN", keterangan);
            parameter.put("TANGGAL_CETAK", java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            return JasperFillManager.fillReport(laporan, parameter, new JRBeanCollectionDataSource(data));
        } catch (IOException e) {
            throw new JRException(e);
        }
    }

    public void eksporPdf(JasperPrint cetak, File tujuan) throws JRException {
        JRPdfExporter eksporter = new JRPdfExporter();
        eksporter.setExporterInput(new SimpleExporterInput(cetak));
        eksporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tujuan));
        eksporter.exportReport();
    }

    public void eksporXls(JasperPrint cetak, File tujuan) throws JRException {
        JRXlsExporter eksporter = new JRXlsExporter();
        eksporter.setExporterInput(new SimpleExporterInput(cetak));
        eksporter.setExporterOutput(new SimpleOutputStreamExporterOutput(tujuan));
        eksporter.exportReport();
    }
}
