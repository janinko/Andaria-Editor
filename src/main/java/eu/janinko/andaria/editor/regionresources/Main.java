package eu.janinko.andaria.editor.regionresources;

import eu.janinko.andaria.ultimasdk.files.Statics;
import eu.janinko.andaria.ultimasdk.files.statics.Static;
import eu.janinko.andaria.editor.regionresources.Area.Rect;
import eu.janinko.andaria.ultimasdk.UOFiles;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class Main {
	private static final String scpPath = "/home/jbrazdil/Ultima/andaria/sphere/";
	private static final Path uoPath = Paths.get("/home/jbrazdil/Ultima/hra/");
	private static final String regionTypePath = scpPath + "region_types.scp";
	private static final String mapPath = scpPath + "map.scp";


	static final Set<String> bannedTypes = new HashSet<String>();
	static {
		bannedTypes.add("r_dungeon_rock");
		bannedTypes.add("r_default_rock");
		bannedTypes.add("r_water");
		bannedTypes.add("r_default_water");
		bannedTypes.add("r_tree");
		bannedTypes.add("r_default_tree");
		bannedTypes.add("r_default_grass");
		bannedTypes.add("r_sand_lewan");
		bannedTypes.add("r_forests");
		bannedTypes.add("r_dul_falesny_svet");
	}
	static final Set<String> bannedRegions = new HashSet<String>();
	static {
		bannedRegions.add("a_sachta_zasypana_4");
		bannedRegions.add("a_sachta_zasypana_3");
		bannedRegions.add("a_sachta_zasypana_2");
		bannedRegions.add("a_sachta_zasypana_1");
		bannedRegions.add("a_dul_zasypany");
		bannedRegions.add("a_podzemni_sachta_stribro");
		bannedRegions.add("a_podzemni_sachta_drahokam");
		bannedRegions.add("a_podzemni_sachta_mytril");
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		RegionResources rr = new RegionResources(new FileReader(regionTypePath));
		Map m = new Map(new FileReader(mapPath));
        
		Statics statics = UOFiles.loadStaticsFromDir(uoPath);

		Set<String> types = new HashSet<String>();
		for(String rt : rr.getRegions().keySet()){
			if(!bannedTypes.contains(rt)){
				types.add(rt);
			}
		}

		Set<Area> interestingAreas = new HashSet<Area>();
		for(Area a : m.getAreas()){
			if(a.getDefname().equals("a_lewan_ryzovani"))
				interestingAreas.add(a);/*
			if(bannedRegions.contains(a.getDefname()))
				continue;
			for(String type : types){
				if(a.getEvents().contains(type)){
					interestingAreas.add(a);
					break;
				}
			}*/
		}
		java.util.Map<String, Double> pocty = new HashMap<String, Double>();
		int total = 0;

		Thread.sleep(1000);
		for(Area a : interestingAreas){
			int count = 0;
			for(Rect r : a.getRects()){
				count += countStatic(r, statics);
			}
			count = 511;
			total += count;
			System.out.println(a.getDefname() + "\t" + count);
			for(String event : a.getEvents()){
				if("r_sand_lewan".equals(event)){
				//if(rr.getRegions().containsKey(event)){
					for(Entry<String, Double> e : rr.getRegions().get(event).getMinResources().entrySet()){
						String item = e.getKey();
						double cnt = e.getValue() * count;
						if(pocty.containsKey(item)){
							cnt += pocty.get(item);
						}
						pocty.put(item, cnt);
					}
				}
			}
		}
		for(Entry<String, Double> e : pocty.entrySet()){
			System.out.println(e.getKey() + "\t" + e.getValue());
		}
		System.out.println("total: " + total);
	}

	static final Set<Integer> acceptedIDs = new HashSet<Integer>();
	static {
		acceptedIDs.add(0x53b);
		acceptedIDs.add(0x53c);
		acceptedIDs.add(0x53d);
		acceptedIDs.add(0x53e);
		acceptedIDs.add(0x53f);
		acceptedIDs.add(0x540);
		acceptedIDs.add(0x541);
		acceptedIDs.add(0x542);
		acceptedIDs.add(0x543);
		acceptedIDs.add(0x544);
		acceptedIDs.add(0x545);
		acceptedIDs.add(0x546);
		acceptedIDs.add(0x547);
		acceptedIDs.add(0x548);
		acceptedIDs.add(0x549);
		acceptedIDs.add(0x54a);
		acceptedIDs.add(0x54b);
		acceptedIDs.add(0x54c);
		acceptedIDs.add(0x54d);
		acceptedIDs.add(0x54e);
		acceptedIDs.add(0x54f);
		acceptedIDs.add(0x551);
		acceptedIDs.add(0x552);
		acceptedIDs.add(0x553);
		acceptedIDs.add(0x56a);
		acceptedIDs.add(0x8e1);
		acceptedIDs.add(0x8e2);
		acceptedIDs.add(0x8e3);
		acceptedIDs.add(0x8e4);
		acceptedIDs.add(0x8e5);
		acceptedIDs.add(0x8e6);
		acceptedIDs.add(0x8e7);
		acceptedIDs.add(0x8e8);
		acceptedIDs.add(0x8e9);
		acceptedIDs.add(0x8ea);
		acceptedIDs.add(0x8e0);
	}
	static int countStatic(Rect r, Statics sts) throws IOException{
		int count = 0;
		for(int x = r.getX1(); x <= r.getX2(); x++){
			for(int y = r.getY1(); y <= r.getY2(); y++){
				List<Static> statics = sts.getStatics(x, y);
				for(Static s : statics){
					if(acceptedIDs.contains(s.getId())){
						count++;
					}
				}
			}
		}
		return count;
	}
}
