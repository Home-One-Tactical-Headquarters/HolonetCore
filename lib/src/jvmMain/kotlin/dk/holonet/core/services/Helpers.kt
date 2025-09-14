package dk.holonet.core.services

fun getHolonetFolder(): String = System.getProperty("user.home") + "/Holonet/"
fun getConfig(): String = getHolonetFolder() + "config.json"
fun getPluginsFolder(): String = getHolonetFolder() + "plugins/"