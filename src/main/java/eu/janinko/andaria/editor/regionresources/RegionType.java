
package eu.janinko.andaria.editor.regionresources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class RegionType {
	private String name;
	private String target;
	private Map<Resource, Integer> resources = new HashMap<Resource, Integer>();
	private int totalResources;

	private RegionType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getTarget() {
		return target;
	}

	public Map<Resource, Integer> getResources() {
		return Collections.unmodifiableMap(resources);
	}

	public int getTotalResources() {
		return totalResources;
	}

	public Map<String, Double> getMinResources(){
		Map<String, Double> ret = new HashMap<String, Double>();
		for(Map.Entry<Resource, Integer> e : resources.entrySet()){
			Resource r = e.getKey();
			for(Map.Entry<String, Double> ee : r.getMinResources().entrySet()){
				String item = ee.getKey();
				double count = ee.getValue() * e.getValue() / totalResources;
				if(ret.containsKey(item)){
					count += ret.get(item);
				}
				ret.put(item, count);
			}
		}
		return ret;
	}

	public Map<String, Double> getMaxResources(){
		Map<String, Double> ret = new HashMap<String, Double>();
		for(Map.Entry<Resource, Integer> e : resources.entrySet()){
			Resource r = e.getKey();
			for(Map.Entry<String, Double> ee : r.getMaxResources().entrySet()){
				String item = ee.getKey();
				double count = ee.getValue() * e.getValue() / totalResources;
				if(ret.containsKey(item)){
					count += ret.get(item);
				}
				ret.put(item, count);
			}
		}
		return ret;
	}

	public static class Builder {

		private RegionType martix;

		public Builder(String name) {
			martix = new RegionType(name);
		}

		public void setTarget(String target) {
			martix.target = target;
		}

		public RegionType build() {
			return martix;
		}

		public void addResources(Resource resource, Integer num) {
			martix.totalResources += num;
			if(martix.resources.containsKey(resource)){
				num += martix.resources.get(resource);
			}
			martix.resources.put(resource, num);
		}

	}
}
