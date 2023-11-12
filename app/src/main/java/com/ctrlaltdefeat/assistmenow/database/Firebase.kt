package com.ctrlaltdefeat.assistmenow.database

import android.app.Application
import android.util.Log
import com.ctrlaltdefeat.assistmenow.data.Models
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firestore.v1.Value
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

object Firebase {
    private var currentUser: FirebaseUser? = null
    private var currentUserRole: String = "donor"

    private fun generateUID(): String {
        val uuid = UUID.randomUUID()

        return String.format(
            "%04x-%04x-%04x-%04x",
            uuid.mostSignificantBits ushr 48,
            uuid.mostSignificantBits ushr 32 and 0xFFFF,
            uuid.mostSignificantBits ushr 16 and 0xFFFF,
            uuid.mostSignificantBits and 0xFFFF
        )
    }

    private fun userSetRole(role: String, callback: (Boolean, String) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("users/${currentUser?.uid}/role")

        reference.setValue(role).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentUserRole = role

                callback(true, "User Role Set")
            } else {
                callback(false, "Failed to Set User Role")
            }
        }
    }

    fun userSignUp(email: String, password: String, role: String, callback: (Boolean, String) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentUser = FirebaseAuth.getInstance().currentUser

                userSetRole(role) { success, message ->
                    if (success) {
                        callback(true, "Sign Up Successful")
                    } else {
                        callback(false, task.exception?.message ?: "Sign Up Failed")
                    }
                }
            } else {
                callback(false, task.exception?.message ?: "Sign Up Failed")
            }
        }
    }

    fun userLogin(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentUser = FirebaseAuth.getInstance().currentUser

                getUserRole(true) { success, role ->
                    if (success) {
                        currentUserRole = role

                        callback(true, "Login Successful", currentUserRole)
                    } else {
                        callback(false, "Login Failed - Couldn't Get User Role", "")
                    }
                }
            } else {
                callback(false, task.exception?.message ?: "Login Failed", "")
            }
        }
    }

    fun getUserRole(fromDB: Boolean, callback: (Boolean, String) -> Unit) {
        if (fromDB) {
            val reference = FirebaseDatabase.getInstance().reference.child("users/${currentUser?.uid}/role")

            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(true, snapshot.value as String)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, "Failed To Get User Role")
                }

            })
        } else {
            callback(true, currentUserRole)
        }
    }

    fun addDonation(donations: List<Models.FinalDonation>, donationLong: Double, donationLat: Double, donationName: String, callback: (Boolean, String) -> Unit) {
        val donationUID = generateUID()
        val donationsReference = FirebaseDatabase.getInstance().reference.child("donations/${donationUID}/donation")
        val donationDropOffLongReference = FirebaseDatabase.getInstance().reference.child("donations/${donationUID}/dropoffLong")
        val donationDropOffLatReference = FirebaseDatabase.getInstance().reference.child("donations/${donationUID}/dropoffLat")
        val donationCreatorReference = FirebaseDatabase.getInstance().reference.child("donations/${donationUID}/creator")
        val donationCreatorUIDReference = FirebaseDatabase.getInstance().reference.child("donations/${donationUID}/creatorUID")
        val donationProcessedReference = FirebaseDatabase.getInstance().reference.child("donations/${donationUID}/processed")
        val donationNameReference = FirebaseDatabase.getInstance().reference.child("donations/${donationUID}/name")

        donationsReference.setValue(Json.encodeToString(donations)).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                donationDropOffLongReference.setValue(donationLong).addOnCompleteListener { dropOffLongTask ->
                    if (dropOffLongTask.isSuccessful) {
                        donationDropOffLatReference.setValue(donationLat).addOnCompleteListener { dropOffLatTask ->
                            if (dropOffLatTask.isSuccessful) {
                                donationCreatorReference.setValue(currentUser?.email).addOnCompleteListener { creatorTask ->
                                    if (creatorTask.isSuccessful) {
                                        donationCreatorUIDReference.setValue(currentUser?.uid).addOnCompleteListener { creatorUIDTask ->
                                            if (creatorUIDTask.isSuccessful) {
                                                donationProcessedReference.setValue(false).addOnCompleteListener { processedTask ->
                                                    if (processedTask.isSuccessful) {
                                                        donationNameReference.setValue(donationName).addOnCompleteListener { donationNameTask ->
                                                            if (donationNameTask.isSuccessful) {
                                                                callback(true, "Added Donation")
                                                            } else {
                                                                callback(false, "Failed to Add Donation")
                                                            }
                                                        }
                                                    } else {
                                                        callback(false, "Failed to Add Donation")
                                                    }
                                                }
                                            } else {
                                                callback(false, "Failed to Add Donation")
                                            }
                                        }
                                    } else {
                                        callback(false, "Failed to Add Donation")
                                    }
                                }
                            } else {
                                callback(false, "Failed to Add Donation")
                            }
                        }
                    } else {
                        callback(false, "Failed to Add Donation")
                    }
                }
            } else {
                callback(false, "Failed to Add Donation")
            }
        }
    }

    fun addRequest(requests: List<Models.FinalRequest>, requestLong: Double, requestLat: Double, requestName: String, callback: (Boolean, String) -> Unit) {
        val requestUID = generateUID()
        val requestsReference = FirebaseDatabase.getInstance().reference.child("requests/${requestUID}/request")
        val requestPickUpLongReference = FirebaseDatabase.getInstance().reference.child("requests/${requestUID}/pickupLong")
        val requestPickUpLatReference = FirebaseDatabase.getInstance().reference.child("requests/${requestUID}/pickupLat")
        val requestCreatorReference = FirebaseDatabase.getInstance().reference.child("requests/${requestUID}/creator")
        val requestCreatorUIDReference = FirebaseDatabase.getInstance().reference.child("requests/${requestUID}/creatorUID")
        val requestProcessedReference = FirebaseDatabase.getInstance().reference.child("requests/${requestUID}/processed")
        val requestNameReference = FirebaseDatabase.getInstance().reference.child("requests/${requestUID}/name")

        requestsReference.setValue(Json.encodeToString(requests)).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                requestPickUpLongReference.setValue(requestLong).addOnCompleteListener { pickUpLongTask ->
                    if (pickUpLongTask.isSuccessful) {
                        requestPickUpLatReference.setValue(requestLat).addOnCompleteListener { pickUpLatTask ->
                            if (pickUpLatTask.isSuccessful) {
                                requestCreatorReference.setValue(currentUser?.email).addOnCompleteListener { creatorTask ->
                                    if (creatorTask.isSuccessful) {
                                        requestCreatorUIDReference.setValue(currentUser?.uid).addOnCompleteListener { creatorUIDTask ->
                                            if (creatorUIDTask.isSuccessful) {
                                                requestProcessedReference.setValue(false).addOnCompleteListener { processedTask ->
                                                    if (processedTask.isSuccessful) {
                                                        requestNameReference.setValue(requestName).addOnCompleteListener { requestNameTask ->
                                                            if (requestNameTask.isSuccessful) {
                                                                callback(true, "Added Request")
                                                            } else {
                                                                callback(false, "Failed to Add Request")
                                                            }
                                                        }
                                                    } else {
                                                        callback(false, "Failed to Add Request")
                                                    }
                                                }
                                            } else {
                                                callback(false, "Failed to Add Request")
                                            }
                                        }
                                    } else {
                                        callback(false, "Failed to Add Request")
                                    }
                                }
                            } else {
                                callback(false, "Failed to Add Request")
                            }
                        }
                    } else {
                        callback(false, "Failed to Add Request")
                    }
                }
            } else {
                callback(false, "Failed to Add Request")
            }
        }
    }

    fun getRequest(requestUID: String, callback: (Boolean, List<Models.FinalRequest>, requestLong: Double, requestLat: Double, creator: String, creatorUID: String, processed: Boolean, requestName: String) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("requests/${requestUID}")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requestUID = snapshot.key as String
                val requestCreator = snapshot.child("creator").value as String
                val requestCreatorUID = snapshot.child("creatorUID").value as String
                val requestPickUpLat = snapshot.child("pickupLat").value as Double
                val requestPickUpLong = snapshot.child("pickupLong").value as Double
                val requestList = Json.decodeFromString<List<Models.FinalRequest>>(snapshot.child("request").value as String)
                val requestProcessed = snapshot.child("processed").value as Boolean
                val requestName = snapshot.child("name").value as String

                callback(true, requestList, requestPickUpLong, requestPickUpLat, requestCreator, requestCreatorUID, requestProcessed, requestName)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, listOf(), 0.0, 0.0, "", "", false, "")
            }
        })
    }

    fun acceptDonation(donationUID: String, callback: (Boolean, String) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("donations/${donationUID}/processed")

        reference.setValue(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Accepted Donation")
            } else {
                callback(false, "Unable to Accept Donation")
            }
        }
    }

    fun acceptRequest(requestUID: String, callback: (Boolean, String) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("requests/${requestUID}/processed")

        reference.setValue(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Accepted Request")
            } else {
                callback(false, "Unable to Accept Request")
            }
        }
    }

    fun removeDonation(donationUID: String, callback: (Boolean, String) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("donations/${donationUID}")

        reference.removeValue().addOnSuccessListener {
            callback(true, "Removed Donation")
        }
        .addOnFailureListener {
            callback(false, "Failed to Remove Donation")
        }
    }

    fun removeRequest(requestUID: String, callback: (Boolean, String) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("requests/${requestUID}")

        reference.removeValue().addOnSuccessListener {
            callback(true, "Removed Request")
        }
        .addOnFailureListener {
            callback(false, "Failed to Remove Request")
        }
    }

    fun getDonation(donationUID: String, callback: (Boolean, List<Models.FinalDonation>, donationLong: Double, donationLat: Double, creator: String, creatorUID: String, processed: Boolean, donationName: String) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("donations/${donationUID}")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val donationUID = snapshot.key as String
                val donationCreator = snapshot.child("creator").value as String
                val donationCreatorUID = snapshot.child("creatorUID").value as String
                val donationDropOffLat = snapshot.child("dropoffLat").value as Double
                val donationDropOffLong = snapshot.child("dropoffLong").value as Double
                val donationList = Json.decodeFromString<List<Models.FinalDonation>>(snapshot.child("donation").value as String)
                val donationProcessed = snapshot.child("processed").value as Boolean
                val donationName = snapshot.child("name").value as String

                callback(true, donationList, donationDropOffLong, donationDropOffLat, donationCreator, donationCreatorUID, donationProcessed, donationName)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, listOf(), 0.0, 0.0, "", "", false, "")
            }
        })
    }

    fun getDonations(callback: (Boolean, List<Models.FinalDonations>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("donations")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var donations: List<Models.FinalDonations> = listOf()

                for (donationSnapshot in snapshot.children) {
                    val donationUID = donationSnapshot.key as String
                    val donationCreator = donationSnapshot.child("creator").value as String
                    val donationCreatorUID = donationSnapshot.child("creatorUID").value as String
                    val donationDropOffLat = donationSnapshot.child("dropoffLat").value as Double
                    val donationDropOffLong = donationSnapshot.child("dropoffLong").value as Double
                    val donationList = Json.decodeFromString<List<Models.FinalDonation>>(donationSnapshot.child("donation").value as String)
                    val donationProcessed = donationSnapshot.child("processed").value as Boolean
                    val donationName = donationSnapshot.child("name").value as String
                    val donation = Models.FinalDonations(donationList, donationUID, donationCreator, donationCreatorUID, donationDropOffLong, donationDropOffLat, donationProcessed, donationName)

                    donations = donations + donation
                }

                callback(true, donations)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, listOf())
            }
        })
    }

    fun getUnprocessedDonations(callback: (Boolean, List<Models.FinalDonations>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("donations")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var donations: List<Models.FinalDonations> = listOf()

                for (donationSnapshot in snapshot.children) {
                    val donationProcessed = donationSnapshot.child("processed").value as Boolean

                    if (!donationProcessed) {
                        val donationUID = donationSnapshot.key as String
                        val donationCreator = donationSnapshot.child("creator").value as String
                        val donationCreatorUID =
                            donationSnapshot.child("creatorUID").value as String
                        val donationDropOffLat =
                            donationSnapshot.child("dropoffLat").value as Double
                        val donationDropOffLong =
                            donationSnapshot.child("dropoffLong").value as Double
                        val donationList = Json.decodeFromString<List<Models.FinalDonation>>(
                            donationSnapshot.child("donation").value as String
                        )
                        val donationName = donationSnapshot.child("name").value as String
                        val donation = Models.FinalDonations(
                            donationList,
                            donationUID,
                            donationCreator,
                            donationCreatorUID,
                            donationDropOffLong,
                            donationDropOffLat,
                            donationProcessed,
                            donationName
                        )

                        donations = donations + donation
                    }
                }

                callback(true, donations)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, listOf())
            }
        })
    }

    fun getProcessedDonations(callback: (Boolean, List<Models.FinalDonations>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("donations")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var donations: List<Models.FinalDonations> = listOf()

                for (donationSnapshot in snapshot.children) {
                    val donationProcessed = donationSnapshot.child("processed").value as Boolean

                    if (donationProcessed) {
                        val donationUID = donationSnapshot.key as String
                        val donationCreator = donationSnapshot.child("creator").value as String
                        val donationCreatorUID =
                            donationSnapshot.child("creatorUID").value as String
                        val donationDropOffLat =
                            donationSnapshot.child("dropoffLat").value as Double
                        val donationDropOffLong =
                            donationSnapshot.child("dropoffLong").value as Double
                        val donationList = Json.decodeFromString<List<Models.FinalDonation>>(
                            donationSnapshot.child("donation").value as String
                        )
                        val donationName = donationSnapshot.child("name").value as String
                        val donation = Models.FinalDonations(
                            donationList,
                            donationUID,
                            donationCreator,
                            donationCreatorUID,
                            donationDropOffLong,
                            donationDropOffLat,
                            donationProcessed,
                            donationName
                        )

                        donations = donations + donation
                    }
                }

                callback(true, donations)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, listOf())
            }
        })
    }

    fun getRequests(callback: (Boolean, List<Models.FinalRequests>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("requests")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var requests: List<Models.FinalRequests> = listOf()

                for (requestSnapshot in snapshot.children) {
                    val requestUID = requestSnapshot.key as String
                    val requestCreator = requestSnapshot.child("creator").value as String
                    val requestCreatorUID = requestSnapshot.child("creatorUID").value as String
                    val requestPickUpLat = requestSnapshot.child("pickupLat").value as Double
                    val requestPickUpLong = requestSnapshot.child("pickupLong").value as Double
                    val requestList = Json.decodeFromString<List<Models.FinalRequest>>(requestSnapshot.child("request").value as String)
                    val requestProcessed = requestSnapshot.child("processed").value as Boolean
                    val requestName = requestSnapshot.child("name").value as String
                    val request = Models.FinalRequests(requestList, requestUID, requestCreator, requestCreatorUID, requestPickUpLong, requestPickUpLat, requestProcessed, requestName)

                    requests = requests + request
                }

                callback(true, requests)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, listOf())
            }
        })
    }

    fun getUnprocessedRequests(callback: (Boolean, List<Models.FinalRequests>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("requests")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var requests: List<Models.FinalRequests> = listOf()

                for (requestSnapshot in snapshot.children) {
                    val requestProcessed = requestSnapshot.child("processed").value as Boolean

                    if (!requestProcessed) {
                        val requestUID = requestSnapshot.key as String
                        val requestCreator = requestSnapshot.child("creator").value as String
                        val requestCreatorUID = requestSnapshot.child("creatorUID").value as String
                        val requestPickUpLat = requestSnapshot.child("pickupLat").value as Double
                        val requestPickUpLong = requestSnapshot.child("pickupLong").value as Double
                        val requestList =
                            Json.decodeFromString<List<Models.FinalRequest>>(requestSnapshot.child("request").value as String)

                        val requestName = requestSnapshot.child("name").value as String
                        val request = Models.FinalRequests(
                            requestList,
                            requestUID,
                            requestCreator,
                            requestCreatorUID,
                            requestPickUpLong,
                            requestPickUpLat,
                            requestProcessed,
                            requestName
                        )

                        requests = requests + request
                    }
                }

                callback(true, requests)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, listOf())
            }
        })
    }

    fun getProcessedRequests(callback: (Boolean, List<Models.FinalRequests>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("requests")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var requests: List<Models.FinalRequests> = listOf()

                for (requestSnapshot in snapshot.children) {
                    val requestProcessed = requestSnapshot.child("processed").value as Boolean

                    if (requestProcessed) {
                        val requestUID = requestSnapshot.key as String
                        val requestCreator = requestSnapshot.child("creator").value as String
                        val requestCreatorUID = requestSnapshot.child("creatorUID").value as String
                        val requestPickUpLat = requestSnapshot.child("pickupLat").value as Double
                        val requestPickUpLong = requestSnapshot.child("pickupLong").value as Double
                        val requestList =
                            Json.decodeFromString<List<Models.FinalRequest>>(requestSnapshot.child("request").value as String)

                        val requestName = requestSnapshot.child("name").value as String
                        val request = Models.FinalRequests(
                            requestList,
                            requestUID,
                            requestCreator,
                            requestCreatorUID,
                            requestPickUpLong,
                            requestPickUpLat,
                            requestProcessed,
                            requestName
                        )

                        requests = requests + request
                    }
                }

                callback(true, requests)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, listOf())
            }
        })
    }

    fun getDonationsByUser(callback: (Boolean, List<Models.FinalDonations>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("donations")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var donations: List<Models.FinalDonations> = listOf()

                for (donationSnapshot in snapshot.children) {
                    val donationCreatorUID = donationSnapshot.child("creatorUID").value as String

                    if (donationCreatorUID == currentUser?.uid) {
                        val donationUID = donationSnapshot.key as String
                        val donationCreator = donationSnapshot.child("creator").value as String
                        val donationDropOffLat = donationSnapshot.child("dropoffLat").value as Double
                        val donationDropOffLong = donationSnapshot.child("dropoffLong").value as Double
                        val donationList = Json.decodeFromString<List<Models.FinalDonation>>(donationSnapshot.child("donation").value as String)
                        val donationProcessed = donationSnapshot.child("processed").value as Boolean
                        val donationName = donationSnapshot.child("name").value as String
                        val donation = Models.FinalDonations(donationList, donationUID, donationCreator, donationCreatorUID, donationDropOffLong, donationDropOffLat, donationProcessed, donationName)

                        donations = donations + donation
                    }
                }

                callback(true, donations)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, listOf())
            }
        })
    }

    fun getRequestsByUser(callback: (Boolean, List<Models.FinalRequests>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().reference.child("requests")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var requests: List<Models.FinalRequests> = listOf()

                for (requestSnapshot in snapshot.children) {
                    val requestCreatorUID = requestSnapshot.child("creatorUID").value as String

                    if (requestCreatorUID == currentUser?.uid) {
                        val requestUID = requestSnapshot.key as String
                        val requestCreator = requestSnapshot.child("creator").value as String
                        val requestPickUpLat = requestSnapshot.child("pickupLat").value as Double
                        val requestPickUpLong = requestSnapshot.child("pickupLong").value as Double
                        val requestList = Json.decodeFromString<List<Models.FinalRequest>>(requestSnapshot.child("request").value as String)
                        val requestProcessed = requestSnapshot.child("processed").value as Boolean
                        val requestName = requestSnapshot.child("name").value as String
                        val request = Models.FinalRequests(requestList, requestUID, requestCreator, requestCreatorUID, requestPickUpLong, requestPickUpLat, requestProcessed, requestName)

                        requests = requests + request
                    }
                }

                callback(true, requests)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, listOf())
            }
        })
    }

    fun init(application: Application) {
        FirebaseApp.initializeApp(application)
    }
}