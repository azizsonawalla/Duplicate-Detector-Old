package model.util;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    /**
     * Create a low res version of an image file and save it in the OS's temp folder. Resized image will have given
     * newWidth and newHeight. Use -1 for newWidth or newHeight to auto-calculate based on the other and the original
     * aspect ratio.
     *
     * If both newWidth and newHeight are negative, image will not be resized and will be saved in the temp folder as-is.
     * If newWidth or newHeight are larger than the original dimensions, image will not be resized and will be saved as-is.
     *
     * @param imageFile image file to downsize
     * @param newWidth width of the resized image (-1 to auto-calculate based on newHeight and aspect ratio)
     * @param newHeight height of the resized image (-1 to auto-calculate based on newWidth and aspect ratio)
     * @return file object for the new resized temporary file
     * @throws IOException if cannot read the given file
     */
    public static File createLowResTemp(File imageFile, int newWidth, int newHeight) throws IOException {
        if (newWidth < 0 && newHeight < 0) {
            // save to temp folder as-is
            // return
        }
        if (newWidth < 0) {
            // calculate newWidth
        }
        if (newHeight < 0) {
            // calculate newHeight
        }

        BufferedImage img = ImageIO.read(imageFile);
        int oldWidth = img.getWidth();
        int oldHeight = img.getHeight();

        if (newHeight >= oldHeight || newWidth >= oldWidth) {
            // save to temp folder as-is
        }

        Image downscaled = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        // save downscaled image

        // TODO:
        throw new NotImplementedException();
    }
}
