package com.example.korisnik.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.korisnik.inventoryapp.data.InventoryContract.InventoryEntry;

//Displays list of product
public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the data loader
    private static final int PRODUCT_LOADER = 0;

    // Adapter for the ListView
    InventoryCursorAdapter mCursorAdapter;

    //Track the quantity for product
    int saleQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with data
        ListView productListView = findViewById(R.id.product_list_view);
        // Setup an Adapter to create a list item for each row of data in the Cursor.
        mCursorAdapter = new InventoryCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        // Setup the item click listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Form the content URI that represents the product that was clicked on,
                // by appending the "id" onto the {@link InventoryEntry#CONTENT_URI}.
                Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                // Set the URI to intent
                intent.setData(currentProductUri);
                // Launch the {@link EditorActivity}
                startActivity(intent);
            }
        });

        //Initialize loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    // After finishing EditorActivity and returning to Main, this will display fresh data
    @Override
    protected void onStart() {
        super.onStart();
    }

    // Inflate the menu options from the res/menu/menu_catalog.xml file.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    //Method to insert hardcoded data into the database. For debugging purposes only.
    private void insertPet() {

        // Create a ContentValues object where column names are the keys,
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, "Nemo");
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, 10);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, 3);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Bugs Bunny");
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "0000000000");

        // Insert a new row for Nemo product in the database
        // Receive the new content URI that will allow us to access Nemo's data in the future.
        getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    //Method to delete all in the database and show toast msg
    private void deleteAll() {
        int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
        Toast.makeText(this, rowsDeleted + getString(R.string.catalog_deleteSuccessful),
                Toast.LENGTH_SHORT).show();
    }

    // Prompt the user to confirm that they want to delete all data.
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.catalog_deleteDialogMsg);
        builder.setPositiveButton(R.string.editor_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete all product from the list.
                deleteAll();
            }
        });
        builder.setNegativeButton(R.string.editor_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog and continue on CatalogActivity
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                InventoryEntry.CONTENT_URI,     // Provider content URI to query
                projection,                     // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,               // No selection arguments
                null);                 // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update with this new cursor containing updated data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    //Method to handle sale Button
    public void saleProduct(long id, int quantity) {
        //Decrease the quantity of the product by one if is not 0

        // Decrement item quantity
        if (quantity > 0) {
            quantity -= 1;
            // Construct new uri and content values
            Uri updateUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            int rowsUpdated = getContentResolver().update(
                    updateUri,
                    values,
                    null,
                    null);
            if (rowsUpdated != 1) {
                Toast.makeText(this, R.string.catalog_saleError, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.catalog_saleSuccessful, Toast.LENGTH_SHORT).show();
            }

        } else {
            // Quantity is 0 --> msg:No more product
            Toast.makeText(this, R.string.catalog_saleZeroMsg, Toast.LENGTH_LONG).show();
        }
    }
}
