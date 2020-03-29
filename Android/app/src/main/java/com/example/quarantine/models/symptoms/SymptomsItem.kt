package com.example.quarantine.models.symptoms

class SymptomsItem {
    var icons:Int ? = 0
    var caption:String ? = null
    constructor(icons: Int?, caption: String)
    {
        this.icons = icons
        this.caption = caption
    }
}