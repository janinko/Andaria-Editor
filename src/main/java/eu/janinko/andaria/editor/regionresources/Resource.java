
package eu.janinko.andaria.editor.regionresources;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Honza Brázdil <jbrazdil@redhat.com>
 */
public class Resource {
	private final String name;
	private int skillL, skillH;
	private int amountL = 1, amountH = 1;
	private int reapAmountL = 1, reapAmountH = 1;
	private int regenL, regenH;
	private final Map<String, Integer> items = new HashMap<String, Integer>();
	private int itemSum;

	private Resource(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getSkillL() {
		return skillL;
	}

	public int getSkillH() {
		return skillH;
	}

	public int getAmountL() {
		return amountL;
	}

	public int getAmountH() {
		return amountH;
	}

	public int getReapAmountL() {
		return reapAmountL;
	}

	public int getReapAmountH() {
		return reapAmountH;
	}

	public int getRegenL() {
		return regenL;
	}

	public int getRegenH() {
		return regenH;
	}

	public int getItemSum() {
		return itemSum;
	}

	public java.util.Map<String, Double> getMinResources(){
		java.util.Map<String, Double> ret = new HashMap<String, Double>();
		for(java.util.Map.Entry<String, Integer> e : items.entrySet()){
			String item = e.getKey();
			double sance = (double) e.getValue() / itemSum;
			ret.put(item, sance * amountL);
		}
		return ret;
	}

	public java.util.Map<String, Double> getMaxResources(){
		java.util.Map<String, Double> ret = new HashMap<String, Double>();
		for(java.util.Map.Entry<String, Integer> e : items.entrySet()){
			String item = e.getKey();
			double sance = (double) e.getValue() / itemSum;
			ret.put(item, sance * amountH);
		}
		return ret;
	}

	public static class Builder {

		private Resource martix;

		public Builder(String name) {
			martix = new Resource(name);
		}

		public void setSkillL(int skillL) {
			martix.skillL = skillL;
		}

		public void setSkillH(int skillH) {
			martix.skillH = skillH;
		}

		public void setAmountL(int amountL) {
			martix.amountL = amountL;
		}

		public void setAmountH(int amountH) {
			martix.amountH = amountH;
		}

		public void setReapAmountL(int reapAmountL) {
			martix.reapAmountL = reapAmountL;
		}

		public void setReapAmountH(int reapAmountH) {
			martix.reapAmountH = reapAmountH;
		}

		public void setRegenL(int regenL) {
			martix.regenL = regenL;
		}

		public void setRegenH(int regenH) {
			martix.regenH = regenH;
		}

		public Resource build(){
			if(martix.regenL == 0 || martix.regenH == 0 || martix.itemSum == 0){
				throw new IllegalStateException("Not initialized all");
			}
			return martix;
		}

		void addItem(String part, int num) {
			martix.itemSum += num;
			if(martix.items.containsKey(part)){
				num += martix.items.get(part);
			}
			martix.items.put(part, num);
		}

	}
}
