
// @GENERATOR:play-routes-compiler
// @SOURCE:D:/PlayServer/AndroidTutorialVideos/conf/routes
// @DATE:Tue May 11 15:32:10 CEST 2021

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:6
package controllers.javascript {

  // @LINE:6
  class ReverseHomeController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:15
    def updateUserLocation: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HomeController.updateUserLocation",
      """
        function(firebaseId0,latitude1,longitude2) {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "position/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("firebaseId", firebaseId0)) + "/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[Double]].javascriptUnbind + """)("latitude", latitude1)) + "/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[Double]].javascriptUnbind + """)("longitude", longitude2)) + "/update"})
        }
      """
    )
  
    // @LINE:10
    def test: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HomeController.test",
      """
        function(firebaseId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "test/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("firebaseId", firebaseId0))})
        }
      """
    )
  
    // @LINE:8
    def tutorial: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HomeController.tutorial",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "tutorial"})
        }
      """
    )
  
    // @LINE:7
    def explore: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HomeController.explore",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "explore"})
        }
      """
    )
  
    // @LINE:16
    def getAllLocations: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HomeController.getAllLocations",
      """
        function(firebaseId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "position/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("firebaseId", firebaseId0)) + "/getAllPositions"})
        }
      """
    )
  
    // @LINE:6
    def index: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HomeController.index",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + """"})
        }
      """
    )
  
  }

  // @LINE:12
  class ReverseAssets(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:12
    def versioned: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Assets.versioned",
      """
        function(file1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[play.api.mvc.PathBindable[Asset]].javascriptUnbind + """)("file", file1)})
        }
      """
    )
  
  }


}
