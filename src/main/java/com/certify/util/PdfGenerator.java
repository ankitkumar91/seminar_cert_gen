package com.certify.util;

import com.certify.model.FieldPosition;
import com.certify.model.Seminar;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class PdfGenerator {
    private final Font regular;
    private final Font bold;

    public PdfGenerator(Path fontDir) {
        this.regular = load(fontDir.resolve("NotoSans-Regular.ttf"), Font.PLAIN);
        this.bold = load(fontDir.resolve("NotoSans-Bold.ttf"), Font.BOLD);
    }

    private Font load(Path path, int style) {
        try (InputStream in = Files.newInputStream(path)) {
            return Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(style, 24f);
        } catch (Exception e) {
            return new Font(Font.SANS_SERIF, style, 24);
        }
    }

    public byte[] generate(Path templatePath, Seminar seminar, List<FieldPosition> positions,
                           Map<String, String> values) throws IOException {
        BufferedImage src = ImageIO.read(templatePath.toFile());
        if (src == null) {
            throw new IOException("Certificate template could not be read.");
        }
        BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawImage(src, 0, 0, null);

        int w = img.getWidth();
        int h = img.getHeight();
        for (FieldPosition pos : positions) {
            String value = values.getOrDefault(pos.getFieldKey(), "");
            if (value == null || value.isBlank()) {
                continue;
            }
            int boxX = (int) Math.round(pos.getXPercent() / 100.0 * w);
            int boxY = (int) Math.round(pos.getYPercent() / 100.0 * h);
            int boxW = (int) Math.round(pos.getWidthPercent() / 100.0 * w);
            Font font = (pos.isFontBold() ? bold : regular).deriveFont((float) pos.getFontSize());
            g.setFont(font);
            g.setColor(parseColor(pos.getFontColor()));
            String fitted = fit(g, value, boxW);
            FontMetrics fm = g.getFontMetrics();
            int textX;
            if ("right".equalsIgnoreCase(pos.getTextAlign())) {
                textX = boxX + boxW - fm.stringWidth(fitted);
            } else if ("center".equalsIgnoreCase(pos.getTextAlign())) {
                textX = boxX + (boxW - fm.stringWidth(fitted)) / 2;
            } else {
                textX = boxX;
            }
            int textY = boxY + fm.getAscent();
            g.drawString(fitted, textX, textY);
        }
        g.dispose();

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(new PDRectangle(img.getWidth(), img.getHeight()));
            doc.addPage(page);
            PDImageXObject pdImage = LosslessFactory.createFromImage(doc, img);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.drawImage(pdImage, 0, 0, img.getWidth(), img.getHeight());
            }
            doc.getDocumentInformation().setTitle(seminar.getTitle() + " — Certificate");
            doc.getDocumentInformation().setCreator("Seminar Certificate Platform");
            doc.save(out);
            return out.toByteArray();
        }
    }

    private static String fit(Graphics2D g, String text, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "…";
        String trimmed = text;
        while (trimmed.length() > 1 && fm.stringWidth(trimmed + ellipsis) > maxWidth) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed + ellipsis;
    }

    private static Color parseColor(String hex) {
        try {
            return Color.decode(hex);
        } catch (Exception e) {
            return new Color(26, 39, 68);
        }
    }
}
