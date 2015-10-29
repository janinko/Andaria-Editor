package eu.janinko.andaria.editor.regionresources;

import eu.janinko.andaria.editor.regionresources.Area.Rect;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class AreaParser {
	private final ParseReader reader;
	private String line;

	public AreaParser(ParseReader r){
		this.reader = r;
	}

	private final Matcher mArea = Pattern.compile("^\\[areadef ([a-z_0-9]+)].*").matcher("");
	public Area parseArea() throws IOException{
		line = reader.readLine();
		if(line == null) throw new IllegalStateException("There is nothing to read.");
		mArea.reset(line);
		if(!mArea.matches()) throw new IllegalStateException("This is not areadef.");

		Area.Builder builder = new Area.Builder(mArea.group(1));

		line = reader.readLine();
		while (line != null && !line.startsWith("[")) {
			if (line.startsWith("rect")) {
				parseRect(builder);
			} else if (line.startsWith("events")) {
				parseEvents(builder);
			} else if (line.startsWith("name")) {
				parseName(builder);
			} else if (line.startsWith("group")) {
				// ignore group
			} else if (line.startsWith("tag.")) {
				// ignore tags
			} else if (line.startsWith("class")) {
				// ignore class
			} else if (line.startsWith("flags")) {
				// ignore flags
			} else if (line.startsWith("mapplane")) {
				// ignore mapplane
			} else if (line.startsWith("p=")) {
				// ignore p
			} else if (line.startsWith("rainchance")) {
				// ignore rainchance
			} else if (line.startsWith("coldchance")) {
				// ignore coldchance
			} else if (line.isEmpty()) {
				// ignore empty line
			} else {
				System.err.println("AP unknown line: " + line);
			}

			line = reader.readLine();
		}

		return builder.build();
	}

	private Matcher mRect = Pattern.compile("^rect *= *([0-9]+),([0-9]+),([0-9]+),([0-9]+)(,[0-5])?").matcher("");
	private void parseRect(Area.Builder builder) {
		mRect.reset(line);
		if(!mRect.matches()) throw new IllegalStateException("Expected rect line. " + line);
		Rect rect = new Rect(Integer.parseInt(mRect.group(1)),
						     Integer.parseInt(mRect.group(2)),
						     Integer.parseInt(mRect.group(3)),
						     Integer.parseInt(mRect.group(4)));
		builder.addRect(rect);
	}

	private Matcher mEvents = Pattern.compile("^events *= *([a-z_0-9, ]+)( *//.*)?").matcher("");
	private void parseEvents(Area.Builder builder) {
		mEvents.reset(line);
		if(!mEvents.matches()) throw new IllegalStateException("Expected events line.");
		for(String s : mEvents.group(1).split(",")){
			builder.addEvent(s.trim());
		}
	}

	private Matcher mName = Pattern.compile("^name *= *([^/]+)( *//.*)?").matcher("");
	private void parseName(Area.Builder builder) {
		mName.reset(line);
		if(!mName.matches()) throw new IllegalStateException("Expected name line.");
		builder.setName(mName.group(1));
	}

}
