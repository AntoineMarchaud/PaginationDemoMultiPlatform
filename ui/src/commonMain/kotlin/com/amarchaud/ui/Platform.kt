package com.amarchaud.ui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform