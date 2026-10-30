package br.com.ifsp.edu.br.nxtdrive.DTO;

public enum PeriodoFiltro {
    ULTIMOS_7_DIAS,
    ULTIMOS_30_DIAS,
    ULTIMOS_12_MESES,
    CUSTOM;

    public static PeriodoFiltro fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return ULTIMOS_30_DIAS;
        }
        try {
            return PeriodoFiltro.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ULTIMOS_30_DIAS;
        }
    }
}
