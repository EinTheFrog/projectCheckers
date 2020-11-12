package view

import tornadofx.*
import view.MainMenu
import view.Styles

fun main(args: Array<String>) {
    launch<MyApp>(args)
}

class MyApp: App(MainMenu::class, Styles:: class)