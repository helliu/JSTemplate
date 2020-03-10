package com.helliu.jstemplate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.helliu.jstemplate.commandline.JSTemplateCommandLine;
import com.helliu.jstemplate.parts.JSTemplatePart;
import com.helliu.jstemplate.parts.JSTemplatePartConverter;
import com.helliu.jstemplate.parts.JSTemplatePartType;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

@SuppressWarnings("restriction")
public class JSTemplate {

    private enum JSCodeActionStep { 
    	START_PRINTTEXT_VAR,
    	START_PRINTTEXT_QUOTTED_STR,
    	APPEND_PRINTTEXT_VAR,
    	APPEND_PRINTTEXT_QUOTTED_STR,
    	EXECUTE_JS_CODE
    };

    public static JSTemplateResult execute(String template) throws JSTemplateException {
    	return execute(template, null);
    }
    
	public static JSTemplateResult execute(String template, String templateFilePath) throws JSTemplateException {
		String jsCode = templateToJSCode(template);
		
		JSTemplateResult templateResut = executeJSCodeTemplate(jsCode, templateFilePath);
		templateResut.setOriginalTemplate(template);
		
		return templateResut;
	}

	private static String templateToJSCode(String template) {
		List<JSTemplatePart> parts = JSTemplatePartConverter.convertToTemplateParts(template);
		StringBuilder jsCode = new StringBuilder();
		boolean isDoingPrintFunction = false;
		boolean previousIgnoreBreakLine = false; //if, for, while, { and } sentences, ignores breaklines on the next printing
		
		for(JSTemplatePart part : parts) {
			
			JSCodeActionStep actionStep = determineActionStep(isDoingPrintFunction, part);
			
			if(previousIgnoreBreakLine && part.getType() ==  JSTemplatePartType.TEXT)
				part.setContent(part.getContent().replaceFirst("^\n|^\r\n", ""));
			
			previousIgnoreBreakLine = false;
			
			switch(actionStep) {
				    
				case START_PRINTTEXT_QUOTTED_STR :
				    jsCode.append("printText(\"");
				    jsCode.append(part.getContentFormattedToPrintInString());
				    jsCode.append("\"");
				    isDoingPrintFunction = true;
				    break;
				    
			    case START_PRINTTEXT_VAR : 
				    jsCode.append("printText(");
				    jsCode.append(part.getContentFormattedToPrintInString());
				    isDoingPrintFunction = true;
				    break;
				    
				case APPEND_PRINTTEXT_QUOTTED_STR:
					jsCode.append(" + ");
			    	jsCode.append("\"");
			    	jsCode.append(part.getContentFormattedToPrintInString());
			    	jsCode.append("\"");
					break;
					
				case APPEND_PRINTTEXT_VAR:
					jsCode.append(" + ");
				    jsCode.append(part.getContentFormattedToPrintInString());
					break;
					
				case EXECUTE_JS_CODE:
					if(isDoingPrintFunction) {
						jsCode.append(");\n");
						isDoingPrintFunction = false;
					}
					
					jsCode.append(part.getContent());
					jsCode.append("\n");
					
					if(contentIsIfSetence(part.getContent()) ||
					   contentIsElseSetence(part.getContent()) ||
					   contentIsForSetence(part.getContent()) ||
					   contentIsWhileSetence(part.getContent()) ||
					   contentIsDoSetence(part.getContent()) ||
					   contentIsEndWhileSetence(part.getContent()) ||
					   contentIsSingleCurlyBrackets(part.getContent()))
						previousIgnoreBreakLine = true;
					
					break;
			
			}
		}
		
		if(isDoingPrintFunction)
			jsCode.append(");");
		
		return jsCode.toString();
	}

	private static JSCodeActionStep determineActionStep(boolean isDoingPrintFunction, JSTemplatePart part) {
		if(!isDoingPrintFunction && part.getType() == JSTemplatePartType.TEXT)
			return JSCodeActionStep.START_PRINTTEXT_QUOTTED_STR;
		
		if(!isDoingPrintFunction && part.getType() == JSTemplatePartType.PRINT_JS_CODE)
			return JSCodeActionStep.START_PRINTTEXT_VAR;
		
		if(isDoingPrintFunction && part.getType() == JSTemplatePartType.TEXT)
			return JSCodeActionStep.APPEND_PRINTTEXT_QUOTTED_STR;
		
		if(isDoingPrintFunction && part.getType() == JSTemplatePartType.PRINT_JS_CODE)
			return JSCodeActionStep.APPEND_PRINTTEXT_VAR;
		
		if(part.getType() == JSTemplatePartType.EXECUTE_JS_CODE)
			return JSCodeActionStep.EXECUTE_JS_CODE;
			
		return null;
	}
	
	private static JSTemplateResult executeJSCodeTemplate(String jsCode, String templateFilePath) throws JSTemplateException {
		final String currentApplicationPath = getCurrentApplicationPath();
		templateFilePath = templateFilePath == null ? currentApplicationPath : templateFilePath.replaceAll("\\\\", "\\\\\\\\"); 
		String templateFileDir = Files.isDirectory(Paths.get(templateFilePath)) ? templateFilePath : Paths.get(templateFilePath).getParent().toAbsolutePath().toString().replaceAll("\\\\", "\\\\\\\\");
        
		final String jsCodeHeaderCode = "var fileName;\r\n" +
        		                        "var fileExtension;\r\n" +
        		                        "var currentApplicationPath = \"" + currentApplicationPath + "\";\r\n" +
        		                        "var templateFilePath = \"" + templateFilePath + "\";\r\n" +
        		                        "var templateFileDir = \"" + templateFileDir + "\";\r\n" +
        		                        "var ____jsTemplateResu = {};\r\n" + 
        		                        "____jsTemplateResu.value = \"\";\r\n" + 
							        	"function printText(text){\r\n" + 
							        	"    ____jsTemplateResu.value += text;\r\n" + 
							        	"}";
        
        final String jsEndCode = "\r\n\r\n____jsTemplateResu.fileName = fileName;\r\n" + 
        		                 "____jsTemplateResu.fileExtension = fileExtension;";
		
        final String fullCode = jsCodeHeaderCode + 
        		          jsCode + 
        		          jsEndCode;
        
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			engine.put("JSTemplateCommandLine", JSTemplateCommandLine.class);
			engine.eval(fullCode);
			
			ScriptObjectMirror templateResutJSObject = (ScriptObjectMirror)engine.get("____jsTemplateResu");
			
			JSTemplateResult templateResut = new JSTemplateResult(templateResutJSObject);
			templateResut.setGenratedNashornCode(fullCode);
			
			return templateResut;
		} catch (ScriptException e) {
			throw new JSTemplateException(e);
		}
	}

	private static String getCurrentApplicationPath() {
		String path = System.getProperty("user.dir");
		
		if(path == null)
			return "";
		
		return path.replaceAll("\\\\", "\\\\\\\\");
	}

	private static boolean contentIsIfSetence(String content) {
		content = content.trim();
		
		if(content.startsWith("if") && content.endsWith("{"))
			return true;
		else
		    return false;
	}

	private static boolean contentIsElseSetence(String content) {
		content = content.trim();
		
		if(content.equals("}else{"))
			return true;
		else
		    return false;
	}

	private static boolean contentIsForSetence(String content) {
		content = content.trim();
		
		if(content.startsWith("for") && content.endsWith("{"))
			return true;
		else
		    return false;
	}

	private static boolean contentIsSingleCurlyBrackets(String content) {
		content = content.trim();
		
		if(content.equals("{") || content.endsWith("}"))
			return true;
		else
		    return false;
	}

	private static boolean contentIsWhileSetence(String content) {
		content = content.trim();
		
		if(content.startsWith("while") && content.endsWith("{"))
			return true;
		else
		    return false;
	}

	private static boolean contentIsDoSetence(String content) {
		content = content.trim();
		
		if(content.startsWith("do") && content.endsWith("{"))
			return true;
		else
		    return false;
	}

	private static boolean contentIsEndWhileSetence(String content) {
		content = content.trim();
		
		if(content.startsWith("}while") && content.endsWith(");"))
			return true;
		else
		    return false;
	}
}
