package es.umu.soccerreport;


import android.content.Context;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Room;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Entity(tableName = "partido")
public class Partido implements Serializable {

    @PrimaryKey()
    @ColumnInfo(name = "idPartido")
    public int idPartido;

    @ColumnInfo(name = "EquipoLocal")
    private String equipoLocal;

    @ColumnInfo(name = "EquipoVisitante")
    private String equipoVisitante;

    @ColumnInfo(name = "Dia")
    private String dia;

    @ColumnInfo(name = "Mes")
    private String mes;

    @ColumnInfo(name = "Anyo")
    private String anyo;

    @ColumnInfo(name = "Horas")
    private String horas;

    @ColumnInfo(name = "Minutos")
    private String minutos;

    @ColumnInfo(name = "serialVersion")
    private static final long serialVersionUID = 1L;

    @ColumnInfo(name = "GolesLocal")
    int golesLocal;

    @ColumnInfo(name = "GolesVisitante")
    int golesVisitante;

    @ColumnInfo(name = "FaltasLocales")
    int faltasLocales;

    @ColumnInfo(name = "FaltasVisitante")
    int faltasVisitantes;

    @ColumnInfo(name = "Lista")
    LinkedList<Incidencia> lista;

    @ColumnInfo(name = "EstadoPartido")
    String estadoPartido;

    @ColumnInfo(name = "TiempoPartido")
    Long tiempoPartido;

    public Partido(String equipoLocal, String equipoVisitante, String dia, String mes, String anyo, String horas, String minutos) {
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
        estadoPartido = "inactivo";
        tiempoPartido = 0L;
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

    public String getDia() {

        return dia;
    }

    public String getMes() {

        return mes;
    }

    public String getAnyo() {

        return anyo;
    }

    public String getHoras() {
        return horas;
    }

    public String getMinutos() {
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

    public Incidencia quitarUltimoEvento(){
        if (hayIncidencias()) {
            Incidencia i = lista.getLast();

            if (i.getNombre().equals("Gol")){
                if (i.getTipo().equals(TipoEquipo.Local)) {
                    golesLocal--;
                }else {
                    golesVisitante--;
                }

            }else if (i.getNombre().equals("Falta")) {
                if (i.getTipo().equals(TipoEquipo.Local))
                    faltasLocales--;
                else
                    faltasVisitantes--;
            }

            return lista.removeLast();
        }

        return null;

    }

    public boolean hayIncidencias(){
        return lista.size() > 0;
    }

    public ArrayList<Incidencia> getIncidenciasFiltradas(ArrayList<String> filtro){
        ArrayList<Incidencia> filtradas = new ArrayList<>();

        for (int i=0; i<lista.size();i++) {
            for (int j = 0; j < filtro.size(); j++) {
                if ((filtro.get(j).compareTo("Lista desplegable") == 0) && lista.get(i).getTipo() == TipoEquipo.INCSPINNER) {
                    filtradas.add(lista.get(i));
                    break;
                } else if (lista.get(i).getNombre().compareTo(filtro.get(j)) == 0) {
                    filtradas.add(lista.get(i));
                    break;
                }
            }
        }

        return filtradas;
    }

    private ArrayList<Incidencia> filtrarIncidencias(Predicate<Incidencia> filtro) {
        List<Incidencia> f = lista.stream().filter(filtro).collect(Collectors.<Incidencia>toList());
        ArrayList<Incidencia> listaFiltrada = new ArrayList<Incidencia>(f);

        return listaFiltrada;
    }

    public ArrayList<Incidencia> filtrarLocal(ArrayList<Incidencia> datos) {
        Predicate<Incidencia> predicadoLocal = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getTipo() == TipoEquipo.Local;
            }
        };

        List<Incidencia> f = datos.stream().filter(predicadoLocal).collect(Collectors.<Incidencia>toList());
        ArrayList<Incidencia> datosfiltrados = new ArrayList<Incidencia>(f);
        return datosfiltrados;
    }

    public ArrayList<Incidencia> filtrarAAA2(ArrayList<Incidencia> datos) {
        Predicate<Incidencia> predicadoAAA2 = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getTipo() == TipoEquipo.AA2;
            }
        };

        List<Incidencia> f = datos.stream().filter(predicadoAAA2).collect(Collectors.<Incidencia>toList());
        ArrayList<Incidencia> datosfiltrados = new ArrayList<Incidencia>(f);
        return datosfiltrados;
    }

    public ArrayList<Incidencia> filtrarAAA1(ArrayList<Incidencia> datos) {
        Predicate<Incidencia> predicadoAAA1 = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getTipo() == TipoEquipo.AA1;
            }
        };

        List<Incidencia> f = datos.stream().filter(predicadoAAA1).collect(Collectors.<Incidencia>toList());
        ArrayList<Incidencia> datosfiltrados = new ArrayList<Incidencia>(f);
        return datosfiltrados;
    }

    public ArrayList<Incidencia> filtrarVisitante(ArrayList<Incidencia> datos) {
        Predicate<Incidencia> predicadoVisitante = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getTipo() == TipoEquipo.Visitante;
            }
        };

        List<Incidencia> f = datos.stream().filter(predicadoVisitante).collect(Collectors.<Incidencia>toList());
        ArrayList<Incidencia> datosfiltrados = new ArrayList<Incidencia>(f);
        return datosfiltrados;
    }

    public ArrayList<Incidencia> getIncidenciasJugadaArea() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Jugada de area");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasPenalti() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Penalti");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasGoles() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Gol");
            }
        };

       return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasTA() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Tarjeta Amarilla");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasTR() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Tarjeta Roja");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasCambios() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Cambio");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasFaltas() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Falta");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasVentaja() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Ventaja");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasAceleracion() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Aceleración");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasPosicionamiento() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Posicionamiento");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasOtra() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("Otra");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasSpinner() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getTipo() == TipoEquipo.INCSPINNER;
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasNO() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("No");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

    public ArrayList<Incidencia> getIncidenciasFJ() {
        Predicate<Incidencia> predicadoGol = new Predicate<Incidencia>() {
            @Override
            public boolean test(Incidencia incidencia) {
                return incidencia.getNombre().equals("FJ");
            }
        };

        return filtrarIncidencias(predicadoGol);
    }

}
