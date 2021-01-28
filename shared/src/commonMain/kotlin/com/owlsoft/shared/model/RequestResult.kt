package com.owlsoft.shared.model

sealed class RequestResult {
    class Success(val code: String) : RequestResult()
    class Error(val message: String) : RequestResult()
}