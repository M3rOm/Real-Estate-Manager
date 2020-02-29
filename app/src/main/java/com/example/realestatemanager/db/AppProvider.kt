package com.example.realestatemanager.db

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
/*
* A public facing api for other apps to safely access our database.
* It also limits the kind of operations that other apps can perform on our database
* */
class AppProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return context?.let { RoomDB.getInstance(it)?.dao?.realEstateWithCursor }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? {
        return null
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }
}