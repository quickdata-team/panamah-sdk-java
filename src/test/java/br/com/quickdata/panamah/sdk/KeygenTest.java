package br.com.quickdata.panamah.sdk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KeygenTest {

	String expected = "{\"assinanteId\": \"1234567890\", \"key\":\"zIT6WjYfhqWJFp/eHk5tYrx4bmw=\", \"ts\":1234567890 }";

	String sample = "{\"assinanteId\": \"00934509022\", \"key\":\"lK3jlGTXY+BwpiNCKZo5VuOR+fo=\", \"ts\":1558445528 }";
	String result = "lK3jlGTXY+BwpiNCKZo5VuOR+fo=";
	String error = "oETCD3pXx0eCoiFMSegvoSwcpCc="; // key nula
	
	@Test
	public void shouldGenerateCorrectKey() {
		PanamahAuth auth = new PanamahAuth();
		auth.setAccessToken("1234567890");
		auth.setKey("1234567890");
		auth.getAssinante().setId("1234567890");
		String b = auth.buildAuth(1234567890);
		// System.out.println(b);
		assertEquals(expected, b);
	}
}
