<?xml version="1.0"?>
<xsl:stylesheet xmlns="http://www.opengis.net/kml/2.2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:skos="http://www.w3.org/2004/02/skos/core#" version="1.0">

<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

<xsl:template match="/">
<kml>
<Document>
	<name>TESTING</name>
	<xsl:apply-templates select="//place"/>
</Document>
</kml>
</xsl:template>

<xsl:template match="place">
  <Placemark>
    <name><xsl:value-of select="name"/></name>
    <description>Coverage...</description>
    <xsl:choose>
    	<xsl:when test="count(coordinates) &gt; 1">
    		<LineString>
    			<coordinates>
    				<xsl:for-each select="coordinates">
    					<xsl:value-of select="."/>
    					<xsl:text> </xsl:text>
    				</xsl:for-each>
    			</coordinates>
    		</LineString>
    	</xsl:when>
    	<xsl:otherwise>
		    <Point>
		      <coordinates><xsl:value-of select="coordinates"/></coordinates>
		    </Point>
    	</xsl:otherwise>
    </xsl:choose>
    <ExtendedData>
    	<xsl:apply-templates select="altLabel"/>
    </ExtendedData>
  </Placemark>
</xsl:template>

<xsl:template match="altLabel">
	<skos:altLabel><xsl:value-of select="."/></skos:altLabel>
</xsl:template>

</xsl:stylesheet>
