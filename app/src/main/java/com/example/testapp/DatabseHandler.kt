package com.example.testapp;

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DatabaseHandler(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS  " + TABLE_SPORTS)
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_SPORTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT,"
                + RECORD_TIME + " TEXT DEFAULT '00:00:00', "
                + RECORD_DATE + " TEXT DEFAULT '0000-00-00',"
                + LAST_TIME + " TEXT DEFAULT '00:00:00', "
                + LAST_DATE + " TEXT DEFAULT '0000-00-00' "
                + ")")
        db.execSQL(CREATE_CONTACTS_TABLE)
        addDefaultData(db);
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPORTS)
        onCreate(db)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun updateDateAndTime(seconds: Int, sportId: Int) {
        val sportById = getSportById(sportId.toLong())
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        val secs = seconds % 60
        val parser = DateTimeFormatter.ofPattern("HH:mm:ss")
        val oldTime = LocalTime.parse(sportById.recordTime, parser)
        val db = this.writableDatabase
        val newTime = LocalTime.of(hours, minutes, secs)
        val date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        println("OLD $oldTime    NEW $newTime")
        if (newTime.compareTo(oldTime) > 0) {
            val values = ContentValues()
            values.put(RECORD_TIME, newTime.toString())
            values.put(RECORD_DATE, date)
            db.update(TABLE_SPORTS, values, KEY_ID + " = ?", arrayOf((sportId + 1).toString()))
        }
        val values = ContentValues()
        values.put(LAST_TIME, newTime.toString())
        values.put(LAST_DATE, date)
        db.update(TABLE_SPORTS, values, KEY_ID + " = ?", arrayOf((sportId + 1).toString()))
        db.close()
    }

    fun getSportById(ID: Long): Sport {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM " + TABLE_SPORTS + " WHERE ID = ?",
            arrayOf((ID + 1).toString()),
            null
        )
        val sport = Sport()
        if (cursor.moveToFirst()) {
            do {
                sport.id = cursor.getLong(0)
                sport.name = cursor.getString(1)
                sport.details = cursor.getString(2)
                sport.recordTime = cursor.getString(3)
                sport.recordDate = cursor.getString(4)
                sport.lastTime = cursor.getString(5)
                sport.lastDate = cursor.getString(6)
            } while (cursor.moveToNext())
        }
        db.close()
        return sport
    }

    fun getAllContacts(): List<Sport?> {
        val contactList: MutableList<Sport?> = ArrayList()
        val selectQuery = "SELECT  * FROM " + TABLE_SPORTS
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val sport = Sport()
                sport.id = cursor.getLong(0)
                sport.name = cursor.getString(1)
                sport.details = cursor.getString(2)
                sport.recordTime = cursor.getString(3)
                sport.recordDate = cursor.getString(4)
                sport.lastTime = cursor.getString(5)
                sport.lastDate = cursor.getString(6)
                contactList.add(sport)
            } while (cursor.moveToNext())
        }
        return contactList
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "nowabaz13"
        private const val TABLE_SPORTS = "lala"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_PH_NO = "details"
        private const val RECORD_TIME = "record_time"
        private const val RECORD_DATE = "record_date"
        private const val LAST_TIME = "last_time"
        private const val LAST_DATE = "last_date"
        private val mInstance: DatabaseHandler? = null
        private var instance: DatabaseHandler? = null
        private var db: SQLiteDatabase? = null
        @Synchronized
        fun getInstance(context: Context?): DatabaseHandler? {
            if (instance == null) {
                instance = DatabaseHandler(context)
                db = instance!!.writableDatabase
            }
            return instance
        }
    }
    private fun addDefaultData(db: SQLiteDatabase) {
        val s1 = Sport("Bobrowy Szlak", "??cie??ka edukacyjno-przyrodnicza ???Bobrowy Szlak??? rozpoczyna si?? we wsi Czmoniec (gmina K??rnik) i biegnie przez lasy, obszary ????k nadrzecznych z szypu??kowymi d??bami, sk??d rozci??ga si?? niepowtarzalny widok na wyst??puj??ce tam starorzecze Warty oraz jej naturalne rozlewisko. Trasa ko??czy sw??j bieg przy boisku w Czmo??cu.");
        val s2 = Sport("Aktywna Tr??jka", "Z my??l?? o aktywnym wypoczynku na ??wie??ym powietrzu przez ca??y rok, z inicjatywy Gmin: Mosina, Puszczykowo, Komorniki i St??szew, przy wsp????pracy z WPN powsta?? projekt pn. ???Aktywna Tr??jka??? ??? trasy trzech aktywno??ci po Wielkopolskim Parku Narodowym. W ramach przeprowadzonych prac zosta??a wyznaczona sie?? tras w formie zamkni??tych p??tli, przeznaczona do uprawiania nordic walking, biegania, a w zimie dla narciarstwa biegowego.");
        val s3 = Sport("Zbiorowiska ro??linne wok???? Jeziora Zielonka", "??cie??ka przyrodnicza wytyczona zosta??a wok???? Jeziora Zielonka po??o??onego w du??ej enklawie ??r??dle??nej na terenie Parku Krajobrazowego Puszcza Zielonka. Jej g????wnym celem jest zapoznanie uczestnik??w z wyst??puj??cymi tu zbiorowiskami ro??linnymi i przybli??enie zmian zachodz??cych w ??rodowisku naturalnym, a tak??e u??wiadomienie konieczno??ci poszanowania otaczaj??cej nas przyrody w obcowaniu na co dzie??.");
        val s4 = Sport("??nie??ycowy Raj", "Rezerwat przyrody ??nie??ycowy Jar. To magiczne miejsce przyci??ga setki ludzi wczesn?? wiosn?? aby podziwia?? g??sto rosn??ce w tym miejscu ??nie??yce wiosenne. Kwiaty te tworz?? pi??kne bia??e dywany ??ciel??ce g????bi?? tych las??w. Osob?? kt??re nigdy tu nie by??y postaram si?? przybli??y?? to miejsce. Na pocz??tku warto zaznaczy??, ??e w okresie kwitni??cia ??nie??ycy przy okazji dni wolnych teren ten jest u??ytkowany przez setki ludzi. Trasa oraz parkingi w ok???? rezerwatu s?? oblegane, dlatego warto zaplanowa?? wypad tutaj w tygodniu lub w godzinach rannych aby unikn???? t??oku. Jednak dla os??b, kt??re wybieraj?? si?? tutaj na niedzielny spacer trasa, kt??r?? zaproponowa??em mo??e by?? alternatyw??,dzi??ki niej poza ??nie??yc?? zobaczymy kilka innych mniej ucz??szczanych miejsc.");
        val s5 = Sport("Rezerwat Meteoryt Morasko", "Wycieczka piesza do Rezerwatu Meteoryt Morasko.\n" +
                "Znajduje si?? on w p????nocnej cz????ci Poznania, na Morasku i graniczy bezpo??rednio z Suchym Lasem. Na jego terenie mieszcz?? si?? kratery, kt??re zdaniem wi??kszo??ci badaczy powsta??y w wyniku upadku meteorytu Morasko ok. 5 tys. lat temu. Polecam to miejsce.");
        val s6 = Sport("??cie??ka Dolina Samy", "Mostki przerzucone nad rzek??, pi??kna zielona ??cie??ka odchodz??ca od g????wnego szlaku, poro??ni??ta paprociami i p??o????cymi ro??linami, ciekawa konstrukcja/sza??as z ga????zi. Dzieci podczas wycieczki obserwowa??y mr??wki wspinaj??ce si?? jak po sznurku na drzewo, sprawdza??y jak mocno k??uj?? kolce akacji, biega??y po ??cie??ce narysowanej patykiem, rzuca??y kamyki do wody, przygl??da??y si?? ??ladom pozostawionym przez sarenk?? i??? przesadza??y ro??liny na ??cie??ce");
        val s7 = Sport("Dolina Cybiny", "O ile sama ulica ??? Dolina Cybiny ??? zdaje si?? by?? do???? cz??sto ucz??szczana, o tyle dalszy odcinek (na mapie z prawej strony) zdaje si?? by?? znaczenie rzadziej odwiedzany (mo??e by do???? g??sto poro??ni??ty ro??linno??ci??). Jeziorko ogl??da si?? z pewnego oddalenia, jest nisko po??o??one. Zamieszkuje je sporo ptak??w.");
        val s8 = Sport("Radojewo", "Z pewno??ci?? pofalowana rze??ba starego parku, pozosta??o??ci po dawnej architekturze, pomniki przyrody, drewniana konstrukcja na wej??ciu, pi??kne du??e li??cie ??opianu. Gdy zdecydowali??my si?? na wyj??cie na otwart?? przestrze??, zaskoczy?? mnie z??oty kolor p??l, jego intensywno????\uD83D\uDC9B");
        val s9 = Sport("Orkowo", "Mn??stwo zieleni ??? pola, wierzby, male jeziorka. Brzeg Warty obro??ni??ty d??bami. Struga wp??ywaj??ca do Warty i bardzo ciekawy przepust drogowy ??? #nieplaczabaw ;)");
        val s10 = Sport("Rezerwat Jezioro Dr????ynek ", "Ciekawa trasa wok???? jeziora. Liczne ??lady obecno??ci bobr??w. Po??o??one jest niedaleko Kocia??kowej G??rki. Niedaleko znajduje Rezerwat Las Li??cisty w Promnie");

        val toAdd = arrayListOf(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10)

        toAdd.forEach {
            val values = ContentValues()
            values.put(KEY_NAME, it.name) // Contact Name
            values.put(KEY_PH_NO, it.details) // Contact Phone
            db.insert(TABLE_SPORTS, null, values)
        }
    }
}