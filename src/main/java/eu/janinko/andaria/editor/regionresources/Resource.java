
package eu.janinko.andaria.editor.regionresources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author janinko
 */
public class Resource {
	private String name;
	private int skillL, skillH;
	private int amountL = 1, amountH = 1;
	private int reapAmountL = 1, reapAmountH = 1;
	private int regenL, regenH;
	private Map<String, Integer> items = new HashMap<String, Integer>();
	private int itemSum;

	public Resource(BufferedReader r) throws IOException, IllegalStateException{
		String line = r.readLine();
		if(!line.toLowerCase().startsWith("[regionresource ")){
			throw new IllegalStateException();
		}
		name = line.substring(16, line.length() -2);
		r.mark(5000);
		line = r.readLine();
		while(line != null && !line.startsWith("[")){
			String lline = line.toLowerCase();
			if(lline.contains("skill")){
				parseSkill(lline);
			}else if(lline.contains("amount")){
				parseAmount(lline);
			}else if(lline.contains("reap")){
				parseReap(lline);
			}else if(lline.contains("reapamount")){
				parseReapAmount(lline);
			}else if(lline.contains("regen")){
				parseRegen(lline);
			}else{
				throw new IllegalStateException();
			}

			r.mark(5000);
			line = r.readLine();
		}
		if(line != null){
			r.reset();
		}
		if(regenL == 0 || regenH == 0 || itemSum == 0){
			throw new IllegalStateException();
		}
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

	public Map<String, Integer> getItems() {
		return items;
	}

	public int getItemSum() {
		return itemSum;
	}

	public Matcher getmSkill() {
		return mSkill;
	}

	public Matcher getmAmount() {
		return mAmount;
	}

	public Matcher getmReap() {
		return mReap;
	}

	public Matcher getmReapAmount() {
		return mReapAmount;
	}

	public Matcher getmRegen() {
		return mRegen;
	}

	private int getLow(Matcher m){
		return Integer.parseInt(m.group(0));
	}

	private int getHigh(Matcher m){
		if(mSkill.group(1) != null){
			return Integer.parseInt(mSkill.group(1));
		}else{
			return Integer.parseInt(m.group(0));
		}
	}

	private Matcher mSkill = Pattern.compile("^skill *= *([0-9.]+),?([0-9.]+)?").matcher("");
	private void parseSkill(String line) {
		mSkill.reset(line);
		skillL = getLow(mSkill);
		skillH = getHigh(mSkill);
	}

	private Matcher mAmount = Pattern.compile("^amount *= *([0-9.]+),?([0-9.]+)?").matcher("");
	private void parseAmount(String line) {
		mAmount.reset(line);
		amountL = getLow(mAmount);
		amountH = getHigh(mAmount);
	}

	private Matcher mReap = Pattern.compile("^reap *= *(.*)").matcher("");
	private void parseReap(String line) {
		mReap.reset(line);
		String part = mReap.group(0);
		if(part.contains("{")){
			String[] parts = line.replace("{", "").replace("}", "").split(" ");
			for(int i=0; i < parts.length - 1; i+=2){
				Integer num = Integer.parseInt(parts[i+1]);
				if(items.containsKey(parts[i])){
					num += items.get(parts[i]);
				}
				items.put(parts[i], num);
			}
		}else{
			items.put(part, 1);
			itemSum = 1;
		}
	}

	private Matcher mReapAmount = Pattern.compile("^reapamount *= *([0-9.]+),?([0-9.]+)?").matcher("");
	private void parseReapAmount(String line) {
		mReapAmount.reset(line);
		reapAmountL = getLow(mReapAmount);
		reapAmountH = getHigh(mReapAmount);
	}

	private Matcher mRegen = Pattern.compile("^regen *= \\{?([0-9*]+) ?([0-9*]+)?\\}?").matcher("");
	private void parseRegen(String line) {
		mRegen.reset(line);
		regenL = getLow(mRegen);
		regenH = getHigh(mRegen);
	}
}
