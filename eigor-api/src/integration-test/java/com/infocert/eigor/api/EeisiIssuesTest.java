package com.infocert.eigor.api;

import com.infocert.eigor.api.ConversionUtil.KeepAll;
import it.infocert.eigor.api.ConversionResult;
import it.infocert.eigor.api.IConversionIssue;
import it.infocert.eigor.api.configuration.ConfigurationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlunit.matchers.CompareMatcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.Base64;
import java.util.List;

import static it.infocert.eigor.test.Utils.invoiceAsStream;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests of issues discovered and fixed during the 2nd phase of development,
 * called 'eeisi'.
 */
public class EeisiIssuesTest extends AbstractIssueTest {



    /**
     * Let's suppose to have an UBL invoice with very long fields like:
     * <pre>
     *     &lt;cac:PartyIdentification&gt;&lt;cbc:ID&gt;IT:ALBO:IngegneriElettroniciInformaticiIngegneriElettroniciInformatici:123456789012345678901234567890123456789012345678901234567890111&lt;/cbc:ID
     * </pre>
     * This is too long to be stored in XML PA, hence, the not truncated value should be stored in an attached file, in CSV format.
     * This CSV should have several columns, one for the untruncated value, the other for the truncated value.
     */
    @Test
    public void issueEeisi22() throws XPathExpressionException, IOException {

        // a conversion UBL - fatturaPA withouth errors.
        ConversionResult<byte[]> conversion = this.conversion.assertConversionWithoutErrors("/issues/issue-eeisi22-ubl.xml", "ubl", "fatturapa");

        // The CSV in base 64 is the 3rd attachment in this case.
        String truncatedValuesCSVInBase64 = evalXpathExpression(conversion, "//*[local-name()='Allegati'][3]/*[local-name()='Attachment']/text()");

        String csvSource = new String(Base64.getDecoder().decode(truncatedValuesCSVInBase64));


        List<CSVRecord> records = CSVFormat
                .DEFAULT
                .withFirstRecordAsHeader()
                .parse( new StringReader(csvSource) ).getRecords();
        assertThat( records.size(), is(2) );
        assertThat( records.get(0).get("Original value"), is("IngegneriElettroniciInformaticiIngegneriElettroniciInformatici") );
        assertThat( records.get(0).get("Trimmed value"), is("IngegneriElettroniciInformaticiIngegneriElettroniciInformati") );
        assertThat( records.get(1).get("Original value"), is("123456789012345678901234567890123456789012345678901234567890111") );
        assertThat( records.get(1).get("Trimmed value"), is("123456789012345678901234567890123456789012345678901234567890") );

    }



}
