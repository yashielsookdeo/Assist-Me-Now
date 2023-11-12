package com.ctrlaltdefeat.assistmenow.objects

import com.ctrlaltdefeat.assistmenow.data.Models

object Request {
    private var items: List<Models.AvailableRequest> = listOf()
    private var pickUpLong: Double = 0.0
    private var pickUpLat: Double = 0.0
    private var name: String = ""

    fun setItems(items: List<Models.AvailableRequest>) {
        this.items = items
    }

    fun getItems(): List<Models.AvailableRequest> {
        return items
    }

    fun resetItems() {
        items = listOf()
    }

    fun setRequestItemSelected(availableRequestID: Int, requestID: Int, selected: Boolean) {
        items[availableRequestID].items[requestID].selected = selected
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getName(): String {
        return name
    }

    fun setPickUp(long: Double, lat: Double) {
        pickUpLong = long
        pickUpLat = lat
    }

    fun getPickUpLong(): Double {
        return pickUpLong
    }

    fun getPickUpLat(): Double {
        return pickUpLat
    }
}