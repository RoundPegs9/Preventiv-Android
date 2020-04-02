package com.example.quarantine.models.symptoms

class SymptomsItem {
    var icons:Int ? = 0
    var caption:String ? = null
    var score:Double ? = null
    constructor(icons: Int?, caption: String, score: Double)
    {
        this.icons = icons
        this.caption = caption
        this.score = score
    }
}