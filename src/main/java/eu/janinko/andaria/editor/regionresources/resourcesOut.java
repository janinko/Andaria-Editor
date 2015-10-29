
package eu.janinko.andaria.editor.regionresources;

import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class resourcesOut {
	private static final String scpPath = "/home/jbrazdil/Ultima/andaria/sphere/";
	private static final String regionTypePath = scpPath + "region_types.scp";
	
	public static void main(String[] args) throws IOException {
		RegionResources rr = new RegionResources(new FileReader(regionTypePath));

		for( Resource resource :rr.getResources().values()){
			//System.out.printf("%s\t", resource.getName(), resource.);
		}
	}

	private String getResources(){

		return "";
	}
}
