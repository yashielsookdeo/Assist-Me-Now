package com.ctrlaltdefeat.assistmenow.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

class Models {
    @Parcelize
    data class AvailableDonation(val name: String, var items: List<DonationItem>) : Parcelable

    @Parcelize
    data class DonationItem(val item: String, var quantity: Int) : Parcelable

    @Parcelize
    data class DonationPosition(val position: Int) : Parcelable

    @Serializable
    data class FinalDonation(val item: String, var quantity: Int, var AvailableDonationID: Int, var DonationItemID: Int)

    data class FinalDonations(val donation: List<FinalDonation>, val uid: String, val creator: String, val creatorUID: String, val longitude: Double, val latitude: Double, val processed: Boolean, val name: String)

    @Parcelize
    data class AvailableRequest(val name: String, var items: List<RequestItem>) : Parcelable

    @Parcelize
    data class RequestItem(val item: String, var selected: Boolean) : Parcelable

    @Parcelize
    data class RequestPosition(val position: Int) : Parcelable

    @Serializable
    data class FinalRequest(val item: String, var selected: Boolean, var AvailableRequestID: Int, var RequestItemID: Int)

    data class FinalRequests(val request: List<FinalRequest>, val uid: String, val creator: String, val creatorUID: String, val longitude: Double, val latitude: Double, val processed: Boolean, val name: String)
}