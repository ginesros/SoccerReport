package es.umu.soccerreport;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import es.umu.soccerreport.Incidencia;

public class Partido implements Serializable {

    private static final long serialVersionUID = 1L;
    private String equipoLocal;
    private String equipoVisitante;
    private int golesLocal;
    private int golesVisitante;
    private int faltasLocales;
    private int faltasVisitantes;
    private int dia;
    private int mes;
    private int anyo;
    private int horas;
    private int minutos;
    private LinkedList<Incidencia> lista;

    public Partido(String equipoLocal, String equipoVisitante, int dia, int mes, int anyo, int horas, int minutos) {
        this.faltasLocales = 0;
        this.faltasVisitantes = 0;
        this.golesLocal = 0;
        this.golesVisitante = 0;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.dia = dia;
        this.mes = mes;
        this.anyo = anyo;
        this.horas = horas;
        this.minutos = minutos;
        this.lista = new LinkedList<Incidencia>();
    }

    public void sumarGol(boolean esLocal) {
        if (esLocal)
            golesLocal++;
        else
            golesVisitante++;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void sumarFalta(boolean esLocal) {
        if (esLocal)
            faltasLocales++;
        else
            faltasVisitantes++;
    }

    public int getFaltasLocales() {
        return faltasLocales;
    }

    public int getFaltasVisitantes() {
        return faltasVisitantes;
    }

    public boolean addIncidencia(Incidencia inci) {

         return lista.add(inci);

    }

    public String getEquipoLocal() {

        return equipoLocal;
    }

    public String getEquipoVisitante() {

        return equipoVisitante;
    }

    public int getDia() {

        return dia;
    }

    public int getMes() {

        return mes;
    }

    public int getAnyo() {

        return anyo;
    }

    public int getHoras() {
        return horas;
    }

    public int getMinutos() {
        return minutos;
    }

    public List<Incidencia> getLista() {
        LinkedList<Incidencia> lista2 = new LinkedList<Incidencia>();
        if (this.lista.size() != 0) {
            for (Incidencia incidencia : lista) {
                lista2.add(incidencia);
            }
            return lista2;
        }
        return null;
    }

    public String getEstado(){
        String info = equipoLocal + " "+ golesLocal;
        info += " --- " + golesVisitante + " " + equipoVisitante + "\n";
        info += "Total faltas local: " + faltasLocales + "\n";
        info += "Total faltas visitante: " + faltasVisitantes + "\n";

        return info;
    }
}
