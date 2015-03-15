package orz.mongo.tochka.schemagen.model

import scala.xml._

case class Schema(name: String, columns: Seq[Column]) {
  
  def toXml = {
    <schema name={name} extends="Schema">
      {columns.map(_.toXml)}
    </schema>
  }
  
}

object Schema {
  
  def apply(schema: Entity, entities: Seq[Entity]) = new Schema(
    schema.name,
    schema.fields.map(Column(_, entities))
  )
  
}
