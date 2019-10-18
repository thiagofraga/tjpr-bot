package br.com.lm.model;

public class Processo {

	private String relator;
	
	private String processo;
	
	private String dataPublicacao;
	
	private String orgaoJulgador;

	private String dataJulgamento;
	
	private String fonte;
	
	private String resumo;
	
	private String link;
	
	public String getRelator() {
		return relator;
	}

	public void setRelator(String relator) {
		this.relator = relator;
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String processo) {
		this.processo = processo;
	}

	public String getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(String dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public String getOrgaoJulgador() {
		return orgaoJulgador;
	}

	public void setOrgaoJulgador(String orgaoJulgador) {
		this.orgaoJulgador = orgaoJulgador;
	}

	public String getDataJulgamento() {
		return dataJulgamento;
	}

	public void setDataJulgamento(String dataJulgamento) {
		this.dataJulgamento = dataJulgamento;
	}

	public String getFonte() {
		return fonte;
	}

	public void setFonte(String fonte) {
		this.fonte = fonte;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	
}
