package br.com.quickdata.panamah.sdk;

public class PanamahException extends Exception {

	private static final long serialVersionUID = 4851561399556567434L;

	public PanamahException(int status, String asString) {
		super(status + ":" + asString);
	}

}
