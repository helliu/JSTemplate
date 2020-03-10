package com.helliu.jstemplate;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

@SuppressWarnings("restriction")
public class JSTemplateResult {

	private String outputFileName;
	private String outputFileExtension;
	private String originalTemplate;
	private String genratedNashornCode;
	private String value;
	
	public JSTemplateResult() {
		super();
	}
	
	public JSTemplateResult(ScriptObjectMirror jsTemplate) {
		super();
		
		this.outputFileName = (String) jsTemplate.get("fileName");
		this.outputFileExtension = (String) jsTemplate.get("fileExtension");
		this.originalTemplate = (String) jsTemplate.get("originalTemplate");
		this.genratedNashornCode = (String) jsTemplate.get("genratedNashornCode");
		this.value = (String) jsTemplate.get("value");
		
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String fileName) {
		this.outputFileName = fileName;
	}

	public String getOutputFileExtension() {
		return outputFileExtension;
	}

	public void setOutputFileExtension(String fileExtension) {
		this.outputFileExtension = fileExtension;
	}

	public String getOriginalTemplate() {
		return originalTemplate;
	}

	public void setOriginalTemplate(String originalTemplate) {
		this.originalTemplate = originalTemplate;
	}

	public String getGenratedNashornCode() {
		return genratedNashornCode;
	}

	public void setGenratedNashornCode(String genratedNashornCode) {
		this.genratedNashornCode = genratedNashornCode;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
