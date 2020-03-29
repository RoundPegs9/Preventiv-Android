package com.example.quarantine.models.symptoms

class SymptomsItem {
    var icons:Int ? = 0
    var caption:String ? = null
    var metadata:String ? = null
    constructor(icons: Int?, caption: String, metadata: String?)
    {
        this.icons = icons
        this.caption = caption
        this.metadata = metadata
    }
}