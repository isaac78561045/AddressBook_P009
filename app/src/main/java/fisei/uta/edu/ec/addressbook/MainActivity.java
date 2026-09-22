// MainActivity.java
// Aloja los fragmentos de la aplicación y maneja la comunicación entre ellos
package fisei.uta.edu.ec.addressbook;

import android.content.pm.LauncherApps;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity
    implements ContactsFragment.ContactsFragmentListener,
    DetailFragment.DetailFragmentListener,
    AddEditFragment.AddEditFragmentListener {

    // clave para almacenar la Uri de un contacto en un Bundle que se pasa a un fragmento
    public static final String CONTACT_URI = "contact_uri";

    private ContactsFragment contactsFragment; // muestra la lista de contactos

    // muestra ContactsFragment cuando MainActivity se carga por primera vez
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        toolbar.setNavigationOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                if (detailFragment == null) {
                    detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.rightPaneContainer);
                }
                
                if (detailFragment != null && detailFragment.isVisible()) {
                    detailFragment.toggleFavorite();
                }
            }
        });

        // si el diseño contiene fragmentContainer, se está utilizando el diseño de teléfono;
        // crea y muestra un ContactsFragment
        if (savedInstanceState == null &&
            findViewById(R.id.fragmentContainer) != null) {
            // crea ContactsFragment
            contactsFragment = new ContactsFragment();

            // agrega el fragmento al FrameLayout
            FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, contactsFragment, "contactsFragment");
            transaction.commit(); // muestra ContactsFragment
        }
        else {
            contactsFragment =
                (ContactsFragment) getSupportFragmentManager().
                    findFragmentById(R.id.contactsFragment);
            if (contactsFragment == null) {
                contactsFragment = (ContactsFragment) getSupportFragmentManager().
                    findFragmentByTag("contactsFragment");
            }
        }
    }

    // muestra DetailFragment para el contacto seleccionado
    @Override
    public void onContactSelected(Uri contactUri) {
        if (findViewById(R.id.fragmentContainer) != null) // teléfono
            displayContact(contactUri, R.id.fragmentContainer);
        else { // tableta
            // elimina la parte superior de la pila de retroceso
            getSupportFragmentManager().popBackStack();

            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

    // muestra AddEditFragment para agregar un nuevo contacto
    @Override
    public void onAddContact() {
        if (findViewById(R.id.fragmentContainer) != null) // teléfono
            displayAddEditFragment(R.id.fragmentContainer, null);
        else // tableta
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }

    // muestra un contacto
    private void displayContact(Uri contactUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // especifica la Uri del contacto como un argumento para DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(CONTACT_URI, contactUri);
        detailFragment.setArguments(arguments);

        // usa un FragmentTransaction para mostrar DetailFragment
        FragmentTransaction transaction =
            getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // hace que DetailFragment se muestre
    }


    // muestra el fragmento para agregar un nuevo contacto o editar uno existente
    private void displayAddEditFragment(int viewID, Uri contactUri) {
        AddEditFragment addEditFragment = new AddEditFragment();

        // si se edita un contacto existente, proporciona contactUri como argumento
        if (contactUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(CONTACT_URI, contactUri);
            addEditFragment.setArguments(arguments);
        }

        // usa un FragmentTransaction para mostrar AddEditFragment
        FragmentTransaction transaction =
            getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // hace que AddEditFragment se muestre
    }

    // regresa a la lista de contactos cuando se elimina el contacto mostrado
    @Override
    public void onContactDeleted() {
        // elimina la parte superior de la pila de retroceso
        getSupportFragmentManager().popBackStack();
        if (contactsFragment != null) {
            contactsFragment.updateContactList(); // actualiza los contactos
        }
    }

    // muestra AddEditFragment para editar un contacto existente
    @Override
    public void onEditContact(Uri contactUri) {
        if (findViewById(R.id.fragmentContainer) != null) // teléfono
            displayAddEditFragment(R.id.fragmentContainer, contactUri);
        else // tableta
            displayAddEditFragment(R.id.rightPaneContainer, contactUri);
    }

    // actualiza la interfaz de usuario después de guardar un contacto nuevo o editado
    @Override
    public void onAddEditCompleted(Uri contactUri) {
        // elimina la parte superior de la pila de retroceso
        getSupportFragmentManager().popBackStack();
        if (contactsFragment != null) {
            contactsFragment.updateContactList(); // actualiza los contactos
        }

        if (findViewById(R.id.fragmentContainer) == null) { // tableta
            // elimina la parte superior de la pila de retroceso
            getSupportFragmentManager().popBackStack();

            // en tableta, muestra el contacto que se acaba de agregar o editar
            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

    public void setFavoriteIcon(boolean isFavorite) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            if (isFavorite) {
                toolbar.setNavigationIcon(android.R.drawable.btn_star_big_on);
            } else {
                toolbar.setNavigationIcon(android.R.drawable.btn_star_big_off);
            }
        }
    }
}
