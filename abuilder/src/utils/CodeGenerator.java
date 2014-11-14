package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import persistency.entities.gamification.PointsCategory;
import persistency.entities.gamification.PointsInstance;

public class CodeGenerator {

	public static List<PointsInstance> generateCodes(int max, List<PointsInstance> allCodes, PointsCategory category) {
		int codeLength = category.getCodeLength();
		List<PointsInstance> newCodes = new ArrayList<PointsInstance>();
		for (int i = 0; i < max; i++) {
			PointsInstance pi = generateCode(codeLength);
			while(allCodes.contains(pi) || newCodes.contains(pi)){
				pi = generateCode(codeLength);
			}
			pi.setCategory(category);
			newCodes.add(pi);
		}
		return newCodes;
	}

	private static String[] letters = {"a","b","c","d","e","f","g","h","i", "j", "k", "l", "m",
										"n", "o","p", "q", "r", "s", "t", "u", "v","w","x", "y", "z"};
	
	private static PointsInstance generateCode(int codeLength) {
		Random r = new Random();
		String code = "";
		for (int i = 0; i < codeLength; i++) {
			code+= letters[r.nextInt(26)];	
		}
		PointsInstance pi = new PointsInstance();
		pi.setCode(code);
		return pi;
	}

	public static List<PointsInstance> generateCompsiteCodes(int max, List<String> codePossitions,
			List<PointsInstance> allCodes, PointsCategory category) {
		List<PointsInstance> generatedCodes = generateCodes(max, allCodes, category);
		int index = 0;
		for (PointsInstance pointsInstance : generatedCodes) {
			pointsInstance.setCompositeCodeId(codePossitions.get(index++));
			if(index == codePossitions.size()){
				index = 0;
			}
		}
		return generatedCodes;
	}

}
