
package eu.janinko.andaria.editor;

import eu.janinko.Andaria.ultimasdk.files.Anims;
import eu.janinko.Andaria.ultimasdk.files.Arts;
import eu.janinko.Andaria.ultimasdk.files.Gumps;
import eu.janinko.Andaria.ultimasdk.files.Hues;
import eu.janinko.Andaria.ultimasdk.files.TileData;
import eu.janinko.Andaria.ultimasdk.files.anims.Anim;
import eu.janinko.Andaria.ultimasdk.files.arts.Art;
import eu.janinko.Andaria.ultimasdk.files.graphics.Bitmap;
import eu.janinko.Andaria.ultimasdk.files.graphics.Color;
import eu.janinko.Andaria.ultimasdk.files.gumps.Gump;
import eu.janinko.Andaria.ultimasdk.files.hues.Hue;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 * @author janinko
 */
public class HueEditor {
	private String uoPath = "/home/jbrazdil/Ultima/hra/";
	private Gumps gumps;
	private Hues hues;
	private TileData tiledata;
	private Arts arts;
	private Anims anims;

	private int width = 800;
	private int height = 30;
	private int part = width / 32;


	public void setUOPath(String uoPath) throws IOException{
		if(Objects.equals(this.uoPath, uoPath)) return;
		this.uoPath = uoPath;
		init();
	}

	public void init() throws IOException {
		File huesmul = new File("/home/janinko/Ultima/grafika/hues/"+ "hues-new.mul");
		File artidx = new File(uoPath, "artidx.mul");
		File artmul = new File(uoPath, "art.mul");
		File gumpidx = new File(uoPath, "gumpidx.mul");
		File gumpart = new File(uoPath, "gumpart.mul");
		File animidx = new File(uoPath, "anim.idx");
		File animmul = new File(uoPath, "anim.mul");
		File tiledatamul = new File(uoPath, "tiledata.mul");

		hues = new Hues(new FileInputStream(huesmul));
		gumps = new Gumps(new FileInputStream(gumpidx), gumpart);
		tiledata = new TileData(new FileInputStream(tiledatamul));
		arts = new Arts(new FileInputStream(artidx), artmul);
		anims = new Anims(new FileInputStream(animidx), animmul);
	}
	
	public void paint(BufferedImage img, int startX, int startY, int width, int height, int rgb){
		for(int x = startX; x<startX + width; x++){
			for(int y = startY; y<startY + height; y++){
				img.setRGB(x, y, rgb);
			}
		}
	}

	public BufferedImage getHueImage(int id){
		Hue h = hues.getHue(id);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		for(int i=0; i<32; i++){
			paint(image,i*part,0,part,height,h.getColor(i).getAGBR());
		}


		return image;
	}

	public BufferedImage getImage(int id) throws IOException{
		Art a = arts.getStatic(id);
		if(a == null) return null;
		return a.getImage();
	}

	public Hue getHue(int id){
		return hues.getHue(id);
	}

	public int getHueOn(int x) {
		return x / part;
	}

	public BufferedImage getColorImage(int red, int green, int blue) {
		Color c = Color.getInstance((short) red, (short) green, (short) blue);

		BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_4BYTE_ABGR);
		paint(image,0,0,50,50,c.getAGBR());
		return image;
	}

	void setHue(int hueId, int selected, int red, int green, int blue) {
		Color c = Color.getInstance((short) red, (short) green, (short) blue);
		hues.getHue(hueId).getColors()[selected]=c;
	}

	public BufferedImage getColoredItem(int hue, int item) throws IOException{
		Hue h = hues.getHue(hue);
		Gump g = gumps.getGump(item);
		Bitmap b = g.getBitmap();
		b.hue(h, false);
		return b.getImage();
	}

	public BufferedImage getColoredAnimation(int hue, int anim, int position) throws IOException{
		Hue h = hues.getHue(hue);
		Anim a = anims.getAnim1(anim, Anims.STAY, position);
		Bitmap b = a.getFrame(0).getBitmap();
		b.hue(h, false);
		return b.getImage();
	}

	private int[] partial = {50404,50405,50406,50407,50408,50409,50410,50411,
		50412,50413,50419,50438,50439,50448,50453,50468,50480,50499,50852,
		50912};
	private int[] nonpart = {50430,50431,50434,50435,50439,50449,
		50455,50465,50466,50469,50476,50477,50480,50490,50492,50542,50543,50544,50545,50546,50560,
		50590,50690,50691,50909,50910,50911,50913};
	public void generate(int hue) throws IOException {
		Hue h = hues.getHue(hue);
		File dir = new File("/tmp/gumps/hue/" + hue);
		dir.mkdirs();
		for (int i : partial) {
			Gump g = gumps.getGump(i);
			if (g == null) {
				System.out.println(i + " null");
				continue;
			}
			Bitmap b = g.getBitmap();
			b.hue(h, true);
			BufferedImage image = b.getImage();
			if (image != null) {
				File out = new File("/tmp/gumps/hue/" + hue + "/" + (i - 50000) + ".png");
				ImageIO.write(image, "png", out);
			}
		}
		for (int i : nonpart) {
			Gump g = gumps.getGump(i);
			if (g == null) {
				System.out.println(i + " null");
				continue;
			}
			Bitmap b = g.getBitmap();
			b.hue(h, false);
			BufferedImage image = b.getImage();
			if (image != null) {
				File out = new File("/tmp/gumps/hue/" + hue + "/" + (i - 50000) + ".png");
				ImageIO.write(image, "png", out);
			}
		}
	}

	public void copy(int from, int to) {
		hues.setHue(to, new Hue(hues.getHue(from)));
	}

	public void save() throws IOException{
		File huesmul = new File("/home/janinko/Ultima/grafika/hues/"+ "hues-new.mul");
		hues.save(new FileOutputStream(huesmul));
	}

	public BufferedImage getPallete(){
		BufferedImage image = new BufferedImage(50*32, 60*16, BufferedImage.TYPE_4BYTE_ABGR);
		for(int x=0; x<50; x++){
			for(int y=0; y<60; y++){
				Hue h = hues.getHue(x*60+y+1);
				for(int c=0; c<32; c++){
					paint(image,x*32+c,y*16,1,16,h.getColor(c).getAGBR());
				}
			}
		}
		return image;
	}
}
