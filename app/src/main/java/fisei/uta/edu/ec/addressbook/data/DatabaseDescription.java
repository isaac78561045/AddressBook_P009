// DatabaseDescription.java
// Describe el nombre de la tabla y los nombres de las columnas para la base de datos de esta aplicación,
// y otra información requerida por el ContentProvider
package fisei.uta.edu.ec.addressbook.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {
    // nombre de ContentProvider: normalmente el nombre del paquete
    public static final String AUTHORITY = "fisei.uta.edu.ec.addressbook.data";

    // URI base utilizada para interactuar con el ContentProvider
    private static final Uri BASE_CONTENT_URI =
        Uri.parse("content://" + AUTHORITY);

    // la clase anidada define el contenido de la tabla de contactos
    public static final class Contact implements BaseColumns {
        public static final String TABLE_NAME = "contacts"; // nombre de la tabla

        // Uri para la tabla de contactos
        public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        // nombres de las columnas de la tabla de contactos
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_ZIP = "zip";

        // crea un Uri para un contacto específico
        public static Uri buildContactUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
