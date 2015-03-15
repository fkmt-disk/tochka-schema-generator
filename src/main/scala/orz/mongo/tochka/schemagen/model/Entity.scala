package orz.mongo.tochka.schemagen.model

import scala.xml._

case class Entity(namespace: String, name: String, fields: Seq[Field], schema: Boolean) {
  
  def toXml = {
    <entity name={name}>
      {fields.map(_.toXml)}
    </entity>
  }
  
  def fqcn = s"${namespace}.${name}"
  
}

object Entity {
  
  def apply(klass: NodeSeq, namespace: String) = new Entity(
    namespace,
    (klass \ "@name").text.trim,
    (klass \ "field").map(Field(_, namespace)),
    (klass \ "@schema").headOption.map(_.text.toBoolean).getOrElse(false)
  )
  
}
