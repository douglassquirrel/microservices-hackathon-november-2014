package controllers

import akka.actor.Actor
import controllers.SubscriberActor.{Initialize, TimerTick}
import play.api.libs.json.{JsString, Json}
import play.api.libs.ws.{WSResponse, WS}

import scala.util.{Failure, Success}

object SubscriberActor {

   case object Initialize

   case object TimerTick

}

class SubscriberActor(topic: String, fn: WSResponse => Unit) extends Actor {
   import scala.concurrent.ExecutionContext.Implicits.global
   import play.api.Play.current
   var retrieval_url: String = ""

   def facts(topic: String) = s"http://combo-squirrel.herokuapp.com/topics/$topic/facts"

   def subscription(topic: String) = s"http://combo-squirrel.herokuapp.com/topics/$topic/subscriptions"


   def receive = {
      case Initialize =>

         WS.url(subscription(topic)).post("")
           .map(resp => {
            retrieval_url = (Json.parse(resp.body) \ "retrieval_url").asInstanceOf[JsString].value
            self ! TimerTick
         })


      case TimerTick =>
         WS.url(retrieval_url).get().onComplete {
            case Success(resp) => {
               println("poll result: " + resp.status + " body: " + resp.body)
               if (resp.status == 200) fn(resp)
               self ! TimerTick
            }



            case Failure(ex) => {
               println(s"Sie zesralem $ex")
               self ! TimerTick
            }
         }

   }
}
