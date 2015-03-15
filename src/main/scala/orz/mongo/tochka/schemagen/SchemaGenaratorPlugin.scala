package orz.mongo.tochka

import sbt._
import sbt.Keys._

import scala.xml._

import java.io._
import javax.xml.transform._
import javax.xml.transform.stream._

import orz.mongo.tochka.schemagen.model._

object SchemaGenaratorPlugin extends AutoPlugin {
  
  lazy val schemaConf = settingKey[String]("schema config xml")
  
  lazy val schemaGen = taskKey[Unit]("tochka schema generator")
  
  override lazy val projectSettings = Seq(
    schemaConf := new File(baseDirectory.value, "tochka-schema.xml").getPath,
    schemaGen := {
      println("tochka schema generator start")
      
      val xml =
        schemaConf.value match {
          case path =>
            println("load xml from: " + path)
            XML.loadFile(path)
        }
      
      val entities =
        for {
          pack <- (xml \ "package")
          klass <- (pack \ "class")
          packageName = (pack \ "@name").text.trim
        } yield Entity(klass, packageName)
      
      val nonSchemas = entities.filterNot(_.schema)
      
      val srcDir: File = (scalaSource in Compile).value
      
      val xsl = new StreamSource(getClass.getResourceAsStream("/tochka-schema.xsl"))
      
      val trans = TransformerFactory.newInstance.newTransformer(xsl)
      
      println("generate scala source file: ")
      for (entity <- entities) {
        val node =
          <tochka>
            <package>{entity.namespace}</package>
            <import>com.mongodb.casbah.Imports._</import>
            <import>orz.mongo.tochka._</import>
            {entity.toXml}
            {if (entity.schema) Schema(entity, nonSchemas).toXml else null}
          </tochka>
        
        val src = new StreamSource(new StringReader(node.toString))
        
        val dir = new File(srcDir, entity.namespace.replaceAll("\\.", "/"))
        dir.mkdirs
        
        val output = new File(dir, s"${entity.name}.scala")
        
        trans.transform(src, new StreamResult(output))
        println("\t" + output.getPath)
      }
      
      println("tochka schema generator finish")
    }
  )
  
}
