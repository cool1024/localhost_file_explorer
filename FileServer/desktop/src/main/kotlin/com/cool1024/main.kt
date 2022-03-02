package com.cool1024

import com.cool1024.server.SpringBootApp
import com.cool1024.server.util.Ip
fun main() {
    println(Ip.getHost())
    SpringBootApp.start("/Users/xiaojian/Documents/GIT", "127.0.0.1" +
            ":8080")
}