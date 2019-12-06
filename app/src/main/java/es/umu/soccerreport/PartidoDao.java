package es.umu.soccerreport;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Dao
public interface PartidoDao {

    @Query("SELECT * FROM partido where idPartido = (:partidoID)")
    Partido getPartido(int partidoID);

    @Query("SELECT max(idPartido) from partido")
    int getMaxIdPartido();

    @Query("SELECT * FROM partido")
    List<Partido> getAll();

    @Query("DELETE FROM partido")
    void deleteAllData();

//    @Query("SELECT * FROM partido")
//    List<PartidoTabla> getAll();
//
//    @Query("SELECT isNUll(max(id), 0) FROM partido")
//    int getMaxID();
//
//    @Query("SELECT * FROM partidotabla where id = (:partidoID)")
//    PartidoTabla getPartido(int partidoID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPartido(Partido partido);


}


