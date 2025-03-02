package eu.janinko.andaria.editor.misc;

import eu.janinko.andaria.ultimasdk.graphics.Bitmap;
import eu.janinko.andaria.ultimasdk.graphics.Color;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import lombok.Setter;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BitmapPixelReader implements PixelReader {

    @Setter
    private javafx.scene.paint.Color backgroundColor;

    private Image backgroundImage;
    private int bgImageDx;
    private int bgImageDy;

    private final Bitmap bitmap;
    private final int width;
    private final int height;
    private final int paddingLeft;
    private final int paddingTop;

    public BitmapPixelReader(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.paddingLeft = 0;
        this.paddingTop = 0;
    }

    public BitmapPixelReader(Bitmap bitmap, int width, int height, int paddingLeft, int paddingTop) {
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
    }

    public void setBackgroundImage(Image image, int biasLeft, int biasTop) {
        bgImageDx = biasLeft;
        bgImageDy = biasTop;
        backgroundImage = image;
    }

    @Override
    public PixelFormat<IntBuffer> getPixelFormat() {
        return PixelFormat.getIntArgbInstance();
    }

    @Override
    public int getArgb(int x, int y) {
        return bitmap.getColor(x, y).getAGBR();
    }

    @Override
    public javafx.scene.paint.Color getColor(int x, int y) {
        return HueHelper.toPaint(bitmap.getColor(x, y));
    }

    @Override
    public <T extends Buffer> void getPixels(int x, int y, int w, int h, WritablePixelFormat<T> pixelformat, T buffer, int scanlineStride) {
        if (pixelformat.getType() == PixelFormat.Type.BYTE_BGRA_PRE) {
            byte[] background = {0, 0, 0, 0};
            if (backgroundColor != null) {
                background[0] = (byte) (backgroundColor.getBlue() * 255);
                background[1] = (byte) (backgroundColor.getGreen() * 255);
                background[2] = (byte) (backgroundColor.getRed() * 255);
                background[3] = (byte) (backgroundColor.getOpacity() * 255);
            }
            ByteBuffer byteBuffer = (ByteBuffer) buffer;

            PixelReader pixelReader = null;
            if (backgroundImage != null) {
                pixelReader = backgroundImage.getPixelReader();
            }


            int position = byteBuffer.position();
            for (int yy = y; yy < y + h; yy++, position += scanlineStride) {
                byteBuffer.position(position);
                for (int xx = x; xx < x + w; xx++) {
                    if (xx < paddingLeft || yy < paddingTop || xx - paddingLeft >= bitmap.getWidth() || yy - paddingTop >= bitmap.getHeight()) {
                        drawBackground(pixelReader, xx, yy, byteBuffer, background);
                        continue;
                    }
                    Color color = bitmap.getColor(xx - paddingLeft, yy - paddingTop);
                    if (color.isAlpha()) {
                        drawBackground(pixelReader, xx, yy, byteBuffer, background);
                    } else {
                        byteBuffer.put(color.getBlueByte());
                        byteBuffer.put(color.getGreenByte());
                        byteBuffer.put(color.getRedByte());
                        byteBuffer.put((byte) 0xff);
                    }
                }
            }
        } else {
            throw new UnsupportedOperationException("Unknown format: " + pixelformat.getType());
        }
    }

    private void drawBackground(PixelReader pixelReader, int xx, int yy, ByteBuffer byteBuffer, byte[] background) {
        if(pixelReader != null) {
            int ix = xx + bgImageDx;
            int iy = yy + bgImageDy;
            if (ix >= 0 && iy >= 0 && ix < backgroundImage.getWidth() && iy < backgroundImage.getHeight()) {
                int argb = pixelReader.getArgb(ix, iy);
                byteBuffer.put((byte) (argb & 0xff));
                byteBuffer.put((byte) (argb >> 8 & 0xff));
                byteBuffer.put((byte) (argb >> 16 & 0xff));
                byteBuffer.put((byte) (argb >> 24 & 0xff));
                return;
            }
        }
        byteBuffer.put(background);
    }


    @Override
    public void getPixels(int x, int y, int w, int h, WritablePixelFormat<ByteBuffer> pixelformat, byte[] buffer, int offset, int scanlineStride) {
        throw new UnsupportedOperationException("Unknown format: " + pixelformat.getType());
    }

    @Override
    public void getPixels(int x, int y, int w, int h, WritablePixelFormat<IntBuffer> pixelformat, int[] buffer, int offset, int scanlineStride) {
        if (pixelformat.getType() != PixelFormat.Type.INT_ARGB) {
            throw new UnsupportedOperationException("Unknown format: " + pixelformat.getType());
        }
        int i = offset;
        for (int xx = x; xx < x + w; xx++, i += (scanlineStride - w)) {
            for (int yy = y; yy < y + h; yy++, i++) {
                buffer[i] = bitmap.getColor(xx, yy).getAGBR();
            }
        }
    }
}
