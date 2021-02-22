package com.owlsoft.shared.sockets

import platform.Foundation.*
import platform.darwin.NSObject

internal actual class PlatformSocket actual constructor(
    private val url: String,
    private val authID: String
) {

    private val socketEndpoint = NSURL.URLWithString(url)!!

    private var webSocket: NSURLSessionWebSocketTask? = null

    actual fun openSocket(listener: PlatformSocketListener) {
        val configuration = NSURLSessionConfiguration.defaultSessionConfiguration()
        configuration.HTTPAdditionalHeaders = mapOf(
            "Authentication" to authID
        )
        val urlSession = NSURLSession.sessionWithConfiguration(
            configuration = configuration,
            delegate = object : NSObject(), NSURLSessionWebSocketDelegateProtocol {

                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didOpenWithProtocol: String?
                ) {
                    listener.onOpen()
                }

                override fun URLSession(
                    session: NSURLSession,
                    webSocketTask: NSURLSessionWebSocketTask,
                    didCloseWithCode: NSURLSessionWebSocketCloseCode,
                    reason: NSData?
                ) {
                    listener.onClosed(didCloseWithCode.toInt(), reason.toString())
                }
            },
            delegateQueue = NSOperationQueue.currentQueue()
        )

        webSocket = urlSession.webSocketTaskWithURL(socketEndpoint)
        listenMessages(listener)
        webSocket?.resume()
    }

    private fun listenMessages(listener: PlatformSocketListener) {
        webSocket?.receiveMessageWithCompletionHandler { message, nsError ->
            if (nsError != null) {
                listener.onFailure(Throwable(nsError.description() ?: ""))
            }
            if (message != null) {
                listener.onMessage(message.string ?: "")
            }
            listenMessages(listener)
        }
    }

    actual fun closeSocket(code: Int, reason: String) {
        webSocket?.cancelWithCloseCode(code.toLong(), null)
        webSocket = null
    }

    actual fun sendMessage(msg: String) {
        val message = NSURLSessionWebSocketMessage(msg)
        webSocket?.sendMessage(message) { err ->
            err?.let { println("send $msg error: $it") }
        }
    }
}