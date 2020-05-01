package com.example.quarantine.onboarding

class ScreenItem {
    private var title:String
    private var description:String
    private var screenImg:Int
    constructor(title:String, description:String, screenImg:Int)
    {
        this.title = title
        this.description = description
        this.screenImg = screenImg
    }
    fun setTitle(title: String)
    {
        this.title = title
    }
    fun setDescription(description: String)
    {
        this.description = description
    }
    fun setScreenImg(screenImg: Int)
    {
        this.screenImg = screenImg
    }

    fun getTitle():String
    {
        return this.title
    }
    fun getDescription():String
    {
        return this.description
    }
    fun getScreenImg():Int
    {
        return this.screenImg
    }
}