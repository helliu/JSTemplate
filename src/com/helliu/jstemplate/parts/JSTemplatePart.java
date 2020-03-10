package com.helliu.jstemplate.parts;

public class JSTemplatePart {

	private String originalContent;
	private String content;
	private JSTemplatePartType type;

	public String getOriginalContent() {
		return originalContent;
	}

	public void setOriginalContent(String originalContent) {
		this.originalContent = originalContent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public JSTemplatePartType getType() {
		return type;
	}

	public void setType(JSTemplatePartType type) {
		this.type = type;
	}
	
	public String getContentFormattedToPrintInString() {
		if(this.type == JSTemplatePartType.PRINT_JS_CODE)
			return formatTextToStrRemainLateralQutoes(this.content);
		else
			return formatTextToStr(this.content);
	}
	

	private String formatTextToStrRemainLateralQutoes(String text) {
		boolean quoatedString = false;
		
		if(text.startsWith("\"") && text.endsWith("\""))
			quoatedString = true;
		
		if(quoatedString)
			text = text.substring(1, text.length()-1);//remove lateral strings, "value" will become value
			
		text = formatTextToStr(text);
		
		if(quoatedString)
			text = "\"" + text + "\"";
		
		return text;
	}


	private static String formatTextToStr(String text) {
		//replace \ to \\
		String formattedText = text.replaceAll("\\\\", "\\\\\\\\");
		
		//and brealines to \n
		formattedText = formattedText.replaceAll("\\r\\n|\\r|\\n", "\\\\n");
		
		//replace " to \"
		formattedText = formattedText.replaceAll("\"", "\\\\\"");
		
		return formattedText;
	}
}
