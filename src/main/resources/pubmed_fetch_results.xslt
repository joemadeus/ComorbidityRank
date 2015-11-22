<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="PubmedArticleSet">
        <PubmedArticleSet>
            <xsl:apply-templates select="PubmedArticle" />
        </PubmedArticleSet>
    </xsl:template>

    <xsl:template match="PubmedArticle">
        <PubmedArticle>
            <PMID><xsl:value-of select="MedlineCitation/PMID" /></PMID>
            <ArticleTitle><xsl:value-of select="MedlineCitation/Article/ArticleTitle" /></ArticleTitle>
            <JournalTitle><xsl:value-of select="MedlineCitation/Article/Journal/Title" /></JournalTitle>
            <PublicationYear><xsl:value-of select="MedlineCitation/Article/Journal/JournalIssue/PubDate/Year" /></PublicationYear>
            <MeshHeadingList>
                <xsl:apply-templates select="MedlineCitation/MeshHeadingList" />
            </MeshHeadingList>
        </PubmedArticle>
    </xsl:template>

    <xsl:template match="MeshHeading">
        <MeshHeading>
            <DescriptorName><xsl:value-of select="DescriptorName" /></DescriptorName>
            <xsl:if test="DescriptorName/@MajorTopicYN = 'N'"><MajorTopic>false</MajorTopic></xsl:if>
            <xsl:if test="DescriptorName/@MajorTopicYN = 'Y'"><MajorTopic>true</MajorTopic></xsl:if>
            <ConceptID><xsl:value-of select="DescriptorName/@UI" /></ConceptID>
            <QualifierList>
                <xsl:for-each select="QualifierName">
                    <Qualifier>
                        <QualifierName><xsl:value-of select="." /></QualifierName>
                        <xsl:if test="@MajorTopicYN = 'N'"><MajorTopic>false</MajorTopic></xsl:if>
                        <xsl:if test="@MajorTopicYN = 'Y'"><MajorTopic>true</MajorTopic></xsl:if>
                        <ConceptID><xsl:value-of select="@UI" /></ConceptID>
                    </Qualifier>
                </xsl:for-each>
            </QualifierList>
        </MeshHeading>
    </xsl:template>

</xsl:stylesheet>
