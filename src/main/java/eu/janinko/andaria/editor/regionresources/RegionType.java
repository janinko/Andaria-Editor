package eu.janinko.andaria.editor.regionresources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class RegionType {
	private String name;
	private String target;
	private Map<Resource, Integer> resources = new HashMap<Resource, Integer>();
	private int totalResources;

	private Matcher mRegionType = Pattern.compile("^\\[regiontype ([a-z_0-9]+) ?([a-z_0-9]+)?].*").matcher("");
	public RegionType(BufferedReader r, Map<String,Resource> res) throws IOException, IllegalStateException{
		String line = r.readLine();
		mRegionType.reset(line.toLowerCase());
		if(!mRegionType.matches()){
			throw new IllegalStateException("this is not regiontype");
		}
		name = mRegionType.group(1);
		target = mRegionType.group(2);
		
		line = r.readLine();
		while(line != null && !line.startsWith("[")){
			String lline = line.toLowerCase();
			try{
				if(lline.contains("resources")){
					parseResources(lline, res);
				}else if(lline.contains("on=@")){
					break;
				}else{
					System.err.println("unknown line: " + line );
				}
			}catch(IllegalStateException ex){
				throw new IllegalStateException("Failed parsing line: " + line, ex);
			}

			line = r.readLine();
		}
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

	private Matcher mResource = Pattern.compile("^resources *= *([0-9]+) ([a-z_0-9]+).*").matcher("");
	private void parseResources(String lline, Map<String, Resource> res) {
		mResource.reset(lline); mResource.matches();
		String rt = mResource.group(2);
		Resource r = res.get(rt);
		if(r == null){
			throw new IllegalStateException("Resource not known: " + rt);
		}
		Integer num = Integer.parseInt(mResource.group(1));
		totalResources += num;
		if(resources.containsKey(r)){
			num += resources.get(r);
		}
		resources.put(r, num);
	}
}
