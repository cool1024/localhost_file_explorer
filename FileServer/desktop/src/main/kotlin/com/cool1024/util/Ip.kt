package com.cool1024.server.util

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

object Ip {
    fun getHost(): List<String> {

        val hostList = arrayListOf<String>()
        val allNetInterfaces = NetworkInterface.getNetworkInterfaces()
        while (allNetInterfaces.hasMoreElements()) {
            val netInterface = allNetInterfaces.nextElement() as NetworkInterface
            if (netInterface.isLoopback || netInterface.isVirtual || !netInterface.isUp) {
                continue
            } else {
                val addresses = netInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val ip = addresses.nextElement()
                    if (ip != null && ip is Inet4Address) {
                        hostList.add(ip.getHostAddress())
                    }
                }
            }
        }
        return hostList
    }
}