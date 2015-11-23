<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes"/>

    <!--
        DescriptorRecord
        \- DescriptorUI
        \- DescriptorName
           \- String
        \- Concept List
           \- Concept
              \- ConceptUI
              \- ConceptName
              \- SemanticTypeList
                 \- SemanticType
                    \- SemanticTypeUI
                    \- SemanticTypeName
              \- TermList
                 \- Term
                    \- TermUI
                    \- String (name?)
    -->

    <xsl:template match="DescriptorRecordSet">
        <DescriptorRecordSet LanguageCode = "eng">
            <xsl:apply-templates select="DescriptorRecord" />
        </DescriptorRecordSet>
    </xsl:template>

    <xsl:template match="DescriptorRecord">
        <DescriptorRecord>
            <DescriptorUI><xsl:value-of select="DescriptorUI" /></DescriptorUI>
            <DescriptorName><xsl:value-of select="DescriptorName/String" /></DescriptorName>
            <SemanticTypeList>
                <xsl:apply-templates select="ConceptList/Concept/SemanticTypeList" />
            </SemanticTypeList>
        </DescriptorRecord>
    </xsl:template>

    <xsl:template match="SemanticType">
        <SemanticTypeUI><xsl:value-of select="SemanticTypeUI" /></SemanticTypeUI>
    </xsl:template>

</xsl:stylesheet>
