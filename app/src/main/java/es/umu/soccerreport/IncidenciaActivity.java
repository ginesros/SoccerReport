package es.umu.soccerreport;

import java.io.FileOutputStream;
import java.util.LinkedList;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import es.umu.soccerreport.Incidencia;
import es.umu.soccerreport.Partido;
import es.umu.soccerreport.IncidenciaActivity;
import es.umu.soccerreport.R;
import es.umu.soccerreport.R.string;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Chronometer;

public class IncidenciaActivity extends Activity{
	private static final int code1 = 1;
	private Partido partido;
	private boolean local=true;
	//Situacion la utilizo para diferenciar entre incidencias de árbitro=0, AA1=1, AA=2
	private int situacion=0;
	private Incidencia inci;
	private Spinner spinner;
	private TextView tjugador1;
	private EditText ejugador1;
	private TextView tjugador2;
	private EditText ejugador2;
	private TextView minuto;
	private EditText minutos;
	private AbsoluteLayout pantalla;
	//Añadimos para el cambio fijo
	private EditText jugador1c;
	private EditText jugador2c;
	private boolean local2=true; //Para saber si es el equipo local o el visitante
	//Añadimos para la descripción de la tarjeta amarilla
	private EditText motivot;
	private EditText jugadort;
	
	//Para saber si es el asistente 1 o 2
	private boolean asistente=true;
    //Añadimos para el cronometro
	private Chronometer cronometro;
	private Button botonParar;
	private Button botonIniciar;
	private Long memoCronometro;
	private String estado="inactivo";
	//Añadimos para establecer el marcador
	private TextView equipol;
	private TextView equipov;
	private TextView goll;
	private TextView golv;
	//Añadimos para el contador de faltas locales y visitantes
	private TextView faltal;
	private TextView faltav;
	//Añadimos para el contador de tarjetas amarillas, rojas y goles
	private TextView talnumeros;
	private TextView tavnumeros;
	private TextView trlnumeros;
	private TextView trvnumeros;
	private TextView gollnumeros;
	private TextView golvnumeros;
    //Para saber si es primera mitad o segunda mitad
	private int parte=1; //Si esta a 1 es la primera parte, si está a 2 es la segunda parte
	//Añadimos para ver el informe
	private TextView listado;
	//Contadores para la generación de ficheros
	private int cont1=1;
	private int cont2=1;
	
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incidencia); 
        
        Intent i=this.getIntent();
        this.partido=(Partido) i.getSerializableExtra("team");
        
        
        this.pantalla=(AbsoluteLayout)findViewById(R.id.layout);
        SurfaceView sf=(SurfaceView)findViewById(R.id.surfaceView1);
		//sf.setBackgroundColor(Color.rgb(0, 0, 100));
        sf.setBackgroundColor(Color.rgb(192, 192, 192));
		
		//Añadimos para el cambio fijo
		jugador1c = (EditText)findViewById(R.id.etsalec);
		jugador2c = (EditText)findViewById(R.id.etentrac);
		//Añadimos para la tarjeta amarilla, tarjeta roja, gol
		motivot = (EditText)findViewById(R.id.motivo);
		jugadort = (EditText)findViewById(R.id.jugadort);
		
		//Añadimos para el cronometro
		//Typeface font = Typeface.createFromAsset(getAssets(), "old_stamper.ttf");
		cronometro = (Chronometer)findViewById(R.id.chronometer1);
		//cronometro.setTypeface(font);
		botonIniciar = (Button)findViewById(R.id.button7);
		botonParar = (Button)findViewById(R.id.button8);
		
		//Añadimos para visualizar Equipo Local 0 Equipo Visitante 0
		equipol = (TextView)findViewById(R.id.tvlocal);
		equipol.setText(this.partido.getEquipoLocal()+"  ");
		goll = (TextView)findViewById(R.id.tvgoll);
		goll.setText("0");
		equipov = (TextView)findViewById(R.id.tvvisitante);
		equipov.setText("  "+this.partido.getEquipoVisitante()+"  ");
		golv = (TextView)findViewById(R.id.tvgolv);
		golv.setText("0");
		//Añadimos para visualizar el número de faltas locales y visitantes. Ya están a 0 inicialmente
		faltal = (TextView)findViewById(R.id.textView2);
		faltav = (TextView)findViewById(R.id.TextView03);
	    //Añadimos para visualizar los números de las ta, tr y goles de local y visitante. Inicialmente, están vacíos
		talnumeros = (TextView)findViewById(R.id.textView3);
		tavnumeros = (TextView)findViewById(R.id.TextView05);
		trlnumeros = (TextView)findViewById(R.id.textView4);
		trvnumeros = (TextView)findViewById(R.id.TextView06);;
		gollnumeros = (TextView)findViewById(R.id.textView5);
		golvnumeros = (TextView)findViewById(R.id.TextView07);
		//Añadimos para ver el informe
		listado = (TextView)findViewById(R.id.textView6);
		
		
		
	
		
        /*
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(this, R.array.claseIncidencia, android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adaptador);*/
        /*Quitado para evitar interferencias
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parent,android.view.View v, int position, long id) {
        		String select=parent.getItemAtPosition(position).toString();
        		if(select.compareTo("Gol")==0 || select.compareTo("Tarjeta Amarilla")==0 || select.compareTo("Tarjeta Roja")==0 || select.compareTo("Penalti")==0){
        			camposUno();
        			situacion=1;
        		}
        		if(select.compareTo("Cambio")==0){
        			camposCambio();
        			situacion=3;
        		}
        		if(select.compareTo("Fuera de Juego")==0 || select.compareTo("Falta")==0 || select.compareTo("No Fuera de Juego")==0 || select.compareTo("Falta que marca el asistente 1")==0 || select.compareTo("Falta que marca el asistente 2")==0 || select.compareTo("Ventaja")==0){
        			camposDos();
        			situacion=4;
        		}
        		
            }
        	public void onNothingSelected(AdapterView<?> parent) {
            }
        	
        });*/ 
	}
	
	
	public void camposUno(){
		if(this.situacion==3){
			this.pantalla.removeView(tjugador1);
		    this.pantalla.removeView(ejugador1);
		    this.pantalla.removeView(tjugador2);
		    this.pantalla.removeView(ejugador2);
		    this.pantalla.removeView(minuto);
		    this.pantalla.removeView(minutos);
		}	
		if(this.situacion==4){
			this.pantalla.removeView(minuto);
		    this.pantalla.removeView(minutos);
		}
		if(this.situacion != 1){
			InputFilter[] FilterArray = new InputFilter[1];
			FilterArray[0] = new InputFilter.LengthFilter(2); 
			this.tjugador1=new TextView(this);
			tjugador1.setText(string.jug);
			tjugador1.setTextSize(20);
			tjugador1.setTextColor(Color.rgb(244, 244, 244));
			tjugador1.setLayoutParams(new AbsoluteLayout.LayoutParams(70, 50, 20, 60));
		
			this.ejugador1=new EditText(this);
			ejugador1.setFilters(FilterArray);
			ejugador1.setInputType(InputType.TYPE_CLASS_NUMBER);
			ejugador1.setLayoutParams(new AbsoluteLayout.LayoutParams(50, 40, 30, 90));
		
			this.minuto=new TextView(this);
			minuto.setText(string.minuto);
			minuto.setTextSize(20);
			minuto.setTextColor(Color.rgb(244, 244, 244));
			minuto.setLayoutParams(new AbsoluteLayout.LayoutParams(70, 50, 110, 60));

			this.minutos=new EditText(this);
			minutos.setFilters(FilterArray);
			minutos.setInputType(InputType.TYPE_CLASS_NUMBER);
			minutos.setLayoutParams(new AbsoluteLayout.LayoutParams(50, 40, 115, 90));
		
		
			this.pantalla.addView(tjugador1);
			this.pantalla.addView(ejugador1);
			this.pantalla.addView(minuto);
			this.pantalla.addView(minutos);
		}
	}
	
	
	public void camposCambio(){
		
		if(this.situacion==1){
			this.pantalla.removeView(this.tjugador1); 
			this.pantalla.removeView(this.ejugador1);
			this.pantalla.removeView(this.minuto);
			this.pantalla.removeView(this.minutos); 
		}
		if(this.situacion==4){
			this.pantalla.removeView(this.minuto);
			this.pantalla.removeView(this.minutos);
		}
		
		if(this.situacion!=3){
			InputFilter[] FilterArray = new InputFilter[1];
			FilterArray[0] = new InputFilter.LengthFilter(2); 
			this.tjugador1=new TextView(this);
			tjugador1.setText(string.sale);
			tjugador1.setTextSize(17);
			tjugador1.setTextColor(Color.rgb(244, 244, 244));
			tjugador1.setLayoutParams(new AbsoluteLayout.LayoutParams(50, 20, 15, 45));
		
			this.ejugador1=new EditText(this);
			ejugador1.setFilters(FilterArray);
			ejugador1.setInputType(InputType.TYPE_CLASS_NUMBER);
			ejugador1.setLayoutParams(new AbsoluteLayout.LayoutParams(40, 40, 15, 67));
			
			this.tjugador2=new TextView(this);
			tjugador2.setText(string.entra);
			tjugador2.setTextSize(17);
			tjugador2.setTextColor(Color.rgb(244, 244, 244));
			tjugador2.setLayoutParams(new AbsoluteLayout.LayoutParams(50, 30, 15, 98));
		
			this.ejugador2=new EditText(this);
			ejugador2.setFilters(FilterArray);
			ejugador2.setInputType(InputType.TYPE_CLASS_NUMBER);
			ejugador2.setLayoutParams(new AbsoluteLayout.LayoutParams(40, 40, 15, 119));
		
			this.minuto=new TextView(this);
			minuto.setText(string.minuto);
			minuto.setTextSize(20);
			minuto.setTextColor(Color.rgb(244, 244, 244));
			minuto.setLayoutParams(new AbsoluteLayout.LayoutParams(70, 50, 90, 60));
			
			this.minutos=new EditText(this);
			minutos.setFilters(FilterArray);
			minutos.setInputType(InputType.TYPE_CLASS_NUMBER);
			minutos.setLayoutParams(new AbsoluteLayout.LayoutParams(50, 40, 95, 90));
		
			this.pantalla.addView(tjugador1);
			this.pantalla.addView(ejugador1);
			this.pantalla.addView(tjugador2);
			this.pantalla.addView(ejugador2);
			this.pantalla.addView(minuto);
			this.pantalla.addView(minutos);
		}
	}
	
	public void camposDos(){
		
		if(this.situacion==1){
			this.pantalla.removeView(this.tjugador1); 
			this.pantalla.removeView(this.ejugador1);
			this.pantalla.removeView(this.minuto);
			this.pantalla.removeView(this.minutos); 
		}
		if(this.situacion==3){
			this.pantalla.removeView(tjugador1);
		    this.pantalla.removeView(ejugador1);
		    this.pantalla.removeView(tjugador2);
		    this.pantalla.removeView(ejugador2);
		    this.pantalla.removeView(minuto);
		    this.pantalla.removeView(minutos);
		}
		
		if(this.situacion!=4){
			InputFilter[] FilterArray = new InputFilter[1];
			FilterArray[0] = new InputFilter.LengthFilter(2);
			this.minuto=new TextView(this);
			minuto.setText(string.minuto);
			minuto.setTextSize(20);
			minuto.setTextColor(Color.rgb(244, 244, 244));
			minuto.setLayoutParams(new AbsoluteLayout.LayoutParams(70, 50, 50, 60));

			this.minutos=new EditText(this);
			minutos.setFilters(FilterArray);
			minutos.setInputType(InputType.TYPE_CLASS_NUMBER);
			minutos.setLayoutParams(new AbsoluteLayout.LayoutParams(50, 40, 60, 90));
		
			this.pantalla.addView(minuto);
			this.pantalla.addView(minutos);
		}
	}

	
	
	public void cambio(View view){
		int duration = Toast.LENGTH_LONG;
		//EditText actual;
		boolean flag=false;
		int min=-1;int number1=0;int number2=0;
		
		    /*
			if(minutoc.getText().toString().compareTo("")==0) 
				flag=true;
			else{
				min=Integer.parseInt(minutoc.getText().toString());
				if(min<0 || min>99) flag=true;  //Se puede meter hasta el minuto 99 de partido
			}*/
			if( jugador1c.getText().toString().compareTo("")==0 || jugador2c.getText().toString().compareTo("")==0){
				flag=true;
			}else{
				number1=Integer.parseInt(jugador1c.getText().toString());
				number2=Integer.parseInt(jugador2c.getText().toString());
				if(number1<=0 || number2<=0 || number1==number2) flag=true;
			}
		
		//Obtiene el tiempo del cronometro automáticamente	
		cronometro.stop();
		String tiempo = cronometro.getText().toString();
		cronometro.start();
			
		if(flag==false){	
			if(this.local2==true){
				this.inci=new Incidencia("Cambio",TipoEquipo.Local,tiempo,number1,number2,"",parte);
			}else{
				this.inci=new Incidencia("Cambio",TipoEquipo.Visitante,tiempo,number1,number2,"",parte);
			}
			
			this.partido.addIncidencia(inci);
			//Limpia los campos utilizados
			jugador1c.setText("");
			jugador2c.setText("");
			//Ponemos el foco en jugadort
			jugadort.requestFocus();
			/*
			Intent intent = new Intent(this, IncidenciaActivity.class);
			intent.putExtra("team", this.partido);
			//startActivity(intent);
			startActivityForResult(intent, code1);*/
		}else{
			Toast toast = Toast.makeText(this, "Revise los campos", duration);
			toast.show();
		}	
	}
	
	public boolean comprobar_minuto_jugador(String minuto) {
		boolean correcto=false;
		int min=-1;
		
		if (minuto.compareTo("")==0) correcto=true;
		else{
			min=Integer.parseInt(minuto);
			if(min<0 || min>99) correcto=true;  //Se puede meter desde el minuto 0 hasta el minuto 99 de partido o número de jugador
		}
		
		return correcto;
	}
	
	//Pasa por parámetro el motivo de la incidencia: "Tarjeta Amarilla","Tarjeta Roja","Gol" 
	public boolean add_incidencia(String texto, String tiempo, TextView textop) {
		int duration = Toast.LENGTH_LONG;
		//EditText actual;
		boolean flag=false;
		int min=-1;int number1=0;int number2=0;
		String cadena="";
		
		
		/*
		//Comprueba el minuto
		if (comprobar_minuto_jugador(minutot.getText().toString())==false) 
			min=Integer.parseInt(minutot.getText().toString());
		else flag=true;*/
		    
		//Comprueba el jugador
		if (comprobar_minuto_jugador(jugadort.getText().toString())==false) {
		    number1=Integer.parseInt(jugadort.getText().toString());
		    //Añadimos el número al texto que nos han pasado por parámetro para que salga en pantalla
		    //Si es la primera vez, sólo añadimos el número del jugador
		    if ( textop.getText().toString().compareTo("                        ")==0 ) {
		    	textop.setText( jugadort.getText().toString() );
		    }
		    //Si es la segunda o resto de veces, añadimos un guión y el número
		    else {
		    	textop.setText( textop.getText().toString() + "-" + jugadort.getText().toString() ); 
		    }
		}
		else flag=true;
					
			
		if(flag==false){	
			if(this.local==true){
				this.inci=new Incidencia(texto,TipoEquipo.Local,tiempo,number1,0,motivot.getText().toString(),parte);
			}else{
				this.inci=new Incidencia(texto,TipoEquipo.Visitante,tiempo,number1,0,motivot.getText().toString(),parte);
			}
			
			this.partido.addIncidencia(inci);
			//Imprimir el resultado en pantalla - Hay que quitarlo
			/*
			cadena=inci.getNombre()+" "+inci.getTipo()+" "+inci.getMinuto()+" "+inci.getJugador1()+" "+inci.getDescripcion();
			Toast toast = Toast.makeText(this, cadena, duration);
			toast.show();*/
			//Limpia los campos utilizados
			jugadort.setText("");
			motivot.setText("");
			//Ponemos el foco en jugadort
			jugadort.requestFocus();
			/*
			Intent intent = new Intent(this, IncidenciaActivity.class);
			intent.putExtra("team", this.partido);
			//startActivity(intent);
			startActivityForResult(intent, code1);*/
			return true;
		}else{
			//Toast toast = Toast.makeText(this, "Revise el minuto o el número de jugador", duration);
			Toast toast = Toast.makeText(this, "Revise el número de jugador", duration);
			toast.show();
			return false;
		}	
	
	}
	
	//Pasa por parámetro el motivo de la incidencia: "Falta" 
	public void add_incidencia_simple(String texto, String tiempo) {
			int duration = Toast.LENGTH_LONG;
			//EditText actual;
			String cadena="";
						
				
			if(this.local==true){
				this.inci=new Incidencia(texto,TipoEquipo.Local,tiempo,0,0,motivot.getText().toString(),parte);
			}else{
				this.inci=new Incidencia(texto,TipoEquipo.Visitante,tiempo,0,0,motivot.getText().toString(),parte);
			}
				
			this.partido.addIncidencia(inci);
			//Imprimir el resultado en pantalla - Hay que quitarlo
			/*
			cadena=inci.getNombre()+" "+inci.getTipo()+" "+inci.getMinuto()+" "+inci.getJugador1()+" "+inci.getDescripcion();
			Toast toast = Toast.makeText(this, cadena, duration);
			toast.show();*/
			//Ponemos el foco en jugadort
			jugadort.requestFocus();
			
				
		
	}
	
	//Pasa por parámetro el motivo de la incidencia: "Falta" 
	public void add_incidencia_arbitros(String texto, String tiempo) {
				int duration = Toast.LENGTH_LONG;
				//EditText actual;
				String cadena=""; 
							
				switch ( situacion ) {
			      case 0:
			    	  this.inci=new Incidencia(texto,TipoEquipo.arbitro,tiempo,0,0,motivot.getText().toString(),parte);
			           break;
			      case 1:
			    	  this.inci=new Incidencia(texto,TipoEquipo.AA1,tiempo,0,0,motivot.getText().toString(),parte);
			           break;
			      case 2:
			    	  this.inci=new Incidencia(texto,TipoEquipo.AA2,tiempo,0,0,motivot.getText().toString(),parte);
			           break;
			      default:
			           return;
			      }
				
					
				this.partido.addIncidencia(inci);
				//Imprimir el resultado en pantalla - Hay que quitarlo
				/*
				cadena=inci.getNombre()+" "+inci.getTipo()+" "+inci.getMinuto()+" "+inci.getJugador1()+" "+inci.getDescripcion();
				Toast toast = Toast.makeText(this, cadena, duration);
				toast.show();*/
				//Limpia los campos utilizados
				motivot.setText("");
				//Ponemos el foco en jugadort
				jugadort.requestFocus();
					
			
			}
	
	public String obtener_tiempo() {
		int duration = Toast.LENGTH_LONG;
		
		cronometro.stop();
		String tiempo = cronometro.getText().toString();
		cronometro.start();
		return tiempo;
	}
	
	public void talocal(View view){
		String cadena = "Tarjeta Amarilla";
		
		this.local = true;
		add_incidencia(cadena, obtener_tiempo(),talnumeros);
		
	}
	
	public void trlocal(View view){
		String cadena = "Tarjeta Roja";
		
		this.local = true;
		add_incidencia(cadena, obtener_tiempo(),trlnumeros);
			
	}
	
	public void tavisitante(View view){
		String cadena = "Tarjeta Amarilla";
		
		this.local = false;
		add_incidencia(cadena, obtener_tiempo(),tavnumeros);
		
	}
	
	public void trvisitante(View view){
		String cadena = "Tarjeta Roja";
		
		this.local = false;
		add_incidencia(cadena, obtener_tiempo(),trvnumeros);
			
	}
	public void gollocal(View view){
		String cadena = "Gol";
		
		this.local = true;
		
		if (add_incidencia(cadena, obtener_tiempo(),gollnumeros)==true) {
			//Actualizar el resultado
			goll.setText( Integer.toString((Integer.parseInt(goll.getText().toString()) + 1)) );
		}
				
	}
	
	public void golvisitante(View view){
		String cadena = "Gol";
		
		this.local = false;
		
		
		if (add_incidencia(cadena, obtener_tiempo(),golvnumeros)==true) {
			//Actualizar el resultado
			golv.setText( Integer.toString((Integer.parseInt(golv.getText().toString()) + 1)) );
		}
		
	}
	
	public void faltalocal(View view){
		String cadena = "Falta";
		
		this.local = true;
		faltal.setText( Integer.toString((Integer.parseInt(faltal.getText().toString()) + 1)) );
		add_incidencia_simple(cadena, obtener_tiempo());	
	}
	
	public void faltavisitante(View view){
		String cadena = "Falta";
		
		this.local = false;
		faltav.setText( Integer.toString((Integer.parseInt(faltav.getText().toString()) + 1)) );
		add_incidencia_simple(cadena, obtener_tiempo());	
	}
	
	public void ventaja(View view){
		String cadena = "Ventaja";
		
		situacion = 0; //Se pone como incidencia del árbitro
		add_incidencia_arbitros(cadena, obtener_tiempo());	
	}
	
	public void aceleracion(View view){
		String cadena = "Aceleración";
		
		situacion = 0; //Se pone como incidencia del árbitro
		add_incidencia_arbitros(cadena, obtener_tiempo());	
	}
	
	public void posicionamiento(View view){
		String cadena = "Posicionamiento";
		
		situacion = 0; //Se pone como incidencia del árbitro
		add_incidencia_arbitros(cadena, obtener_tiempo());	
	}
	
	public void otra(View view){
		String cadena = "Otra";
		
		situacion = 0; //Se pone como incidencia del árbitro
		add_incidencia_arbitros(cadena, obtener_tiempo());	
	}
	
	
	public void faltaaa1(View view){
		//Hacer la de falta
		String cadena = "Falta";
		
		situacion = 1; //Se pone como incidencia del AA1
		add_incidencia_arbitros(cadena, obtener_tiempo());	
	}
	
	public void noaa1(View view){
		String cadena = "No";
		
		situacion = 1; //Se pone como incidencia del AA1
		add_incidencia_arbitros(cadena, obtener_tiempo());	
	}
	
	public void fjaa1(View view){
		String cadena = "FJ";
		
		situacion = 1; //Se pone como incidencia del AA1
		add_incidencia_arbitros(cadena, obtener_tiempo());	
	}
	
	public void faltaaa2(View view){
		//Hacer la de falta
		String cadena = "Falta";
		
		situacion = 2; //Se pone como incidencia del AA2
		add_incidencia_arbitros(cadena, obtener_tiempo());	
	}
	
	public void noaa2(View view){
		String cadena = "No";
		
		situacion = 2; //Se pone como incidencia del AA2
		add_incidencia_arbitros(cadena, obtener_tiempo());	
	}
	
	public void fjaa2(View view){
		String cadena = "FJ";
		
		situacion = 2; //Se pone como incidencia del AA1
		add_incidencia_arbitros(cadena, obtener_tiempo());	
	}
	public void clickcrono(View view) {
		if (estado == "inactivo") {
			cronometro.setBase(SystemClock.elapsedRealtime());
			cronometro.start();
			estado = "activo";
			botonIniciar.setText("Pausar");
			return;
		}
		if (estado == "activo") {
			memoCronometro = SystemClock.elapsedRealtime();
			cronometro.stop();
			estado = "pausado";
			botonIniciar.setText("Continuar");
			return;
		} else {
			cronometro.setBase(cronometro.getBase() + SystemClock.elapsedRealtime() - memoCronometro);
			cronometro.start();
			estado = "activo";
			botonIniciar.setText("Pausar");
		}
	}
	
	public void clickparar(View view) {
			cronometro.stop();
			estado = "inactivo";
			botonIniciar.setText("Iniciar");
	}
	
	
	public void nuevo(View view){
		/*
		int duration = Toast.LENGTH_LONG;
		//EditText actual;
		boolean flag=false;
		int min=-1;int number1=0;int number2=0;
		if(this.situacion==1){
			if(this.minutos.getText().toString().compareTo("")==0)
				flag=true;
			else{
				min=Integer.parseInt(this.minutos.getText().toString());
				if(min<0 || min>90) flag=true;
			}
			if(this.ejugador1.getText().toString().compareTo("")==0){
				flag=true;
			}else{
				number1=Integer.parseInt(this.ejugador1.getText().toString());
				if(number1<=0) flag=true;
			}
		}
		if(this.situacion==3){
			if(this.minutos.getText().toString().compareTo("")==0)
				flag=true;
			else{
				min=Integer.parseInt(this.minutos.getText().toString());
				if(min<0 || min>90) flag=true;
			}
			if(this.ejugador1.getText().toString().compareTo("")==0 || this.ejugador2.getText().toString().compareTo("")==0){
				flag=true;
			}else{
				number1=Integer.parseInt(this.ejugador1.getText().toString());
				number2=Integer.parseInt(this.ejugador2.getText().toString());
				if(number1<=0 || number2<=0 || number1==number2) flag=true;
			}
		}
		if(this.situacion==4){
			if(this.minutos.getText().toString().compareTo("")==0)
				flag=true;
			else{
				min=Integer.parseInt(this.minutos.getText().toString());
				if(min<0 || min>90) flag=true;
			}
		}
			
		if(flag==false){	
			if(this.local==true){
				this.inci=new Incidencia(this.spinner.getSelectedItem().toString(),TipoEquipo.Local,min,number1,number2,((EditText) findViewById(R.id.motivo)).getText().toString());
			}else{
				this.inci=new Incidencia(this.spinner.getSelectedItem().toString(),TipoEquipo.Visitante,min,number1,number2,((EditText) findViewById(R.id.motivo)).getText().toString());
			}
			
			this.partido.addIncidencia(inci);
			
			Intent intent = new Intent(this, IncidenciaActivity.class);
			intent.putExtra("team", this.partido);
			//startActivity(intent);
			startActivityForResult(intent, code1);
		}else{
			Toast toast = Toast.makeText(this, "Revise los campos", duration);
			toast.show();
		}	
		*/
	}
	
    public void salir(View view){
        setResult(Activity.RESULT_OK, null);
      	finish();
	}
	
	public void generarPdf(View view) {
		int duration = Toast.LENGTH_LONG;
		//EditText actual;
		boolean flag=false;
		int min=-1;int number1=0;int number2=0;
		
		/*
		if(this.situacion==1){
			if(this.minutos.getText().toString().compareTo("")==0)
				flag=true;
			else{
				min=Integer.parseInt(this.minutos.getText().toString());
				if(min<0 || min>90) flag=true;
			}
			if(this.ejugador1.getText().toString().compareTo("")==0){
				flag=true;
			}else{
				number1=Integer.parseInt(this.ejugador1.getText().toString());
				if(number1<=0) flag=true;
			}
		}
		if(this.situacion==3){
			if(this.minutos.getText().toString().compareTo("")==0)
				flag=true;
			else{
				min=Integer.parseInt(this.minutos.getText().toString());
				if(min<0 || min>90) flag=true;
			}
			if(this.ejugador1.getText().toString().compareTo("")==0 || this.ejugador2.getText().toString().compareTo("")==0){
				flag=true;
			}else{
				number1=Integer.parseInt(this.ejugador1.getText().toString());
				number2=Integer.parseInt(this.ejugador2.getText().toString());
				if(number1<=0 || number2<=0 || number1==number2) flag=true;
			}
		}
		if(this.situacion==4){
			if(this.minutos.getText().toString().compareTo("")==0)
				flag=true;
			else{
				min=Integer.parseInt(this.minutos.getText().toString());
				if(min<0 || min>90) flag=true;
			}
		}*/
		/*	
		if(flag==false){
			if(this.local==true){
				this.inci=new Incidencia(this.spinner.getSelectedItem().toString(),TipoEquipo.Local,min,number1,number2,((EditText) findViewById(R.id.motivo)).getText().toString());
			}else{
				this.inci=new Incidencia(this.spinner.getSelectedItem().toString(),TipoEquipo.Visitante,min,number1,number2,((EditText) findViewById(R.id.motivo)).getText().toString());
			}
			this.partido.addIncidencia(inci);*/
			String nombre=""+this.partido.getEquipoLocal()+"-"+this.partido.getEquipoVisitante();
			try {
				FileOutputStream file = new FileOutputStream("/sdcard/"+nombre+Integer.toString(cont1)+".pdf");
				cont1++;
				Document document = new Document();
				
				PdfWriter.getInstance(document,file);
	            document.open();
	            document.add(new Paragraph("INFORME \n\n",FontFactory.getFont("Calibri",22,Font.BOLD,BaseColor.BLACK)));
	            String cadena=informe();
	            document.add(new Paragraph(cadena));
	            document.close();
	            //Asigno el texto del pdf a listado
	            listado.setText(cadena);
	           
	            //listado.requestFocus();
	            /*Finaliza cuando hace el pdf, lo quito
	            setResult(Activity.RESULT_OK,null);
	           	finish();*/
			} catch (Exception e) { e.printStackTrace();}
		//}
		/*	
		else{
			Toast toast = Toast.makeText(this, "Revise los campos", duration);
			toast.show();
		}*/
		
	}
	
	public void txt(View view) {
		
			String nombre=""+this.partido.getEquipoLocal()+"-"+this.partido.getEquipoVisitante();
			try {
				FileOutputStream file = new FileOutputStream("/sdcard/"+nombre+"o"+Integer.toString(cont2)+".pdf");
				cont2++;
				Document document = new Document();
				PdfWriter.getInstance(document,file);
				document.open();
				document.add(new Paragraph("INFORME"+nombre+"\n\n"));
				String cadena=informe_ordenado();
				document.add(new Paragraph(cadena));
	            document.close();
	            
			} catch (Exception e) { e.printStackTrace();}
			
	}
	
	
	
	
	public String informe(){
		int golesL=0;int golesV=0;
		String cadena="";
		String golesl="";
		String golesv="";
		String tarjetal="";
		String tarjetav="";
		String rojal="";
		String rojav="";
		String cambiol="";
		String cambiov="";
		String faltasl="";
		String faltasv="";
		String ventaja="";
		String aceleracion="";
		String pos="";
		String otra="";
		String aa1f="";
		String aa2f="";
		String aa1no="";
		String aa2no="";
		String aa1fj="";
		String aa2fj="";
		int minutosA=0;
		String segundosA="";
		String mt;
		
		cadena="  Equipo Local: "+partido.getEquipoLocal()+"\n  Equipo Visitante: "+partido.getEquipoVisitante()+
		"\n  Fecha: "+partido.getDia()+"-"+partido.getMes()+"-"+partido.getAnyo()+"\n  Hora: "+partido.getHoras()+
		":"+partido.getMinutos()+"\n\n";
		
		cadena+="______________________________________________________________________________\n";
		cadena+="RESULTADO: "+goll.getText().toString()+" - "+golv.getText().toString()+"\n\n";
		
		//Inicializo el número total de faltas
		faltasl+= faltal.getText().toString()+" Minutos: ";
		faltasv+= faltav.getText().toString()+" Minutos: ";
		//Recorro la lista completa y voy analizando
		LinkedList<Incidencia> l1=(LinkedList<Incidencia>)this.partido.getLista();
		for (Incidencia incidencia : l1) {
			mt="";
			//Si es una incidencia de la segunda parte, le sumamos 45
			if (incidencia.getParte()==2) {
				//Si la longitud es igual a 4: tipo 0:11, 2:14
				if (incidencia.getMinuto().length()==4) {
					minutosA = Integer.valueOf(incidencia.getMinuto().substring(0, 1))+45;
					segundosA = incidencia.getMinuto().substring(2, 4);
					
				}
				else { //Longitud debe ser igual a 5: tipo 10:22, 45:11
					minutosA = Integer.valueOf(incidencia.getMinuto().substring(0, 2))+45;
					segundosA = incidencia.getMinuto().substring(3, 5);
					
				}
				mt=Integer.toString(minutosA)+":"+segundosA;
			}
			else {
				mt = incidencia.getMinuto();
			}
			if(incidencia.getNombre().compareTo("Gol")==0){
				if(incidencia.getTipo()==TipoEquipo.Local) {
					golesl+="Nº "+incidencia.getJugador1()+" Min: "+mt+" "+incidencia.getDescripcion()+" ";
				}
				else {
					golesv+="Nº "+incidencia.getJugador1()+" Min: "+mt+" "+incidencia.getDescripcion()+" ";
				}
			}
			if(incidencia.getNombre().compareTo("Falta")==0){
				//Falta marcada por AA1 
				if (incidencia.getTipo()==TipoEquipo.AA1) {
					aa1f+=" "+mt; //incidencia.getMinuto();
				}
				//Falta marcada por AA2
				if (incidencia.getTipo()==TipoEquipo.AA2) {
					aa2f+=" "+mt; //incidencia.getMinuto();
				}
				//Falta normal para un equipo u otro
				if(incidencia.getTipo()==TipoEquipo.Local) {
					faltasl+=mt+" ";//incidencia.getMinuto()+" ";
				}
				if(incidencia.getTipo()==TipoEquipo.Visitante) {
					faltasv+=mt+" ";//incidencia.getMinuto()+" ";
				}
			}
			//cadena+="- "+incidencia.getNombre()+"\n";
			if(incidencia.getNombre().compareTo("Tarjeta Amarilla")==0){
				if(incidencia.getTipo()==TipoEquipo.Local) {
					tarjetal+="Nº "+incidencia.getJugador1()+" Min: "+mt+" "+incidencia.getDescripcion()+"\n";
				}
				else {
					tarjetav+="Nº "+incidencia.getJugador1()+" Min: "+mt+" "+incidencia.getDescripcion()+"\n";
				}
			}
			if(incidencia.getNombre().compareTo("Tarjeta Roja")==0){
				if(incidencia.getTipo()==TipoEquipo.Local) {
					rojal+=incidencia.getJugador1()+" "+mt+" "+incidencia.getDescripcion()+"\n";
				}
				else {
					rojav+=incidencia.getJugador1()+" "+mt+" "+incidencia.getDescripcion()+"\n";
				}
			}
			
			if ( incidencia.getNombre().compareTo("Ventaja")==0 ) {
				ventaja+=" "+mt;//incidencia.getMinuto();
			}
			if ( incidencia.getNombre().compareTo("Aceleración")==0 ) {
				aceleracion+=" "+mt;//incidencia.getMinuto();
			}
			if ( incidencia.getNombre().compareTo("Posicionamiento")==0 ) {
				pos+=" "+mt+" "+incidencia.getDescripcion();//incidencia.getMinuto();
			}
			if ( incidencia.getNombre().compareTo("Otra")==0 ) {
				otra+=" "+mt+" "+incidencia.getDescripcion()+"\n";
			}
			if(incidencia.getNombre().compareTo("Cambio")==0){
				if(incidencia.getTipo()==TipoEquipo.Local) {
					cambiol+="Nº "+incidencia.getJugador2()+" X "+"Nº "+incidencia.getJugador1()+" Min:"+mt+"\n";
				}
				else {
					cambiov+="Nº "+incidencia.getJugador2()+" X "+"Nº "+incidencia.getJugador1()+" Min:"+mt+"\n";
				}			
			}
			if(incidencia.getNombre().compareTo("No")==0){
				if (incidencia.getTipo()==TipoEquipo.AA1) {
					aa1no+=" "+mt+" "+incidencia.getDescripcion();//incidencia.getMinuto();
				}
				else {
					aa2no+=" "+mt+" "+incidencia.getDescripcion();//incidencia.getMinuto();
				}
			}
			if(incidencia.getNombre().compareTo("FJ")==0){
				if (incidencia.getTipo()==TipoEquipo.AA1) {
					aa1fj+=" "+mt+" "+incidencia.getDescripcion();//incidencia.getMinuto();
				}
				else {
					aa2fj+=" "+mt+" "+incidencia.getDescripcion();//incidencia.getMinuto();
				}
			}
		}
		cadena+="Goles local: "+golesl+"\nGoles visitante: "+golesv+"\n\nTA local: "+tarjetal+"\nTR local: "+rojal+"\nTA visitante: "+tarjetav+"\nTR visitante: "+rojav+
				"\n\nCambios local: "+
				cambiol+"Cambios visitante: "+cambiov+"\n\nFaltas local: "+faltasl+"\nFaltas visitante: "+faltasv+"\nVentaja: "+ventaja+"\nAceleración: "+aceleracion+
				"\nPosicionamiento: "+pos+"\n\nIncidencias: "+otra+"\n\nAA1: NO: "+aa1no+"\nFJ: "+aa1fj+"\nFALTAS: "+aa1f+"\n\nAA2: NO: "+aa2no+"\nFJ: "+aa2fj+"\nFALTAS: "+aa2f;
		cadena+="\n\n";
		cadena+="______________________________________________________________________________\n";
		
		cadena+="FINAL DEL PARTIDO";
		return cadena;
	}
	
	public String informe_ordenado(){
		
		String cadena="";
		
		//Recorro la lista completa y voy analizando
		LinkedList<Incidencia> l1=(LinkedList<Incidencia>)this.partido.getLista();
		for (Incidencia incidencia : l1) {
			
			cadena+=incidencia.getMinuto()+" ";
			if( (incidencia.getNombre().compareTo("Gol")==0) || (incidencia.getNombre().compareTo("Tarjeta Amarilla")==0) || (incidencia.getNombre().compareTo("Tarjeta Roja")==0)){
				cadena+=incidencia.getNombre()+" "+incidencia.getTipo()+" Nº "+incidencia.getJugador1()+" "+incidencia.getDescripcion();
			}
			if ( (incidencia.getNombre().compareTo("Ventaja")==0) || (incidencia.getNombre().compareTo("Aceleración")==0 ) ) {
				cadena+=incidencia.getNombre();
			}
			
			if ( (incidencia.getNombre().compareTo("Posicionamiento")==0) || (incidencia.getNombre().compareTo("Otra")==0) ) {
				cadena+=incidencia.getNombre()+" "+incidencia.getDescripcion();
			}
			
			if(incidencia.getNombre().compareTo("Cambio")==0){
				cadena+=incidencia.getNombre()+" "+incidencia.getTipo()+" Nº "+incidencia.getJugador2()+" X "+"Nº "+incidencia.getJugador1();			
			}
			
			if(incidencia.getNombre().compareTo("No")==0){
				cadena+="Deja continuar ajustado"+" "+incidencia.getTipo()+" "+incidencia.getDescripcion();
			}
			if(incidencia.getNombre().compareTo("FJ")==0){
				cadena+=incidencia.getNombre()+" "+incidencia.getTipo()+" "+incidencia.getDescripcion();
			}
			
			if(incidencia.getNombre().compareTo("Falta")==0){
				cadena+=incidencia.getNombre()+" "+incidencia.getTipo();
			}
			
			cadena+="\n"; 
		}
		
		cadena+="FINAL DEL PARTIDO";
		return cadena;
	}
	
	
	
	//Métodos para el cambio fijo que activa o desactiva la variable local2 a true si es equipo local o a false si es el equipo visitante
	public void clickLocalc(View view){
		this.local2=true;
	}
	
	public void clickVisitantec(View view){
		this.local2=false;
	}
	
	public void mitad(View view){
		//Cada vez que hacemos click cambia la parte en la que se encuentra (1=1parte, 2=2parte)
		if (this.parte == 1) {
			this.parte = 2;
		}
		else {
			this.parte = 1;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == code1)
		{
			if (resultCode == RESULT_OK)
			{
			    setResult(Activity.RESULT_OK, null);
			    finish();
			}
		}
	}
}
