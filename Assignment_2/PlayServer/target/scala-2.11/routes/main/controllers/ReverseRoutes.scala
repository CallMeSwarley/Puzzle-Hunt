
// @GENERATOR:play-routes-compiler
// @SOURCE:D:/PlayServer/AndroidTutorialVideos/conf/routes
// @DATE:Tue May 11 15:32:10 CEST 2021

import play.api.mvc.Call


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:6
package controllers {

  // @LINE:6
  class ReverseHomeController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:15
    def updateUserLocation(firebaseId:String, latitude:Double, longitude:Double): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "position/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("firebaseId", firebaseId)) + "/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[Double]].unbind("latitude", latitude)) + "/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[Double]].unbind("longitude", longitude)) + "/update")
    }
  
    // @LINE:10
    def test(firebaseId:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "test/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("firebaseId", firebaseId)))
    }
  
    // @LINE:8
    def tutorial(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "tutorial")
    }
  
    // @LINE:7
    def explore(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "explore")
    }
  
    // @LINE:16
    def getAllLocations(firebaseId:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "position/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("firebaseId", firebaseId)) + "/getAllPositions")
    }
  
    // @LINE:6
    def index(): Call = {
      
      Call("GET", _prefix)
    }
  
  }

  // @LINE:12
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:12
    def versioned(file:Asset): Call = {
      implicit lazy val _rrc = new play.core.routing.ReverseRouteContext(Map(("path", "/public"))); _rrc
      Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[play.api.mvc.PathBindable[Asset]].unbind("file", file))
    }
  
  }


}
