package com.certify.util;

import com.certify.config.AppConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class ImageValidator {
    public record Result(boolean ok, String message, int width, int height) {}

    private ImageValidator() {}

    public static Result validate(InputStream in) throws IOException {
        BufferedImage image = ImageIO.read(in);
        if (image == null) {
            return new Result(false, "The file is not a readable PNG or JPEG image.", 0, 0);
        }
        int w = image.getWidth();
        int h = image.getHeight();
        if (Math.abs(w - AppConfig.CERT_WIDTH) > AppConfig.CERT_DIM_TOLERANCE
                || Math.abs(h - AppConfig.CERT_HEIGHT) > AppConfig.CERT_DIM_TOLERANCE) {
            return new Result(false,
                    "Certificate design must be exactly " + AppConfig.CERT_WIDTH + " × "
                            + AppConfig.CERT_HEIGHT + " pixels. This file is " + w + " × " + h + ".",
                    w, h);
        }
        return new Result(true, "ok", w, h);
    }
}
