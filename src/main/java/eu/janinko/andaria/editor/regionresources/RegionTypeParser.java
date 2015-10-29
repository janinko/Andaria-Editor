package eu.janinko.andaria.editor.regionresources;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class RegionTypeParser {
	private final ParseReader reader;
	private final Map<String,Resource> resources;
	private String line;

	public RegionTypeParser(ParseReader r, Map<String,Resource> res){
		this.reader = r;
		this.resources = res;
	}

	private Matcher mRegionType = Pattern.compile("^\\[regiontype ([a-z_0-9]+) ?([a-z_0-9]+)?].*").matcher("");
	public RegionType parseRegionType() throws IOException{
		line = reader.readLine();
		if(line == null) throw new IllegalStateException("There is nothing to read.");
		mRegionType.reset(line);
		if(!mRegionType.matches()) throw new IllegalStateException("This is not regiontype.");

		RegionType.Builder builder = new RegionType.Builder(mRegionType.group(1));
		builder.setTarget(mRegionType.group(2));

		line = reader.readLine();
		while(line != null && !line.startsWith("[")){
			try{
				if(line.contains("resources")){
					parseResources(builder);
				}else if(line.contains("on=@")){
					break;
				}else if(line.isEmpty()){
					// ok
				}else{
					System.err.println("RTP unknown line: " + line );
				}
			}catch(IllegalStateException ex){
				throw new IllegalStateException("Failed parsing line: " + line, ex);
			}

			line = reader.readLine();
		}

		return builder.build();
	}

	private Matcher mResource = Pattern.compile("^resources *= *([0-9]+) ([a-z_0-9]+).*").matcher("");
	private void parseResources(RegionType.Builder builder) {
		mResource.reset(line);
		if(!mResource.matches()) throw new IllegalStateException("Expected resources line.");

		String resourceName = mResource.group(2);
		Resource resource = resources.get(resourceName);
		if(resource == null) throw new IllegalStateException("Unknown resource: " + resourceName);

		Integer num = Integer.parseInt(mResource.group(1));
		builder.addResources(resource, num);
	}
}
