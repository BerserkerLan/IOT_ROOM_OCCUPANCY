package no.nordicsemi.android.blinky.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*

data class DataPoint(val id: String, val direction: String, val timestamp: String)


@Suppress("unused")
class LogDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    /*
     This is databaseInstance short global explanation how the db functions have been made atomic - the reason atomicity is important is to
     maintain the integrity of the app - no database, the app will become databaseInstance waste of space :-)
     try {
         db.beginTransaction() //Start the databaseTransaction
         //Does the database work whatever it may be
         db.setTransactionSuccessful() //Set the transaction as successful, hence when db.endTransaction() is called, the changes will be applied

     } catch(e :Exception){
         /*If we get into this block something has happened, finally will be cancelled, but as we didn't call
         db.setTransactionSuccessful() changes will not be made, hence atomic operation */
     } finally {
        db.endTransaction() // End the database transaction, make changes iff db.setTransactionSuccessful()
     }
     */

    @SuppressLint("SimpleDateFormat")
    private fun getTimeStamp(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        return dateFormat.format(date)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME (id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,direction TEXT,timestamp TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


    /**
     * This function will add to the database, atomically of course
     */

    fun addToDatabase(direction: String): Boolean {
        var returnValue = false
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            val contentValues = ContentValues()
            //COL_1 not required as the database will auto increment itself
            contentValues.put(COL_2, direction)
            contentValues.put(COL_3, getTimeStamp())
            db.insert(TABLE_NAME, null, contentValues)
            db.setTransactionSuccessful()
            returnValue = true
        } catch (e: SQLException) {

        } finally {
            db.endTransaction()
            db.close()
        }
        return returnValue
    }

    /**
     * This function will allow you to get everything in the database currently
     */

    fun getEverything(): ArrayList<DataPoint> {
        val db = this.writableDatabase
        val everything = ArrayList<DataPoint>()
        db.rawQuery("select * from $TABLE_NAME", null).use {
            while (it.moveToNext()) {
                try {
                    everything.add(DataPoint(it.getString(it.getColumnIndex("id")), it.getString(it.getColumnIndex("direction")), it.getString(it.getColumnIndex("timestamp"))))
                } catch (e: Exception) {

                }
            }
        }
        db.close()
        return everything
    }

    /**
     * This function will delete an individual item from the database
     */

    fun deleteData(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "id = ?", arrayOf(id))
    }


    /**
     * This function will delete the ENTIRE database, very dangerous
     */

    fun deleteEntireDB() {
        //This is databaseInstance very dangerous function, it will delete the entire database
        val db = this.writableDatabase
        try {
            db.beginTransaction()
            db.delete(TABLE_NAME, null, null)
            db.setTransactionSuccessful()
        } catch (e: SQLException) {
            // do some error handling
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        private const val DATABASE_NAME = "theDatabase.db"
        private const val TABLE_NAME = "TheDatabase"
        private const val COL_1 = "id"
        private const val COL_2 = "direction"
        private const val COL_3 = "timestamp"
    }
}