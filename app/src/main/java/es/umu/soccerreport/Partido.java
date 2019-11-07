package es.umu.soccerreport;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import es.umu.soccerreport.Incidencia;

public class Partido implements Serializable {

    private static final long serialVersionUID = 1L;
    private String equipoLocal;
    private String equipoVisitante;
    private int dia;
    private int mes;
    private int anyo;
    private int horas;
    private int minutos;
    private LinkedList<Incidencia> lista;

    public Partido(String equipoLocal, String equipoVisitante, int dia, int mes, int anyo, int horas, int minutos) {
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.dia = dia;
        this.mes = mes;
        this.anyo = anyo;
        this.horas = horas;
        this.minutos = minutos;
        this.lista = new LinkedList<Incidencia>();
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
}
