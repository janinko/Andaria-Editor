
package eu.janinko.andaria.editor.regionresources;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class DolyCounter {
	private static final String scpPath = "/home/jbrazdil/Ultima/andaria/sphere/";
	private static final String regionTypePath = scpPath + "region_types.scp";
	private static final String mapPath = scpPath + "map.scp";
	/*
	Andorsky dul c.1: 645 tezitelnych policek a_dul_andor_1
	Andorsky dul c.2: 575 tezitelnych policek a_dul_andor_2
	Poustni dul: 299 tezitelnych policek a_dul_poustni
	Thyrsky dul c.1: 621 tezitelnych policek a_dul_thyris_1
	Thyrsky dul c.2: 293 tezitelnych policek a_dul_thyris_2
	Thyrsky dul c.3: 435 tezitelnych policek a_dul_thyris_3
	Thyrsky dul c.4: 324 tezitelnych policek a_dul_thyris_4
	a_dul_thyris_5
	Ilerensky dul: 325 tezitelnych policek a_dul_ileren
	Vodni dul: 239 tezitelnych policek a_dul_vodni
	Lewansky potok c. 1: 296 poli a_lewan_ryzovani
	Lewansky potok c. 2: 215 poli a_lewan_ryzovani
	 */
	private static final HashMap<String, Integer> doly = new HashMap<>();
	static {
		doly.put("a_dul_andor_1", 645);
		doly.put("a_dul_andor_2", 575);
		doly.put("a_dul_poustni", 299);
		doly.put("a_dul_thyris_1", 621);
		doly.put("a_dul_thyris_2", 293);
		doly.put("a_dul_thyris_3", 435);
		doly.put("a_dul_thyris_4", 324);
		doly.put("a_dul_ileren", 325);
		doly.put("a_dul_vodni", 239);
		doly.put("a_lewan_ryzovani", 296 + 215);
	}

	public static void main(String[] args) throws IOException {
		RegionResources rr = new RegionResources(new FileReader(regionTypePath));
		Map m = new Map(new FileReader(mapPath));

		java.util.Map<String, RegionType> regions = rr.getRegions();

		int total = 0;
		HashMap<String, Double> minResources = new HashMap<>();
		HashMap<String, Double> maxResources = new HashMap<>();
		for(java.util.Map.Entry<String, Integer> e : doly.entrySet()){
			String defname = e.getKey();
			int rozloha = e.getValue();
			Area area = m.getAreas().get(defname);

			for(String event : area.getEvents()){
				if(regions.containsKey(event)){
					RegionType rt = regions.get(event);
					mergeMaps(minResources, rt.getMinResources(), rozloha);
					mergeMaps(maxResources, rt.getMaxResources(), rozloha);
				}
			}
			total += rozloha;
		}
		for (String resource : minResources.keySet()) {
			double min = minResources.get(resource) / total;
			double max = maxResources.get(resource) / total;
			System.out.printf("%s\t%f\t%f\n", resource, min, max);
		}
		System.out.println("total: " + total);
	}

	private static void mergeMaps(java.util.Map<String, Double> dst, java.util.Map<String, Double> add, int rozloha){
		for(java.util.Map.Entry<String, Double> e : add.entrySet()){
			String key = e.getKey();
			Double value = e.getValue() * rozloha;
			if(dst.containsKey(key)){
				value += dst.get(key);
			}
			dst.put(key, value);
		}
	}
}
