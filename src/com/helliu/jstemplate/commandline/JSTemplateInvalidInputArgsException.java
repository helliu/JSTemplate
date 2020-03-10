package com.helliu.jstemplate.commandline;

public class JSTemplateInvalidInputArgsException extends Exception {

	private static final long serialVersionUID = -3802364441991297410L;

	public enum Type { INVALID_FILE, INVALID_EXTENSION, INVALID_NAME };
	
	private Type type;
	
	public JSTemplateInvalidInputArgsException(Type type) {
		super();
		this.type = type;
	}

	public JSTemplateInvalidInputArgsException() {
		super();
	}

	public JSTemplateInvalidInputArgsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JSTemplateInvalidInputArgsException(String message, Throwable cause) {
		super(message, cause);
	}

	public JSTemplateInvalidInputArgsException(String message) {
		super(message);
	}

	public JSTemplateInvalidInputArgsException(Throwable cause) {
		super(cause);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
