import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

class NearestColourCalculator extends JPanel {
    ArrayList<Color> referenceColors = new ArrayList<Color>();
    ArrayList<Color> nearestColors = new ArrayList<Color>();
    Image sourceImage;
    int sourceImageSize = 100; //assumes square!
    boolean debug = true;

    // Constructor
    public NearestColourCalculator(ArrayList<Color> colors) {
        this.referenceColors = colors;
    }

    // Members
    public void setNearestColors(ArrayList<Color> colors) {
        this.nearestColors = colors;
    }

    public void setSourceImage(Image sourceImage) {
        this.sourceImage = sourceImage;
    }

    // Calculations
    public Color calculateNearest(Color source) {
        Color result = new Color(0, 0, 0);
        double current = Double.MAX_VALUE;

        for (Color cyanometerColor : this.referenceColors) {
            double diff = calculateDistance(cyanometerColor, source);
            if (diff < current) {
                result = cyanometerColor;
                current = diff;
            }
        }
        return result;
    }


    private double calculateDistance(Color first, Color second) {
        double dbl_input_red = second.getRed();
        double dbl_input_green = second.getGreen();
        double dbl_input_blue = second.getBlue();

        // compute the Euclidean distance between the two colors
        // note, that the alpha-component is not used in this example
        double dbl_test_red = Math.pow(first.getRed() - dbl_input_red, 2.0);
        double dbl_test_green = Math.pow(first.getGreen() - dbl_input_green, 2.0);
        double dbl_test_blue = Math.pow(first.getBlue() - dbl_input_blue, 2.0);

        double temp = Math.sqrt(dbl_test_blue + dbl_test_green + dbl_test_red);
        return temp;
    }


    // UI
    public void paint(Graphics g) {
        Image cyanometerPreviewImage = createPreview();
        //Image scaled = img.getScaledInstance(sourceImageSize * previewPixelSize, sourceImageSize * previewPixelSize, Image.SCALE_DEFAULT);

        g.drawImage(sourceImage, 0, 0, this);
        g.drawImage(cyanometerPreviewImage, this.sourceImageSize, 0, this);
    }

    public Image createPreview() {
        BufferedImage bufferedImage = new
                BufferedImage(sourceImageSize, sourceImageSize, BufferedImage.TYPE_INT_RGB);

        int xpos = 0;
        int ypos = 0;
        int count = 0;

        Color[][] previewCyanometerImagePixels = new Color[sourceImageSize][sourceImageSize];

        // create a Color array from the derived nearest colors
        for (Color nearestColor : this.nearestColors) {
            previewCyanometerImagePixels[xpos][ypos] = nearestColor;
            if (xpos < sourceImageSize - 1) {
                xpos++;
            } else {
                xpos = 0;
                ypos++;
            }

            count++;
        }


        for (int x = 0; x < previewCyanometerImagePixels.length; x++) {
            for (int y = 0; y < previewCyanometerImagePixels[x].length; y++) {
                int _xpos = x;
                int _ypos = y;
                bufferedImage.setRGB(_xpos, _ypos, previewCyanometerImagePixels[x][y].getRGB());
            }
        }


        return bufferedImage;
    }

    static public void main(String args[]) throws Exception {
        int[] referenceColorsHex = new int[]{0x03152, 0x33455, 0x53657, 0xA3859, 0xC3A5B, 0xF3D5E, 0x144162, 0x174463,
                                           0x1B4666, 0x1F4B68, 0x254E6C, 0x29526E, 0x2E5670, 0x325A74, 0x385D78,
                                           0x3D627C, 0x42667E, 0x476B83, 0x4D6F88, 0x54748B, 0x59798E, 0x5F7E93,
                                           0x648397, 0x6A869B, 0x718C9F, 0x7691A4, 0x7C96A7, 0x839AAA, 0x889FAF,
                                           0x8EA4B2, 0x95A8B6, 0x9AADBB, 0xA1B3BF, 0xA5B7C3, 0xABBCC6, 0xB2C0CB,
                                           0xB7C5CE, 0xBCC9D2, 0xC1CED6, 0xC8D3D9, 0xCDD6DD, 0xD2DBE2, 0xD5DEE3,
                                           0xDAE1E7, 0xE0E5E9, 0xE4E9EC, 0xE8ECEF, 0xECF0F3, 0xF1F2F6, 0xF2F6F7,
                                           0xF7F8FA, 0xFAFBFD, 0xFCFEFD};

        ArrayList<Color> referenceColors = new ArrayList<Color>();
        ArrayList<Color> nearestColors = new ArrayList<Color>();

        for (int i = 0; i < referenceColorsHex.length; i++) {
            referenceColors.add(Color.decode(String.valueOf(referenceColorsHex[i])));
        }

        NearestColourCalculator nearestColourCalculator = new NearestColourCalculator(referenceColors);

        try {
            System.out.println("starting..");
            File input = new File("/data/development/java/cyanometer/src/test.png");

            BufferedImage sourceImage = ImageIO.read(input);
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();
            int count = 0;

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    count++;
                    Color fromPixel = new Color(sourceImage.getRGB(j, i));
                    Color nearestColor = nearestColourCalculator.calculateNearest(fromPixel);
                    nearestColors.add(nearestColor);
                    if (nearestColourCalculator.debug) {
                        System.out.println("["+count+"] \t pixel: "+fromPixel+" \t nearest: "+nearestColor);
                    }
                }
            }

            // store the original image
            nearestColourCalculator.setSourceImage(sourceImage);

            // store the cyanometer pixel colors we derived from the image
            nearestColourCalculator.setNearestColors(nearestColors);

            // display a preview
            JFrame frame = new JFrame();
            frame.getContentPane().add(nearestColourCalculator);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(nearestColourCalculator.sourceImageSize * 4, nearestColourCalculator.sourceImageSize * 4);
            frame.setVisible(true);

        } catch (Exception e) {
            System.err.println("Bang! " + e.getMessage());
        }
    }
}