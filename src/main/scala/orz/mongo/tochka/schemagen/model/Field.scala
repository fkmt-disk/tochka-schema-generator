package orz.mongo.tochka.schemagen.model

import scala.xml._

case class Field(name: String, rawTypeName: String, defaultValue: String, option: Boolean = false, sequence: Boolean = false) {
  
  def toXml = {
    <field name={name} type={typeName} default={defaultValue} />
  }
  
  def typeName = (option, sequence) match {
    case (true, true)   => s"Option[Seq[${rawTypeName}]]"
    case (true, false)  => s"Option[${rawTypeName}]"
    case (false, true)  => s"Seq[${rawTypeName}]"
    case _              => rawTypeName
  }
  
}

object Field {
  
  val OptSeqDot = """Option\[Seq\[\.([\w]+)\]\]""".r
  val OptSeq    = """Option\[Seq\[([\w]+)\]\]""".r
  val OptDot    = """Option\[\.([\w]+)\]""".r
  val Opt       = """Option\[([\w]+)\]""".r
  val SeqDot    = """Seq\[\.([\w]+)\]""".r
  val Seq       = """Seq\[([\w]+)\]""".r
  
  def apply(field: NodeSeq, namespace: String): Field = {
    val name = (field \ "@name").text.trim
    val defval = (field \ "@default").headOption.map(_.text.trim).getOrElse("")
    (field \ "@type").text match {
      case OptSeqDot(raw) =>
        val rawTypeName = s"${namespace}.${raw}"
        new Field(name, rawTypeName, defval, true, true)
      case OptSeq(raw) =>
        new Field(name, raw, defval, true, true)
      case OptDot(raw) =>
        new Field(name, s"${namespace}.${raw}", defval, true, false)
      case Opt(raw) =>
        new Field(name, raw, defval, true)
      case SeqDot(raw) =>
        new Field(name, s"${namespace}.${raw}", defval, false, true)
      case Seq(raw) =>
        new Field(name, raw, defval, false, true)
      case raw if raw.startsWith(".") =>
        new Field(name, s"${namespace}${raw}", defval)
      case raw =>
        new Field(name, raw, defval)
    }
  }
  
}
