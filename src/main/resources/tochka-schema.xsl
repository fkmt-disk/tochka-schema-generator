<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="text" encoding="utf-8"/>

<!-- ***************************************************************** main -->
<xsl:template match="/tochka"><!--
-->package <xsl:value-of select="package"/>

<xsl:value-of select="'&#10;'"/>

<xsl:for-each select="import">
import <xsl:value-of select="."/><xsl:value-of select="'&#10;'"/>
</xsl:for-each>

<xsl:apply-templates select="entity"/>

<xsl:apply-templates select="schema"/>
</xsl:template>

<!-- ***************************************************************** case-class -->
<xsl:template match="entity">
<xsl:variable name="indent" select="'  '"/>
case class <xsl:value-of select="@name"/>(
<xsl:for-each select="field">
<xsl:variable name="default"><xsl:if test="string-length(@default)!=0"><xsl:value-of select="concat(' = ', @default)"/></xsl:if></xsl:variable>
<xsl:variable name="line-end"><xsl:if test="position()!=last()">,</xsl:if><xsl:value-of select="'&#10;'"/></xsl:variable>
<xsl:value-of select="concat($indent, @name, ': ', @type, $default, $line-end)"/>
</xsl:for-each>)
</xsl:template>

<!-- ***************************************************************** object -->
<xsl:template match="schema">
object <xsl:value-of select="@name"/> extends <xsl:value-of select="@extends"/> {
<xsl:for-each select="column">
<xsl:apply-templates select="."><xsl:with-param name="indent" select="'  '"/></xsl:apply-templates>
</xsl:for-each>}
</xsl:template>

<!-- ***************************************************************** case-object -->
<xsl:template match="column">
<xsl:param name="indent" select="'  '"/>
<xsl:choose>
<xsl:when test="count(./column)=0">
<xsl:value-of select="concat($indent, 'case object ', @name, ' extends ', @type)"/><xsl:value-of select="'&#10;'"/>
</xsl:when>
<xsl:otherwise>
<xsl:value-of select="concat($indent, 'case object ', @name, ' extends ', @type)"/> {<xsl:value-of select="'&#10;'"/>
<xsl:for-each select="./column">
<xsl:apply-templates select="."><xsl:with-param name="indent" select="concat($indent, '  ')"/></xsl:apply-templates>
</xsl:for-each><xsl:value-of select="$indent"/>}
</xsl:otherwise>
</xsl:choose>
</xsl:template>

</xsl:stylesheet>
