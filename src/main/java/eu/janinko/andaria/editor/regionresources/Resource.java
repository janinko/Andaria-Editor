
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

	private Matcher mResource = Pattern.compile("^\\[regionresource ([a-z_0-9]+)].*").matcher("");
	public Resource(BufferedReader r) throws IOException, IllegalStateException{
		String line = r.readLine();
		mResource.reset(line.toLowerCase());
		if(!mResource.matches()){
			throw new IllegalStateException("this is not regiontype");
		}
		name = mResource.group(1);

		line = r.readLine();
		while(line != null && !line.startsWith("[")){
			try{
				String lline = line.toLowerCase();
				if(lline.contains("skill")){
					parseSkill(lline);
				}else if(lline.contains("reapamount")){
					parseReapAmount(lline);
				}else if(lline.contains("amount")){
					parseAmount(lline);
				}else if(lline.contains("reap")){
					parseReap(lline);
				}else if(lline.contains("regen")){
					parseRegen(lline);
				}else if(lline.trim().isEmpty()){
					// ok
				}else{
					System.err.println("unknown line: " + line );
				}
			}catch(IllegalStateException ex){
				throw new IllegalStateException("Failed parsing line: " + line, ex);
			}

			line = r.readLine();
		}
		if(regenL == 0 || regenH == 0 || itemSum == 0){
			throw new IllegalStateException("Not initialized all");
		}
	}

	public Map<String, Double> getMinResources(){
		Map<String, Double> ret = new HashMap<String, Double>();
		for(Map.Entry<String, Integer> e : items.entrySet()){
			String item = e.getKey();
			double sance = (double) e.getValue() / itemSum;
			ret.put(item, sance * amountL);
		}
		return ret;
	}

	public Map<String, Double> getMaxResources(){
		Map<String, Double> ret = new HashMap<String, Double>();
		for(Map.Entry<String, Integer> e : items.entrySet()){
			String item = e.getKey();
			double sance = (double) e.getValue() / itemSum;
			ret.put(item, sance * amountH);
		}
		return ret;
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

	private int toInt(String s){
		if(s.contains("*")){
			int r = 1;
			for(String p : s.split("\\*")){
				r *= Integer.parseInt(p);
			}
			return r;
		}else if (s.contains(".")){
			return Integer.parseInt(s.replaceFirst("\\.", ""));
		}else{
			return Integer.parseInt(s);
		}
	}

	private int getLow(Matcher m){
		return toInt(m.group(1));
	}

	private int getHigh(Matcher m){
		if(m.group(2) != null){
			return toInt(m.group(2));
		}else{
			return toInt(m.group(1));
		}
	}

	private Matcher mSkill = Pattern.compile("^skill *= *([0-9.]+),?([0-9.]+)?").matcher("");
	private void parseSkill(String line) {
		mSkill.reset(line); mSkill.matches();
		skillL = getLow(mSkill);
		skillH = getHigh(mSkill);
	}

	private Matcher mAmount = Pattern.compile("^amount *= *([0-9.]+),?([0-9.]+)?").matcher("");
	private void parseAmount(String line) {
		mAmount.reset(line); mAmount.matches();
		amountL = getLow(mAmount);
		amountH = getHigh(mAmount);
	}

	private Matcher mReap = Pattern.compile("^reap *= *(.*)").matcher("");
	private void parseReap(String line) {
		mReap.reset(line); mReap.matches();
		String part = mReap.group(1);
		if(part.contains("{")){
			String[] parts = part.replace("{", "").replace("}", "").trim().split(" ");
			for(int i=0; i < parts.length - 1; i+=2){
				Integer num = Integer.parseInt(parts[i+1]);
				itemSum += num;
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
		mReapAmount.reset(line); mReapAmount.matches();
		reapAmountL = getLow(mReapAmount);
		reapAmountH = getHigh(mReapAmount);
	}

	private Matcher mRegen = Pattern.compile("^regen *= *\\{?([0-9*]+) ?([0-9*]+)?\\}?").matcher("");
	private void parseRegen(String line) {
		mRegen.reset(line); mRegen.matches();
		regenL = getLow(mRegen);
		regenH = getHigh(mRegen);
	}
}
