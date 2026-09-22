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

import android.graphics.Color;
import java.util.HashSet;
import java.util.Set;

import fisei.uta.edu.ec.addressbook.data.DatabaseDescription.Contact;

public class ContactsAdapter
    extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    // interfaz implementada por ContactsFragment para responder
    // cuando el usuario toca un elemento en el RecyclerView
    public interface ContactClickListener {
        void onClick(Uri contactUri);
        void onLongClick(Uri contactUri, int position); // Para selección múltiple
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
                        if (isSelectionMode()) {
                            toggleSelection(rowID);
                        } else {
                            clickListener.onClick(Contact.buildContactUri(rowID));
                        }
                    }
                }
            );
            
            // Listener para mantener presionado (selección múltiple)
            itemView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        toggleSelection(rowID);
                        clickListener.onLongClick(Contact.buildContactUri(rowID), getAdapterPosition());
                        return true;
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
    private Set<Long> selectedItems = new HashSet<>(); // Almacena los IDs seleccionados

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
        long rowID = cursor.getLong(cursor.getColumnIndexOrThrow(Contact._ID));
        holder.setRowID(rowID);
        
        String name = cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_NAME));
        int favoriteIndex = cursor.getColumnIndex(Contact.COLUMN_FAVORITE);
        boolean isFavorite = favoriteIndex != -1 && cursor.getInt(favoriteIndex) == 1;
        
        // Muestra la estrella si es favorito
        if (isFavorite) {
            holder.textView.setText("⭐ " + name);
        } else {
            holder.textView.setText(name);
        }
        
        // Cambia el color de fondo si está seleccionado
        if (selectedItems.contains(rowID)) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    
    // Métodos para selección múltiple
    public void toggleSelection(long rowId) {
        if (selectedItems.contains(rowId)) {
            selectedItems.remove(rowId);
        } else {
            selectedItems.add(rowId);
        }
        notifyDataSetChanged();
    }
    
    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }
    
    public boolean isSelectionMode() {
        return !selectedItems.isEmpty();
    }
    
    public Set<Long> getSelectedItems() {
        return selectedItems;
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
