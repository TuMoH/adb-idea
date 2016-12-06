package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class ScreenshotCommand implements Command {
    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        try {
            RawImage rawImage = device.getScreenshot();
            BufferedImage image = bufferedImageFrom(rawImage);

            String folder = System.getProperty("user.home") + "/Pictures";
            String date = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
            String name = device.getName() + "_" + date + ".png";
            ImageIO.write(image, "png", new File(folder, name));

            info(String.format("Saved: %s", name));
            return true;
        } catch (Exception e) {
            error("Fail... " + e.getMessage());
        }
        return false;
    }

    private static BufferedImage bufferedImageFrom(RawImage rawImage) {
        BufferedImage image = new BufferedImage(rawImage.width, rawImage.height, TYPE_INT_ARGB);

        int index = 0;
        int bytesPerPixel = rawImage.bpp >> 3;
        for (int y = 0; y < rawImage.height; y++) {
            for (int x = 0; x < rawImage.width; x++) {
                image.setRGB(x, y, rawImage.getARGB(index) | 0xff000000);
                index += bytesPerPixel;
            }
        }
        return image;
    }
}
