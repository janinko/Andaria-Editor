
package eu.janinko.andaria.editor;

import eu.janinko.Andaria.ultimasdk.files.Arts;
import eu.janinko.Andaria.ultimasdk.files.Gumps;
import eu.janinko.Andaria.ultimasdk.files.Hues;
import eu.janinko.Andaria.ultimasdk.files.TileData;
import eu.janinko.Andaria.ultimasdk.files.arts.Art;
import eu.janinko.Andaria.ultimasdk.files.tiledata.ItemData;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author janinko
 */
public class TiledataEditor {
	private String uoPath = "/home/janinko/Ultima/hra";
	private TileData tiledata;
	private Arts arts;

	public TiledataEditor() {
		File artidx = new File(uoPath, "artidx.mul");
		File artmul = new File(uoPath, "art.mul");
		File tiledatamul = new File(uoPath, "tiledata.mul");
		try {

			tiledata = new TileData(new FileInputStream(tiledatamul));
			arts = new Arts(new FileInputStream(artidx), artmul);
		} catch (IOException ex) {
			Logger.getLogger(TiledataEditor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	ItemData getTiledata(int selected) {
		return tiledata.getItem(selected);
	}

	BufferedImage getImage(int selected) throws IOException {
		Art a = arts.getStatic(selected);
		if(a==null) return null;
		return a.getImage();
	}

	public void save() throws IOException{
		File tiledatamul = new File(uoPath, "tiledata.mul");
		tiledata.save(new FileOutputStream(tiledatamul));
	}

	void copy(int from, int to) {
		tiledata.setItem(to, new ItemData(tiledata.getItem(from)));
	}
}
