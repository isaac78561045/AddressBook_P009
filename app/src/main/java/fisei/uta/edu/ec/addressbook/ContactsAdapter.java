// ContactsAdapter.java
// Subclase de RecyclerView.Adapter que vincula los contactos al RecyclerView
package fisei.uta.edu.ec.addressbook;

import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import fisei.uta.edu.ec.addressbook.data.DatabaseDescription.Contact;

public class ContactsAdapter
    extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    // interfaz implementada por ContactsFragment para responder
    // cuando el usuario toca un elemento en el RecyclerView
    public interface ContactClickListener {
        void onClick(Uri contactUri);
    }
    public class ContactoFavorito
            extends RecyclerView.Adapter<fisei.uta.edu.ec.addressbook.ContactsAdapter.ContactoFavorito> {

        // interfaz implementada por ContactsFragment para responder
        // cuando el usuario toca un elemento en el RecyclerView
        public interface ContactClickListener {
            void onClick(Uri contactUri);
        }
    // subclase anidada de RecyclerView.ViewHolder utilizada para implementar
    // el patrón view-holder en el contexto de un RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        private long rowID;

        // configura el ViewHolder de un elemento del RecyclerView
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);

            // adjunta el listener al itemView
            itemView.setOnClickListener(
                new View.OnClickListener() {
                    // se ejecuta cuando se hace clic en el contacto de este ViewHolder
                    @Override
                    public void onClick(View view) {
                        clickListener.onClick(Contact.buildContactUri(rowID));
                    }
                }
            );
        }

        // establece el ID de la fila de la base de datos para el contacto en este ViewHolder
        public void setRowID(long rowID) {
            this.rowID = rowID;
        }
    }

    // variables de instancia de ContactsAdapter
    private Cursor cursor = null;
    private final ContactClickListener clickListener;

    // constructor
    public ContactsAdapter(ContactClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // configura un nuevo elemento de lista y su ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infla el diseño android.R.layout.simple_list_item_1
        View view = LayoutInflater.from(parent.getContext()).inflate(
            android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view); // devuelve el ViewHolder del elemento actual
    }

    // establece el texto del elemento de la lista para mostrar el nombre del contacto
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setRowID(cursor.getLong(cursor.getColumnIndexOrThrow(Contact._ID)));
        holder.textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(
            Contact.COLUMN_NAME)));
    }

    // devuelve el número de elementos que el adaptador vincula
    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    // intercambia el Cursor actual de este adaptador por uno nuevo
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
