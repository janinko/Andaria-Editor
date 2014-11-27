package eu.janinko.andaria.editor.regionresources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class Map {
	private Set<Area> areas = new HashSet<Area>();

	private static final int MARK = 50000;

	public Map(Reader r) throws IOException{
		BufferedReader reader = new BufferedReader(r);
		reader.mark(MARK);
		String line = reader.readLine();
		while (line != null) {
			System.out.println(line);
			if (line.startsWith("[")) {
				reader.reset();
				try {
					if (line.toLowerCase().contains("[areadef")) {
						Area area = new Area(reader);
						areas.add(area);
					}
				} catch (IllegalStateException ex) {
					System.err.println("Chyba pri parsovani: " + ex.getMessage());
					ex.printStackTrace();
				}
				reader.reset();
				line = reader.readLine();
				reader.mark(MARK);
				line = reader.readLine();
				while(line != null && !line.startsWith("[")){
					reader.mark(MARK);
					line = reader.readLine();
				}
				reader.reset();
			}
			reader.mark(MARK);
			line = reader.readLine();
		}
	}

	public Set<Area> getAreas() {
		return Collections.unmodifiableSet(areas);
	}
}
