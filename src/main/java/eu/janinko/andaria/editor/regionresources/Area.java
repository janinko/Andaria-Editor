
package eu.janinko.andaria.editor.regionresources;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class Area {
	private final String defname;
	private String name;
	private final Set<Area.Rect> rects = new HashSet<Area.Rect>();
	private final Set<String> events = new HashSet<String>();

	public Area(String defname) {
		this.defname = defname;
	}
	
	public String getDefname() {
		return defname;
	}

	public String getName() {
		return name;
	}

	public Set<Area.Rect> getRects() {
		return Collections.unmodifiableSet(rects);
	}

	public Set<String> getEvents() {
		return Collections.unmodifiableSet(events);
	}

	public static class Builder {

		private Area martix;

		public Builder(String defname) {
			martix = new Area(defname);
		}
		
		public void setName(String name) {
			martix.name = name;
		}

		public Area build() {
			if(martix.name == null || martix.rects.isEmpty()){
				throw new IllegalStateException("Not initialized all ("+martix.defname+")");
			}
			return martix;
		}

		public void addRect(Rect rect) {
			martix.rects.add(rect);
		}

		public void addEvent(String event) {
			martix.events.add(event);
		}
	}
	
	public static class Rect{
		private final int x1, y1;
		private final int x2, y2;

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

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 11 * hash + this.x1;
			hash = 11 * hash + this.y1;
			hash = 11 * hash + this.x2;
			hash = 11 * hash + this.y2;
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			final Rect other = (Rect) obj;
			if (this.x1 != other.x1) return false;
			if (this.y1 != other.y1) return false;
			if (this.x2 != other.x2) return false;
			if (this.y2 != other.y2) return false;
			return true;
		}
	}
}
