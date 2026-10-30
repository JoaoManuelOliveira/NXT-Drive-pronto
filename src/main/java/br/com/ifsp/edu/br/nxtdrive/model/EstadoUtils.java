package br.com.ifsp.edu.br.nxtdrive.model;

import java.util.Map;

public class EstadoUtils {

	private static final Map<String, String> NOME_PARA_SIGLA = Map.ofEntries(Map.entry("ACRE", "AC"),
			Map.entry("ALAGOAS", "AL"), Map.entry("AMAPÁ", "AP"), Map.entry("AMAPA", "AP"), Map.entry("AMAZONAS", "AM"),
			Map.entry("BAHIA", "BA"), Map.entry("CEARÁ", "CE"), Map.entry("CEARA", "CE"),
			Map.entry("DISTRITO FEDERAL", "DF"), Map.entry("ESPÍRITO SANTO", "ES"), Map.entry("ESPIRITO SANTO", "ES"),
			Map.entry("GOIÁS", "GO"), Map.entry("GOIAS", "GO"), Map.entry("MARANHÃO", "MA"),
			Map.entry("MARANHAO", "MA"), Map.entry("MATO GROSSO", "MT"), Map.entry("MATO GROSSO DO SUL", "MS"),
			Map.entry("MINAS GERAIS", "MG"), Map.entry("PARÁ", "PA"), Map.entry("PARA", "PA"),
			Map.entry("PARAÍBA", "PB"), Map.entry("PARAIBA", "PB"), Map.entry("PARANÁ", "PR"),
			Map.entry("PARANA", "PR"), Map.entry("PERNAMBUCO", "PE"), Map.entry("PIAUÍ", "PI"),
			Map.entry("PIAUI", "PI"), Map.entry("RIO DE JANEIRO", "RJ"), Map.entry("RIO GRANDE DO NORTE", "RN"),
			Map.entry("RIO GRANDE DO SUL", "RS"), Map.entry("RONDÔNIA", "RO"), Map.entry("RONDONIA", "RO"),
			Map.entry("RORAIMA", "RR"), Map.entry("SANTA CATARINA", "SC"), Map.entry("SÃO PAULO", "SP"),
			Map.entry("SAO PAULO", "SP"), Map.entry("SERGIPE", "SE"), Map.entry("TOCANTINS", "TO"));

	public static String normalizarUf(String entrada) {
		if (entrada == null || entrada.isBlank())
			return null;
		String limpo = entrada.trim().toUpperCase();
		if (limpo.length() == 2)
			return limpo; // Já é a sigla (ex: "SP")
		return NOME_PARA_SIGLA.getOrDefault(limpo, limpo.substring(0, Math.min(2, limpo.length())));
	}
}