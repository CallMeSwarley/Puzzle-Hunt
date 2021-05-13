
// @GENERATOR:play-routes-compiler
// @SOURCE:D:/PlayServer/AndroidTutorialVideos/conf/routes
// @DATE:Tue May 11 15:32:10 CEST 2021


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
