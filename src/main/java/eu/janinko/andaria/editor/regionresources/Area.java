package eu.janinko.andaria.editor.regionresources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class Area {
	private String defname;
	private String name;
	private Set<Rect> rects = new HashSet<Rect>();
	private Set<String> events = new HashSet<String>();

	private Matcher mResource = Pattern.compile("^\\[areadef ([a-z_0-9]+)].*").matcher("");
	public Area(BufferedReader r) throws IOException, IllegalStateException{
		String line = r.readLine();
		mResource.reset(line.toLowerCase());
		if(!mResource.matches()){
			throw new IllegalStateException("this is not areadef");
		}
		defname = mResource.group(1);

		line = r.readLine();
		while(line != null && !line.startsWith("[")){
			try{
				String lline = line.toLowerCase();
				if(lline.startsWith("rect")){
					parseRect(lline);
				}else if(lline.startsWith("events")){
					parseEvents(lline);
				}else if(lline.startsWith("name")){
					parseName(lline);
				}else if(lline.startsWith("group")){
					// ignore group
				}else if(lline.startsWith("tag.")){
					// ignore tags
				}else if(lline.trim().isEmpty()){
					// ignore empty line
				}else{
					System.err.println("unknown line: " + line );
				}
			}catch(IllegalStateException ex){
				throw new IllegalStateException("Failed parsing line: " + line, ex);
			}

			line = r.readLine();
		}
		if(name == null || rects.isEmpty()){
			throw new IllegalStateException("Not initialized all");
		}
	}

	public String getDefname() {
		return defname;
	}

	public String getName() {
		return name;
	}

	public Set<Rect> getRects() {
		return Collections.unmodifiableSet(rects);
	}

	public Set<String> getEvents() {
		return Collections.unmodifiableSet(events);
	}

	private Matcher mRect = Pattern.compile("^rect *= *([0-9]+),([0-9]+),([0-9]+),([0-9]+)(,0)?( *//.*)?").matcher("");
	private void parseRect(String lline) {
		mRect.reset(lline); mRect.matches();
		rects.add(new Rect(Integer.parseInt(mRect.group(1)),
						   Integer.parseInt(mRect.group(2)),
						   Integer.parseInt(mRect.group(3)),
						   Integer.parseInt(mRect.group(4))));
	}

	private Matcher mEvents = Pattern.compile("^events *= *([a-z_0-9, ]+)( *//.*)?").matcher("");
	private void parseEvents(String lline) {
		mEvents.reset(lline); mEvents.matches();
		for(String s : mEvents.group(1).split(",")){
			events.add(s.trim());
		}
	}

	private Matcher mName = Pattern.compile("^name *= *([^/]+)( *//.*)?").matcher("");
	private void parseName(String lline) {
		mName.reset(lline); mName.matches();
		name = mName.group(1);
	}


	public static class Rect{
		private int x1, y1;
		private int x2, y2;

		public Rect(int x1, int y1, int x2, int y2) {
			this.x1 = Math.min(x1, x2);
			this.y1 = Math.min(y1, y2);
			this.x2 = Math.max(x1, x2);
			this.y2 = Math.max(y1, y2);
		}

		public int getX1() {
			return x1;
		}

		public int getY1() {
			return y1;
		}

		public int getX2() {
			return x2;
		}

		public int getY2() {
			return y2;
		}
	}
}
