
package eu.janinko.andaria.editor.regionresources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author janinko
 */
public class ResourceParser {
	private final ParseReader reader;
	private String line;

	public ResourceParser(ParseReader r){
		this.reader = r;
	}

	private final Matcher mResource = Pattern.compile("^\\[regionresource ([a-z_0-9]+)]").matcher("");
	public Resource parseResource() throws IOException{
		line = reader.readLine();
		if(line == null) throw new IllegalStateException("There is nothing to read.");
		mResource.reset(line);
		if(!mResource.matches()) throw new IllegalStateException("This is not regiontype.");

		Resource.Builder builder = new Resource.Builder(mResource.group(1));

		line = reader.readLine();
		while(line != null && !line.startsWith("[")){
			try{
				if(line.contains("skill")){
					parseSkill(builder);
				}else if(line.contains("reapamount")){
					parseReapAmount(builder);
				}else if(line.contains("amount")){
					parseAmount(builder);
				}else if(line.contains("reap")){
					parseReap(builder);
				}else if(line.contains("regen")){
					parseRegen(builder);
				}else if(line.isEmpty()){
					// ok
				}else{
					System.err.println("RP unknown line: " + line );
				}
			}catch(IllegalStateException ex){
				throw new IllegalStateException("Failed parsing line: " + line, ex);
			}

			line = reader.readLine();
		}

		return builder.build();
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

	private final Matcher mSkill = Pattern.compile("^skill *= *([0-9.]+),?([0-9.]+)?").matcher("");
	private void parseSkill(Resource.Builder builder) {
		mSkill.reset(line); mSkill.matches();
		builder.setSkillL(getLow(mSkill));
		builder.setSkillH(getHigh(mSkill));
	}

	private final Matcher mAmount = Pattern.compile("^amount *= *([0-9.]+),?([0-9.]+)?").matcher("");
	private void parseAmount(Resource.Builder builder) {
		mAmount.reset(line); mAmount.matches();
		builder.setAmountL(getLow(mAmount));
		builder.setAmountH(getHigh(mAmount));
	}

	private final Matcher mReap = Pattern.compile("^reap *= *(.*)").matcher("");
	private void parseReap(Resource.Builder builder) {
		mReap.reset(line); mReap.matches();
		String part = mReap.group(1);
		if(part.contains("{")){
			String[] parts = part.replace("{", "").replace("}", "").trim().split(" ");
			for(int i=0; i < parts.length - 1; i+=2){
				Integer num = Integer.parseInt(parts[i+1]);

				builder.addItem(parts[i], num);
			}
		}else{
			builder.addItem(part, 1);
		}
	}

	private final Matcher mReapAmount = Pattern.compile("^reapamount *= *([0-9.]+),?([0-9.]+)?").matcher("");
	private void parseReapAmount(Resource.Builder builder) {
		mReapAmount.reset(line); mReapAmount.matches();
		builder.setReapAmountL(getLow(mReapAmount));
		builder.setReapAmountH(getHigh(mReapAmount));
	}

	private final Matcher mRegen = Pattern.compile("^regen *= *\\{?([0-9*]+) ?([0-9*]+)?\\}?").matcher("");
	private void parseRegen(Resource.Builder builder) {
		mRegen.reset(line); mRegen.matches();
		builder.setRegenL(getLow(mRegen));
		builder.setRegenH(getHigh(mRegen));
	}
}
