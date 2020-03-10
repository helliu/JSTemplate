package com.helliu.jstemplate.commandline;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.helliu.jstemplate.JSTemplate;
import com.helliu.jstemplate.JSTemplateException;
import com.helliu.jstemplate.JSTemplateResult;
import com.helliu.jstemplate.commandline.JSTemplateInvalidInputArgsException.Type;

public class JSTemplateCommandLine {
	
	public static void main(String[] args) {	
		JSTemplateInputArgs templateInputArgs = processInputArguments(args);
		
		try {
			if(templateInputArgs != null && templateInputArgs.getFile() != null) {
			    String templateText = readContentsOfFile(templateInputArgs.getFile());
			    
			    JSTemplateResult result = JSTemplate.execute(templateText, templateInputArgs.getFile());
			    
			    if(templateInputArgs.getOutputFileExtension() != null)
			    	result.setOutputFileExtension(templateInputArgs.getOutputFileExtension());
			    
			    if(templateInputArgs.getOutputFileName() != null)
			    	result.setOutputFileName(templateInputArgs.getOutputFileName());
			    
			    generateOutputResult(templateInputArgs, result);
			}
		} catch (JSTemplateException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void generateOutputResult(JSTemplateInputArgs templateInputArgs, JSTemplateResult result) throws IOException {
		if(templateInputArgs.isPrintJSNashornCodeOnScreen())
			System.out.println(result.getGenratedNashornCode() + "\n\n");
		
		if(templateInputArgs.isPrintResultOnScreen())
			System.out.println(result.getValue());
		else
			createOutputFile(templateInputArgs.getFile(), result);
	}

	private static JSTemplateInputArgs processInputArguments(String[] args) {
		try {
			JSTemplateInputArgs templateInputArgs = new JSTemplateInputArgs(args);
			
			if(templateInputArgs.isHasHelpArg())
				System.out.println(JSTemplateInputArgs.getHelpTextArguments());
			
			return templateInputArgs;
		}catch (JSTemplateInvalidInputArgsException e) {
			if(e.getType() == Type.INVALID_FILE)
				System.out.println("Invalid File.");
			else
			    System.out.println("Invalid Option.");
			
			System.out.println(JSTemplateInputArgs.getHelpTextArguments());
			return null;
		}
	}

	private static String readContentsOfFile(String file) throws IOException {
		Path path = Paths.get(file);
		
		return String.join(System.lineSeparator(), Files.readAllLines(path));
	}

	private static void createOutputFile(String fileTemplate, JSTemplateResult result) throws IOException {
		String path = generatePathFromTemplateResult(fileTemplate, result);
		
		Files.write(Paths.get(path), result.getValue().getBytes());
	}

	private static String generatePathFromTemplateResult(String fileTemplate, JSTemplateResult result) {
		String currentDir = System.getProperty("user.dir");
		String templateFileName = Paths.get(fileTemplate).getFileName().toString();
		String currentDirFilePath = currentDir + "/" + templateFileName; 
		
		if(result.getOutputFileName() != null && result.getOutputFileExtension() == null)
			return result.getOutputFileName();
		
		if(result.getOutputFileName() != null && result.getOutputFileExtension() != null)
			return changeOrAddExtension(result.getOutputFileName(), result.getOutputFileExtension());
		
		if(result.getOutputFileName() == null && result.getOutputFileExtension() == null)
			return currentDirFilePath + ".templateResult";
		
		if(result.getOutputFileName() == null && result.getOutputFileExtension() != null)
			return changeOrAddExtension(currentDirFilePath, result.getOutputFileExtension());
		
		return null;
	}

	private static String changeOrAddExtension(String fileName, String extension) {
		if(fileName.contains("."))
			return fileName.substring(0, fileName.lastIndexOf(".") + 1) + extension;
		else
			return fileName + "." + extension;
	}

	
}
