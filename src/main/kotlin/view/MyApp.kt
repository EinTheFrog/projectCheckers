package view

import tornadofx.*
import view.MainMenu
import view.Styles

fun launchApp(args: Array<String>) {
    launch<MyApp>(args)
}

class MyApp: App(MainMenu::class, Styles:: class)