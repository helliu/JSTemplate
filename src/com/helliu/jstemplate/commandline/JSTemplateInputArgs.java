package com.helliu.jstemplate.commandline;

import java.nio.file.Files;
import java.nio.file.Paths;

public class JSTemplateInputArgs {

	private String file;
	private String outputFileExtension;
	private String outputFileName;
	private boolean printResultOnScreen;
	private boolean printJSNashornCodeOnScreen;
	private boolean hasHelpArg;
	
	public JSTemplateInputArgs(String[] args) throws JSTemplateInvalidInputArgsException {
		if(args.length <= 0)
			throw new JSTemplateInvalidInputArgsException(JSTemplateInvalidInputArgsException.Type.INVALID_FILE);
		
		for(int i = 0; i < args.length; i++) {
			String arg = args[i];
			
			if(isHelpArgs(arg)) {
				this.hasHelpArg = true;
			}else if(i == 0) {
				if(!Files.exists(Paths.get(arg)))
					throw new JSTemplateInvalidInputArgsException(JSTemplateInvalidInputArgsException.Type.INVALID_FILE);
				
				this.file = arg;
			}
			
			if(arg.trim().equals("-ext")) {
				if((i + 1) == args.length)
					throw new JSTemplateInvalidInputArgsException(JSTemplateInvalidInputArgsException.Type.INVALID_EXTENSION);
				
				String nextArg = args[++i];
				outputFileExtension = nextArg;
			}
			
			if(arg.trim().equals("-name")) {
				if((i + 1) == args.length)
					throw new JSTemplateInvalidInputArgsException(JSTemplateInvalidInputArgsException.Type.INVALID_NAME);
				
				String nextArg = args[++i];
				outputFileName = nextArg;
			}
			
			if(arg.trim().equals("-p")) {
				printResultOnScreen = true;
			}
			
			else if(arg.trim().equals("-c")) {
				printJSNashornCodeOnScreen = true;
			}
		}
		
	}

	public static String getHelpTextArguments() {
		return "JSTemplate {FILENAME OR FULL PATH} [-ext] [-name]\r\n" + 
				"\r\n" + 
				"FILENAME OR FULL PATH - File name of template in the current directory. Or fullpath of the template file.\r\n" + 
				"\r\n" + 
				"-ext - Specify extension for the output file.\r\n" + 
				"\r\n" + 
				"-name - Specify name for the output file.\r\n" + 
				"\r\n" + 
				"-p - Print output in screen and generate no output file. \r\n" + 
				"\r\n" + 
				"-c - Print generated JS Nashorn on the screen.";
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getOutputFileExtension() {
		return outputFileExtension;
	}

	public void setOutputFileExtension(String fileExtension) {
		this.outputFileExtension = fileExtension;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String fileName) {
		this.outputFileName = fileName;
	}

	public boolean isPrintResultOnScreen() {
		return printResultOnScreen;
	}

	public void setPrintResultOnScreen(boolean printResultOnScreen) {
		this.printResultOnScreen = printResultOnScreen;
	}

	public boolean isPrintJSNashornCodeOnScreen() {
		return printJSNashornCodeOnScreen;
	}

	public void setPrintJSNashornCodeOnScreen(boolean printJSNashornCodeOnScreen) {
		this.printJSNashornCodeOnScreen = printJSNashornCodeOnScreen;
	}

	public boolean isHasHelpArg() {
		return hasHelpArg;
	}

	public void setHasHelpArg(boolean hasHelpArg) {
		this.hasHelpArg = hasHelpArg;
	}

	private boolean isHelpArgs(String arg) {
		return arg.trim().equals("?") || 
			   arg.trim().equals("-?") || 
			   arg.trim().equals("/?") || 
			   arg.trim().equals("\\?");
	}
}
