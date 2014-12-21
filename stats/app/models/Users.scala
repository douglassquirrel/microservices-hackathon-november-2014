package models
case class Player(uuid: String, name:String)
case class Game(uuid: String)

object Users {
   var users = Set[Player]()
   var gamesInProgress = Set[Game]()

}
