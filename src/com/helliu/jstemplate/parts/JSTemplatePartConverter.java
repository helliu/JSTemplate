package com.helliu.jstemplate.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSTemplatePartConverter {

	public static List<JSTemplatePart> convertToTemplateParts(String template) {
		List<JSTemplatePart> parts = new ArrayList<>();
		
	    Pattern pattern = Pattern.compile("<#.*?#>", Pattern.DOTALL);
	    Matcher matcher = pattern.matcher(template);
	    int textIndex = 0;
	    
	    while (matcher.find()) {
	    	int foundTextStartIndex = matcher.start();
	    	int foundTextEndIndex = matcher.end();
	    	String foundText = matcher.group();
	    	
	    	//plain text before the match
	    	//text between current index and match index
	    	if(textIndex < foundTextStartIndex) {
	    		JSTemplatePart jsTemplateTextPart = createJSTemplateTextPart(template, textIndex, foundTextStartIndex);
	    		parts.add(jsTemplateTextPart);
	    	}
	    	
	    	JSTemplatePart part;
	    	
	    	if(foundText.startsWith("<#="))
	    		part = createJSTemplateJSPart(foundText, JSTemplatePartType.PRINT_JS_CODE);
	    	else
	    		part = createJSTemplateJSPart(foundText, JSTemplatePartType.EXECUTE_JS_CODE);
	    	
	    	parts.add(part);
	    	
	    	textIndex = foundTextEndIndex;
	    }
	    
	    //text ending, if the ending is not js code but regular text
	    if(textIndex < template.length()) {
    		JSTemplatePart textPart = createJSTemplateTextPart(template, textIndex, template.length());
    		parts.add(textPart);
	    }
		
		return parts;
	}

	private static JSTemplatePart createJSTemplateJSPart(String text, JSTemplatePartType jsTemplateType) {
		JSTemplatePart part = new JSTemplatePart();
    	String contentWithoutCodeTemplateSign = removeCodeTemplateSigns(text);
    	part.setOriginalContent(text);
    	part.setContent(contentWithoutCodeTemplateSign);
    	part.setType(jsTemplateType);
    	
		return part;
	}

	private static String removeCodeTemplateSigns(String text) {
		text = text.replaceAll("<#=|<#", "");
		text = text.replaceAll("#>", "");
		
		return text;
	}

	private static JSTemplatePart createJSTemplateTextPart(String text, int startIndex, int endIndex) {
		String selectedText = text.substring(startIndex, endIndex);
		
		JSTemplatePart textPart = new JSTemplatePart();
		textPart.setOriginalContent(selectedText);
		textPart.setContent(selectedText);
		textPart.setType(JSTemplatePartType.TEXT);
		
		return textPart;
	}
}
