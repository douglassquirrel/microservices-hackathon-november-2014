package controllers

import akka.actor.{Actor, ActorLogging, Props}
import models.{Game, Player, Users}
import play.api.libs.ws.{WS, WSResponse}
import play.libs.Akka

import scala.util.{Failure, Success}

class PingActor extends Actor with ActorLogging {
  import controllers.PingActor._
  var counter = 0

   import scala.concurrent.ExecutionContext.Implicits.global
   import play.api.Play.current

   def facts(topic:String) = s"http://combo-squirrel.herokuapp.com/topics/$topic/facts"
   def subscription(topic:String) = s"http://combo-squirrel.herokuapp.com/topics/$topic/subscriptions"
   def topicsUrl = s"http://combo-squirrel.herokuapp.com/topics"



   def receive = {
  	case Initialize =>
      log.info("Getting topics lists")

      val subscriber = Akka.system().actorOf(Props(classOf[SubscriberActor], "player.joined", onPlayerJoined.apply _  ))
      val subscriberGameStart = Akka.system().actorOf(Props(classOf[SubscriberActor], "game.started", onGameStarted.apply _  ))
      subscriber ! SubscriberActor.Initialize
      subscriberGameStart ! SubscriberActor.Initialize


  }
   import play.api.libs.json._
   object onPlayerJoined {
      def apply(resp : WSResponse): Unit = {

         val json = Json.parse(resp.body)
         Users.users = Users.users + Player(
            (json \ "uuid").asInstanceOf[JsString].value,
            (json \ "name").asInstanceOf[JsString].value)



         publish("player.list", Json.obj( "players" ->
           Json.arr(Users.users.map(p =>
              Json.obj( "uuid"-> p.uuid, "name"-> p.name)).seq
               )))
      }
   }

   object onGameStarted {
      def apply(resp : WSResponse): Unit = {

         val json = Json.parse(resp.body)
         Users.gamesInProgress = Users.gamesInProgress + Game(
            (json \ "fakeuuid").asInstanceOf[JsString].value)


         publish("stats.universe_stats", Json.obj( "games_in_progress" ->
           Json.arr(Users.gamesInProgress.map(p =>
              Json.obj( "uuid"-> p.uuid)).seq
           )))
      }
   }

   def publish(topic:String, json : JsObject) = {
      WS.url(facts(topic)).post(
         json).onComplete{
         case Success(resp) => println("post result: " + resp.status + " body: "+resp.body)
         case Failure(ex) => println(ex)
      }
   }
}

object PingActor {
  val props = Props[PingActor]
  case object Initialize
  case class PingMessage(text: String)
}