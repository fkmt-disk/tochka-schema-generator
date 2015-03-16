package orz.mongo.tochka.schemagen.model

import scala.xml._

case class Column(name: String, typeName: String, childs: Seq[Column]) {
  
  def toXml: NodeSeq = {
    <column name={name} type={typeName}>{childs.map(_.toXml)}</column>
  }
  
}

object Column {
  
  val TypeMap = Map(
    "Boolean"   -> "BoolField",
    "Int"       -> "IntField",
    "Long"      -> "LongField",
    "Double"    -> "DoubleField",
    "String"    -> "StringField",
    "ObjectId"  -> "IdField"
  )
  
  def apply(field: Field, entities: Seq[Entity]): Column = {
    val typeName =
      if (field.sequence)
        Some(s"SeqField[${field.rawTypeName}]")
      else
        TypeMap.get(field.rawTypeName)
    
    val childs =
      if (typeName.isEmpty)
        entities.find(_.fqcn == field.rawTypeName).map(e => e.fields.map(Column(_, entities)))
      else
        None
    
    new Column(
      field.name,
      typeName.getOrElse(s"AnyRefField[${field.rawTypeName}]"),
      childs.getOrElse(Seq.empty)
    )
  }
  
}
