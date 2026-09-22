// ContactsFragment.java
// Subclase de Fragmento que muestra la lista alfabética de nombres de contactos
package fisei.uta.edu.ec.addressbook;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import fisei.uta.edu.ec.addressbook.data.DatabaseDescription.Contact;

public class ContactsFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    // método de devolución de llamada implementado por MainActivity
    public interface ContactsFragmentListener {
        // llamado cuando se selecciona un contacto
        void onContactSelected(Uri contactUri);

        // llamado cuando se presiona el botón de agregar
        void onAddContact();
    }

    private static final int CONTACTS_LOADER = 0; // identifica el Loader

    // utilizado para informar a MainActivity cuando se selecciona un contacto
    private ContactsFragmentListener listener;

    private ContactsAdapter contactsAdapter; // adaptador para recyclerView

    // configura la interfaz de usuario de este fragmento
    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // el fragmento tiene elementos de menú para mostrar

        // infla la interfaz de usuario y obtiene una referencia al RecyclerView
        View view = inflater.inflate(
            R.layout.fragment_contacts, container, false);
        RecyclerView recyclerView =
            (RecyclerView) view.findViewById(R.id.recyclerView);

        // recyclerView debería mostrar elementos en una lista vertical
        recyclerView.setLayoutManager(
            new LinearLayoutManager(getActivity().getBaseContext()));

        // crea el adaptador del recyclerView y el detector de clics de elementos
        contactsAdapter = new ContactsAdapter(
            new ContactsAdapter.ContactClickListener() {
                @Override
                public void onClick(Uri contactUri) {
                    listener.onContactSelected(contactUri);
                }
            }
        );
        recyclerView.setAdapter(contactsAdapter); // establece el adaptador

        // adjunta un ItemDecorator personalizado para dibujar divisores entre elementos de la lista
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        // mejora el rendimiento si el tamaño del diseño del RecyclerView nunca cambia
        recyclerView.setHasFixedSize(true);

        // obtiene el FloatingActionButton y configura su detector
        FloatingActionButton addButton =
            (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(
            new View.OnClickListener() {
                // muestra el AddEditFragment cuando se toca el FAB
                @Override
                public void onClick(View view) {
                    listener.onAddContact();
                }
            }
        );

        return view;
    }

    // establece ContactsFragmentListener cuando se adjunta el fragmento
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (ContactsFragmentListener) context;
    }

    // elimina ContactsFragmentListener cuando se desconecta el fragmento
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // inicializa un Loader cuando se crea la actividad de este fragmento
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager.getInstance(this).initLoader(CONTACTS_LOADER, null, this);
    }
    @Override
    public void onActivityFavotito(budle saveInstanceState){
        super.onCreateView(saveInstanceState);
        LoaderManager.GetInstance(ower:this). initLoader(CONTACTS_LOADER, args:null, setEnterSharedElementCallback();

    }

    // llamado desde MainActivity cuando otro fragmento actualiza la base de datos
    public void updateContactList() {
        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged();
        }
    }

    // llamado por LoaderManager para crear un Loader
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // crea un CursorLoader apropiado basado en el argumento id;
        // solo un Loader en este fragmento, por lo que el switch es innecesario
        switch (id) {
            case CONTACTS_LOADER:
                return new CursorLoader(getActivity(),
                    Contact.CONTENT_URI, // Uri de la tabla de contactos
                    null, // la proyección null devuelve todas las columnas
                    null, // la selección null devuelve todas las filas
                    null, // sin argumentos de selección
                    Contact.COLUMN_NAME + " COLLATE NOCASE ASC"); // orden de clasificación
            default:
                return null;
        }
    }

    // llamado por LoaderManager cuando se completa la carga
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        contactsAdapter.swapCursor(data);
    }

    // llamado por LoaderManager cuando el Loader se está reiniciando
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        contactsAdapter.swapCursor(null);
    }
}
