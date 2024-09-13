package br.com.quickdata.panamah.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.quickdata.panamah.sdk.nfe.CanonicalizationMethod;
import br.com.quickdata.panamah.sdk.nfe.Det;
import br.com.quickdata.panamah.sdk.nfe.DetPag;
import br.com.quickdata.panamah.sdk.nfe.DigestMethod;
import br.com.quickdata.panamah.sdk.nfe.Emit;
import br.com.quickdata.panamah.sdk.nfe.EnderEmit;
import br.com.quickdata.panamah.sdk.nfe.Evento;
import br.com.quickdata.panamah.sdk.nfe.Icms;
import br.com.quickdata.panamah.sdk.nfe.IcmsSn102;
import br.com.quickdata.panamah.sdk.nfe.IcmsTot;
import br.com.quickdata.panamah.sdk.nfe.Ide;
import br.com.quickdata.panamah.sdk.nfe.Imposto;
import br.com.quickdata.panamah.sdk.nfe.InfEvento;
import br.com.quickdata.panamah.sdk.nfe.InfNFe;
import br.com.quickdata.panamah.sdk.nfe.InfNFeSupl;
import br.com.quickdata.panamah.sdk.nfe.InfProt;
import br.com.quickdata.panamah.sdk.nfe.KeyInfo;
import br.com.quickdata.panamah.sdk.nfe.NFe;
import br.com.quickdata.panamah.sdk.nfe.NFeProc;
import br.com.quickdata.panamah.sdk.nfe.Pag;
import br.com.quickdata.panamah.sdk.nfe.Prod;
import br.com.quickdata.panamah.sdk.nfe.ProtNFe;
import br.com.quickdata.panamah.sdk.nfe.Reference;
import br.com.quickdata.panamah.sdk.nfe.Signature;
import br.com.quickdata.panamah.sdk.nfe.SignatureMethod;
import br.com.quickdata.panamah.sdk.nfe.SignedInfo;
import br.com.quickdata.panamah.sdk.nfe.Total;
import br.com.quickdata.panamah.sdk.nfe.Transform;
import br.com.quickdata.panamah.sdk.nfe.Transforms;
import br.com.quickdata.panamah.sdk.nfe.Transp;
import br.com.quickdata.panamah.sdk.nfe.X509Data;

public class XmlConversionTest {

	private InputStream x(String res) throws Exception {
		return XmlConversionTest.class.getResourceAsStream("resources/xml/" + res + ".xml");
	}

	@Test
	public void shouldReadEvento() throws Exception {
		String id = "ID1101111319050712894500013265508100000090100000004001";
		try (InputStream in = x(id)) {
			XStream x = PanamahUtil.buildXStream();
			Evento e = (Evento) x.fromXML(in);
			assertEquals(id, e.getInfEvento().getId());
		}
	}

	@Test
	public void shouldWriteEvento() throws Exception {
		Evento e = new Evento();
		InfEvento infEvento = new InfEvento();
		infEvento.setDhEvento(new Date());
		infEvento.setId("ID1101111319050712894500013265508100000090100000004001");
		e.setInfEvento(infEvento);
		XStream x = PanamahUtil.buildXStream();
		String xml = x.toXML(e);
		assertNotNull(xml);
	}

	@Test
	public void shouldReadNfeProc() throws Exception {
		String proc = "NFe13190507128945000132652340000000099000000079";
		try (InputStream in = x(proc)) {
			XStream x = PanamahUtil.buildXStream();
			NFeProc procNfe = (NFeProc) x.fromXML(in);
			assertEquals("07128945000132", procNfe.getNfe().getInfNFe().getEmit().getCnpj());
		}
	}

	@Test
	public void shouldWriteNfeProc() throws Exception {

		NFeProc p = new NFeProc();
		p.setVersao("4.00");
		p.setNfe(new NFe());
		p.getNfe().setInfNFe(new InfNFe());
		p.getNfe().getInfNFe().setId("NFe13190507128945000132652340000000099000000079");
		p.getNfe().getInfNFe().setVersao("4.00");
		p.getNfe().getInfNFe().setIde(new Ide());
		p.getNfe().getInfNFe().getIde().setcDV(9);
		p.getNfe().getInfNFe().getIde().setcUF(13);
		p.getNfe().getInfNFe().setEmit(new Emit());
		p.getNfe().getInfNFe().getEmit().setCnpj("07128945000132");
		p.getNfe().getInfNFe().getEmit().setEnderEmit(new EnderEmit());
		p.getNfe().getInfNFe().getEmit().getEnderEmit().setUF("AM");
		p.getNfe().getInfNFe().getEmit().getEnderEmit().setCEP("69028302");
		p.getNfe().getInfNFe().getEmit().setCrt("1");
		p.getNfe().getInfNFe().setDet(new ArrayList<Det>());
		p.getNfe().getInfNFe().getDet().add(new Det());
		p.getNfe().getInfNFe().getDet().get(0).setnItem(1);
		p.getNfe().getInfNFe().getDet().add(new Det());
		p.getNfe().getInfNFe().getDet().get(1).setnItem(2);
		p.getNfe().getInfNFe().getDet().get(1).setProd(new Prod());
		p.getNfe().getInfNFe().getDet().get(1).getProd().setcEAN("SEM GTIN");
		p.getNfe().getInfNFe().getDet().get(1).setImposto(new Imposto());
		p.getNfe().getInfNFe().getDet().get(1).getImposto().setvTotTrib(new BigDecimal(1.2));
		p.getNfe().getInfNFe().getDet().get(1).getImposto().setIcms(new Icms());
		p.getNfe().getInfNFe().getDet().get(1).getImposto().getIcms().setIcmsSn102(new IcmsSn102());
		p.getNfe().getInfNFe().getDet().get(1).getImposto().getIcms().getIcmsSn102().setCsosn(1);
		p.getNfe().getInfNFe().getDet().get(1).getImposto().getIcms().getIcmsSn102().setOrig(1);
		p.getNfe().getInfNFe().setTotal(new Total());
		p.getNfe().getInfNFe().getTotal().setIcmsTot(new IcmsTot());
		p.getNfe().getInfNFe().getTotal().getIcmsTot().setvBCST(new BigDecimal(1.1));
		p.getNfe().getInfNFe().setTransp(new Transp());
		p.getNfe().getInfNFe().getTransp().setModFrete(1);
		p.getNfe().getInfNFe().setPag(new Pag());
		p.getNfe().getInfNFe().getPag().setvTroco(new BigDecimal(1.1));
		p.getNfe().getInfNFe().getPag().setDetPag(new DetPag());
		p.getNfe().getInfNFe().getPag().getDetPag().settPag("aaa");
		p.getNfe().getInfNFe().getPag().getDetPag().setvPag(new BigDecimal(1.1));
		p.getNfe().setInfNFeSupl(new InfNFeSupl());
		p.getNfe().getInfNFeSupl().setQrCode("");
		p.getNfe().setSignature(new Signature());
		p.getNfe().getSignature().setSignedInfo(new SignedInfo());
		p.getNfe().getSignature().getSignedInfo().setCanonicalizationMethod(new CanonicalizationMethod());
		p.getNfe().getSignature().getSignedInfo().setSignatureMethod(new SignatureMethod());
		p.getNfe().getSignature().getSignedInfo().setReference(new Reference());
		p.getNfe().getSignature().getSignedInfo().getReference()
				.setUri("#NFe13190507128945000132652340000000099000000079");
		p.getNfe().getSignature().getSignedInfo().getReference().setTransforms(new Transforms());
		p.getNfe().getSignature().getSignedInfo().getReference().getTransforms()
				.setTransform(new ArrayList<Transform>());
		p.getNfe().getSignature().getSignedInfo().getReference().getTransforms().getTransform().add(new Transform());
		p.getNfe().getSignature().getSignedInfo().getReference().getTransforms().getTransform().get(0)
				.setAlgorithm("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
		p.getNfe().getSignature().getSignedInfo().getReference().setDigestMethod(new DigestMethod());
		p.getNfe().getSignature().getSignedInfo().getReference().getDigestMethod()
				.setAlgorithm("http://www.w3.org/2000/09/xmldsig#sha1");
		p.getNfe().getSignature().getSignedInfo().getReference().setDigestValue("asdfsgsdgds");
		p.getNfe().getSignature().setSignatureValue("dsafsdsdfgadsfsadf");
		p.getNfe().getSignature().setKeyInfo(new KeyInfo());
		p.getNfe().getSignature().getKeyInfo().setX509Data(new X509Data());
		p.getNfe().getSignature().getKeyInfo().getX509Data().setX509Certificate("ssdfgsgsdgsdg");
		p.setProtNFe(new ProtNFe());
		p.getProtNFe().setInfProt(new InfProt());
		p.getProtNFe().getInfProt().setcStat(3);

		XStream x = PanamahUtil.buildXStream();
		String xml = x.toXML(p);
		assertNotNull(xml);
		// System.out.println(xml);
	}

	@Test
	public void shouldReadNfe() throws Exception {
		try (InputStream in = x("NFe13190507128945000132652340000000099000000079")) {
			NFeProc proc = (NFeProc) PanamahUtil.buildXStream().fromXML(in);
			NFe nfe = (NFe) proc.getNfe();
			
			assertNotNull(nfe);
			assertEquals("NFe13190507128945000132652340000000099000000079", nfe.getInfNFe().getId());

			List<Det> dets = nfe.getInfNFe().getDet();
			
			for (int i = 0; i < dets.size(); i++) {
				if(i == 0) {
					Det det = dets.get(i);
					Prod prod = det.getProd();
					assertEquals("00000075", prod.getcProd());
					assertEquals("00854011370054", prod.getcEAN());
					assertEquals(new BigDecimal("1.0000") , prod.getqCom());
					assertEquals(new BigDecimal("12.00") , prod.getvUnCom());
					assertEquals(new BigDecimal("12.00") , prod.getvProd());
					assertEquals(new BigDecimal("0.20") , prod.getvDesc());
				}
			}
		}
	}
}
