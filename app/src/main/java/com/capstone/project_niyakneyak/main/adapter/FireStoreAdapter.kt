package com.capstone.project_niyakneyak.main.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
/**
 * Abstract Class to show data in a single firebase collection
 * Use [FireStoreAdapter.startListening] in {onStart} which is an override Function
 * Use [FireStoreAdapter.stopListening] in {onStop} which is an override Function
 * Use [FireStoreAdapter.setQuery] when you want to filter data list
 * */
abstract class FireStoreAdapter<VH: RecyclerView.ViewHolder>(private var query: Query?) :
    RecyclerView.Adapter<VH>(),
    EventListener<QuerySnapshot> {
    private var registration: ListenerRegistration? = null
    private val snapshots = ArrayList<DocumentSnapshot>()

    fun startListening() {
        if (registration == null) {
            registration = query?.addSnapshotListener(this)
        }
    }

    fun stopListening() {
        registration?.remove()
        registration = null

        snapshots.clear()
        notifyDataSetChanged()
    }

    fun setQuery(query: Query) {
        // Stop listening
        stopListening()

        // Clear existing data
        snapshots.clear()
        notifyDataSetChanged()

        // Listen to new query
        this.query = query
        startListening()
    }

    open fun onError(e: FirebaseFirestoreException) {
        Log.w(TAG, "onError", e)
    }

    open fun onDataChanged() {}

    override fun onEvent(documentSnapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if(error != null){
            Log.w(TAG, "onEvent:error", error)
            return
        }

        if(documentSnapshot != null){
            for(change in documentSnapshot.documentChanges){
                when(change.type){
                    DocumentChange.Type.ADDED -> {
                        onDocumentAdded(change)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        onDocumentModified(change)
                    }
                    DocumentChange.Type.REMOVED -> {
                        onDocumentRemoved(change)
                    }
                }
            }
            onDataChanged()
        }
    }

    private fun onDocumentAdded(change: DocumentChange) {
        snapshots.add(change.newIndex, change.document)
        Log.w("Adapter", "Data Added!")
        notifyItemInserted(change.newIndex)
    }

    private fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            // Item changed but remained in same position
            snapshots[change.oldIndex] = change.document
            notifyItemChanged(change.oldIndex)
        } else {
            // Item changed and changed position
            snapshots.removeAt(change.oldIndex)
            snapshots.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    private fun onDocumentRemoved(change: DocumentChange) {
        snapshots.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

    override fun getItemCount(): Int {
        return snapshots.size
    }

    protected fun getSnapshot(index: Int): DocumentSnapshot {
        return snapshots[index]
    }

    companion object {
        private const val TAG = "FireStoreAdapter"
    }
}