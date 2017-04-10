package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.FattPA2CenMapper;
import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class FattPA2CenConverter implements ToCenConversion {

    private static final Logger LOGGER = LoggerFactory.getLogger(FattPA2CenConverter.class);

    public BG0000Invoice convert(InputStream input) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            doc = dBuilder.parse(input);
        } catch ( IOException | ParserConfigurationException | SAXException e) {
            LOGGER.error(e.getMessage(), e);
        }
        assert doc != null;
        doc.getDocumentElement().normalize();


        return null;
    }

    public BG0000Invoice convert(String fileName) {
        return convert(new File(fileName));
    }

    public BG0000Invoice convert(File file) {
        BG0000Invoice converted = null;

        try(FileInputStream input = new FileInputStream(file)) {
            converted = convert(input);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return converted;
    }


    @Override
    public boolean support(String format) {
        return false;
    }
}
