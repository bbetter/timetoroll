package com.owlsoft.shared.remote

import io.ktor.client.request.*

fun HttpRequestBuilder.jsonHeader() {
    header("Content-Type", "application/json")
}