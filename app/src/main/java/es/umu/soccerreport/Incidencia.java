package es.umu.soccerreport;

import java.io.Serializable;

public class Incidencia implements Serializable{
	private String nombre;
	private TipoEquipo tipo;
	//Valor para el mm:ss automático del cronometro
	private String minuto;
	private int jugador1;
	private int jugador2;
	private String descripcion;
	private int parte;
	
	//Metemos valores para el cambio
	//private int minutoc;
	//private int jugador1c;
	//private int jugador2c;
	
	
	public Incidencia(String nombre, TipoEquipo tipo, String minuto,int jugador1,int jugador2, String descripcion, int parte) { //, int minutoc, int jugador1c, int jugador2c) {
		this.nombre = nombre;
		this.tipo = tipo;
		this.minuto = minuto;
		this.jugador1 = jugador1;
		this.jugador2 = jugador2;
		this.descripcion = descripcion;
		this.parte = parte;
		//this.minutoc = minutoc;
		//this.jugador1c = jugador1c;
		//this.jugador2c = jugador2c;
	}
	
	public Incidencia(String minuto) {	
		this.minuto = minuto;
	}

	public String getNombre() {
		return nombre;
	}

	public TipoEquipo getTipo() {
		return tipo;
	}

	public String getMinuto() {
		return minuto;
	}

	public int getJugador1() {
		return jugador1;
	}
	
	public int getJugador2() {
		return jugador2;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public int getParte() {
		return parte;
	}
	
	
	/*
	public int getJugador1c() {
		return jugador1c;
	}
	
	public int getJugador2c() {
		return jugador2c;
	}
	
	public int getMinutoc() {
		return minutoc;
	}
	*/
}
