package com.certify.util;

import com.certify.config.AppConfig;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public final class SampleCertificateFactory {
    private SampleCertificateFactory() {}

    public static Font loadFont(int style, float size) {
        String resource = style == Font.BOLD ? "/fonts/NotoSans-Bold.ttf" : "/fonts/NotoSans-Regular.ttf";
        try (InputStream in = SampleCertificateFactory.class.getResourceAsStream(resource)) {
            if (in != null) {
                Font base = Font.createFont(Font.TRUETYPE_FONT, in);
                return base.deriveFont(style, size);
            }
        } catch (Exception ignored) {
            // fall through to bundled file path via servlet context elsewhere
        }
        return new Font(Font.SANS_SERIF, style, Math.round(size));
    }

    public static void writeDemoTemplate(Path dest) throws IOException {
        write(dest, "Institute of Technology",
                "Certificate of Participation",
                "National Workshop on Cloud-Native Java",
                "This is to certify that the participant named below successfully attended the workshop organised by the Department of Computer Applications.",
                "15 August 2026");
    }

    public static void writePendingTemplate(Path dest) throws IOException {
        write(dest, "IQAC & Department of CSE",
                "Certificate of Completion",
                "Faculty Development Programme on Secure Web Apps",
                "This is to certify that the faculty member named below has completed the five-day faculty development programme.",
                "October 2026");
    }

    private static void write(Path dest, String org, String heading, String event, String body, String date) throws IOException {
        BufferedImage img = new BufferedImage(AppConfig.CERT_WIDTH, AppConfig.CERT_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(new Color(246, 241, 231));
        g.fillRect(0, 0, AppConfig.CERT_WIDTH, AppConfig.CERT_HEIGHT);

        g.setColor(new Color(15, 39, 68));
        g.setStroke(new BasicStroke(18f));
        g.draw(new RoundRectangle2D.Double(36, 36, AppConfig.CERT_WIDTH - 72, AppConfig.CERT_HEIGHT - 72, 12, 12));
        g.setColor(new Color(196, 163, 90));
        g.setStroke(new BasicStroke(6f));
        g.draw(new RoundRectangle2D.Double(56, 56, AppConfig.CERT_WIDTH - 112, AppConfig.CERT_HEIGHT - 112, 8, 8));

        g.setColor(new Color(196, 163, 90));
        g.fillRect(AppConfig.CERT_WIDTH / 2 - 80, 110, 160, 6);

        drawCentered(g, org.toUpperCase(), loadFont(Font.BOLD, 22f), new Color(15, 39, 68), 160);
        drawCentered(g, heading, loadFont(Font.BOLD, 56f), new Color(15, 39, 68), 250);
        drawCentered(g, event, loadFont(Font.BOLD, 32f), new Color(122, 92, 38), 330);

        drawCentered(g, body, loadFont(Font.PLAIN, 22f), new Color(55, 48, 40), 400);

        g.setColor(new Color(196, 163, 90));
        g.setStroke(new BasicStroke(2f));
        int lineY = 720;
        int lineW = 920;
        int lineX = (AppConfig.CERT_WIDTH - lineW) / 2;
        g.drawLine(lineX, lineY, lineX + lineW, lineY);

        drawCentered(g, "Awarded on " + date, loadFont(Font.PLAIN, 20f), new Color(15, 39, 68), 980);

        g.setColor(new Color(15, 39, 68));
        g.drawLine(280, 1140, 620, 1140);
        g.drawLine(AppConfig.CERT_WIDTH - 620, 1140, AppConfig.CERT_WIDTH - 280, 1140);
        drawAt(g, "Programme Coordinator", loadFont(Font.PLAIN, 16f), new Color(15, 39, 68), 450, 1175, true);
        drawAt(g, "Head of Department", loadFont(Font.PLAIN, 16f), new Color(15, 39, 68), AppConfig.CERT_WIDTH - 450, 1175, true);

        g.dispose();
        ImageIO.write(img, "png", dest.toFile());
    }

    private static void drawCentered(Graphics2D g, String text, Font font, Color color, int y) {
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        int x = (AppConfig.CERT_WIDTH - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private static void drawAt(Graphics2D g, String text, Font font, Color color, int cx, int y, boolean center) {
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        int x = center ? cx - fm.stringWidth(text) / 2 : cx;
        g.drawString(text, x, y);
    }
}
