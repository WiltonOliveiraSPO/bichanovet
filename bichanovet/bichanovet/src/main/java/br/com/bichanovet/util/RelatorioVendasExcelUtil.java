package br.com.bichanovet.util;

import br.com.bichanovet.model.RelatorioVendaRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RelatorioVendasExcelUtil {

    private RelatorioVendasExcelUtil() {
    }

    public static File gerarRelatorio(List<RelatorioVendaRow> dados) throws Exception {
        File pasta = new File("C:/bichanovet/relatorios");
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        String data = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File arquivo = new File(pasta, "relatorio_vendas_" + data + ".xlsx");

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet("Relatorio Vendas");

            int rowIndex = 0;

            inserirLogo(wb, sheet);

            XSSFRow tituloRow = sheet.createRow(rowIndex++);
            tituloRow.setHeightInPoints(22f);
            Cell tituloCell = tituloRow.createCell(0);
            tituloCell.setCellValue("BICHANOVET");
            tituloCell.setCellStyle(estiloTitulo(wb));

            rowIndex++;

            XSSFRow headerRow = sheet.createRow(rowIndex++);
            String[] headers = {"Data", "ID Venda", "Cliente", "CPF", "Telefone", "Email", "Endereco", "Forma Pagamento", "Valor Total"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i + 3);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(estiloHeader(wb));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            DecimalFormat df = new DecimalFormat("0.00");
            double totalGeral = 0;

            for (RelatorioVendaRow r : dados) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(3).setCellValue(sdf.format(r.getDataVenda()));
                row.createCell(4).setCellValue(r.getIdVenda());
                row.createCell(5).setCellValue(nvl(r.getNomeCliente()));
                row.createCell(6).setCellValue(nvl(r.getCpf()));
                row.createCell(7).setCellValue(nvl(r.getTelefone()));
                row.createCell(8).setCellValue(nvl(r.getEmail()));
                row.createCell(9).setCellValue(nvl(r.getEndereco()));
                row.createCell(10).setCellValue(nvl(r.getFormaPagamento()));
                row.createCell(11).setCellValue(df.format(r.getValorTotal()));
                totalGeral += r.getValorTotal() == null ? 0 : r.getValorTotal();
            }

            rowIndex += 2;
            Row totalRow = sheet.createRow(rowIndex);
            Cell label = totalRow.createCell(10);
            label.setCellValue("Total Geral:");
            label.setCellStyle(estiloHeader(wb));
            Cell valor = totalRow.createCell(11);
            valor.setCellValue(df.format(totalGeral));
            valor.setCellStyle(estiloHeader(wb));

            for (int i = 3; i <= 11; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(arquivo)) {
                wb.write(out);
            }
        }

        return arquivo;
    }

    private static void inserirLogo(Workbook wb, XSSFSheet sheet) throws Exception {
        try (FileInputStream is = new FileInputStream("C:/bichanovet/icons/caramelo.png")) {
            byte[] bytes = IOUtils.toByteArray(is);
            int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(0);
            XSSFPicture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize(0.2);
        }
    }

    private static CellStyle estiloTitulo(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(new XSSFColor(new Color(150, 90, 40), null));
        style.setFont(font);
        return style;
    }

    private static CellStyle estiloHeader(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private static String nvl(String valor) {
        return valor == null ? "" : valor;
    }
}