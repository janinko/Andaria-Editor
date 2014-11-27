package eu.janinko.andaria.editor.regionresources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class RegionResources {
	private Map<String, RegionType> regions = new HashMap<String, RegionType>();
	private Map<String, Resource> resources = new HashMap<String, Resource>();

	private static final int MARK = 50000;

	public RegionResources(Reader r) throws IOException{
		BufferedReader reader = new BufferedReader(r);
		reader.mark(MARK);
		String line = reader.readLine();
		while (line != null) {
			System.out.println(line);
			if (line.startsWith("[")) {
				reader.reset();
				try {
					if (line.toLowerCase().contains("regionresource")) {
						Resource res = new Resource(reader);
						resources.put(res.getName(), res);
					} else if (line.toLowerCase().contains("regiontype")) {
						RegionType rt = new RegionType(reader, resources);
						if(rt.getTotalResources() > 0)
							regions.put(rt.getName(), rt);
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

	public Map<String, RegionType> getRegions() {
		return Collections.unmodifiableMap(regions);
	}

	public Map<String, Resource> getResources() {
		return Collections.unmodifiableMap(resources);
	}
}
