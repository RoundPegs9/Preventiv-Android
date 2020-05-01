package ml.preventiv.onboarding

class ScreenItem(
    private var title: String,
    private var description: String,
    private var screenImg: Int
) {
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