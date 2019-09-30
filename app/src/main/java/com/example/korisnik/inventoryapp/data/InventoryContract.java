package com.example.korisnik.inventoryapp.data;
import android.provider.BaseColumns;
import android.net.Uri;
import android.content.ContentResolver;

public final class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {}

    // Help to identify the Content Provider
    public static final String CONTENT_AUTHORITY = "com.example.korisnik.inventoryapp";

    //Create the base of all URI's
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible path
    public static final String PATH_INVENTORY = "inventory";

    // Inner class that defines constant values for the item database table.
    public static final class InventoryEntry implements BaseColumns {

        //Content URi to access the data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        // The MIME type of the {@link #CONTENT_URI} for a list of product.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        // The MIME type of the {@link #CONTENT_URI} for a single product.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        // Name of database table
        public final static String TABLE_NAME = "inventory";

        //Unique ID number for the product (Type: INTEGER)
        public final static String _ID = BaseColumns._ID;

        // Name of the product (Type: TEXT)
        public final static String COLUMN_PRODUCT_NAME ="productName";

        // Type: INTEGER
        public final static String COLUMN_PRODUCT_PRICE = "productPrice";

        // Type: INTEGER
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        // Name of the supplier (Type: TEXT)
        public final static String COLUMN_SUPPLIER_NAME ="supplierName";

        // Type: TEXT
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplierPhoneNumber";
    }
}
