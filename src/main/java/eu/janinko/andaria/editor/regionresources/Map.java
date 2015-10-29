package eu.janinko.andaria.editor.regionresources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class Map {
	private final HashMap<String, Area> areas = new HashMap<String, Area>();

	public Map(Reader r) throws IOException{
		ParseReader reader = new ParseReader(new BufferedReader(r));
		AreaParser areaParser = new AreaParser(reader);
		reader.mark();
		String line = reader.readLine();
		while (line != null) {
			//System.out.println(line);
			if (line.startsWith("[")) {
				reader.reset();
				try {
					if (line.toLowerCase().contains("[areadef")) {
						Area area = areaParser.parseArea();
						areas.put(area.getDefname(), area);
					}
				} catch (IllegalStateException ex) {
					System.err.println("Chyba pri parsovani: " + ex.getMessage());
					System.err.println(reader.getCurrentOriginalLine());
				}
				reader.reset();
				reader.readLine();
				reader.mark();
				line = reader.readLine();
				while(line != null && !line.startsWith("[")){
					reader.mark();
					line = reader.readLine();
				}
				reader.reset();
			}
			reader.mark();
			line = reader.readLine();
		}
	}

	public java.util.Map<String, Area> getAreas() {
		return Collections.unmodifiableMap(areas);
	}
}
