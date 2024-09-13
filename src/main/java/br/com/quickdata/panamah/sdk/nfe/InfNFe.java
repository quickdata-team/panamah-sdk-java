package br.com.quickdata.panamah.sdk.nfe;

import java.util.List;

public class InfNFe {

	private String id;
	private String versao;
	private Ide ide;
	private Emit emit;
	private Dest dest;
	private List<Det> det;
	private Total total;
	private Transp transp;
	private Pag pag;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public Ide getIde() {
		return ide;
	}

	public void setIde(Ide ide) {
		this.ide = ide;
	}

	public Emit getEmit() {
		return emit;
	}

	public void setEmit(Emit emit) {
		this.emit = emit;
	}
	
	public Dest getDest() {
		return dest;
	}
	
	public void setDest(Dest dest) {
		this.dest = dest;
	}

	public List<Det> getDet() {
		return det;
	}

	public void setDet(List<Det> det) {
		this.det = det;
	}

	public Total getTotal() {
		return total;
	}

	public void setTotal(Total total) {
		this.total = total;
	}

	public Transp getTransp() {
		return transp;
	}

	public void setTransp(Transp transp) {
		this.transp = transp;
	}

	public Pag getPag() {
		return pag;
	}

	public void setPag(Pag pag) {
		this.pag = pag;
	}

}
