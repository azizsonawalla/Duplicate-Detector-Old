package util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static util.FileSystemUtil.copyFile;

/**
 * Util methods to work with image data
 */
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
        BufferedImage img = ImageIO.read(imageFile);
        int oldWidth = img.getWidth();
        int oldHeight = img.getHeight();

        File tmp = FileSystemUtil.createTempFileReference(imageFile);
        if ((newWidth < 0 && newHeight < 0) || newHeight >= oldHeight || newWidth >= oldWidth) {
            return copyFile(imageFile, tmp);
        }

        Image downscaled = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return saveImage(downscaled, tmp);
    }

    /**
     * Save an image to disk
     * @param img image data
     * @param dest destination file object
     * @return file reference to destination after image data has been written
     * @throws IOException if there is an error saving the image to disk
     */
    private static File saveImage(Image img, File dest) throws IOException {
        BufferedImage buffImg = getBufferedImage(img);
        ImageIO.write(buffImg, "PNG", dest);
        return dest;
    }

    /**
     * Converts java.awt image to Buffered Image
     * @param img original AWT image
     * @return converted Buffered Image
     */
    private static BufferedImage getBufferedImage(Image img) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = buffImg.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return buffImg;
    }
}
