package com.example.quarantine.firebase

import android.os.Handler
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.common.collect.Lists
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source

class Firestore {
    private val db: FirebaseFirestore? = FirebaseFirestore.getInstance()
    private val TAG = "FIREBASE_FIRESTORE"
    /**
     * Adds a new document to an existing/new collection
     * @param collection: Collection to insert data into
     * @param objects: Type Object for data to be inserted into the collection
     * @return a pending task object. Should be implemented with a click listener.
     */
    fun add(collection:String, objects: Any):Task<DocumentReference>
    {
        val data:HashMap<String, Any> = hashMapOf()
        data[collection] = objects
        return db!!.collection(collection).add(objects)
    }

    /**
     * Gets all the documents from a specific collection
     * @param collection: Collection to get all data from
     * @return a pending task object. Should be implemented with a click listener.
     */
    fun get(collection: String):Task<QuerySnapshot>
    {
        return db!!.collection(collection).get()
    }

    /**
     * Deletes a specific document from a collection
     * @param collection: The collection to delete document from
     * @param documentID: The ID of the document from the collection to delete it from
     * @return True if successful, otherwise false for all other errors/exceptions
     */
    fun delete(collection: String, documentID:String): Task<Void> {
        return db!!.collection(collection).document(documentID).delete()
    }

    /**
     * Updates a specific document from a collection
     * @param collection: The collection to Update document
     * @param documentID: The ID of the document from the collection to Update it from
     * @param data: Map of key and value to change
     * @return True if successful, otherwise false for all other errors/exceptions
     */
    fun update(collection: String, documentID: String, data:HashMap<String, Any>):Task<Void>
    {
        return db!!.collection(collection).document(documentID).update(data)
    }

    companion object
    {
        fun newInstance() = Firestore()
    }
}