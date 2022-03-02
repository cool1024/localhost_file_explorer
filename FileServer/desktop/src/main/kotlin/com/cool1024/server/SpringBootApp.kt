package com.cool1024.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class SpringBootApp {
    companion object {
        var sScanPath = ""
        var sHost = ""
        var app: ConfigurableApplicationContext? = null

        fun start(scanPath: String, host: String) {
            sScanPath = scanPath
            sHost = host
            if (app != null) {
                app!!.close()
            } else {
                app = runApplication<SpringBootApp>()
            }
        }
    }
}