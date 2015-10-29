package eu.janinko.andaria.editor.regionresources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class RegionResources {
	private final Map<String, RegionType> regions = new HashMap<String, RegionType>();
	private final Map<String, Resource> resources = new HashMap<String, Resource>();


	public RegionResources(Reader r) throws IOException{
		ParseReader reader = new ParseReader(new BufferedReader(r));
		ResourceParser resourceParser = new ResourceParser(reader);
		RegionTypeParser regionTypeParser = new RegionTypeParser(reader, resources);
		reader.mark();
		String line = reader.readLine();
		while (line != null) {
			//System.out.println(line);
			if (line.startsWith("[")) {
				reader.reset();
				try {
					if (line.toLowerCase().contains("regionresource")) {
						Resource res = resourceParser.parseResource();
						resources.put(res.getName(), res);
					} else if (line.toLowerCase().contains("regiontype")) {
						RegionType rt = regionTypeParser.parseRegionType();
						if(rt.getTotalResources() > 0)
							regions.put(rt.getName(), rt);
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

	public Map<String, RegionType> getRegions() {
		return Collections.unmodifiableMap(regions);
	}

	public Map<String, Resource> getResources() {
		return Collections.unmodifiableMap(resources);
	}
}
