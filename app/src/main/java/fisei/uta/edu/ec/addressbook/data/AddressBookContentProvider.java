// AddressBookContentProvider.java
// Subclase de ContentProvider para manipular la base de datos de la aplicación
package fisei.uta.edu.ec.addressbook.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import fisei.uta.edu.ec.addressbook.R;
import fisei.uta.edu.ec.addressbook.data.DatabaseDescription.Contact;

public class AddressBookContentProvider extends ContentProvider {
    // utilizado para acceder a la base de datos
    private AddressBookDatabaseHelper dbHelper;

    // UriMatcher ayuda a ContentProvider a determinar la operación a realizar
    private static final UriMatcher uriMatcher =
        new UriMatcher(UriMatcher.NO_MATCH);

    // constantes utilizadas con UriMatcher para determinar la operación a realizar
    private static final int ONE_CONTACT = 1; // manipular un contacto
    private static final int CONTACTS = 2; // manipular la tabla de contactos

    // bloque estático para configurar el UriMatcher de este ContentProvider
    static {
        // Uri para el Contacto con el id especificado (#)
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
            Contact.TABLE_NAME + "/#", ONE_CONTACT);

        // Uri para la tabla Contactos
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
            Contact.TABLE_NAME, CONTACTS);
    }

    // llamado cuando se crea AddressBookContentProvider
    @Override
    public boolean onCreate() {
        // crea el AddressBookDatabaseHelper
        dbHelper = new AddressBookDatabaseHelper(getContext());
        return true; // ContentProvider creado con éxito
    }

    // método requerido: No se usa en esta aplicación, así que devolvemos null
    @Override
    public String getType(Uri uri) {
        return null;
    }

    // consultar la base de datos
    @Override
    public Cursor query(Uri uri, String[] projection,
        String selection, String[] selectionArgs, String sortOrder) {

        // crea SQLiteQueryBuilder para consultar la tabla de contactos
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contact.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT: // se seleccionará el contacto con el id especificado
                queryBuilder.appendWhere(
                    Contact._ID + "=" + uri.getLastPathSegment());
                break;
            case CONTACTS: // se seleccionarán todos los contactos
                break;
            default:
                throw new UnsupportedOperationException(
                    getContext().getString(R.string.invalid_query_uri) + uri);
        }

        // ejecuta la consulta para seleccionar uno o todos los contactos
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
            projection, selection, selectionArgs, null, null, sortOrder);

        // configura para observar cambios en el contenido
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // inserta un nuevo contacto en la base de datos
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newContactUri = null;

        switch (uriMatcher.match(uri)) {
            case CONTACTS:
                // inserta el nuevo contacto: el éxito produce el id de la fila del nuevo contacto
                long rowId = dbHelper.getWritableDatabase().insert(
                    Contact.TABLE_NAME, null, values);

                // si el contacto fue insertado, crea una Uri apropiada;
                // de lo contrario, lanza una excepción
                if (rowId > 0) { // los IDs de las filas en SQLite comienzan en 1
                    newContactUri = Contact.buildContactUri(rowId);

                    // notifica a los observadores que la base de datos cambió
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                else
                    throw new SQLException(
                        getContext().getString(R.string.insert_failed) + uri);
                break;
            default:
                throw new UnsupportedOperationException(
                    getContext().getString(R.string.invalid_insert_uri) + uri);
        }

        return newContactUri;
    }

    // actualiza un contacto existente en la base de datos
    @Override
    public int update(Uri uri, ContentValues values,
        String selection, String[] selectionArgs) {
        int numberOfRowsUpdated; // 1 si la actualización fue exitosa; 0 en caso contrario

        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                // obtiene de la uri el id del contacto a actualizar
                String id = uri.getLastPathSegment();

                // actualiza el contacto
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                    Contact.TABLE_NAME, values, Contact._ID + "=" + id,
                    selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                    getContext().getString(R.string.invalid_update_uri) + uri);
        }

        // si se hicieron cambios, notifica a los observadores que la base de datos cambió
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(Contact.CONTENT_URI, null);
        }

        return numberOfRowsUpdated;
    }

    // elimina un contacto existente de la base de datos
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                // obtiene de la uri el id del contacto a actualizar
                String id = uri.getLastPathSegment();

                // elimina el contacto
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                    Contact.TABLE_NAME, Contact._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                    getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        // notifica a los observadores que la base de datos cambió
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(Contact.CONTENT_URI, null);
        }

        return numberOfRowsDeleted;
    }
}
