package eu.janinko.andaria.editor.regions;

import java.util.Collection;
import java.util.Objects;

import eu.janinko.andaria.spherescript.sphere.objects.Areadef;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.Getter;

/**
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class Mapa {
    
    private Color drawColor = Color.CRIMSON;

    @Getter
    private int x;
    @Getter
    private int y;
    private int width;
    private int height;
    private final Image image;

    public Mapa(Image image) {
        this.image = Objects.requireNonNull(image, "Provided image is null.");
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();
        x = width / 2;
        y = height / 2;
    }
    
    public Image getOverlay(int w, int h, int zoom, Collection<Areadef> regions, Areadef selected){
        TransCoords t = new TransCoords(w, h, zoom);
        
        WritableImage wi = new WritableImage(width, height);
        PixelWriter pw = wi.getPixelWriter();
        
        drawColor = Color.CRIMSON;
        for (Areadef region : regions) {
            for (Areadef.Rect rect : region.getRects()) {
                int rsx = rect.getSx();
                int rsy = rect.getSy();
                int rex = rect.getEx()-1;
                int rey = rect.getEy()-1;
                if(rex < rsx){
                    int a = rsx;
                    rsx = rex;
                    rex = a;
                }
                if(rey < rsy){
                    int a = rsy;
                    rsy = rey;
                    rey = a;
                }
                
                drawRect(pw, rsx, rsy, rex, rey, t, w, h);
            }
        }
        if(selected != null){
            drawColor = Color.CHARTREUSE;
            drawCross(pw, selected.getX(), selected.getY(), t, w, h);
            for (Areadef.Rect rect : selected.getRects()) {
                int rsx = rect.getSx();
                int rsy = rect.getSy();
                int rex = rect.getEx()-1;
                int rey = rect.getEy()-1;
                if(rex < rsx){
                    System.err.println("wrong order of rect in " + selected.getDefname() + ": " + rect);
                    int a = rsx;
                    rsx = rex;
                    rex = a;
                }
                if(rey < rsy){
                    System.err.println("wrong order of rect in " + selected.getDefname() + ": " + rect);
                    int a = rsy;
                    rsy = rey;
                    rey = a;
                }
                
                drawRect(pw, rsx, rsy, rex, rey, t, w, h);
            }
        }
        
        drawColor = Color.CRIMSON;
        drawCross(pw, x, y, t, w, h);
        
        return wi;
    }
    
    private void drawRect(PixelWriter pw, int sx, int sy, int ex, int ey, TransCoords t, int w, int h){
        boolean notX = ex < t.sx || sx > (t.sx + t.widthOnOrig);
        boolean notY = ey < t.sy || sy > (t.sy + t.heightOnOrig);
        if(notX || notY) {
            return;
        }

        int modSX = t.firstX(sx);
        int modEX = t.lastX(ex);
        int modSY = t.firstY(sy);
        int modEY = t.lastY(ey);
                
        for(int dx = modSX; dx<= modEX; dx++){
            safeDraw(pw,w,h, dx, modSY);
            safeDraw(pw,w,h, dx, modEY);
        }
        for(int dy = modSY; dy<= modEY; dy++){
            safeDraw(pw,w,h, modSX, dy);
            safeDraw(pw,w,h, modEX, dy);
        }
    }


    private void drawCross(PixelWriter pw, int x, int y, TransCoords t, int w, int h) {
        int dx = t.firstX(x);
        int dy = t.firstY(y);
        
        for(int s=0; s<5; s++){
            safeDraw(pw,w,h, dx-s-1, dy-s-1);
            safeDraw(pw,w,h, dx+t.zoom+s, dy-s-1);
            safeDraw(pw,w,h, dx-s-1, dy+t.zoom+s);
            safeDraw(pw,w,h, dx+t.zoom+s, dy+t.zoom+s);
        }
    }
    
    public Image getImage(int w, int h, int zoom) {
        TransCoords t = new TransCoords(w, h, zoom);

        return zoomedImg(image, t.sx, t.sy, t.widthOnOrig, t.heightOnOrig, t.zoom);
    }

    private Image zoomedImg(Image input, int sx, int sy, int sw, int sh, int zoom) {
        WritableImage output = new WritableImage(sw * zoom, sh * zoom);

        PixelReader reader = input.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < sh; y++) {
            for (int x = 0; x < sw; x++) {
                final int argb = reader.getArgb(x + sx, y + sy);
                for (int dy = 0; dy < zoom; dy++) {
                    for (int dx = 0; dx < zoom; dx++) {
                        writer.setArgb(x * zoom + dx, y * zoom + dy, argb);
                    }
                }
            }
        }

        return output;
    }

    void setPos(int x, int y, int w, int h, int zoom) {
        TransCoords t = new TransCoords(w, h, zoom);
        int xOnOrig = x / zoom;
        int yOnOrig = y / zoom;
        setPos(t.sx + xOnOrig, t.sy + yOnOrig);
    }

    public void setPos(int x, int y) {
        setX(x);
        setY(y);
    }

    public void setX(int x) {
        if (x < 0) {
            this.x = 0;
            return;
        }
        if (x >= width) {
            this.x = width - 1;
            return;
        }
        this.x = x;
    }

    public void setY(int y) {
        if (y < 0) {
            this.y = 0;
            return;
        }
        if (y >= height) {
            this.y = height - 1;
            return;
        }
        this.y = y;
    }

    private void safeDraw(PixelWriter pw, int w, int h, int x, int y) {
        if(x<0) return;
        if(y<0) return;
        if(x>= w) return;
        if(y>= h) return;
        pw.setColor(x, y, drawColor);
    }
    
    private class TransCoords{

        private final int widthOnOrig;
        private final int heightOnOrig;
        private final int sx;
        private final int sy;
        private final int zoom;

        public TransCoords(int w, int h, int zoom) {
            if (zoom < 1) {
                this.zoom = 1;
            } else if (zoom > 16) {
                this.zoom = 16;
            } else {
                this.zoom = zoom;
            }

            widthOnOrig = dimensionOnOrig(width, w);
            heightOnOrig = dimensionOnOrig(height, h);

            sx = start(width, x, widthOnOrig);
            sy = start(height, y, heightOnOrig);
        }

        private int start(int dimension, int p, int dimensioOnOrig) {
            int sx = p - dimensioOnOrig / 2;
            if (sx < 0) sx = 0;
            if (sx + dimensioOnOrig >= dimension) sx = dimension - dimensioOnOrig - 1;
            return sx;
        }

        private int dimensionOnOrig(int origDim, int dim) {
            int dimensionOnOrig = dim / zoom;
            if (dimensionOnOrig > origDim) dimensionOnOrig = origDim;
            return dimensionOnOrig;
        }
        
        private int firstX(int x) {
            return (x - sx) * zoom;
        }

        private int firstY(int y) {
            return (y - sy) * zoom;
        }

        private int lastX(int x) {
            return (x - sx + 1) * zoom - 1;
        }

        private int lastY(int y) {
            return (y - sy + 1) * zoom - 1;
        }
    }
}
