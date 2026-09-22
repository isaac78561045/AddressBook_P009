// AddEditFragment.java
// Fragmento para agregar un nuevo contacto o editar uno existente
package fisei.uta.edu.ec.addressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import fisei.uta.edu.ec.addressbook.data.DatabaseDescription.Contact;

public class AddEditFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    // define el metodo de devolución de llamada implementado por MainActivity
    public interface AddEditFragmentListener {
        // llamado cuando el contacto se guarda
        void onAddEditCompleted(Uri contactUri);
    }

    // constante utilizada para identificar el Loader
    private static final int CONTACT_LOADER = 0;

    private AddEditFragmentListener listener; // MainActivity
    private Uri contactUri; // Uri del contacto seleccionado
    private boolean addingNewContact = true; // agregando (true) o editando

    // EditTexts para la información del contacto
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout streetTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout zipTextInputLayout;
    private FloatingActionButton saveContactFAB;

    private CoordinatorLayout coordinatorLayout; // utilizado con SnackBars

    // establece AddEditFragmentListener cuando se adjunta el Fragment
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }

    // elimina AddEditFragmentListener cuando se desconecta el Fragment
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // llamado cuando se necesita crear la vista del Fragment
    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // el fragmento tiene elementos de menú para mostrar

        // infla la interfaz de usuario y obtiene referencias a los EditTexts
        View view =
            inflater.inflate(R.layout.fragment_add_edit, container, false);
        nameTextInputLayout =
            (TextInputLayout) view.findViewById(R.id.nameTextInputLayout);
        nameTextInputLayout.getEditText().addTextChangedListener(
            nameChangedListener);
        phoneTextInputLayout =
            (TextInputLayout) view.findViewById(R.id.phoneTextInputLayout);
        emailTextInputLayout =
            (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);
        streetTextInputLayout =
            (TextInputLayout) view.findViewById(R.id.streetTextInputLayout);
        cityTextInputLayout =
            (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        stateTextInputLayout =
            (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        zipTextInputLayout =
            (TextInputLayout) view.findViewById(R.id.zipTextInputLayout);


        // establece el detector de eventos para el FloatingActionButton
        saveContactFAB = (FloatingActionButton) view.findViewById(
            R.id.saveFloatingActionButton);
        saveContactFAB.setOnClickListener(saveContactButtonClicked);
        updateSaveButtonFAB();

        // utilizado para mostrar SnackBars con mensajes breves
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(
            R.id.coordinatorLayout);

        Bundle arguments = getArguments(); // null si se crea un nuevo contacto

        if (arguments != null) {
            addingNewContact = false;
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        }

        // si se edita un contacto existente, crea un Loader para obtener el contacto
        if (contactUri != null)
            LoaderManager.getInstance(this).initLoader(
                CONTACT_LOADER, null, this);

        return view;
    }

    // detecta cuándo cambia el texto en el EditText de nameTextInputLayout
    // para ocultar o mostrar saveButtonFAB
    private final TextWatcher nameChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {}

        // llamado cuando el texto en nameTextInputLayout cambia
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
            int count) {
            updateSaveButtonFAB();
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    // muestra saveButtonFAB solo si el nombre no está vacío
    private void updateSaveButtonFAB() {
        String input =
            nameTextInputLayout.getEditText().getText().toString();

        // si hay un nombre para el contacto, muestra el FloatingActionButton
        if (input.trim().length() != 0)
            saveContactFAB.show();
        else
            saveContactFAB.hide();
    }

    // responde al evento generado cuando el usuario guarda un contacto
    private final View.OnClickListener saveContactButtonClicked =
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // oculta el teclado virtual
                ((InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    getView().getWindowToken(), 0);
                saveContact(); // guarda el contacto en la base de datos
            }
        };

    // guarda la información del contacto en la base de datos
    private void saveContact() {
        // crea un objeto ContentValues que contiene los pares clave-valor del contacto
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contact.COLUMN_NAME,
            nameTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_PHONE,
            phoneTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_EMAIL,
            emailTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_STREET,
            streetTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_CITY,
            cityTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_STATE,
            stateTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_ZIP,
            zipTextInputLayout.getEditText().getText().toString());

        if (addingNewContact) {
            // usa el ContentResolver de Activity para invocar
            // insertar en AddressBookContentProvider
            Uri newContactUri = getActivity().getContentResolver().insert(
                Contact.CONTENT_URI, contentValues);

            if (newContactUri != null) {
                Snackbar.make(coordinatorLayout,
                    R.string.contact_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newContactUri);
            }
            else {
                Snackbar.make(coordinatorLayout,
                    R.string.contact_not_added, Snackbar.LENGTH_LONG).show();
            }
        }
        else {
            // usa el ContentResolver de Activity para invocar
            // actualizar en AddressBookContentProvider
            int updatedRows = getActivity().getContentResolver().update(
                contactUri, contentValues, null, null);

            if (updatedRows > 0) {
                listener.onAddEditCompleted(contactUri);
                Snackbar.make(coordinatorLayout,
                    R.string.contact_updated, Snackbar.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(coordinatorLayout,
                    R.string.contact_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // llamado por LoaderManager para crear un Loader
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // crea un CursorLoader apropiado basado en el argumento id;
        // solo un Loader en este fragmento, por lo que el switch es innecesario
        switch (id) {
            case CONTACT_LOADER:
                return new CursorLoader(getActivity(),
                    contactUri, // Uri del contacto a mostrar
                    null, // la proyección null devuelve todas las columnas
                    null, // la selección null devuelve todas las filas
                    null, // sin argumentos de selección
                    null); // orden de clasificación
            default:
                return null;
        }
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

            // llena los EditTexts con los datos recuperados
            nameTextInputLayout.getEditText().setText(
                data.getString(nameIndex));
            phoneTextInputLayout.getEditText().setText(
                data.getString(phoneIndex));
            emailTextInputLayout.getEditText().setText(
                data.getString(emailIndex));
            streetTextInputLayout.getEditText().setText(
                data.getString(streetIndex));
            cityTextInputLayout.getEditText().setText(
                data.getString(cityIndex));
            stateTextInputLayout.getEditText().setText(
                data.getString(stateIndex));
            zipTextInputLayout.getEditText().setText(
                data.getString(zipIndex));

            updateSaveButtonFAB();
        }
    }

    // llamado por LoaderManager cuando el Loader se está reiniciando
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { }
}
