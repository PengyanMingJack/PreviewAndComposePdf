package com.zchd.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.FixedDashedBorder;
import com.itextpdf.layout.borders.InsetBorder;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Copyright (C), 2021-2023, 中传互动（湖北）信息技术有限公司
 * Author: 彭艳明
 * Date:2023年04月27日
 * Description:
 */
public class PdfPageHelper {
    int width = 686;
    int height = 960;
    int itemHeight = 90;

    public void pdfTest() {
        String signPdfFilePath = Utils.INSTANCE.getApplication().getCacheDir().getPath() + "/sign_authorize_book.pdf";
        try {
            PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(signPdfFilePath));
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument, new PageSize(width, height), true);
            document.setBorder(Border.NO_BORDER);
            document.setMargins(10f, 30f, 10f, 30f);
//            document.setBaseDirection(BaseDirection.NO_BIDI);
//            document.setFont(PdfFontFactory.createFont(StandardFonts.COURIER));
            int dy = 12;
            document.add(buildTitleText("户主姓名", "彭艳明"));
            PdfPage firstPage = pdfDocument.getFirstPage();
            dy = dy + itemHeight;
            buildLine(firstPage, height - dy);
            document.add(buildTitleText("身份证号码", "420381111111111111"));
            dy = dy + itemHeight;
            buildLine(firstPage, height - dy);
            document.add(buildTitleText("家庭地址", "湖北十堰"));
            dy = dy + itemHeight;
            buildLine(firstPage, height - dy);
            document.add(buildTitleText("户籍地址", "湖北十堰"));
            dy = dy + itemHeight;
            buildLine(firstPage, height - dy);
            document.add(buildTitleText("救助业务", "居民最低生活保障"));
            dy = dy + itemHeight;
            buildLine(firstPage, height - dy);
            document.add(buildTitleText("在保状态", "在保"));
            dy = dy + itemHeight;
            buildLine(firstPage, height - dy);
            document.add(buildTitleText("家庭人口", "5"));
            dy = dy + itemHeight;
            buildLine(firstPage, height - dy);
            document.add(buildTitleText("电子证件失效日期", "2023-2-25"));
            dy = dy + itemHeight;
            buildLine(firstPage, height - dy);
            document.add(buildTitleText("发证机关", "民政部低收入家庭认定指导中心"));
            dy = dy + itemHeight;
            buildLine(firstPage, height - dy);

            document.add(buildBottomText());
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void buildLine(PdfPage pdfPage, int dy) {
        PdfCanvas canvas = new PdfCanvas(pdfPage);
        canvas.setStrokeColor(new DeviceRgb(238, 238, 238));
        canvas.setLineWidth(1f);
        canvas.moveTo(30f, dy);
        canvas.lineTo(width - 30, dy);
        canvas.fillStroke();
    }

    private Paragraph buildTitleText(String title, String content) {
        String fontStyle = Utils.INSTANCE.getAssetsCacheFile(Utils.INSTANCE.getApplication(), "SimSun.ttf");
        PdfFont font;
        try {
            font = PdfFontFactory.createFont(fontStyle, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Paragraph paragraph = new Paragraph();
        paragraph.setVerticalAlignment(VerticalAlignment.MIDDLE);
        paragraph.setHeight(itemHeight);
        paragraph.setKeepTogether(false);
        Style style = new Style();
        style.setKeepTogether(false);
        paragraph.addStyle(style);
        paragraph.setMargins(0, 0, 0, 0);
        Text text = new Text(title);
        text.setFont(font);
        text.setFontSize(30f);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFontColor(new DeviceRgb(102, 102, 102));
        paragraph.add(text);
        paragraph.setFont(font);
        paragraph.setFontSize(30f);
        paragraph.setBold();
        paragraph.setFontColor(new DeviceRgb(51, 51, 51));
        paragraph.add("   " + content);
        return paragraph;
    }

    private Paragraph buildBottomText() {
        String fontStyle = Utils.INSTANCE.getAssetsCacheFile(Utils.INSTANCE.getApplication(), "SimSun.ttf");
        PdfFont font;
        try {
            font = PdfFontFactory.createFont(fontStyle, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Paragraph paragraph = new Paragraph();
        paragraph.setVerticalAlignment(VerticalAlignment.MIDDLE);
        paragraph.setHorizontalAlignment(HorizontalAlignment.CENTER);
        paragraph.setTextAlignment(TextAlignment.CENTER);
        paragraph.setMargins(20, 0, 0, 0);
        paragraph.setFont(font);
        paragraph.setFontSize(24f);
        paragraph.setBold();
        paragraph.setFontColor(new DeviceRgb(36, 108, 255));
        paragraph.add("数据来源：民政部低收入家庭动态监测库\n数据更新日期：2023-02-13");
        return paragraph;
    }

    public static void pdf2Pic(PdfDocument document, String pdfPath, String path, String fileName) {

    }

}
