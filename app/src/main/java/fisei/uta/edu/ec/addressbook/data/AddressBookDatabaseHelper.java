// AddressBookDatabaseHelper.java
// Subclase de SQLiteOpenHelper que define la base de datos de la aplicación
package fisei.uta.edu.ec.addressbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fisei.uta.edu.ec.addressbook.data.DatabaseDescription.Contact;

class AddressBookDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AddressBook.db";
    private static final int DATABASE_VERSION = 2;

    // constructor
    public AddressBookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // crea la tabla de contactos cuando se crea la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL para crear la tabla de contactos
        final String CREATE_CONTACTS_TABLE =
            "CREATE TABLE " + Contact.TABLE_NAME + "(" +
            Contact._ID + " integer primary key, " +
            Contact.COLUMN_NAME + " TEXT, " +
            Contact.COLUMN_PHONE + " TEXT, " +
            Contact.COLUMN_EMAIL + " TEXT, " +
            Contact.COLUMN_STREET + " TEXT, " +
            Contact.COLUMN_CITY + " TEXT, " +
            Contact.COLUMN_STATE + " TEXT, " +
            Contact.COLUMN_ZIP + " TEXT, " +
            Contact.COLUMN_FAVORITE + " INTEGER DEFAULT 0);";
        db.execSQL(CREATE_CONTACTS_TABLE); // crea la tabla de contactos
    }

    // normalmente define cómo actualizar la base de datos cuando cambia el esquema
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + Contact.TABLE_NAME + " ADD COLUMN " + Contact.COLUMN_FAVORITE + " INTEGER DEFAULT 0;");
        }
    }
}
