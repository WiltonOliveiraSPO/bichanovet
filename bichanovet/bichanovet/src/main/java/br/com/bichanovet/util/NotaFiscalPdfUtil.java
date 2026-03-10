package br.com.bichanovet.util;

import br.com.bichanovet.model.Cliente;
import br.com.bichanovet.model.ItemNotaFiscal;
import br.com.bichanovet.model.Venda;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotaFiscalPdfUtil {

    private NotaFiscalPdfUtil() {
    }

    public static File gerarNota(Venda venda, Cliente cliente, double total, List<ItemNotaFiscal> itens) throws Exception {
        File pasta = new File("C:/bichanovet/notas");
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        String data = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File arquivo = new File(pasta, "nota_venda_" + data + ".pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(arquivo));
        document.open();

        Image logo = Image.getInstance("C:/bichanovet/icons/caramelo.png");
        logo.scalePercent(20f);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);

        Font fonteTitulo = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(150, 90, 40));
        Paragraph titulo = new Paragraph("BICHANOVET", fonteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(10f);
        document.add(titulo);

        Font fonteNormal = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font fonteCab = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        document.add(new Paragraph("Venda: " + nvlId(venda.getIdVenda()), fonteNormal));
        document.add(new Paragraph("Cliente: " + nvl(cliente.getNome()), fonteNormal));
        document.add(new Paragraph("CPF: " + nvl(cliente.getCpf()), fonteNormal));
        document.add(new Paragraph("Telefone: " + nvl(cliente.getTelefone()), fonteNormal));
        document.add(new Paragraph("Email: " + nvl(cliente.getEmail()), fonteNormal));
        document.add(new Paragraph("Endereco: " + nvl(cliente.getEndereco()), fonteNormal));
        document.add(new Paragraph("Data da Venda: " + sdf.format(venda.getDataVenda()), fonteNormal));
        document.add(new Paragraph("Forma de Pagamento: " + nvl(venda.getFormaPagamento()), fonteNormal));
        document.add(new Paragraph(" "));

        PdfPTable tabela = new PdfPTable(4);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{4f, 1.2f, 1.6f, 1.6f});

        tabela.addCell(celulaCab("Descricao", fonteCab));
        tabela.addCell(celulaCab("Qtd", fonteCab));
        tabela.addCell(celulaCab("Preco", fonteCab));
        tabela.addCell(celulaCab("Subtotal", fonteCab));

        DecimalFormat df = new DecimalFormat("0.00");
        for (ItemNotaFiscal item : itens) {
            tabela.addCell(celulaLinha(item.getDescricao(), fonteNormal));
            tabela.addCell(celulaLinha(String.valueOf(item.getQuantidade()), fonteNormal));
            tabela.addCell(celulaLinha(df.format(item.getPreco()), fonteNormal));
            tabela.addCell(celulaLinha(df.format(item.getSubtotal()), fonteNormal));
        }

        document.add(tabela);
        document.add(new Paragraph(" "));

        Paragraph totalPar = new Paragraph("Total Geral: " + df.format(total), fonteTitulo);
        totalPar.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalPar);

        document.close();
        return arquivo;
    }

    private static PdfPCell celulaCab(String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fonte));
        cell.setBackgroundColor(new Color(240, 224, 200));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4f);
        return cell;
    }

    private static PdfPCell celulaLinha(String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fonte));
        cell.setPadding(4f);
        return cell;
    }

    private static String nvl(String valor) {
        return valor == null ? "" : valor;
    }

    private static String nvlId(Integer valor) {
        return valor == null ? "" : String.valueOf(valor);
    }
}