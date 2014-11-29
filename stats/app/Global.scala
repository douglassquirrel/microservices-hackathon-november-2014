import akka.actor.ActorRef
import controllers.PingActor
import play.api._
import play.libs.Akka

object Global extends GlobalSettings {

 //  val system = ActorSystem("MyActorSystem")
   var subscriber : Option[ActorRef] = None

   override def onStart(app: Application) {
      Logger.info("Application has started")

      subscriber = Some(Akka.system().actorOf(PingActor.props, "pingActor"))

      subscriber.get ! PingActor.Initialize

   }

   override def onStop(app: Application) {
      Logger.info("Application shutdown...")
    //  Akka.system().shutdown()
   }

}