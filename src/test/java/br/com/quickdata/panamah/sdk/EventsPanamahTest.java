package br.com.quickdata.panamah.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import br.com.quickdata.panamah.sdk.model.PanamahCliente;
import br.com.quickdata.panamah.sdk.model.PanamahVenda;

public class EventsPanamahTest {

	private PanamahConfig config = PanamahConfig.fromEnv("staging");

	private String r(String json) throws Exception {
		StringWriter w = new StringWriter();
		try (Reader reader = new InputStreamReader(
				BasePanamahTest.class.getResourceAsStream("resources/" + json + ".json"))) {
			IOUtils.copy(reader, w);
		}
		return w.toString();
	}

	@Test
	public void deveriaEspionarUmSalvamento() throws Exception {

		final PanamahCliente model = PanamahUtil.buildGson().fromJson(r("cliente"), PanamahCliente.class);
		PanamahStream p = PanamahStream.init(config);
		p.setOnSave(new PanamahListener() {

			@Override
			public void notify(PanamahEvent panamahEvent) {
				assertEquals(model, panamahEvent.getOp().getData());
			}
		});
		p.save(model);

		p.flush(true);

	}

	@Test
	public void deveriaCancelarUmSalvamento() throws Exception {

		final PanamahCliente model = PanamahUtil.buildGson().fromJson(r("cliente"), PanamahCliente.class);
		PanamahStream p = PanamahStream.init(config);
		p.setOnSave(new PanamahListener() {

			@Override
			public void notify(PanamahEvent panamahEvent) {
				panamahEvent.setCancelled(true);
			}
		});
		p.getTask().deletaLoteAtual();
		p.save(model);
		assertEquals(0, p.getTask().getLoteAtual().getOperacoes().size());
		p.flush();

	}

	@Test
	public void deveriaEspionarUmaDelecao() throws Exception {

		final PanamahCliente model = PanamahUtil.buildGson().fromJson(r("cliente"), PanamahCliente.class);
		PanamahStream p = PanamahStream.init(config);
		p.setOnDel(new PanamahListener() {

			@Override
			public void notify(PanamahEvent panamahEvent) {
				assertEquals(model, panamahEvent.getOp().getData());
			}
		});
		p.delete(model);

		p.flush(true);

	}

	@Test
	public void deveriaCancelarUmaDelecao() throws Exception {

		final PanamahCliente model = PanamahUtil.buildGson().fromJson(r("cliente"), PanamahCliente.class);
		PanamahStream p = PanamahStream.init(config);
		p.setOnDel(new PanamahListener() {

			@Override
			public void notify(PanamahEvent panamahEvent) {
				panamahEvent.setCancelled(true);
			}
		});
		p.getTask().deletaLoteAtual();
		p.delete(model);
		assertEquals(0, p.getTask().getLoteAtual().getOperacoes().size());
		p.flush(true);

	}

	@Test
	public void deveriaCapturarSucesso() throws Exception {
		final PanamahCliente model = PanamahUtil.buildGson().fromJson(r("cliente"), PanamahCliente.class);
		PanamahStream p = PanamahStream.init(config);
		p.getTask().setOnSendSuccess(new PanamahListener() {

			@Override
			public void notify(PanamahEvent panamahEvent) {
				// System.out.println(panamahEvent);
				assertNotNull(panamahEvent.getRetornoLote());
			}
		});
		p.save(model);

		p.flush(true);
	}

	@Test
	public void deveriaCapturarErro() throws Exception {
		final PanamahVenda model = PanamahUtil.buildGson().fromJson(r("venda"), PanamahVenda.class);
		PanamahStream p = PanamahStream.init(config);
		p.getTask().setOnError(new PanamahListener() {

			@Override
			public void notify(PanamahEvent panamahEvent) {
				// System.out.println(panamahEvent);
				assertNotNull(panamahEvent.getEx());
			}
		});
		p.save(model);

		p.flush(true);
	}
}
