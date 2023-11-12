package com.ctrlaltdefeat.assistmenow.objects

import com.ctrlaltdefeat.assistmenow.data.Models

object Donation {
    private var items: List<Models.AvailableDonation> = listOf()
    private var dropOffLong: Double = 0.0
    private var dropOffLat: Double = 0.0
    private var name: String = ""

    fun setItems(items: List<Models.AvailableDonation>) {
        this.items = items
    }

    fun getItems(): List<Models.AvailableDonation> {
        return items
    }

    fun resetItems() {
        items = listOf()
    }

    fun setDonationItemQuantity(availableDonationID: Int, donationID: Int, quantity: Int) {
        items[availableDonationID].items[donationID].quantity = quantity
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getName(): String {
        return name
    }

    fun setDropOff(long: Double, lat: Double) {
        dropOffLong = long
        dropOffLat = lat
    }

    fun getDropOffLong(): Double {
        return dropOffLong
    }

    fun getDropOffLat(): Double {
        return dropOffLat
    }
}