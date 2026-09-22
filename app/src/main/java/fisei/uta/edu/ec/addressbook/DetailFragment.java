// DetailFragment.java
// Subclase de fragmento que muestra los detalles de un contacto
package fisei.uta.edu.ec.addressbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import fisei.uta.edu.ec.addressbook.data.DatabaseDescription.Contact;

public class DetailFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    // métodos de devolución de llamada implementados por MainActivity
    public interface DetailFragmentListener {
        void onContactDeleted(); // llamado cuando se elimina un contacto

        // pasar Uri del contacto a editar a DetailFragmentListener
        void onEditContact(Uri contactUri);
    }

    private static final int CONTACT_LOADER = 0; // identifica el Loader

    private DetailFragmentListener listener; // MainActivity
    private Uri contactUri; // Uri del contacto seleccionado

    private TextView nameTextView; // muestra el nombre del contacto
    private TextView phoneTextView; // muestra el teléfono del contacto
    private TextView emailTextView; // muestra el correo electrónico del contacto
    private TextView streetTextView; // muestra la calle del contacto
    private TextView cityTextView; // muestra la ciudad del contacto
    private TextView stateTextView; // muestra el estado/provincia del contacto
    private TextView zipTextView; // muestra el código postal del contacto

    // establece DetailFragmentListener cuando se adjunta el fragmento
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }

    // elimina DetailFragmentListener cuando se desconecta el fragmento
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // llamado cuando se necesita crear la vista de DetailFragmentListener
    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // este fragmento tiene elementos de menú para mostrar

        // obtiene el Bundle de argumentos y luego extrae la Uri del contacto
        Bundle arguments = getArguments();

        if (arguments != null)
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);

        // infla el diseño de DetailFragment
        View view =
            inflater.inflate(R.layout.fragment_details, container, false);

        // obtiene los TextViews
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
        emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        streetTextView = (TextView) view.findViewById(R.id.streetTextView);
        cityTextView = (TextView) view.findViewById(R.id.cityTextView);
        stateTextView = (TextView) view.findViewById(R.id.stateTextView);
        zipTextView = (TextView) view.findViewById(R.id.zipTextView);

        // carga el contacto
        LoaderManager.getInstance(this).initLoader(
            CONTACT_LOADER, null, this);
        return view;
    }


    // muestra los elem
    // entos de menú de este fragmento
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    // maneja las selecciones de elementos de menú
    // NOTA: los valores de R.id no están garantizados como constantes en tiempo de compilación
    // en esta versión del plugin de Android Gradle del proyecto, así que usamos
    // if/else en lugar de una declaración switch (que requiere constantes).
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_edit) {
            listener.onEditContact(contactUri); // pasa la Uri al listener
            return true;
        }
        else if (itemId == R.id.action_delete) {
            deleteContact();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // elimina un contacto
    private void deleteContact() {
        if (getActivity() == null) return;

        // crea un AlertDialog Builder y muestra el diálogo de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_title);
        builder.setMessage(R.string.confirm_message);

        // proporciona un botón Aceptar que elimina el contacto
        builder.setPositiveButton(R.string.button_delete,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int button) {
                    if (getActivity() != null && contactUri != null) {
                        // usa el ContentResolver de Activity para invocar
                        // eliminar en AddressBookContentProvider
                        getActivity().getContentResolver().delete(
                            contactUri, null, null);
                    }
                    if (listener != null) {
                        listener.onContactDeleted(); // notifica al listener
                    }
                }
            }
        );

        builder.setNegativeButton(R.string.button_cancel, null);
        builder.create().show(); // devuelve y muestra el AlertDialog
    }

    // llamado por LoaderManager para crear un Loader
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // crea un CursorLoader apropiado basado en el argumento id;
        // solo un Loader en este fragmento, por lo que el if es innecesario
        CursorLoader cursorLoader;

        if (id == CONTACT_LOADER) {
            cursorLoader = new CursorLoader(getActivity(),
                contactUri, // Uri del contacto a mostrar
                null, // la proyección null devuelve todas las columnas
                null, // la selección null devuelve todas las filas
                null, // sin argumentos de selección
                null); // orden de clasificación
        }
        else {
            cursorLoader = null;
        }

        return cursorLoader;
    }

    // llamado por LoaderManager cuando se completa la carga
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // si el contacto existe en la base de datos, muestra sus datos
        if (data != null && data.moveToFirst()) {
            // obtiene el índice de la columna para cada elemento de datos
            int nameIndex = data.getColumnIndexOrThrow(Contact.COLUMN_NAME);
            int phoneIndex = data.getColumnIndexOrThrow(Contact.COLUMN_PHONE);
            int emailIndex = data.getColumnIndexOrThrow(Contact.COLUMN_EMAIL);
            int streetIndex = data.getColumnIndexOrThrow(Contact.COLUMN_STREET);
            int cityIndex = data.getColumnIndexOrThrow(Contact.COLUMN_CITY);
            int stateIndex = data.getColumnIndexOrThrow(Contact.COLUMN_STATE);
            int zipIndex = data.getColumnIndexOrThrow(Contact.COLUMN_ZIP);

            // llena los TextViews con los datos recuperados
            nameTextView.setText(data.getString(nameIndex));
            phoneTextView.setText(data.getString(phoneIndex));
            emailTextView.setText(data.getString(emailIndex));
            streetTextView.setText(data.getString(streetIndex));
            cityTextView.setText(data.getString(cityIndex));
            stateTextView.setText(data.getString(stateIndex));
            zipTextView.setText(data.getString(zipIndex));
        }
    }

    // llamado por LoaderManager cuando el Loader se está reiniciando
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { }
}
